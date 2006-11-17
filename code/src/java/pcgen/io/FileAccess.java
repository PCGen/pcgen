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
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.io.filters.*;

import java.io.*;

/**
 * <code>FileAccess</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class FileAccess
{
	private static OutputFilter outputFilter = null;
	private static int maxLength = -1;
	
	/**
	 * Filter the supplied string according to the current output filter. This
	 * can do things such as escaping HTML entities.
	 *
	 * @param aString The string to be filtered
	 * @return The filtered string.
	 */
	public static String filterString(String aString)
	{
		if (null == outputFilter)
			return aString;
		else
			return outputFilter.filterString(aString);		
	}

	/**
	 * Set the current output filter (legacy - should be deprecated later)
	 * @param filterName (used to create instance of CharacterFilter)
	 */
	public static void setCurrentOutputFilter(String filterName)
	{
		try
		{
			outputFilter = new PatternFilter(filterName);
		}
		catch (IOException e)
		{
			outputFilter = new CharacterFilter(filterName);
		}
	}

	/**
	 * Set the current output filter
	 * @param filter
	 */
	public static void setCurrentOutputFilter(OutputFilter filter)
	{
		outputFilter = filter;
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
		if (aString == null)
		{
			return;
		}
		
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
