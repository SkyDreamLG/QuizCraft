package org.skydream.sphinxriddle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class QuizScoreboard {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final File scoreboardFile;
    private Map<String, Integer> playerScores;

    public QuizScoreboard(File configDir) {
        this.scoreboardFile = new File(configDir, "scoreboard.json");
        this.playerScores = new HashMap<>();
        loadScoreboard();
    }

    public void loadScoreboard() {
        if (!scoreboardFile.exists()) {
            playerScores = new HashMap<>();
            return;
        }

        try (FileReader reader = new FileReader(scoreboardFile)) {
            PlayerScore[] scores = GSON.fromJson(reader, PlayerScore[].class);
            playerScores = new HashMap<>();
            for (PlayerScore score : scores) {
                playerScores.put(score.getPlayerName(), score.getScore());
            }
        } catch (IOException e) {
            playerScores = new HashMap<>();
        }
    }

    public void saveScoreboard() {
        try (FileWriter writer = new FileWriter(scoreboardFile)) {
            List<PlayerScore> scores = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : playerScores.entrySet()) {
                scores.add(new PlayerScore(entry.getKey(), entry.getValue()));
            }
            GSON.toJson(scores, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addScore(String playerName, int points) {
        int currentScore = playerScores.getOrDefault(playerName, 0);
        playerScores.put(playerName, currentScore + points);
        saveScoreboard();
    }

    public void resetScoreboard() {
        playerScores.clear();
        saveScoreboard();
    }

    public Map<String, Integer> getRankings() {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(playerScores.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        Map<String, Integer> sortedScores = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedScores.put(entry.getKey(), entry.getValue());
        }
        return sortedScores;
    }

    // 计分板显示功能
    public void initializeScoreboard(MinecraftServer server) {
        // 根据配置决定是否显示计分板
        if (Config.SHOW_SCOREBOARD.get()) {
            createScoreboard(server);
        } else {
            removeScoreboard(server);
        }
    }

    public void updateScoreboardDisplay(MinecraftServer server) {
        // 根据配置决定是否显示计分板
        if (Config.SHOW_SCOREBOARD.get()) {
            createScoreboard(server);
        } else {
            removeScoreboard(server);
        }
    }

    // 新增：移除计分板的方法
    private void removeScoreboard(MinecraftServer server) {
        Scoreboard scoreboard = server.getScoreboard();
        String objectiveName = "sphinxriddle_ranking";

        Objective oldObj = scoreboard.getObjective(objectiveName);
        if (oldObj != null) {
            scoreboard.removeObjective(oldObj);
        }
    }

    private void createScoreboard(MinecraftServer server) {
        Scoreboard scoreboard = server.getScoreboard();
        String objectiveName = "sphinxriddle_ranking";

        // 移除旧目标
        Objective oldObj = scoreboard.getObjective(objectiveName);
        if (oldObj != null) {
            scoreboard.removeObjective(oldObj);
        }

        // 创建新目标 - 使用国际化标题
        Objective objective = scoreboard.addObjective(
                objectiveName,
                ObjectiveCriteria.DUMMY,
                Component.translatable("sphinxriddle.scoreboard.title"),
                ObjectiveCriteria.RenderType.INTEGER,
                false,
                null
        );

        // 显示在右侧
        scoreboard.setDisplayObjective(DisplaySlot.SIDEBAR, objective);

        // 添加分数
        int rank = 1;
        for (Map.Entry<String, Integer> entry : getRankings().entrySet()) {
            if (rank > 15) break;

            String displayName = getRankDisplayName(rank, entry.getKey());
            ScoreHolder holder = ScoreHolder.forNameOnly(displayName);

            // 设置分数
            scoreboard.getOrCreatePlayerScore(holder, objective).set(entry.getValue());
            rank++;
        }
    }

    private String getRankDisplayName(int rank, String playerName) {
        String color = switch (rank) {
            case 1 -> "§6§l"; // 金色加粗 - 冠军
            case 2 -> "§b§l"; // 天蓝色加粗 - 亚军
            case 3 -> "§a§l"; // 绿色加粗 - 季军
            case 4 -> "§e";   // 黄色 - 第四名
            case 5 -> "§d";   // 粉色 - 第五名
            default -> "§7";   // 灰色 - 其他名次
        };

        return color + rank + ". " + playerName;
    }

    // 内部类
    private static class PlayerScore {
        private final String playerName;
        private final int score;

        public PlayerScore(String playerName, int score) {
            this.playerName = playerName;
            this.score = score;
        }
        public String getPlayerName() { return playerName; }
        public int getScore() { return score; }

    }
}