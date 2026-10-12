package com.customdonuthud;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/** Parses "127M", "1.5B", "500K", "127000000" and formats numbers back to the compact style. */
public final class NumberParser {
	private NumberParser() {}

	/** @throws NumberFormatException with a player-friendly message */
	public static long parse(String input) {
		if (input == null) throw new NumberFormatException("Enter a number");
		String s = input.trim().replace(",", "").replace("_", "").replace(" ", "");
		if (s.startsWith("$")) s = s.substring(1);
		if (s.isEmpty()) throw new NumberFormatException("Enter a number");

		BigDecimal mult = BigDecimal.ONE;
		char last = Character.toUpperCase(s.charAt(s.length() - 1));
		switch (last) {
			case 'K' -> mult = BigDecimal.valueOf(1_000L);
			case 'M' -> mult = BigDecimal.valueOf(1_000_000L);
			case 'B' -> mult = BigDecimal.valueOf(1_000_000_000L);
			case 'T' -> mult = BigDecimal.valueOf(1_000_000_000_000L);
			default -> { }
		}
		if (!Character.isDigit(last) && last != '.') s = s.substring(0, s.length() - 1);

		BigDecimal v;
		try {
			v = new BigDecimal(s);
		} catch (NumberFormatException e) {
			throw new NumberFormatException("\"" + input.trim() + "\" is not a valid number (try 127M, 1.5B, 500K)");
		}
		if (v.signum() < 0) throw new NumberFormatException("Value cannot be negative");
		v = v.multiply(mult).setScale(0, RoundingMode.DOWN);
		if (v.compareTo(BigDecimal.valueOf(Long.MAX_VALUE)) > 0) throw new NumberFormatException("Number is too large");
		return v.longValueExact();
	}

	public static String format(long value, boolean compact) {
		if (!compact || value < 1_000) return Long.toString(value);
		if (value >= 1_000_000_000_000L) return trim(value, 1_000_000_000_000L) + "T";
		if (value >= 1_000_000_000L) return trim(value, 1_000_000_000L) + "B";
		if (value >= 1_000_000L) return trim(value, 1_000_000L) + "M";
		return trim(value, 1_000L) + "K";
	}

	private static String trim(long value, long unit) {
		BigDecimal d = BigDecimal.valueOf(value).divide(BigDecimal.valueOf(unit), 2, RoundingMode.DOWN).stripTrailingZeros();
		return d.toPlainString().toUpperCase(Locale.ROOT);
	}
}
