package org.skydream.quizcraft;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class QuizCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("qc")
                // question 命令放在最前面，所有用户都可以使用
                .then(Commands.literal("question")
                        .requires(source -> source.hasPermission(0))
                        .executes(context -> askNewQuestion(context.getSource())))

                // 管理命令放在后面，需要 OP 权限
                .then(Commands.literal("reload")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> reloadConfig(context.getSource())))

                .then(Commands.literal("add")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("question")
                                .then(Commands.argument("question", StringArgumentType.greedyString())
                                        .then(Commands.argument("answer", StringArgumentType.string())
                                                .executes(context -> addQuestion(context.getSource(),
                                                        StringArgumentType.getString(context, "question"),
                                                        StringArgumentType.getString(context, "answer")))))
                                .then(Commands.literal("reward")
                                        .then(Commands.argument("item", StringArgumentType.string())
                                                .then(Commands.argument("maxAmount", IntegerArgumentType.integer(1))
                                                        .executes(context -> addReward(context.getSource(),
                                                                StringArgumentType.getString(context, "item"),
                                                                IntegerArgumentType.getInteger(context, "maxAmount"))))))
                        )
                )
        );
    }

    private static int reloadConfig(CommandSourceStack source) {
        QuizManager manager = Quizcraft.getQuizManager();
        if (manager != null) {
            manager.reload();
            source.sendSuccess(() -> Component.literal(
                    Config.CONFIG_RELOADED_MESSAGE.get().replace("&", "§")), true);
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }

    private static int addQuestion(CommandSourceStack source, String question, String answer) {
        QuizManager manager = Quizcraft.getQuizManager();
        if (manager != null) {
            manager.addQuestion(new Question(question, answer));
            source.sendSuccess(() -> Component.literal("§a问题添加成功: " + question), true);
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }

    private static int addReward(CommandSourceStack source, String itemId, int maxAmount) {
        QuizManager manager = Quizcraft.getQuizManager();
        if (manager != null) {
            manager.addReward(new Reward(itemId, maxAmount));
            source.sendSuccess(() -> Component.literal("§a奖励添加成功: " + itemId + " (最多: " + maxAmount + ")"), true);
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }

    private static int askNewQuestion(CommandSourceStack source) {
        QuizManager manager = Quizcraft.getQuizManager();
        if (manager != null) {
            manager.askRandomQuestion();
            source.sendSuccess(() -> Component.literal("§a已发布新问题"), true);
            return Command.SINGLE_SUCCESS;
        }
        return 0;
    }
}