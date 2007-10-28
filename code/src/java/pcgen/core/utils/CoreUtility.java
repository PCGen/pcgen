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

import pcgen.core.Constants;
import pcgen.core.Equipment;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

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
	public static Comparator<Equipment> equipmentComparator = new Comparator<Equipment>()
	{
		private int compareInts(final int obj1Index, final int obj2Index)
		{
			if (obj1Index > obj2Index)
			{
				return 1;
			}
			else if (obj1Index < obj2Index)
			{
				return -1;
			}
			else
			{
				return 0;
			}
		}

		public int compare(final Equipment obj1, final Equipment obj2)
		{
			int o1i = obj1.getOutputIndex();
			int o2i = obj2.getOutputIndex();

			// Force unset items (index of 0) to appear at the end
			o1i = (o1i == 0) ? 999 : o1i;
			o2i = (o2i == 0) ? 999 : o2i;

			final int result1 = compareInts(o1i, o2i);

			if (result1 != 0)
			{
				return result1;
			}

			final int result2 = compareInts(obj1.getOutputSubindex(), obj2.getOutputSubindex());

			if (result2 != 0)
			{
				return result2;
			}

			final int result3 = obj1.getName().compareToIgnoreCase(obj2.getName());

			if (result3 != 0)
			{
				return result3;
			}

			return obj1.getParentName().compareToIgnoreCase(obj2.getParentName());
		}

		public boolean equals(final Equipment obj)
		{
			return false;
		}

		public int hashCode()
		{
			return 0;
		}
	};

	private CoreUtility()
	{
		super();
	}

	/**
	 * Converts an array of Objects into a List of Objects
	 *
	 * @param array the array to be converted. If this array is null then this method
	 *              will return an empty list;
	 *
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
		list.addAll(Arrays.asList(array));
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
	 * return true if the protocol of the URL represented is FTP or HTTP
	 *
	 * @param URLString the URL to test for a network protocol
	 * @return true if the string begins with FTP or HTTP
	 */
	public static boolean isNetURL(final String URLString)
	{
		return (URLString.startsWith("http:") || URLString.startsWith("ftp:"));
	}

	/**
	 * return true if the protocol of the URL represented is FTP or HTTP
	 * @param url the URL object to test for a network protocol
	 * @return true if the protocol of this URL is FTP or HTTP
	 */
	public static boolean isNetURL(final URL url)
	{
		return "http".equalsIgnoreCase(url.getProtocol())
				|| "ftp".equalsIgnoreCase(url.getProtocol());
	}

	/**
	 * return true if the protocol of the URL represented is FTP or HTTP or FILE
	 * @param URLString the string to test for a suitable protocol
	 * @return true if the string begins with ftp: or http: or file:
	 */
	public static boolean isURL(final String URLString)
	{
		return (URLString.startsWith("http:") || 
		        URLString.startsWith("ftp:") || 
		        URLString.startsWith("file:"));
	}

	/**
	 * Capitalize the first letter of every word in a string
	 * @param aString the string to convert to Title case
	 * @return a new string with the first letter of every word capitalised 
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

//  this method is unused at the release of 5.13.3 alpha
//
//	/**
//	 * Stick a comma between every character of a string.
//	 * @param oldString
//	 * @return String
//	 */
//	public static String commaDelimit(final String oldString)
//	{
//		final int oldStringLength = oldString.length();
//		final StringBuffer newString = new StringBuffer(oldStringLength);
//
//		for (int i = 0; i < oldStringLength; ++i)
//		{
//			if (i != 0)
//			{
//				newString.append(',');
//			}
//
//			newString.append(oldString.charAt(i));
//		}
//
//		return newString.toString();
//	}
//
//	/**
//	 * Simple passthrough, calls join(stringArray, ',') to do the work.
//	 * @param stringArray
//	 * @return String
//	 */
//	public static String commaDelimit(final Collection<String> stringArray)
//	{
//		return join(stringArray, ", ");
//	}

	/**
	 * Compare two doubles within a given epsilon.
	 * @param a first operand
	 * @param b second operand
	 * @param eps the epsilon (or deadband)
	 * @return TRUE if abs(a - b) < eps, else FALSE
	 */
	public static boolean compareDouble(final double a, final double b, final double eps)
	{
		// If the difference is less than epsilon, treat as equal.
		return Math.abs(a - b) < eps;
	}

	/**
	 * Returns true if the checklist contains any row from targets.
	 * @param checklist The collection to check
	 * @param targets The collection to find in the checklist
	 * @return TRUE if equal, ELSE false
	 */
	public static <T> boolean containsAny(final Collection<T> checklist, final Collection<T> targets)
	{
		for (T target : targets)
		{
			if (checklist.contains(target))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Compare two doubles within an epsilon of 0.0001.
	 * @param a first operand
	 * @param b second operand
	 * @return TRUE if equal, else FALSE
	 */
	public static boolean doublesEqual(final double a, final double b)
	{
		// If the difference is less than epsilon, treat as equal.
		return compareDouble(a, b, 0.0001);
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
	 * Get the inner most String end
	 * @param aString The string to be searched for the innermost (
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
	 * @param aString the string sto be searched for the ) that closes the innermost
	 * parenthesised expression
	 * 
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

//	/**
//	 * Concatenates the List into a String using the separator
//	 * as the delimitor.
//	 *
//	 * Note the actual delimitor is the separator + " "
//	 *
//	 * @param  strings    An ArrayList of strings
//	 * @param  separator  The separating character
//	 * @return            A 'separator' separated String
//	 */
//	public static String join(final Collection<?> strings, final char separator)
//	{
//		return join(strings, separator + " ");
//	}

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
		final StringBuffer result;

		if (strings == null)
		{
			result = new StringBuffer();
		}
		else
		{

			result = new StringBuffer(strings.size() * 10);

			boolean needjoin = false;

			for (final Object obj : (Collection<?>) strings)
			{
				if (needjoin)
				{
					result.append(separator);
				}
				needjoin = true;
				result.append(obj.toString());
			}
		}

		return result.toString();
	}

	/**
	 * Return the english suffix for a given ordinal value
	 * @param iValue the ordinal value
	 * @return ordinal suffix (st, nd, etc.)
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
	 * Replace all occurrences of original in source with replacement 
	 * @param source the source string
	 * @param original the substring to search for
	 * @param replacement the substring to substitute
	 * @return a new String based on source where original has been replaced with replacement
	 */
	public static String replaceAll(
			final String source,
			final String original,
			final String replacement)
	{
		return source.replaceAll(Pattern.quote(original), replacement);
		
//		final char[] working = source.toCharArray();
//		final StringBuffer sb = new StringBuffer(source.length() + replacement.length());
//		int startindex = source.indexOf(original);
//
//		if (startindex < 0)
//		{
//			return source;
//		}
//
//		int currindex = 0;
//
//		while (startindex > -1)
//		{
//			for (int i = currindex; i < startindex; ++i)
//			{
//				sb.append(working[i]);
//			}
//
//			currindex = startindex;
//			sb.append(replacement);
//			currindex += original.length();
//			startindex = source.indexOf(original, currindex);
//		}
//
//		for (int i = currindex; i < working.length; ++i)
//		{
//			sb.append(working[i]);
//		}
//
//		return sb.toString();
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

	public static List<Equipment> cloneEquipmentInList(final Iterable<Equipment> aList)
	{
		final List<Equipment> workingList = new ArrayList<Equipment>();

		for (final Equipment eq : aList)
		{
			workingList.add(eq.clone());
		}
		return workingList;
	}

	/**
	 * Merge the equipment list
	 *
	 * @param aList the list of Equipment
	 * @param merge The type of merge to perform
	 *
	 * @return merged list
	 */
	public static List<Equipment> mergeEquipmentList(final List<Equipment> aList, final int merge)
	{
		Collections.sort(aList, equipmentComparator);

		// no merging, just sorting (calling this is really stupid,
		// just use the sort above)
		if (merge == Constants.MERGE_NONE)
		{
			return aList;
		}

		final List<Equipment> workingList = cloneEquipmentInList(aList);

		int endIndex = workingList.size();

		for (int i = 0; i < endIndex; i++)
		{
			final Equipment eq1 = workingList.get(i);
			double eQty = eq1.qty();

			for (int j = i + 1; j < endIndex; j++)
			{
				final Equipment eq2 = workingList.get(j);

				// no container merge or Temporary Bonus generated equipment must not merge
				if (eq1.isContainer() || eq1.isType("TEMPORARY") || eq2.isType("TEMPORARY"))
				{
					continue;
				}

				if (eq1.getName().equals(eq2.getName()))
				{
					// merge all like equipment together
					if (merge == Constants.MERGE_ALL ||

					    // merge like equipment within same container
					    (merge == Constants.MERGE_LOCATION
					     && (eq1.getLocation() == eq2.getLocation())
					     && eq1.getParentName().equals(eq2.getParentName())))
					{
						workingList.remove(eq2);
						eQty += eq2.qty();
						endIndex--;
					}
				}
			}

			eq1.setQty(eQty);
		}

		return workingList;
	}
}
