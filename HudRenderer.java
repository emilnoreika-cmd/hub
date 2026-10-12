package com.customdonuthud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/** Draws the fake scoreboard. Purely local; reads only from HudConfig. */
public final class HudRenderer {
	private static final Identifier MONEY = tex("money");
	private static final Identifier SHARDS = tex("shards");
	private static final Identifier KILLS = tex("kills");
	private static final Identifier DEATHS = tex("deaths");

	private static final int PAD = 3;
	private static final int ICON = 8;     // icon is drawn 8x8 GUI px (texture is 16x16)
	private static final int GAP = 3;
	private static final int LINE = 10;
	private static final int TEXT_COLOR = 0xFFFFFFFF;

	private HudRenderer() {}

	private static Identifier tex(String name) {
		return Identifier.of(CustomDonutHud.MOD_ID, "textures/gui/" + name + ".png");
	}

	public static void register() {
		HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
				Identifier.of(CustomDonutHud.MOD_ID, "scoreboard"),
				(context, tickCounter) -> {
					MinecraftClient mc = MinecraftClient.getInstance();
					if (mc.options.hudHidden || mc.player == null) return; // respects F1
					draw(context, ConfigManager.get());
				});
	}

	/** Draw using the given config (the config screen passes its draft for live preview). */
	public static void draw(DrawContext ctx, HudConfig c) {
		if (!c.visible) return;
		MinecraftClient mc = MinecraftClient.getInstance();
		TextRenderer tr = mc.textRenderer;

		String name = (c.username == null || c.username.isBlank()) ? mc.getSession().getUsername() : c.username;
		String[] values = {
				NumberParser.format(c.money, c.compactMoney),
				Long.toString(c.shards),
				Long.toString(c.kills),
				Long.toString(c.deaths)
		};
		Identifier[] icons = {MONEY, SHARDS, KILLS, DEATHS};

		int valueW = 0;
		for (String v : values) valueW = Math.max(valueW, tr.getWidth(v));
		int contentW = Math.max(tr.getWidth(name), ICON + GAP + valueW);
		int w = contentW + PAD * 2;
		int h = 4 * LINE + 8 + PAD * 2;

		float s = c.scale;
		float x = Math.max(0f, (ctx.getScaledWindowWidth() - w * s) * c.xPercent / 100f);
		float y = Math.max(0f, (ctx.getScaledWindowHeight() - h * s) * c.yPercent / 100f);

		int alpha = Math.round(Math.max(0f, Math.min(1f, c.opacity)) * 255f);

		ctx.getMatrices().pushMatrix();
		ctx.getMatrices().translate(x, y);
		ctx.getMatrices().scale(s, s);

		ctx.fill(0, 0, w, h, alpha << 24);

		ctx.drawText(tr, name, PAD, PAD, TEXT_COLOR, true);
		for (int i = 0; i < 4; i++) {
			int rowY = PAD + (i + 1) * LINE;
			ctx.drawTexture(RenderPipelines.GUI_TEXTURED, icons[i], PAD, rowY, 0f, 0f, ICON, ICON, 16, 16, 16, 16);
			ctx.drawText(tr, values[i], PAD + ICON + GAP, rowY, TEXT_COLOR, true);
		}

		ctx.getMatrices().popMatrix();
	}
}
