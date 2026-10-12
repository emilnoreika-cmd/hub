package com.customdonuthud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

/** The "Custom Donut HUD" menu. Edits a draft copy; Apply pushes it to the live HUD, Save writes to disk. */
public class ConfigScreen extends Screen {
	private interface Setter { void set(String text) throws NumberFormatException; }

	private final Screen parent;
	private final HudConfig draft;
	/** index 0 username (never errors), 1 money, 2 shards, 3 kills, 4 deaths */
	private final String[] errors = new String[5];
	private String status = "";
	private int statusColor = 0xFFAAAAAA;

	public ConfigScreen(Screen parent) {
		super(Text.literal("Custom Donut HUD"));
		this.parent = parent;
		this.draft = ConfigManager.get().copy();
	}

	@Override
	protected void init() {
		int left = (width - 320) / 2;
		int right = left + 170;
		int y0 = 40;
		String real = client.getSession().getUsername();

		// ---- left column: text fields (labels are drawn in render()) ----
		TextFieldWidget username = field(left, y0 + 10, 96, draft.username, 32, 0, t -> draft.username = t.trim());
		username.setPlaceholder(Text.literal(real));
		addDrawableChild(ButtonWidget.builder(Text.literal("Real name"), b -> {
			draft.username = "";
			username.setText("");
		}).dimensions(left + 98, y0 + 10, 52, 18).build());

		field(left, y0 + 40, 96, NumberParser.format(draft.money, draft.compactMoney), 24, 1,
				t -> draft.money = NumberParser.parse(t));
		addDrawableChild(ButtonWidget.builder(compactLabel(), b -> {
			draft.compactMoney = !draft.compactMoney;
			b.setMessage(compactLabel());
		}).dimensions(left + 98, y0 + 40, 52, 18).build());

		field(left, y0 + 70, 150, Long.toString(draft.shards), 24, 2, t -> draft.shards = NumberParser.parse(t));
		field(left, y0 + 100, 150, Long.toString(draft.kills), 24, 3, t -> draft.kills = NumberParser.parse(t));
		field(left, y0 + 130, 150, Long.toString(draft.deaths), 24, 4, t -> draft.deaths = NumberParser.parse(t));

		// ---- right column: appearance ----
		addDrawableChild(new FloatSlider(right, y0, 150, 20, "HUD Scale", 0.5f, 3f, draft.scale,
				v -> draft.scale = v, v -> String.format(Locale.ROOT, "%.2fx", v)));
		addDrawableChild(new FloatSlider(right, y0 + 24, 150, 20, "Opacity", 0f, 1f, draft.opacity,
				v -> draft.opacity = v, v -> Math.round(v * 100) + "%"));
		addDrawableChild(new FloatSlider(right, y0 + 48, 150, 20, "Horizontal", 0f, 100f, draft.xPercent,
				v -> draft.xPercent = v, v -> Math.round(v) + "%"));
		addDrawableChild(new FloatSlider(right, y0 + 72, 150, 20, "Vertical", 0f, 100f, draft.yPercent,
				v -> draft.yPercent = v, v -> Math.round(v) + "%"));
		addDrawableChild(ButtonWidget.builder(visibleLabel(), b -> {
			draft.visible = !draft.visible;
			b.setMessage(visibleLabel());
		}).dimensions(right, y0 + 100, 150, 20).build());
		addDrawableChild(ButtonWidget.builder(Text.literal("Reset Appearance"), b -> {
			draft.resetAppearance();
			clearAndInit();
		}).dimensions(right, y0 + 124, 150, 20).build());

		// ---- bottom buttons ----
		int bx = (width - 316) / 2;
		int by = height - 26;
		addDrawableChild(ButtonWidget.builder(Text.literal("Apply"), b -> apply()).dimensions(bx, by, 76, 20).build());
		addDrawableChild(ButtonWidget.builder(Text.literal("Save"), b -> {
			if (apply()) {
				ConfigManager.save();
				status = "Applied and saved.";
				statusColor = 0xFF55FF55;
			}
		}).dimensions(bx + 80, by, 76, 20).build());
		addDrawableChild(ButtonWidget.builder(Text.literal("Reset"), b -> {
			draft.copyFrom(new HudConfig());
			Arrays.fill(errors, null);
			status = "Defaults restored (press Apply/Save to keep).";
			statusColor = 0xFFFFFF55;
			clearAndInit();
		}).dimensions(bx + 160, by, 76, 20).build());
		addDrawableChild(ButtonWidget.builder(Text.literal("Close"), b -> close()).dimensions(bx + 240, by, 76, 20).build());
	}

	private Text compactLabel() {
		return Text.literal("Short: " + (draft.compactMoney ? "ON" : "OFF"));
	}

	private Text visibleLabel() {
		return Text.literal("HUD: " + (draft.visible ? "Shown" : "Hidden"));
	}

	private TextFieldWidget field(int x, int y, int w, String initial, int maxLen, int idx, Setter setter) {
		TextFieldWidget f = new TextFieldWidget(textRenderer, x, y, w, 18, Text.empty());
		f.setMaxLength(maxLen);
		f.setText(initial == null ? "" : initial);
		f.setChangedListener(t -> {
			try {
				setter.set(t);
				errors[idx] = null;
				f.setEditableColor(0xE0E0E0);
			} catch (NumberFormatException e) {
				errors[idx] = e.getMessage();
				f.setEditableColor(0xFF5555);
			}
		});
		addDrawableChild(f);
		return f;
	}

	private boolean apply() {
		for (String e : errors) {
			if (e != null) {
				status = "Not applied: " + e;
				statusColor = 0xFFFF5555;
				return false;
			}
		}
		draft.sanitize();
		ConfigManager.get().copyFrom(draft);
		status = "Applied.";
		statusColor = 0xFF55FF55;
		return true;
	}

	@Override
	public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
		super.render(ctx, mouseX, mouseY, delta);
		int left = (width - 320) / 2;
		int y0 = 40;

		ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, 12, 0xFFFFFFFF);
		ctx.drawCenteredTextWithShadow(textRenderer,
				Text.literal("Cosmetic only - these values are fake, local, and never sent to any server."),
				width / 2, 24, 0xFFAAAAAA);

		ctx.drawTextWithShadow(textRenderer, "Username", left, y0, 0xFFFFFFFF);
		ctx.drawTextWithShadow(textRenderer, "Money (127M, 1.5B, 500K...)", left, y0 + 30, 0xFFFFFFFF);
		ctx.drawTextWithShadow(textRenderer, "Shards", left, y0 + 60, 0xFFFFFFFF);
		ctx.drawTextWithShadow(textRenderer, "Kills", left, y0 + 90, 0xFFFFFFFF);
		ctx.drawTextWithShadow(textRenderer, "Deaths", left, y0 + 120, 0xFFFFFFFF);

		// first invalid field wins the error line
		String err = null;
		for (String e : errors) {
			if (e != null) { err = e; break; }
		}
		if (err != null) {
			ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(err), width / 2, height - 40, 0xFFFF5555);
		} else if (!status.isEmpty()) {
			ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(status), width / 2, height - 40, statusColor);
		}

		// live preview of the unsaved draft
		HudRenderer.draw(ctx, draft);
	}

	@Override
	public void close() {
		client.setScreen(parent);
	}

	/** Slider mapping 0..1 onto [min,max]. */
	private static class FloatSlider extends SliderWidget {
		private final String label;
		private final float min, max;
		private final Consumer<Float> setter;
		private final Function<Float, String> fmt;

		FloatSlider(int x, int y, int w, int h, String label, float min, float max, float current,
					Consumer<Float> setter, Function<Float, String> fmt) {
			super(x, y, w, h, Text.empty(), (current - min) / (max - min));
			this.label = label;
			this.min = min;
			this.max = max;
			this.setter = setter;
			this.fmt = fmt;
			updateMessage();
		}

		private float real() {
			return min + (float) value * (max - min);
		}

		@Override
		protected void updateMessage() {
			if (label == null) return;
			setMessage(Text.literal(label + ": " + fmt.apply(real())));
		}

		@Override
		protected void applyValue() {
			setter.accept(real());
		}
	}
}
