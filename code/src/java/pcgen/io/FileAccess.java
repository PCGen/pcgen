/*
 * FileAccess.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 *
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.io;

import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.util.Delta;
import pcgen.util.Logging;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <code>FileAccess</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class FileAccess
{
	private static String outputFilterName = "";
	private static Map<Integer, String> outputFilter = null;
	private static int maxLength = -1;

	/**
	 * Set the current output filter
	 * @param filterName
	 */
	public static void setCurrentOutputFilter(String filterName)
	{
		final int idx = filterName.lastIndexOf('.');

		if (idx >= 0)
		{
			filterName = filterName.substring(idx + 1);
		}

		filterName = filterName.toLowerCase();

		if (filterName.equals(outputFilterName))
		{
			return;
		}

		outputFilter = null;

		filterName = Globals.getDefaultPath() + File.separator + "system" + File.separator + "outputFilters"
			+ File.separator + filterName + Constants.s_PCGEN_LIST_EXTENSION;

		final File filterFile = new File(filterName);

		try
		{
			if (filterFile.canRead() && filterFile.isFile())
			{
				final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filterFile),
							"UTF-8"));

				if (br != null)
				{
					outputFilterName = filterName;
					outputFilter = new HashMap<Integer, String>();

					for (;;)
					{
						final String aLine = br.readLine();

						if (aLine == null)
						{
							break;
						}

						final List<String> filterEntry = CoreUtility.split(aLine, '\t');

						if (filterEntry.size() >= 2)
						{
							try
							{
								final Integer key = Delta.decode(filterEntry.get(0));
								outputFilter.put(key, filterEntry.get(1));
							}
							catch (NullPointerException e)
							{
								Logging.errorPrint("Exception in setCurrentOutputFilter", e);
							}
							catch (NumberFormatException e)
							{
								Logging.errorPrint("Exception in setCurrentOutputFilter", e);
							}
						}
					}

					br.close();
				}
			}
		}
		catch (IOException e)
		{
			//Should this be ignored?
		}
	}

	/**
	 * Write, but with encoding
	 * @param output
	 * @param aString
	 */
	public static void encodeWrite(Writer output, String aString)
	{
		write(output, filterString(aString));
	}

	/**
	 * Filter the supplied string according to the current output filter. This
	 * can do things such as escaping HTML entities.
	 *
	 * @param aString The string to be filtered
	 * @return The filtered string.
	 */
	public static String filterString(String aString)
	{
		if ((outputFilter != null) && (outputFilter.size() != 0))
		{
			final StringBuffer xlatedString = new StringBuffer(aString.length());

			for (int i = 0; i < aString.length(); i++)
			{
				final char c = aString.charAt(i);
				final String xlation = outputFilter.get(c);

				if (xlation != null)
				{
					xlatedString.append(xlation);
				}
				else
				{
					xlatedString.append(c);
				}
			}

			aString = xlatedString.toString();
		}
		return aString;
	}

	/**
	 * Set the max length
	 * @param anInt
	 */
	public static void maxLength(int anInt)
	{
		maxLength = anInt;
	}

	/**
	 * Write a newline
	 * @param output
	 */
	public static void newLine(BufferedWriter output)
	{
		try
		{
			output.newLine();
		}
		catch (IOException exception)
		{
			ShowMessageDelegate.showMessageDialog(exception.getMessage(), Constants.s_APPNAME, MessageType.ERROR);
		}
	}

	/**
	 * Write to the output
	 * @param output
	 * @param aString
	 */
	public static void write(Writer output, String aString)
	{
		if ((maxLength > 0) && (aString.length() > maxLength))
		{
			aString = aString.substring(0, maxLength);
		}

		try
		{
			output.write(aString);
		}
		catch (IOException exception)
		{
			ShowMessageDelegate.showMessageDialog(exception.getMessage(), Constants.s_APPNAME, MessageType.ERROR);
		}
	}
}
