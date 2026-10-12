package com.customdonuthud;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public final class Keybinds {
	private static KeyBinding openMenu;

	private Keybinds() {}

	public static void register() {
		KeyBinding.Category category = KeyBinding.Category.create(Identifier.of(CustomDonutHud.MOD_ID, "main"));
		openMenu = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.customdonuthud.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, category));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openMenu.wasPressed()) {
				if (client.currentScreen == null) {
					client.setScreen(new ConfigScreen(null));
				}
			}
		});
	}
}
