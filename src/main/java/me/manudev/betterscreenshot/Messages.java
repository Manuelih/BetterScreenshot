package me.manudev.betterscreenshot;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;
import java.net.URI;

public final class Messages {
	private static final SystemToast.Type TOAST = new SystemToast.Type(3000L);

	private Messages() {
	}

	private static MutableText prefix() {
		return Text.literal("[").formatted(Formatting.DARK_GRAY)
			.append(Text.literal("Better Screenshot").formatted(Formatting.AQUA))
			.append(Text.literal("] ").formatted(Formatting.DARK_GRAY));
	}

	private static void chat(MinecraftClient client, Text text) {
		client.inGameHud.getChatHud().addMessage(text);
	}

	private static void toast(MinecraftClient client, Text title, Text description) {
		if (BetterScreenshot.config().showToasts) {
			SystemToast.show(client.getToastManager(), TOAST, title, description);
		}
	}

	private static MutableText button(String key, Formatting color, String command) {
		return Text.translatable(key).formatted(color, Formatting.BOLD)
			.styled(style -> style
				.withClickEvent(new ClickEvent.RunCommand(command))
				.withHoverEvent(new HoverEvent.ShowText(Text.translatable(key + ".hover"))));
	}

	private static String command(String action, File file) {
		return "/" + BetterScreenshot.MOD_ID + " " + action + " " + file.getName();
	}

	/** "Saved screenshot as <file> [Upload] [Copy]" */
	public static Text saved(Text vanillaMessage, File file, boolean withUpload) {
		MutableText text = vanillaMessage.copy();
		if (withUpload) {
			text.append(" ").append(button("betterscreenshot.button.upload", Formatting.GREEN, command("upload", file)));
		}
		return text.append(" ").append(button("betterscreenshot.button.copy", Formatting.YELLOW, command("copy", file)));
	}

	public static void savedInScreen(MinecraftClient client, File file) {
		toast(client, Text.translatable("betterscreenshot.toast.saved"), Text.literal(file.getName()));
	}

	public static void uploading(MinecraftClient client) {
		toast(client, Text.translatable("betterscreenshot.toast.uploading"), Text.literal(BetterScreenshot.config().host));
	}

	public static void uploaded(MinecraftClient client, String url, boolean copied) {
		MutableText link = Text.literal(url).formatted(Formatting.AQUA, Formatting.UNDERLINE)
			.styled(style -> style
				.withClickEvent(new ClickEvent.OpenUrl(URI.create(url)))
				.withHoverEvent(new HoverEvent.ShowText(Text.translatable("betterscreenshot.link.hover"))));
		MutableText copy = Text.translatable("betterscreenshot.button.copy_link").formatted(Formatting.YELLOW)
			.styled(style -> style
				.withClickEvent(new ClickEvent.CopyToClipboard(url))
				.withHoverEvent(new HoverEvent.ShowText(Text.translatable("betterscreenshot.button.copy_link.hover"))));
		chat(client, prefix()
			.append(Text.translatable(copied ? "betterscreenshot.uploaded.copied" : "betterscreenshot.uploaded").formatted(Formatting.GREEN))
			.append(" ").append(link).append(" ").append(copy));
		toast(client, Text.translatable("betterscreenshot.toast.done"),
			Text.translatable(copied ? "betterscreenshot.toast.copied" : "betterscreenshot.toast.ready"));
	}

	public static void failed(MinecraftClient client, File file, String reason) {
		MutableText retry = Text.translatable("betterscreenshot.button.retry").formatted(Formatting.GOLD)
			.styled(style -> style.withClickEvent(new ClickEvent.RunCommand(command("upload", file))));
		chat(client, prefix()
			.append(Text.translatable("betterscreenshot.error.upload", reason).formatted(Formatting.RED))
			.append(" ").append(retry));
		toast(client, Text.translatable("betterscreenshot.toast.failed"), Text.literal(reason));
	}

	public static void imageCopied(MinecraftClient client) {
		chat(client, prefix().append(Text.translatable("betterscreenshot.image.copied").formatted(Formatting.GREEN)));
		toast(client, Text.translatable("betterscreenshot.image.copied"), null);
	}

	public static void copyFailed(MinecraftClient client, String reason) {
		chat(client, prefix().append(Text.translatable("betterscreenshot.error.copy", reason).formatted(Formatting.RED)));
	}
}
