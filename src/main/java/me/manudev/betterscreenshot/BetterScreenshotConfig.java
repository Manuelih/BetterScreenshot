package me.manudev.betterscreenshot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class BetterScreenshotConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve(BetterScreenshot.MOD_ID + ".json");

	/** "catbox" (no account needed) or "imgur" (requires imgurClientId). */
	public String host = "catbox";
	/** Client-ID from https://api.imgur.com/oauth2/addclient, only used when host is "imgur". */
	public String imgurClientId = "";
	/** Upload right after F2 instead of waiting for a click on [Upload]. */
	public boolean autoUpload = false;
	/** Copy the link to the clipboard once an upload is done. */
	public boolean copyToClipboard = true;
	/** Show toast notifications while uploading. */
	public boolean showToasts = true;

	public static BetterScreenshotConfig load() {
		BetterScreenshotConfig config = null;
		if (Files.exists(PATH)) {
			try (Reader reader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
				config = GSON.fromJson(reader, BetterScreenshotConfig.class);
			} catch (Exception e) {
				BetterScreenshot.LOGGER.error("Invalid config {}, using defaults", PATH, e);
			}
		}
		if (config == null) {
			config = new BetterScreenshotConfig();
		}
		if (config.host == null) {
			config.host = "catbox";
		}
		config.save();
		return config;
	}

	public void save() {
		try {
			Files.createDirectories(PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(PATH, StandardCharsets.UTF_8)) {
				GSON.toJson(this, writer);
			}
		} catch (IOException e) {
			BetterScreenshot.LOGGER.error("Could not save config {}", PATH, e);
		}
	}
}
