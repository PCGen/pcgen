/*
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
 */
package pcgen.core.utils;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import pcgen.cdom.base.Constants;
import pcgen.core.Equipment;
import pcgen.system.PCGenPropBundle;
import pcgen.util.Logging;

/**
 * {@code CoreUtility}.
 *
 * Assorted generic-ish functionality moved from Globals and PlayerCharacter
 * (the two biggest classes in the project.) Some of this code seems awfully
 * similar, and should probably be further refactored.
 *
 */
public final class CoreUtility
{

	private static final double EPSILON = 0.0001d;

	public static final Comparator<Equipment> EQUIPMENT_COMPARATOR = new Comparator<>()
	{
		@Override
		public int compare(final Equipment obj1, final Equipment obj2)
		{
			int o1i = obj1.getOutputIndex();
			int o2i = obj2.getOutputIndex();

			// Force unset items (index of 0) to appear at the end
			o1i = (o1i == 0) ? 999 : o1i;
			o2i = (o2i == 0) ? 999 : o2i;

			final int result1 = Integer.compare(o1i, o2i);

			if (result1 != 0)
			{
				return result1;
			}

			final int result2 = Integer.compare(obj1.getOutputSubindex(), obj2.getOutputSubindex());

			if (result2 != 0)
			{
				return result2;
			}

			final int result3 = obj1.getName().compareToIgnoreCase(obj2.getName());

			if (result3 != 0)
			{
				return result3;
			}

			final int result4 = obj1.getAppliedName().compareToIgnoreCase(obj2.getAppliedName());

			if (result4 != 0)
			{
				return result4;
			}

			return obj1.getParentName().compareToIgnoreCase(obj2.getParentName());
		}

		@Override
		public boolean equals(final Object obj)
		{
			return false;
		}

		@Override
		public int hashCode()
		{
			return 0;
		}
	};

	private CoreUtility()
	{
	}

	/**
	 * return true if the protocol of the URL represented is FTP or HTTP
	 *
	 * @param uri
	 *            the URI object to test for a network protocol
	 * @return true if the protocol of this URI is FTP or HTTP
	 */
	public static boolean isNetURI(final URI uri)
	{
		return !"file".equals(uri.getScheme());
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

	/**
	 * Compare two doubles within a given epsilon.
	 *
	 * @param a
	 *            first operand
	 * @param b
	 *            second operand
	 * @param eps
	 *            the epsilon (or deadband)
	 * @return TRUE {@literal if abs(a - b) < eps}, else FALSE
	 */
	public static boolean compareDouble(final double a, final double b, final double eps)
	{
		// If the difference is less than epsilon, treat as equal.
		return Math.abs(a - b) < eps;
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
		return compareDouble(a, b, EPSILON);
	}

	/**
	 * protect the floor function from the vagaries of floating point precision.
	 * @param d the double that we would like the floor value for
	 * @return the floor after adding epsilon
	 */
	public static double epsilonFloor(double d)
	{
		return Math.floor(d + EPSILON);
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
		return argFileName.replace('/', File.separatorChar).replace('\\', File.separatorChar);
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
				case 1 -> suffix = "st";
				case 2 -> suffix = "nd";
				case 3 -> suffix = "rd";
				default -> {
				}
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
		final List<String> temp = new ArrayList<>();
		final String sepStr = Pattern.quote(String.valueOf(separator));

		if (aString.trim().isEmpty())
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
	 * Merge the equipment list
	 *
	 * @param equip
	 *            the collection of Equipment
	 * @param merge
	 *            The type of merge to perform
	 *
	 * @return merged list
	 */
	public static List<Equipment> mergeEquipmentList(final Collection<Equipment> equip, final int merge)
	{
		List<Equipment> workingList = new ArrayList<>();
		for (Equipment e : equip)
		{
			workingList.add(e.clone());
		}
		workingList.sort(EQUIPMENT_COMPARATOR);

		// no merging, just sorting (calling this is really stupid,
		// just use the sort above)
		if (merge == Constants.MERGE_NONE)
		{
			return workingList;
		}

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
				if (eq1.isContainer() || eq1.isType("TEMPORARY") || eq2.isType("TEMPORARY"))
				{
					continue;
				}

				if (eq1.getName().equals(eq2.getName()))
				{
					// merge all like equipment together
					if (merge == Constants.MERGE_ALL

						// merge like equipment within same container
						|| (merge == Constants.MERGE_LOCATION && (eq1.getLocation() == eq2.getLocation())
							&& eq1.getParentName().equals(eq2.getParentName())))
					{
						workingList.remove(eq2);
						eQty += eq2.qty();
						endIndex--;
					}
				}
			}

			workingList.get(i).setQty(eQty);
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
			return Integer.compare(ver[0], compVer[0]);
		}
		if (ver[1] != compVer[1])
		{
			return Integer.compare(ver[1], compVer[1]);
		}
		return Integer.compare(ver[2], compVer[2]);
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
			return compareVersions(convertVersionToNumber(ver), convertVersionToNumber(compVer));
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
		return CoreUtility.compareVersions(version, PCGenPropBundle.getVersionNumber()) <= 0;
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
		int[] intVer = {0, 0, 0};

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
					Logging.debugPrint("we are not concerned about Release candidates");
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
		if (ver.equals(PCGenPropBundle.getVersionNumber()))
		{
			return true;
		}
		int[] inVer = convertVersionToNumber(ver);
		int[] currVer = convertVersionToNumber(PCGenPropBundle.getVersionNumber());
		return (inVer[0] == currVer[0] && inVer[1] == currVer[1]);
	}

	/**
	 * Check if the two versions are different only in release number. i.e.
	 * they have the same major and minor versions.
	 *
	 * @param ver1 A PCGen version number to be compared.
	 * @param ver2 A PCGen version number to be compared.
	 * @return true if they have the same major and minor versions.
	 */
	public static boolean sameMajorMinorVer(int[] ver1, int[] ver2)
	{
		return (ver1[0] == ver2[0] && ver1[1] == ver2[1]);
	}

}
