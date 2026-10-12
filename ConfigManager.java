package com.customdonuthud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Loads/saves config/custom_donut_hud.json. Never throws: bad files fall back to defaults. */
public final class ConfigManager {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static HudConfig config = new HudConfig();

	private ConfigManager() {}

	/** The live config object the HUD renders from. */
	public static HudConfig get() {
		return config;
	}

	public static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve("custom_donut_hud.json");
	}

	public static void load() {
		Path p = path();
		if (!Files.exists(p)) {
			config = new HudConfig();
			save();
			return;
		}
		try (Reader r = Files.newBufferedReader(p)) {
			HudConfig loaded = GSON.fromJson(r, HudConfig.class);
			if (loaded == null) throw new IllegalStateException("config file is empty");
			loaded.sanitize();
			config = loaded;
		} catch (IOException | RuntimeException e) {
			CustomDonutHud.LOGGER.error("Could not read {} - using defaults. Reason: {}", p, e.toString());
			config = new HudConfig();
			try {
				Files.move(p, p.resolveSibling("custom_donut_hud.json.broken"), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException ignored) {
				// not critical
			}
			save();
		}
	}

	public static void save() {
		Path p = path();
		try {
			Files.createDirectories(p.getParent());
			try (Writer w = Files.newBufferedWriter(p)) {
				GSON.toJson(config, w);
			}
		} catch (IOException e) {
			CustomDonutHud.LOGGER.error("Could not write {}: {}", p, e.toString());
		}
	}
}
