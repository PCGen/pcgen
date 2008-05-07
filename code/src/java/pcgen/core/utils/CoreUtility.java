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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import pcgen.cdom.base.Constants;
import pcgen.core.Equipment;
import pcgen.gui.PCGenProp;
import pcgen.util.Logging;

/**
 * <code>CoreUtility</code>.
 * 
 * Assorted generic-ish functionality moved from Globals and PlayerCharacter
 * (the two biggest classes in the project.) Some of this code seems awfully
 * similar, and should probably be further refactored.
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

			final int result2 = compareInts(obj1.getOutputSubindex(), obj2
					.getOutputSubindex());

			if (result2 != 0)
			{
				return result2;
			}

			final int result3 = obj1.getName().compareToIgnoreCase(
					obj2.getName());

			if (result3 != 0)
			{
				return result3;
			}

			return obj1.getParentName().compareToIgnoreCase(
					obj2.getParentName());
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
	 * @param array
	 *            the array to be converted. If this array is null then this
	 *            method will return an empty list;
	 * 
	 * @return The list containing the objects passed in.
	 * 
	 * CONSIDER This should really be eliminated, as the only value over
	 * Arrays.asList is the null check... - thpr 11/3/06
	 */
	public static <T> List<T> arrayToList(final T[] array)
	{
		if (array == null)
		{
			return new ArrayList<T>();
		}

		final List<T> list = new ArrayList<T>(array.length);
		list.addAll(Arrays.asList(array));
		return list;
	}

	/**
	 * Verifies that a string is all numeric (integer).
	 * 
	 * @param numString
	 *            String to check if all numeric [integer]
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
	 * @param URLString
	 *            the URL to test for a network protocol
	 * @return true if the string begins with FTP or HTTP
	 */
	public static boolean isNetURL(final String URLString)
	{
		return (URLString.startsWith("http:") || URLString.startsWith("ftp:"));
	}

	/**
	 * return true if the protocol of the URL represented is FTP or HTTP
	 * 
	 * @param url
	 *            the URL object to test for a network protocol
	 * @return true if the protocol of this URL is FTP or HTTP
	 */
	public static boolean isNetURL(final URL url)
	{
		return "http".equalsIgnoreCase(url.getProtocol())
				|| "ftp".equalsIgnoreCase(url.getProtocol());
	}

	/**
	 * return true if the protocol of the URL represented is FTP or HTTP or FILE
	 * 
	 * @param URLString
	 *            the string to test for a suitable protocol
	 * @return true if the string begins with ftp: or http: or file:
	 */
	public static boolean isURL(final String URLString)
	{
		return (URLString.startsWith("http:") || URLString.startsWith("ftp:") || URLString
				.startsWith("file:"));
	}

	/**
	 * Capitalize the first letter of every word in a string
	 * 
	 * @param aString
	 *            the string to convert to Title case
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

	// this method is unused at the release of 5.13.3 alpha
	//
	// /**
	// * Stick a comma between every character of a string.
	// * @param oldString
	// * @return String
	// */
	// public static String commaDelimit(final String oldString)
	// {
	// final int oldStringLength = oldString.length();
	// final StringBuffer newString = new StringBuffer(oldStringLength);
	//
	// for (int i = 0; i < oldStringLength; ++i)
	// {
	// if (i != 0)
	// {
	// newString.append(',');
	// }
	//
	// newString.append(oldString.charAt(i));
	// }
	//
	// return newString.toString();
	// }
	//
	// /**
	// * Simple passthrough, calls join(stringArray, ',') to do the work.
	// * @param stringArray
	// * @return String
	// */
	// public static String commaDelimit(final Collection<String> stringArray)
	// {
	// return join(stringArray, ", ");
	// }

	/**
	 * Compare two doubles within a given epsilon.
	 * 
	 * @param a
	 *            first operand
	 * @param b
	 *            second operand
	 * @param eps
	 *            the epsilon (or deadband)
	 * @return TRUE if abs(a - b) < eps, else FALSE
	 */
	public static boolean compareDouble(final double a, final double b,
			final double eps)
	{
		// If the difference is less than epsilon, treat as equal.
		return Math.abs(a - b) < eps;
	}

	/**
	 * Returns true if the checklist contains any row from targets.
	 * 
	 * @param checklist
	 *            The collection to check
	 * @param targets
	 *            The collection to find in the checklist
	 * @return TRUE if equal, ELSE false
	 */
	public static <T> boolean containsAny(final Collection<T> checklist,
			final Collection<T> targets)
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
	 * 
	 * @param a
	 *            first operand
	 * @param b
	 *            second operand
	 * @return TRUE if equal, else FALSE
	 */
	public static boolean doublesEqual(final double a, final double b)
	{
		// If the difference is less than epsilon, treat as equal.
		return compareDouble(a, b, 0.0001);
	}

	/**
	 * Changes a path to make sure all instances of \ or / are replaced with
	 * File.separatorChar.
	 * 
	 * @param argFileName
	 *            The path to be fixed
	 * @return String
	 */
	public static String fixFilenamePath(final String argFileName)
	{
		return argFileName.replace('/', File.separatorChar).replace('\\',
				File.separatorChar);
	}

	/**
	 * Get the inner most String end
	 * 
	 * @param aString
	 *            The string to be searched for the innermost (
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
	 * 
	 * @param aString
	 *            the string sto be searched for the ) that closes the innermost
	 *            parenthesised expression
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

	// /**
	// * Concatenates the List into a String using the separator
	// * as the delimitor.
	// *
	// * Note the actual delimitor is the separator + " "
	// *
	// * @param strings An ArrayList of strings
	// * @param separator The separating character
	// * @return A 'separator' separated String
	// */
	// public static String join(final Collection<?> strings, final char
	// separator)
	// {
	// return join(strings, separator + " ");
	// }

	/**
	 * Return the english suffix for a given ordinal value
	 * 
	 * @param iValue
	 *            the ordinal value
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
	 * Turn a 'separator' separated string into a ArrayList of strings, each
	 * corresponding to one trimmed 'separator'-separated portion of the
	 * original string.
	 * 
	 * @param aString
	 *            The string to be split
	 * @param separator
	 *            The separator that separates the string.
	 * @return a List of Strings
	 */
	public static List<String> split(final String aString, final char separator)
	{
		final List<String> temp = new ArrayList<String>();
		final String sepStr = Pattern.quote(String.valueOf(separator));

		if (aString.trim().length() == 0)
		{
			return temp;
		}

		for (final String s : Arrays.asList(aString.split(sepStr)))
		{
			temp.add(s.trim());
		}

		return temp;
	}

	/**
	 * Unescape the : character
	 * 
	 * @param in
	 *            the string to operate on
	 * @return the modified string
	 */
	public static String unEscapeColons2(final String in)
	{
		return in.replaceAll(Pattern.quote("&#59;"), ":");
	}

	/**
	 * Convert to a String representation
	 * 
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

	public static List<Equipment> cloneEquipmentInList(
			final Iterable<Equipment> aList)
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
	 * @param aList
	 *            the list of Equipment
	 * @param merge
	 *            The type of merge to perform
	 * 
	 * @return merged list
	 */
	public static List<Equipment> mergeEquipmentList(
			final List<Equipment> aList, final int merge)
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

				// no container merge or Temporary Bonus generated equipment
				// must not merge
				if (eq1.isContainer() || eq1.isType("TEMPORARY")
						|| eq2.isType("TEMPORARY"))
				{
					continue;
				}

				if (eq1.getName().equals(eq2.getName()))
				{
					// merge all like equipment together
					if (merge == Constants.MERGE_ALL ||

					// merge like equipment within same container
							(merge == Constants.MERGE_LOCATION
									&& (eq1.getLocation() == eq2.getLocation()) && eq1
									.getParentName()
									.equals(eq2.getParentName())))
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

	/**
	 * Compare the two PCGen versions.
	 * 
	 * @param ver
	 *            The first version
	 * @param compVer
	 *            The second version
	 * @return the value 0 if the PCG versions are equal; a value less than 0 if
	 *         the first version is less than the second version; and a value
	 *         greater than 0 if the first version is greater than the second
	 *         version.
	 */
	public static int compareVersions(int[] ver, int[] compVer)
	{
		if (ver[0] != compVer[0])
		{
			return new Integer(ver[0]).compareTo(compVer[0]);
		}
		if (ver[1] != compVer[1])
		{
			return new Integer(ver[1]).compareTo(compVer[1]);
		}
		return new Integer(ver[2]).compareTo(compVer[2]);
	}

	/**
	 * Compare the two PCGen versions.
	 * 
	 * @param ver
	 *            The first version
	 * @param compVer
	 *            The second version
	 * @return the value 0 if the PCG versions are equal; a value less than 0 if
	 *         the first version is less than the second version; and a value
	 *         greater than 0 if the first version is greater than the second
	 *         version.
	 */
	public static int compareVersions(String ver, String compVer)
	{
		if (!ver.equals(compVer))
		{
			return compareVersions(convertVersionToNumber(ver),
					convertVersionToNumber(compVer));
		}
		return 0;
	}

	/**
	 * Check if a version is earlier or equal to the current pcgen version.
	 * 
	 * @param version
	 *            PCGen version to be checked.
	 * @return True if the version is before or equal to the current pcgen
	 *         version.
	 */
	public static boolean isPriorToCurrent(String version)
	{
		return CoreUtility.compareVersions(version, PCGenProp
				.getVersionNumber()) <= 0;
	}

	/**
	 * Convert a PCGen version to its numerical format.
	 * 
	 * @param version
	 *            the String version
	 * @return the version as an array of 3 ints
	 */
	public static int[] convertVersionToNumber(String version)
	{
		int[] intVer = { 0, 0, 0 };

		// extract the tokens from the version line
		String[] tokens = version.split(" |\\.|\\-", 4); //$NON-NLS-1$

		for (int idx = 0; idx < 3 && idx < tokens.length; idx++)
		{
			try
			{
				intVer[idx] = Integer.parseInt(tokens[idx]);
			}
			catch (NumberFormatException e)
			{
				if (idx == 2 && (tokens[idx].startsWith("RC")))
				{
					// Ignore we are not concerned about Release candidates
				}
				else
				{
					// Something in the first 3 digits was not an integer
					Logging.errorPrint("Invalid PCGen version: " + version);
				}
			}
		}
		return intVer;
	}

	/**
	 * Checks if the supplied version shares the same major and minor versions
	 * as the currently running version of PCGen.
	 * 
	 * @param ver
	 *            the version to check
	 * @return true, if it is the current minor version
	 */
	public static boolean isCurrMinorVer(String ver)
	{
		if (ver.equals(PCGenProp.getVersionNumber()))
		{
			return true;
		}
		int[] inVer = convertVersionToNumber(ver);
		int[] currVer = convertVersionToNumber(PCGenProp.getVersionNumber());
		return (inVer[0] == currVer[0] && inVer[1] == currVer[1]);
	}
}
