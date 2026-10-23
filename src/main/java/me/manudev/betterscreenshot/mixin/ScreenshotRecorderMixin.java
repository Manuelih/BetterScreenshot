package me.manudev.betterscreenshot.mixin;

import me.manudev.betterscreenshot.BetterScreenshot;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.io.File;
import java.util.function.Consumer;

/**
 * The 3-argument overload is the one the screenshot key (F2) uses; its signature is the same
 * from 1.21.8 to 1.21.11, unlike Keyboard#onKey.
 */
@Mixin(ScreenshotRecorder.class)
public abstract class ScreenshotRecorderMixin {
	@ModifyVariable(
		method = "saveScreenshot(Ljava/io/File;Lnet/minecraft/client/gl/Framebuffer;Ljava/util/function/Consumer;)V",
		at = @At("HEAD"),
		argsOnly = true
	)
	private static Consumer<Text> betterscreenshot$replaceMessage(Consumer<Text> original, File gameDirectory, Framebuffer framebuffer) {
		return message -> {
			File file = betterscreenshot$savedFile(message);
			original.accept(file != null ? BetterScreenshot.onScreenshotSaved(message, file) : message);
		};
	}

	@Unique
	private static File betterscreenshot$savedFile(Text message) {
		if (message.getContent() instanceof TranslatableTextContent content
			&& "screenshot.success".equals(content.getKey())
			&& content.getArgs().length > 0
			&& content.getArgs()[0] instanceof Text name
			&& name.getStyle().getClickEvent() instanceof ClickEvent.OpenFile openFile) {
			return new File(openFile.path());
		}
		return null;
	}
}
