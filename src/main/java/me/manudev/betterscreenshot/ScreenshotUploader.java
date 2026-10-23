package me.manudev.betterscreenshot;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public final class ScreenshotUploader {
	private static final HttpClient HTTP = HttpClient.newBuilder()
		.connectTimeout(Duration.ofSeconds(10))
		.followRedirects(HttpClient.Redirect.NORMAL)
		.build();
	private static final String USER_AGENT = "BetterScreenshot/1.0 (Minecraft Fabric mod)";

	private ScreenshotUploader() {
	}

	public static File resolveScreenshot(String name) {
		File dir = new File(MinecraftClient.getInstance().runDirectory, "screenshots");
		File file = new File(dir, name);
		try {
			if (!file.isFile() || !file.getCanonicalFile().getParentFile().equals(dir.getCanonicalFile())) {
				return null;
			}
		} catch (IOException e) {
			return null;
		}
		return file;
	}

	public static void upload(File file) {
		MinecraftClient client = MinecraftClient.getInstance();
		BetterScreenshotConfig config = BetterScreenshot.config();
		client.execute(() -> Messages.uploading(client));

		CompletableFuture.supplyAsync(() -> {
			try {
				return switch (config.host.toLowerCase(Locale.ROOT)) {
					case "imgur" -> uploadImgur(file, config.imgurClientId);
					default -> uploadCatbox(file);
				};
			} catch (IOException e) {
				throw new CompletionException(e);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new CompletionException(e);
			}
		}).whenComplete((url, error) -> client.execute(() -> {
			if (error != null) {
				Throwable cause = error instanceof CompletionException && error.getCause() != null ? error.getCause() : error;
				BetterScreenshot.LOGGER.warn("Upload of {} failed", file.getName(), cause);
				String reason = cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName();
				Messages.failed(client, file, reason);
				return;
			}
			BetterScreenshot.LOGGER.info("Uploaded {} -> {}", file.getName(), url);
			if (config.copyToClipboard) {
				client.keyboard.setClipboard(url);
			}
			Messages.uploaded(client, url, config.copyToClipboard);
		}));
	}

	private static String uploadCatbox(File file) throws IOException, InterruptedException {
		HttpRequest request = new Multipart()
			.field("reqtype", "fileupload")
			.file("fileToUpload", file)
			.build(HttpRequest.newBuilder(URI.create("https://catbox.moe/user/api.php")))
			.header("User-Agent", USER_AGENT)
			.timeout(Duration.ofSeconds(60))
			.build();
		HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
		String text = response.body().trim();
		if (response.statusCode() != 200 || !text.startsWith("https://")) {
			throw new IOException("catbox.moe HTTP " + response.statusCode() + (text.isEmpty() ? "" : " - " + text));
		}
		return text;
	}

	private static String uploadImgur(File file, String clientId) throws IOException, InterruptedException {
		if (clientId == null || clientId.isBlank()) {
			throw new IOException("imgurClientId is empty in config/betterscreenshot.json");
		}
		HttpRequest request = new Multipart()
			.field("type", "file")
			.file("image", file)
			.build(HttpRequest.newBuilder(URI.create("https://api.imgur.com/3/image")))
			.header("Authorization", "Client-ID " + clientId.trim())
			.header("User-Agent", USER_AGENT)
			.timeout(Duration.ofSeconds(60))
			.build();
		HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
		JsonObject data;
		try {
			JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
			data = json.getAsJsonObject("data");
			if (json.get("success").getAsBoolean()) {
				return data.get("link").getAsString();
			}
		} catch (RuntimeException e) {
			throw new IOException("imgur HTTP " + response.statusCode());
		}
		throw new IOException("imgur HTTP " + response.statusCode() + (data != null && data.has("error") ? " - " + data.get("error") : ""));
	}

	private static final class Multipart {
		private final String boundary = "----BetterScreenshot" + UUID.randomUUID().toString().replace("-", "");
		private final ByteArrayOutputStream out = new ByteArrayOutputStream();

		Multipart field(String name, String value) {
			write("--" + boundary + "\r\nContent-Disposition: form-data; name=\"" + name + "\"\r\n\r\n" + value + "\r\n");
			return this;
		}

		Multipart file(String name, File file) throws IOException {
			write("--" + boundary + "\r\nContent-Disposition: form-data; name=\"" + name + "\"; filename=\"" + file.getName()
				+ "\"\r\nContent-Type: image/png\r\n\r\n");
			out.write(Files.readAllBytes(file.toPath()));
			write("\r\n");
			return this;
		}

		HttpRequest.Builder build(HttpRequest.Builder builder) {
			write("--" + boundary + "--\r\n");
			return builder
				.header("Content-Type", "multipart/form-data; boundary=" + boundary)
				.POST(HttpRequest.BodyPublishers.ofByteArray(out.toByteArray()));
		}

		private void write(String s) {
			out.writeBytes(s.getBytes(StandardCharsets.UTF_8));
		}
	}
}
