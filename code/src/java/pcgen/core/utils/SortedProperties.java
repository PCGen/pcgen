/*
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.utils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import pcgen.util.Logging;

/**
 * An subclass of Properties whose output is sorted
 */
public class SortedProperties extends Properties
{

	/**
	 * Store properties
	 * @param out
	 * @param header
	 */
	public void mystore(final FileOutputStream out, final String header)
	{

		final SortedMap<Object, Object> aMap = new TreeMap<>(this);
		final Iterator<Map.Entry<Object, Object>> entries = aMap.entrySet().iterator();
		Map.Entry<Object, Object> entry;

		try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, "8859_1")))
		{
			bw.write(header);
			bw.newLine();

			while (entries.hasNext())
			{
				entry = entries.next();

				// The following characters must be escaped:
				// #, !, = and :
				final String aString = fixUp((String) entry.getValue());
				bw.write(convertStringToKey((String) entry.getKey()) + "=" + aString);
				bw.newLine();
			}

			bw.flush();
		}
		catch (final IOException ex)
		{
			Logging.errorPrint("Error writing to the options.ini file: ", ex);
		}
	}

	private static String fixUp(final String aString)
	{
		final StringBuilder ab = new StringBuilder(aString.length());

		for (int i = 0; i < aString.length(); i++)
		{
			// #, !, = and :
			if ((aString.charAt(i) == '#') || (aString.charAt(i) == '\\') || (aString.charAt(i) == '!')
				|| (aString.charAt(i) == '=') || (aString.charAt(i) == ':'))
			{
				ab.append("\\").append(aString.charAt(i));
			}
			else
			{
				ab.append(aString.charAt(i));
			}
		}

		return ab.toString();
	}

	/**
	 * Convert the supplied string into a property key, escaping any 
	 * terminator characters within the string.  
	 * @param rawKey The text to be converted
	 * @return The valid properties key
	 */
	private static String convertStringToKey(String rawKey)
	{
		String key = rawKey.replaceAll(" ", "\\\\ ");
		key = key.replaceAll(":", "\\\\:");
		key = key.replaceAll("=", "\\\\=");
		return key;
	}

}
