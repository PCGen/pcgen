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
 * Chooses the default paper size for the print-preview dialog (CODE-2537):
 * persisted value wins; otherwise the default printer's paper size; otherwise
 * the OS locale decides Letter vs A4.
 */
final class PrintPreviewPaperDefault
{
	/** Maximum per-axis delta (in points) for a dimension match to be accepted. ~1.8 mm. */
	private static final double DIMENSION_TOLERANCE_POINTS = 5.0;

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
	 * Convenience seam used by the controller: reads the JVM default locale's
	 * country and delegates to {@link #chooseDefault(String, String, List)}.
	 * Kept for callers that do not have printer information.
	 */
	static String chooseDefaultForCurrentLocale(String persisted, List<String> available)
	{
		return chooseDefault(persisted, Locale.getDefault().getCountry(), available);
	}

	/**
	 * Convenience used by the controller: reads the JVM locale country and the default
	 * printer's paper size, then delegates to the dimension+locale chooser.
	 * A headless environment or missing printer is fully handled — the dialog is never broken.
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
			// Headless / no printer / toolkit issue: fall through with 0 dims → dimension match skipped.
			Logging.log(Logging.DEBUG, "No default printer paper available for print-preview default", ex);
		}
		return chooseDefault(persisted, country, w, h, availableNames, options);
	}

	/**
	 * Chooses a paper name by priority: persisted value; then the default printer's
	 * paper dimensions matched against {@code options}; then locale (Letter for US/CA,
	 * A4 elsewhere); finally first available. Returns {@code null} only if {@code availableNames}
	 * is empty.
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
		return localeDefault(country, availableNames);
	}

	/** Chooses a paper name by priority: persisted value if available, else Letter for US/CA or A4 elsewhere; null only if {@code available} is empty. */
	static String chooseDefault(String persisted, String country, List<String> available)
	{
		if (available.isEmpty())
		{
			return null;
		}
		if (StringUtils.isNotBlank(persisted))
		{
			String match = available.stream()
			                        .filter(name -> name.equalsIgnoreCase(persisted.strip()))
			                        .findFirst()
			                        .orElse(null);
			if (match != null)
			{
				return match;
			}
		}
		return localeDefault(country, available);
	}

	/** Applies the locale heuristic: Letter for US/CA, A4 elsewhere; first available as last resort. */
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
