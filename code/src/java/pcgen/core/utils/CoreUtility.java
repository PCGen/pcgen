/*
 * CoreUtility.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on Feb 18, 2002, 5:20:42 PM
 *
 * $Id$
 */
package pcgen.core.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * <code>CoreUtility</code>.
 *
 * Assorted generic-ish functionality moved from Globals and PlayerCharacter (the two biggest classes in the project.)
 * Some of this code seems awfully similar, and should probably be further refactored.
 *
 * @author Jonas Karlsson <pjak@yahoo.com>
 * @version $Revision$
 */
public final class CoreUtility
{
	private CoreUtility()
	{
		super();
	}

	/**
	 * Converts an array of Objects into a List of Objects
	 * 
	 * @param <T>
	 * @param array
	 *            the array to be converted. If this array is null then this
	 *            method will return an empty list;
	 * @return The list containing the objects passed in.
	 * 
	 * CONSIDER This should really be eliminated, as the only value over
	 * Arrays.asList is the null check... - thpr 11/3/06
	 */
	public static <T> List<T> arrayToList(final T[] array)
	{
		if (array==null)
		{
			return new ArrayList<T>();
		}

		final List<T> list = new ArrayList<T>(array.length);
		for (int i = 0; i < array.length; i++)
		{
			list.add(array[i]);
		}
		return list;
	}

	/**
	 * Verifies that a string is all numeric (integer).
	 * @param numString String to check if all numeric [integer]
	 * @return true if the String is numeric, else false
	 */
	public static boolean isIntegerString(final String numString)
	{
		boolean result;

		try
		{
			Integer.parseInt(numString);
			result = true;
		}
		catch (NumberFormatException nfe)
		{
			result = false;
		}

		return result;
	}

	/**
	 * return true if FTP or HTTP
	 * @param aFile
	 * @return true if FTP or HTTP
	 */
	public static boolean isNetURL(final String aFile)
	{
		return (aFile.startsWith("http:") || aFile.startsWith("ftp:"));
	}

	/**
	 * return true if FTP or HTTP or FILE
	 * @param aFile
	 * @return true if FTP or HTTP or FILE
	 */
	public static boolean isURL(final String aFile)
	{
		return (aFile.startsWith("http:") || aFile.startsWith("ftp:") || aFile.startsWith("file:"));
	}

	/**
	 * Capitalize the first letter of every word in a string
	 * @param aString
	 * @return String
	 */
	public static String capitalizeFirstLetter(final String aString)
	{
		boolean toUpper = true;
		final char[] a = aString.toLowerCase().toCharArray();

		for (int i = 0; i < a.length; ++i)
		{
			if (Character.isWhitespace(a[i]))
			{
				toUpper = true;
			}
			else
			{
				if (toUpper && Character.isLowerCase(a[i]))
				{
					a[i] = Character.toUpperCase(a[i]);
				}

				toUpper = false;
			}
		}

		return new String(a);
	}

	/**
	 * Stick a comma between every character of a string.
	 * @param oldString
	 * @return String
	 */
	public static String commaDelimit(final String oldString)
	{
		final int oldStringLength = oldString.length();
		final StringBuffer newString = new StringBuffer(oldStringLength);

		for (int i = 0; i < oldStringLength; ++i)
		{
			if (i != 0)
			{
				newString.append(',');
			}

			newString.append(oldString.charAt(i));
		}

		return newString.toString();
	}

	/**
	 * Simple passthrough, calls join(stringArray, ',') to do the work.
	 * @param stringArray
	 * @return String
	 */
	public static String commaDelimit(final Collection<String> stringArray)
	{
		return join(stringArray, ',');
	}

	/**
	 * Compare two doubles within a given epsilon.
	 * @param a
	 * @param b
	 * @param eps
	 * @return TRUE if equal, else FALSE
	 */
	public static boolean compareDouble(final double a, final double b, final double eps)
	{
		// If the difference is less than epsilon, treat as equal.
		return Math.abs(a - b) < eps;
	}

	/**
	 * Returns true if the checklist contains any row from targets.
	 * @param <T> 
	 * @param checklist The collection to check
	 * @param targets The collection to find in the checklist
	 * @return TRUE if equal, ELSE false
	 */
	public static <T> boolean containsAny(final Collection<T> checklist, final Collection<T> targets)
	{
		for (Iterator<T> i = targets.iterator(); i.hasNext();)
		{
			if (checklist.contains(i.next()))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Compare two doubles within a given epsilon, using a default epsilon of 0.0001.
	 * @param a
	 * @param b
	 * @return TRUE if equal, else FALSE
	 */
	public static boolean doublesEqual(final double a, final double b)
	{
		// If the difference is less than epsilon, treat as equal.
		return compareDouble(a, b, 0.0001);
	}

	/**
	 * Convert File to a URL
	 * @param fileName
	 * @return URL
	 * @throws MalformedURLException
	 */
	public static String fileToURL(final String fileName)
		throws MalformedURLException
	{
		final File aFile = new File(fileName);

		return aFile.toURI().toURL().toString();
	}

	/**
	 * Changes a path to make sure all instances of \ or / are replaced with File.separatorChar.
	 *
	 * @param argFileName The path to be fixed
	 * @return String
	 */
	public static String fixFilenamePath(final String argFileName)
	{
		return argFileName.replace('/', File.separatorChar).replace('\\', File.separatorChar);
	}

	/**
	 * Fix a broken URL
	 * @param url
	 * @return URL
	 * @throws MalformedURLException
	 */
	public static String fixURL(final String url) throws MalformedURLException
	{
		return new URL(url.replace('\\', '/')).toString();
	}

	/**
	 * Fix the URL path
	 * @param pccPath
	 * @param url
	 * @return URL path
	 * @throws MalformedURLException
	 */
	public static String fixURLPath(final String pccPath, final String url)
		throws MalformedURLException
	{
		final StringBuffer path = new StringBuffer(url.length());
		final String result;

		if (url.startsWith("file:"))
		{
			path.append(pccPath.replace('\\', '/'));
			path.append(url.substring(5).replace('\\', '/'));
			result = new URL("file:" + path.toString()).toString();
		}
		else
		{
			result = new URL(url.replace('\\', '/')).toString();
		}

		return result;
	}

	/**
	 * Get the inner most String end
	 * @param aString
	 * @return inner most String end
	 */
	public static int innerMostStringEnd(final String aString)
	{
		int index = 0;
		int hi = 0;
		int current = 0;

		for (int i = 0; i < aString.length(); ++i)
		{
			if (aString.charAt(i) == '(')
			{
				++current;

				if (current > hi)
				{
					hi = current;
				}
			}
			else if (aString.charAt(i) == ')')
			{
				if (current == hi)
				{
					index = i;
				}

				--current;
			}
		}

		return index;
	}

	/**
	 * Get the innermost String start
	 * @param aString
	 * @return innermost String start
	 */
	public static int innerMostStringStart(final String aString)
	{
		int index = 0;
		int hi = 0;
		int current = 0;

		for (int i = 0; i < aString.length(); ++i)
		{
			if (aString.charAt(i) == '(')
			{
				++current;

				if (current >= hi)
				{
					hi = current;
					index = i;
				}
			}
			else if (aString.charAt(i) == ')')
			{
				--current;
			}
		}

		return index;
	}

	/**
	 * Concatenates the List into a String using the separator
	 * as the delimitor.
	 *
	 * Note the actual delimitor is the separator + " "
	 *
	 * @param  strings    An ArrayList of strings
	 * @param  separator  The separating character
	 * @return            A 'separator' separated String
	 */
	public static String join(final Collection<?> strings, final char separator)
	{
		return join(strings, separator + " ");
	}

	/**
	 * Concatenates the List into a String using the separator
	 * as the delimitor.
	 *
	 * Note the actual delimitor is the separator + " "
	 *
	 * @param  strings    An ArrayList of strings
	 * @param  separator  The separating character
	 * @return            A 'separator' separated String
	 */
	public static String join(final Collection<?> strings, final String separator)
	{
		return joinToStringBuffer(strings, separator).toString();
	}

	/**
	 * Concatenates the List into a StringBuffer using the separator
	 * as the delimitor.
	 *
	 * @param  strings    An ArrayList of strings
	 * @param  separator  The separating character
	 * @return            A 'separator' separated String
	 */
	public static StringBuffer joinToStringBuffer(final Collection<?> strings, final String separator)
	{
		if (strings == null) {
			return new StringBuffer();
		}
		
		final StringBuffer result = new StringBuffer(strings.size() * 10);

		boolean needjoin = false;
		
		for (Object obj : strings) {
			if (needjoin) {
				result.append(separator);
			}
			needjoin = true;
			result.append(obj.toString());
		}
		
		return result;
	}

	/**
	 * Return the ordinal value
	 * @param iValue
	 * @return ordinal value
	 */
	public static String ordinal(final int iValue)
	{
		String suffix = "th";

		if ((iValue < 4) || (iValue > 20))
		{
			switch (iValue % 10)
			{
				case 1:
					suffix = "st";

					break;

				case 2:
					suffix = "nd";

					break;

				case 3:
					suffix = "rd";

					break;

				default:
					break;
			}
		}

		return Integer.toString(iValue) + suffix;
	}

	/**
	 * Replace all
	 * @param in
	 * @param find
	 * @param newStr
	 * @return String
	 */
	public static String replaceAll(final String in, final String find, final String newStr)
	{
		final char[] working = in.toCharArray();
		final StringBuffer sb = new StringBuffer(in.length() + newStr.length());
		int startindex = in.indexOf(find);

		if (startindex < 0)
		{
			return in;
		}

		int currindex = 0;

		while (startindex > -1)
		{
			for (int i = currindex; i < startindex; ++i)
			{
				sb.append(working[i]);
			}

			currindex = startindex;
			sb.append(newStr);
			currindex += find.length();
			startindex = in.indexOf(find, currindex);
		}

		for (int i = currindex; i < working.length; ++i)
		{
			sb.append(working[i]);
		}

		return sb.toString();
	}

	/**
	 * Replace this with String.replaceFirst once we switch to jdk 1.4
	 * @param original
	 * @param word
	 * @param replacement
	 * @return String
	 */
	public static String replaceFirst(final String original, final String word, final String replacement)
	{
		final int start = original.indexOf(word);
		final StringBuffer sb = new StringBuffer(50);
		sb.append(original.substring(0, start));
		sb.append(replacement);
		sb.append(original.substring(start + word.length()));

		return sb.toString();
	}

	/**
	 *  Turn a 'separator' separated string into a ArrayList of strings, each
	 *  corresponding to one trimmed 'separator'-separated portion of the original
	 *  string.
	 *
	 * @param  aString    The string to be split
	 * @param  separator  The separator that separates the string.
	 * @return            a List of Strings
	 */
	public static List<String> split(final String aString, final char separator)
	{
		ArrayList<String> temp = new ArrayList<String>();
		String sepStr = "\\" + String.valueOf(separator);
		if (aString.trim().length() == 0)
		{
			return temp;
		}

		for (Iterator<String> iter = Arrays.asList(aString.split(sepStr)).iterator(); iter
			.hasNext();)
		{
			temp.add(iter.next().trim());
		}

		return temp;
	}

	/**
	 * Unescape the : character
	 * @param in
	 * @return String
	 */
	public static String unEscapeColons2(final String in)
	{
		return replaceAll(in, "&#59;", ":");
	}

	/**
	 * Convert to a String representation
	 * @param list
	 * @return List of Strings
	 */
	public static List<String> toStringRepresentation(List<?> list)
	{
		final List<String> returnList = new ArrayList<String>(list.size());
		for (Iterator<?> i = list.iterator(); i.hasNext();)
		{
			returnList.add(i.next().toString());
		}
		return returnList;
	}
}
