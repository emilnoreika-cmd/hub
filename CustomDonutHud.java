package com.customdonuthud;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Common initializer. Nothing to do: the mod is purely a local, client-side overlay. */
public class CustomDonutHud implements ModInitializer {
	public static final String MOD_ID = "customdonuthud";
	public static final Logger LOGGER = LoggerFactory.getLogger("CustomDonutHUD");

	@Override
	public void onInitialize() {
		LOGGER.info("Custom Donut HUD loaded (cosmetic, local-only overlay).");
	}
}
