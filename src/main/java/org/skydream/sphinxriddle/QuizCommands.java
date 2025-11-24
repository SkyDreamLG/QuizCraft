package org.skydream.sphinxriddle;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Map;

public class QuizCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        System.out.println("QuizCommands: Starting command registration");

        // 主命令 - 帮助
        dispatcher.register(Commands.literal("sr")
                .executes(ctx -> {
                    showHelp(ctx.getSource());
                    return Command.SINGLE_SUCCESS;
                })
        );

        // 普通玩家命令 - 根据配置决定权限等级
        dispatcher.register(Commands.literal("sr")
                .then(Commands.literal("question")
                        // 根据配置决定权限等级：允许手动发起则为0，否则为2(OP)
                        .requires(source -> source.hasPermission(
                                Config.ALLOW_MANUAL_QUESTION.get() ? 0 : 2
                        ))
                        .executes(ctx -> askNewQuestion(ctx.getSource())))
        );

        // 重载命令
        dispatcher.register(Commands.literal("sr")
                .then(Commands.literal("reload")
                        .requires(source -> source.hasPermission(2))
                        .executes(ctx -> reloadConfig(ctx.getSource())))
        );

        // 添加问题
        dispatcher.register(Commands.literal("sr")
                .then(Commands.literal("add")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("question")
                                .then(Commands.argument("question", StringArgumentType.string())
                                        .then(Commands.argument("answer", StringArgumentType.string())
                                                .executes(ctx -> addQuestion(ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "question"),
                                                        StringArgumentType.getString(ctx, "answer"))))))
                )
        );

        // 添加奖励（物品自动补全）
        dispatcher.register(Commands.literal("sr")
                .then(Commands.literal("add")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("reward")
                                .then(Commands.argument("item", ItemArgument.item(context))
                                        .then(Commands.argument("maxAmount", IntegerArgumentType.integer(1))
                                                .executes(ctx -> {

                                                    ItemStack stack = ItemArgument.getItem(ctx, "item").createItemStack(1, false);
                                                    Item item = stack.getItem();
                                                    ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);

                                                    return addReward(ctx.getSource(),
                                                            id.toString(),
                                                            IntegerArgumentType.getInteger(ctx, "maxAmount"));
                                                }))))
                )
        );

        // 列出问题
        dispatcher.register(Commands.literal("sr")
                .then(Commands.literal("list")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("question")
                                .executes(ctx -> listQuestions(ctx.getSource())))
                )
        );

        // 列出奖励
        dispatcher.register(Commands.literal("sr")
                .then(Commands.literal("list")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("reward")
                                .executes(ctx -> listRewards(ctx.getSource())))
                )
        );

        // 排行榜
        dispatcher.register(Commands.literal("sr")
                .then(Commands.literal("list")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("ranking")
                                .executes(ctx -> listRanking(ctx.getSource())))
                )
        );

        // 重置排行榜
        dispatcher.register(Commands.literal("sr")
                .then(Commands.literal("reset")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("ranking")
                                .executes(ctx -> resetRanking(ctx.getSource())))
                )
        );

        // 移除问题
        dispatcher.register(Commands.literal("sr")
                .then(Commands.literal("remove")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("question")
                                .then(Commands.argument("question", StringArgumentType.string())
                                        .executes(ctx -> removeQuestion(ctx.getSource(),
                                                StringArgumentType.getString(ctx, "question"))))
                        )
                )
        );

        // 移除奖励（同样使用物品自动补全）
        dispatcher.register(Commands.literal("sr")
                .then(Commands.literal("remove")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("reward")
                                .then(Commands.argument("item", ItemArgument.item(context))
                                        .executes(ctx -> {

                                            ItemStack stack = ItemArgument.getItem(ctx, "item").createItemStack(1, false);
                                            Item item = stack.getItem();
                                            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);

                                            return removeReward(ctx.getSource(), id.toString());
                                        }))
                        )
                )
        );

        System.out.println("QuizCommands: All commands registered successfully");
    }

    private static void showHelp(CommandSourceStack source) {
        source.sendSuccess(() -> Component.translatable("sphinxriddle.command.help.title"), false);

        // 根据配置显示不同的权限提示
        if (Config.ALLOW_MANUAL_QUESTION.get()) {
            source.sendSuccess(() -> Component.translatable("sphinxriddle.command.help.question"), false);
        } else {
            source.sendSuccess(() -> Component.translatable("sphinxriddle.command.help.question_op"), false);
        }

        source.sendSuccess(() -> Component.translatable("sphinxriddle.command.help.add_reward"), false);
        source.sendSuccess(() -> Component.translatable("sphinxriddle.command.help.remove_reward"), false);
    }

    private static int reloadConfig(CommandSourceStack source) {
        QuizManager manager = SphinxRiddle.getQuizManager();
        if (manager != null) {
            manager.reload();
            source.sendSuccess(() -> Component.translatable("sphinxriddle.command.reload.success"), true);
            return Command.SINGLE_SUCCESS;
        }
        source.sendSuccess(() -> Component.translatable("sphinxriddle.command.reload.failed"), true);
        return 0;
    }

    private static int addQuestion(CommandSourceStack source, String question, String answer) {
        QuizManager manager = SphinxRiddle.getQuizManager();
        if (manager != null) {
            manager.addQuestion(new Question(question, answer));
            source.sendSuccess(() -> Component.translatable("sphinxriddle.command.add_question.success", question), true);
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }

    private static int addReward(CommandSourceStack source, String itemId, int maxAmount) {
        QuizManager manager = SphinxRiddle.getQuizManager();
        if (manager != null) {
            manager.addReward(new Reward(itemId, maxAmount));
            source.sendSuccess(() -> Component.translatable("sphinxriddle.command.add_reward.success", itemId, maxAmount), true);
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }

    private static int askNewQuestion(CommandSourceStack source) {
        QuizManager manager = SphinxRiddle.getQuizManager();
        if (manager != null) {
            manager.askRandomQuestion();
            source.sendSuccess(() -> Component.translatable("sphinxriddle.command.question.success"), true);
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }

    private static int listQuestions(CommandSourceStack source) {
        QuizManager manager = SphinxRiddle.getQuizManager();
        if (manager != null) {
            source.sendSuccess(() -> Component.translatable("sphinxriddle.command.list.questions"), false);
            int i = 1;
            for (Question q : manager.getQuestions()) {
                int idx = i++;
                source.sendSuccess(() ->
                        Component.literal("§e" + idx + ". §f" + q.getQuestion() + " §7-> §a" + q.getAnswer()), false);
            }
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }

    private static int listRewards(CommandSourceStack source) {
        QuizManager manager = SphinxRiddle.getQuizManager();
        if (manager != null) {
            source.sendSuccess(() -> Component.translatable("sphinxriddle.command.list.rewards"), false);
            int i = 1;
            for (Reward r : manager.getRewards()) {
                int idx = i++;
                source.sendSuccess(() ->
                        Component.literal("§e" + idx + ". §f" + r.getItemId() + " §7(最多: §a" + r.getMaxAmount() + "§7)"), false);
            }
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }

    private static int listRanking(CommandSourceStack source) {
        QuizManager manager = SphinxRiddle.getQuizManager();
        if (manager != null) {
            Map<String, Integer> rankings = manager.getScoreboard().getRankings();
            source.sendSuccess(() -> Component.translatable("sphinxriddle.command.list.ranking"), false);

            if (rankings.isEmpty()) {
                source.sendSuccess(() -> Component.translatable("sphinxriddle.command.list.no_data"), false);
            } else {
                int rank = 1;
                for (Map.Entry<String, Integer> entry : rankings.entrySet()) {
                    String color = rank == 1 ? "§6" : rank == 2 ? "§7" : rank == 3 ? "§c" : "§f";
                    int finalRank = rank;
                    source.sendSuccess(() -> Component.literal(color + finalRank + ". §f" + entry.getKey() + " §7- §a" + entry.getValue() +
                            Component.translatable("sphinxriddle.scoreboard.points").getString()), false);
                    rank++;
                    if (rank > 10) break;
                }
            }
            return Command.SINGLE_SUCCESS;
        }
        source.sendSuccess(() -> Component.translatable("sphinxriddle.command.reload.failed"), true);
        return 0;
    }

    private static int resetRanking(CommandSourceStack source) {
        QuizManager manager = SphinxRiddle.getQuizManager();
        if (manager != null) {
            manager.getScoreboard().resetScoreboard();
            manager.updateScoreboardForAllPlayers();
            source.sendSuccess(() -> Component.translatable("sphinxriddle.command.reset_ranking.success"), true);
            return Command.SINGLE_SUCCESS;
        }
        source.sendSuccess(() -> Component.translatable("sphinxriddle.command.reload.failed"), true);
        return 0;
    }

    private static int removeQuestion(CommandSourceStack source, String text) {
        QuizManager manager = SphinxRiddle.getQuizManager();
        if (manager != null) {
            manager.removeQuestion(text);
            source.sendSuccess(() -> Component.translatable("sphinxriddle.command.remove_question.success", text), true);
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }

    private static int removeReward(CommandSourceStack source, String itemId) {
        QuizManager manager = SphinxRiddle.getQuizManager();
        if (manager != null) {
            manager.removeReward(itemId);
            source.sendSuccess(() -> Component.translatable("sphinxriddle.command.remove_reward.success", itemId), true);
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }
}