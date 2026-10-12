package com.customdonuthud;

import net.fabricmc.api.ClientModInitializer;

public class CustomDonutHudClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ConfigManager.load();
		Keybinds.register();
		HudRenderer.register();
	}
}
