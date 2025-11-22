package org.skydream.quizcraft;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;

@Mod(Quizcraft.MODID)
public class Quizcraft {
    public static final String MODID = "quizcraft";
    private static final Logger LOGGER = LogUtils.getLogger();

    private static QuizManager quizManager;

    public Quizcraft(IEventBus modEventBus, ModContainer modContainer) {
        // 注册通用设置事件
        modEventBus.addListener(this::commonSetup);

        // 注册客户端事件（只在客户端运行）
        modEventBus.addListener(this::onClientSetup);

        // 注册服务器端事件
        NeoForge.EVENT_BUS.register(this);

        // 注册配置
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("QuizCraft mod initialized");
    }

    // 客户端设置方法
    private void onClientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("QuizCraft client setup complete");
        // 注意：在客户端设置中访问 Minecraft 实例需要确保在正确的线程
        event.enqueueWork(() -> {
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        });
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        try {
            quizManager = new QuizManager(event.getServer());
            quizManager.loadConfig();
            quizManager.startAutoQuestionTimer();
            LOGGER.info("QuizCraft started successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to start QuizCraft", e);
        }
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        if (quizManager != null) {
            quizManager.stopAutoQuestionTimer();
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        QuizCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        if (quizManager != null) {
            quizManager.handlePlayerAnswer(event.getPlayer(), event.getRawText());
        }
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        if (quizManager != null) {
            quizManager.tick();
        }
    }

    public static QuizManager getQuizManager() {
        return quizManager;
    }
}