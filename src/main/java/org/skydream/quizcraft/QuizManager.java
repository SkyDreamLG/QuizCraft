package org.skydream.quizcraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class QuizManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuizManager.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final MinecraftServer server;
    private final File configDir;
    private final File questionsFile;
    private final File rewardsFile;

    private List<Question> questions = new ArrayList<>();
    private List<Reward> rewards = new ArrayList<>();
    private ScheduledExecutorService scheduler;

    private ActiveQuestion currentQuestion;
    private int tickCounter = 0;
    private Set<UUID> answeredPlayers = new ConcurrentHashMap().newKeySet();

    public QuizManager(MinecraftServer server) {
        this.server = server;
        this.configDir = new File("config/quizcraft");
        this.questionsFile = new File(configDir, "questions.json");
        this.rewardsFile = new File(configDir, "rewards.json");

        if (!configDir.exists()) {
            configDir.mkdirs();
        }
    }

    public void loadConfig() {
        loadQuestions();
        loadRewards();
        LOGGER.info("Loaded {} questions and {} rewards", questions.size(), rewards.size());
    }

    private void loadQuestions() {
        if (!questionsFile.exists()) {
            createDefaultQuestions();
            return;
        }

        try (FileReader reader = new FileReader(questionsFile)) {
            Question[] questionArray = GSON.fromJson(reader, Question[].class);
            questions = new ArrayList<>(Arrays.asList(questionArray));
        } catch (IOException e) {
            LOGGER.error("Failed to load questions", e);
            createDefaultQuestions();
        }
    }

    private void loadRewards() {
        if (!rewardsFile.exists()) {
            createDefaultRewards();
            return;
        }

        try (FileReader reader = new FileReader(rewardsFile)) {
            Reward[] rewardArray = GSON.fromJson(reader, Reward[].class);
            rewards = new ArrayList<>(Arrays.asList(rewardArray));
        } catch (IOException e) {
            LOGGER.error("Failed to load rewards", e);
            createDefaultRewards();
        }
    }

    private void createDefaultQuestions() {
        questions = new ArrayList<>();
        questions.add(new Question("Minecraft中哪种生物会爆炸？", "苦力怕"));
        questions.add(new Question("用来合成火把的两种材料是什么？", "煤炭和木棍"));
        saveQuestions();
    }

    private void createDefaultRewards() {
        rewards = new ArrayList<>();
        rewards.add(new Reward("minecraft:diamond", 3));
        rewards.add(new Reward("minecraft:emerald", 5));
        rewards.add(new Reward("minecraft:iron_ingot", 10));
        saveRewards();
    }

    public void saveQuestions() {
        try (FileWriter writer = new FileWriter(questionsFile)) {
            GSON.toJson(questions, writer);
        } catch (IOException e) {
            LOGGER.error("Failed to save questions", e);
        }
    }

    public void saveRewards() {
        try (FileWriter writer = new FileWriter(rewardsFile)) {
            GSON.toJson(rewards, writer);
        } catch (IOException e) {
            LOGGER.error("Failed to save rewards", e);
        }
    }

    public void startAutoQuestionTimer() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }

        if (Config.AUTO_QUESTION_ENABLED.get()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            int interval = Config.QUESTION_INTERVAL.get();
            scheduler.scheduleAtFixedRate(this::askRandomQuestion, interval, interval, TimeUnit.SECONDS);
            LOGGER.info("Auto question timer started with {} second interval", interval);
        }
    }

    public void stopAutoQuestionTimer() {
        if (scheduler != null) {
            scheduler.shutdown();
            LOGGER.info("Auto question timer stopped");
        }
    }

    public void askRandomQuestion() {
        if (questions.isEmpty()) {
            LOGGER.warn("No questions available");
            return;
        }

        Question question = questions.get(new Random().nextInt(questions.size()));
        askQuestion(question);
    }

    public void askQuestion(Question question) {
        if (currentQuestion != null) {
            // Expire current question
            currentQuestion = null;
        }

        currentQuestion = new ActiveQuestion(question, System.currentTimeMillis());
        answeredPlayers.clear();

        String message = Config.NEW_QUESTION_MESSAGE.get()
                .replace("%question%", question.getQuestion());

        broadcastMessage(message);
        LOGGER.info("New question: {}", question.getQuestion());
    }

    public void handlePlayerAnswer(ServerPlayer player, String message) {
        if (currentQuestion == null || answeredPlayers.contains(player.getUUID())) {
            return;
        }

        String answer = currentQuestion.getQuestion().getAnswer().toLowerCase();
        if (message.toLowerCase().contains(answer)) {
            // Correct answer
            answeredPlayers.add(player.getUUID());
            rewardPlayer(player);
            currentQuestion = null; // Question answered, clear it
        }
    }

    private void rewardPlayer(ServerPlayer player) {
        if (rewards.isEmpty()) {
            LOGGER.warn("No rewards available");
            return;
        }

        Reward reward = rewards.get(new Random().nextInt(rewards.size()));

        Item item = BuiltInRegistries.ITEM.get(reward.getItemResourceLocation());

        if (item != null) {
            int amount = new Random().nextInt(reward.getMaxAmount()) + 1;
            ItemStack itemStack = new ItemStack(item, amount);

            if (player.getInventory().add(itemStack)) {
                // 修复物品名称显示
                String itemId = reward.getItemId();
                String[] parts = itemId.split(":");
                String itemName = parts.length > 1 ? parts[1] : itemId;

                // 简单转换：iron_ingot -> Iron Ingot
                itemName = itemName.replace("_", " ");
                String[] words = itemName.split(" ");
                StringBuilder displayName = new StringBuilder();
                for (String word : words) {
                    if (!word.isEmpty()) {
                        displayName.append(Character.toUpperCase(word.charAt(0)))
                                .append(word.substring(1)).append(" ");
                    }
                }

                String finalName = displayName.toString().trim();
                String rewardMessage = Config.REWARD_MESSAGE.get()
                        .replace("%player%", player.getScoreboardName())
                        .replace("%reward%", amount + "x " + finalName);

                broadcastMessage(rewardMessage);
                LOGGER.info("Player {} received reward: {}x {}", player.getScoreboardName(), amount, finalName);
            }
        } else {
            LOGGER.error("Invalid reward item: {}", reward.getItemId());
        }
    }

    public void tick() {
        if (currentQuestion != null) {
            tickCounter++;
            if (tickCounter >= 20) { // Check every second
                tickCounter = 0;
                long currentTime = System.currentTimeMillis();
                long elapsed = (currentTime - currentQuestion.getStartTime()) / 1000;

                if (elapsed >= Config.QUESTION_TIMEOUT.get()) {
                    currentQuestion = null; // Question expired
                    answeredPlayers.clear();
                    LOGGER.info("Question expired due to timeout");
                }
            }
        }
    }

    private void broadcastMessage(String message) {
        // Remove color codes for server log
        String cleanMessage = message.replaceAll("&[0-9a-f]", "");
        Component component = Component.literal(message.replace("&", "§"));

        server.getPlayerList().getPlayers().forEach(player ->
                player.sendSystemMessage(component));

        LOGGER.info(cleanMessage);
    }

    // Getters and setters
    public List<Question> getQuestions() { return questions; }
    public List<Reward> getRewards() { return rewards; }
    public ActiveQuestion getCurrentQuestion() { return currentQuestion; }

    public void addQuestion(Question question) {
        questions.add(question);
        saveQuestions();
    }

    public void addReward(Reward reward) {
        rewards.add(reward);
        saveRewards();
    }

    public void reload() {
        loadConfig();
        stopAutoQuestionTimer();
        startAutoQuestionTimer();
        currentQuestion = null;
        answeredPlayers.clear();
    }
}