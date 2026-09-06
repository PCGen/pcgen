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

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

/**
 * Chooses the default paper size for the print-preview dialog (CODE-2537):
 * a previously saved value wins, otherwise the OS locale decides Letter vs A4.
 */
final class PrintPreviewPaperDefault
{
	private PrintPreviewPaperDefault()
	{
	}

	/**
	 * Convenience seam used by the controller: reads the JVM default locale's
	 * country and delegates to {@link #chooseDefault(String, String, List)}.
	 * Kept separate so the locale-reading wiring is unit-testable.
	 */
	static String chooseDefaultForCurrentLocale(String persisted, List<String> available)
	{
		return chooseDefault(persisted, Locale.getDefault().getCountry(), available);
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
