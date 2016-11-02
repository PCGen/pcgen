/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.util;

import pcgen.base.text.ParsingSeparator;

/**
 * CSVUtilities are methods that are designed to simplify the processing of CSV-like files.
 */
public final class CSVUtilities
{

	private CSVUtilities()
	{
		//Do not construct utility class
	}

	/**
	 * Escapes a given entry. This escapes quotes (and detects commas that would
	 * require the line to be escaped).
	 * 
	 * @param string
	 *            The string to be escaped into its encoded state
	 * @return The escaped entry (with CSV quoting)
	 */
	public static String escape(String string)
	{
		if (string.contains("\"") || string.contains(","))
		{
			StringBuilder escaped = new StringBuilder(string.length() + 20);
			escaped.append('"');
			escaped.append(string.replace("\"", "\"\""));
			escaped.append('"');
			return escaped.toString();
		}
		return string;
	}

	/**
	 * Unescapes a given entry. This performs whitespace padding removal both
	 * before and after the removal of the optional escaping quotes available in
	 * the CSV file format.
	 * 
	 * @param entry
	 *            The entry to be unescaped into its base state
	 * @return The unescaped entry (trimmed and with CSV quoting removed)
	 */
	public static String unescape(String entry)
	{
		String unescaped = entry.trim();
		if (unescaped.startsWith("\"") && unescaped.endsWith("\""))
		{
			unescaped = unescaped.substring(1, unescaped.length() - 1);
			unescaped = unescaped.replace("\"\"", "\"");
		}
		return unescaped.trim();
	}

	/**
	 * Generates a new "naive" CSV separator. This is not formally CSV compliant
	 * because it ignores "embedded" new lines. For purposes of PCGen this is
	 * acceptable.
	 * 
	 * @param lstLine
	 *            The line to be processed by a CSV-like ParsingSeparator
	 * @return A ParsingSeparator for the given line
	 */
	public static ParsingSeparator generateCSVSeparator(String lstLine)
	{
		ParsingSeparator ps = new ParsingSeparator(lstLine, ',');
		ps.addGroupingPair('"', '"');
		return ps;
	}

}
