/*
 * Copyright 2026 Vest <Vest@users.noreply.github.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.gui3.dialog;

import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import pcgen.util.Logging;

/**
 * Chooses the default paper size for the print-preview dialog: a persisted value wins, else the
 * default printer's paper size, else the OS locale decides Letter (US/CA) vs A4.
 */
final class PrintPreviewPaperDefault
{
	/** Maximum per-axis delta (in points) for a dimension match to be accepted. ~1.8 mm. */
	private static final double DIMENSION_TOLERANCE_POINTS = 5.0;

	// Nominal media sizes in 1/72-inch points, used to identify Letter/A4 by dimension (locale-proof).
	private static final double LETTER_WIDTH_POINTS = 612.0;
	private static final double LETTER_HEIGHT_POINTS = 792.0;
	private static final double A4_WIDTH_POINTS = 595.28;
	private static final double A4_HEIGHT_POINTS = 841.89;

	private PrintPreviewPaperDefault()
	{
	}

	/** A paper option with its dimensions in 1/72-inch points, for printer-size matching. */
	record PaperOption(String name, double widthPoints, double heightPoints) { }

	/**
	 * Parses a PaperInfo dimension string (e.g. {@code "297mm"}, {@code "8.5in"}) to
	 * 1/72-inch points. Returns {@link Double#NaN} on any parse failure or unrecognised unit.
	 */
	static double parseDimensionToPoints(String dimension)
	{
		if (dimension == null)
		{
			return Double.NaN;
		}
		String s = dimension.strip();
		if (s.isEmpty())
		{
			return Double.NaN;
		}
		String lower = s.toLowerCase(Locale.ROOT);
		try
		{
			if (lower.endsWith("mm"))
			{
				double mm = Double.parseDouble(s.substring(0, s.length() - 2).strip());
				return mm * 72.0 / 25.4;
			}
			if (lower.endsWith("in"))
			{
				double in = Double.parseDouble(s.substring(0, s.length() - 2).strip());
				return in * 72.0;
			}
		}
		catch (NumberFormatException ignored)
		{
			// fall through to NaN
		}
		return Double.NaN;
	}

	/**
	 * Returns the name of the option whose dimensions best match the target (within
	 * {@value #DIMENSION_TOLERANCE_POINTS} points per axis), orientation-agnostically.
	 * Returns {@code null} if no option is close enough or if inputs are degenerate.
	 */
	static String matchByDimensions(double targetWidthPoints, double targetHeightPoints, List<PaperOption> options)
	{
		if (options == null || options.isEmpty() || targetWidthPoints <= 0 || targetHeightPoints <= 0)
		{
			return null;
		}
		double tMin = Math.min(targetWidthPoints, targetHeightPoints);
		double tMax = Math.max(targetWidthPoints, targetHeightPoints);

		String bestName = null;
		double bestDelta = Double.MAX_VALUE;
		for (PaperOption opt : options)
		{
			if (Double.isNaN(opt.widthPoints()) || Double.isNaN(opt.heightPoints()))
			{
				continue;
			}
			double oMin = Math.min(opt.widthPoints(), opt.heightPoints());
			double oMax = Math.max(opt.widthPoints(), opt.heightPoints());
			double dMin = Math.abs(tMin - oMin);
			double dMax = Math.abs(tMax - oMax);
			if (dMin <= DIMENSION_TOLERANCE_POINTS && dMax <= DIMENSION_TOLERANCE_POINTS)
			{
				double total = dMin + dMax;
				if (total < bestDelta)
				{
					bestDelta = total;
					bestName = opt.name();
				}
			}
		}
		return bestName;
	}

	/**
	 * Reads the JVM locale country and the default printer's paper size, then delegates to the
	 * dimension+locale chooser. A headless environment or missing printer is handled gracefully.
	 */
	static String chooseDefaultForCurrentLocaleAndPrinter(String persisted, List<String> availableNames,
			List<PaperOption> options)
	{
		String country = Locale.getDefault().getCountry();
		double w = 0;
		double h = 0;
		try
		{
			Paper paper = PrinterJob.getPrinterJob().defaultPage().getPaper();
			w = paper.getWidth();
			h = paper.getHeight();
		}
		catch (final RuntimeException | Error ex)
		{
			// No printer/headless: leave dims at 0 so the dimension match is skipped.
			Logging.log(Logging.DEBUG, "No default printer paper available for print-preview default", ex);
		}
		return chooseDefault(persisted, country, w, h, availableNames, options);
	}

	/**
	 * Chooses a paper name by priority: persisted value, then printer dimensions matched against
	 * {@code options}, then locale (Letter for US/CA, else A4), then first available. {@code null}
	 * only if {@code availableNames} is empty.
	 */
	static String chooseDefault(String persisted, String country, double printerWidthPoints,
			double printerHeightPoints, List<String> availableNames, List<PaperOption> options)
	{
		if (availableNames.isEmpty())
		{
			return null;
		}
		// 1. Persisted value wins.
		if (StringUtils.isNotBlank(persisted))
		{
			String match = availableNames.stream()
			                             .filter(name -> name.equalsIgnoreCase(persisted.strip()))
			                             .findFirst()
			                             .orElse(null);
			if (match != null)
			{
				return match;
			}
		}
		// 2. Default printer dimensions.
		String printerMatch = matchByDimensions(printerWidthPoints, printerHeightPoints, options);
		if (printerMatch != null && availableNames.contains(printerMatch))
		{
			return printerMatch;
		}
		// 3. Locale fallback.
		return localeDefault(country, availableNames, options);
	}

	/**
	 * Locale default by DIMENSION (Letter for US/CA, else A4), matched against {@code options} so it
	 * is name/locale-proof. Falls back to the name-based match when dimensions are unusable.
	 */
	private static String localeDefault(String country, List<String> available, List<PaperOption> options)
	{
		boolean isLetterLocale = "US".equalsIgnoreCase(country) || "CA".equalsIgnoreCase(country);
		double targetWidth = isLetterLocale ? LETTER_WIDTH_POINTS : A4_WIDTH_POINTS;
		double targetHeight = isLetterLocale ? LETTER_HEIGHT_POINTS : A4_HEIGHT_POINTS;
		String byDimension = matchByDimensions(targetWidth, targetHeight, options);
		if (byDimension != null && available.contains(byDimension))
		{
			return byDimension;
		}
		return localeDefault(country, available);
	}

	/** Name-based locale default: Letter for US/CA, else A4; first available as last resort. */
	private static String localeDefault(String country, List<String> available)
	{
		boolean isLetterLocale = "US".equalsIgnoreCase(country) || "CA".equalsIgnoreCase(country);
		if (isLetterLocale)
		{
			return available.stream()
			                .filter(name -> StringUtils.containsIgnoreCase(name, "Letter"))
			                .findFirst()
			                .orElse(available.get(0));
		}
		return available.stream()
		                .filter(name -> StringUtils.containsIgnoreCase(name, "A4"))
		                .findFirst()
		                .orElse(available.get(0));
	}
}
