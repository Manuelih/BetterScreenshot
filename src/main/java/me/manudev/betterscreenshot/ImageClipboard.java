package me.manudev.betterscreenshot;

import net.minecraft.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

/**
 * Copies a PNG file to the system clipboard as an image. Minecraft runs AWT headless,
 * so this goes through the platform's own clipboard tools.
 */
public final class ImageClipboard {
	private ImageClipboard() {
	}

	public static CompletableFuture<Void> copy(File file) {
		return CompletableFuture.runAsync(() -> {
			try {
				run(file);
			} catch (IOException e) {
				throw new CompletionException(e);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new CompletionException(e);
			}
		});
	}

	private static void run(File file) throws IOException, InterruptedException {
		String path = file.getAbsolutePath();
		ProcessBuilder builder = switch (Util.getOperatingSystem()) {
			case WINDOWS -> new ProcessBuilder(List.of("powershell", "-NoProfile", "-NonInteractive", "-STA", "-Command",
				"Add-Type -AssemblyName System.Windows.Forms,System.Drawing;"
					+ "$i=[System.Drawing.Image]::FromFile($env:BSS_FILE);"
					+ "[System.Windows.Forms.Clipboard]::SetImage($i);$i.Dispose()"));
			case OSX -> new ProcessBuilder(List.of("osascript", "-e",
				"set the clipboard to (read (POSIX file (system attribute \"BSS_FILE\")) as «class PNGf»)"));
			default -> System.getenv("WAYLAND_DISPLAY") != null
				? new ProcessBuilder(List.of("wl-copy", "--type", "image/png")).redirectInput(file)
				: new ProcessBuilder(List.of("xclip", "-selection", "clipboard", "-t", "image/png", "-i", path));
		};
		builder.environment().put("BSS_FILE", path);
		builder.redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD);

		Process process;
		try {
			process = builder.start();
		} catch (IOException e) {
			throw new IOException("clipboard tool not found (" + builder.command().get(0) + ")", e);
		}
		if (!process.waitFor(15, TimeUnit.SECONDS)) {
			process.destroyForcibly();
			throw new IOException("clipboard tool timed out");
		}
		if (process.exitValue() != 0) {
			throw new IOException(builder.command().get(0) + " exited with code " + process.exitValue());
		}
	}
}
