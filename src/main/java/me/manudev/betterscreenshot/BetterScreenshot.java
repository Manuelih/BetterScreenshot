package me.manudev.betterscreenshot;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.function.Consumer;

public class BetterScreenshot implements ClientModInitializer {
	public static final String MOD_ID = "betterscreenshot";
	public static final Logger LOGGER = LoggerFactory.getLogger("Better Screenshot");

	private static BetterScreenshotConfig config;

	@Override
	public void onInitializeClient() {
		config = BetterScreenshotConfig.load();

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
			ClientCommandManager.literal(MOD_ID)
				.then(fileCommand("upload", ScreenshotUploader::upload))
				.then(fileCommand("copy", BetterScreenshot::copyImage))
				.then(ClientCommandManager.literal("reload")
					.executes(ctx -> {
						config = BetterScreenshotConfig.load();
						ctx.getSource().sendFeedback(Text.translatable("betterscreenshot.config.reloaded"));
						return 1;
					}))
		));

		LOGGER.info("Better Screenshot loaded (host: {})", config.host);	}

	private static LiteralArgumentBuilder<FabricClientCommandSource> fileCommand(String name, Consumer<File> action) {
		return ClientCommandManager.literal(name)
			.then(ClientCommandManager.argument("file", StringArgumentType.greedyString())
				.executes(ctx -> {
					String fileName = StringArgumentType.getString(ctx, "file");
					File file = ScreenshotUploader.resolveScreenshot(fileName);
					if (file == null) {
						ctx.getSource().sendError(Text.translatable("betterscreenshot.error.not_found", fileName));
						return 0;
					}
					action.accept(file);
					return 1;
				}));
	}

	public static BetterScreenshotConfig config() {
		return config;
	}

	/** Called on the IO thread when F2 saved a screenshot; returns the chat message that replaces the vanilla one. */
	public static Text onScreenshotSaved(Text vanillaMessage, File file) {
		if (config.autoUpload) {
			ScreenshotUploader.upload(file);
		}
		return Messages.saved(vanillaMessage, file, !config.autoUpload);
	}

	public static void copyImage(File file) {
		MinecraftClient client = MinecraftClient.getInstance();
		ImageClipboard.copy(file).whenComplete((ignored, error) -> client.execute(() -> {
			if (error != null) {
				Throwable cause = error.getCause() != null ? error.getCause() : error;
				LOGGER.warn("Could not copy {} to the clipboard", file.getName(), cause);
				Messages.copyFailed(client, String.valueOf(cause.getMessage()));
			} else {
				Messages.imageCopied(client);
			}
		}));
	}
}
