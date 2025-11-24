package org.skydream.sphinxriddle;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Integer> QUESTION_INTERVAL;
    public static final ModConfigSpec.ConfigValue<Integer> QUESTION_TIMEOUT;
    public static final ModConfigSpec.ConfigValue<Boolean> AUTO_QUESTION_ENABLED;
    public static final ModConfigSpec.ConfigValue<String> QUESTION_PREFIX;
    public static final ModConfigSpec.ConfigValue<String> REWARD_MESSAGE;
    public static final ModConfigSpec.ConfigValue<String> NEW_QUESTION_MESSAGE;
    public static final ModConfigSpec.ConfigValue<String> CONFIG_RELOADED_MESSAGE;

    // ★ 新增配置项
    public static final ModConfigSpec.ConfigValue<Boolean> ALLOW_MANUAL_QUESTION;
    public static final ModConfigSpec.ConfigValue<Boolean> SHOW_SCOREBOARD;

    static {
        BUILDER.push("SphinxRiddle Configuration");

        QUESTION_INTERVAL = BUILDER
                .comment("Time between auto questions (in seconds)")
                .defineInRange("questionInterval", 300, 60, 3600);

        QUESTION_TIMEOUT = BUILDER
                .comment("Time before question expires (in seconds)")
                .defineInRange("questionTimeout", 60, 30, 300);

        AUTO_QUESTION_ENABLED = BUILDER
                .comment("Enable automatic questions")
                .define("autoQuestionEnabled", true);

        ALLOW_MANUAL_QUESTION = BUILDER
                .comment("Allow players to manually start questions with /sr question")
                .define("allowManualQuestion", true);

        SHOW_SCOREBOARD = BUILDER
                .comment("Show scoreboard on the right side of client")
                .define("showScoreboard", true);

        QUESTION_PREFIX = BUILDER
                .comment("Prefix for questions")
                .define("questionPrefix", "&6[问答]&r ");

        REWARD_MESSAGE = BUILDER
                .comment("Message when player wins reward")
                .define("rewardMessage", "&a恭喜 %player% 正确回答问题！获得奖励: %reward%");

        NEW_QUESTION_MESSAGE = BUILDER
                .comment("Message when new question starts")
                .define("newQuestionMessage", "&e新问题: %question% &e(输入答案到聊天框)");

        CONFIG_RELOADED_MESSAGE = BUILDER
                .comment("Message when config is reloaded")
                .define("configReloadedMessage", "&aSphinxRiddle 配置重载完成");

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}