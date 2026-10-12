package com.customdonuthud;

/** Plain data model, serialised to JSON with Gson (public fields on purpose). */
public class HudConfig {
	/** Blank = use the real Minecraft username. */
	public String username = "";
	public long money = 127_000_000L;
	/** true = show 127M / 1.5B / 500K, false = show the full number. */
	public boolean compactMoney = true;
	public long shards = 144;
	public long kills = 80;
	public long deaths = 132;

	public boolean visible = true;
	public float scale = 1.0f;
	/** Background opacity 0..1. */
	public float opacity = 0.4f;
	/** Position as a percentage of the free screen space (0 = left/top edge, 100 = right/bottom edge). */
	public float xPercent = 0f;
	public float yPercent = 40f;

	public HudConfig copy() {
		HudConfig c = new HudConfig();
		c.copyFrom(this);
		return c;
	}

	public void copyFrom(HudConfig o) {
		username = o.username;
		money = o.money;
		compactMoney = o.compactMoney;
		shards = o.shards;
		kills = o.kills;
		deaths = o.deaths;
		visible = o.visible;
		scale = o.scale;
		opacity = o.opacity;
		xPercent = o.xPercent;
		yPercent = o.yPercent;
	}

	public void resetAppearance() {
		HudConfig d = new HudConfig();
		visible = d.visible;
		scale = d.scale;
		opacity = d.opacity;
		xPercent = d.xPercent;
		yPercent = d.yPercent;
	}

	/** Clamp everything into safe ranges (used after loading a hand-edited file). */
	public void sanitize() {
		if (username == null) username = "";
		if (username.length() > 32) username = username.substring(0, 32);
		money = Math.max(0, money);
		shards = Math.max(0, shards);
		kills = Math.max(0, kills);
		deaths = Math.max(0, deaths);
		scale = clamp(scale, 0.25f, 4f);
		opacity = clamp(opacity, 0f, 1f);
		xPercent = clamp(xPercent, 0f, 100f);
		yPercent = clamp(yPercent, 0f, 100f);
	}

	private static float clamp(float v, float lo, float hi) {
		if (Float.isNaN(v)) return lo;
		return Math.max(lo, Math.min(hi, v));
	}
}
