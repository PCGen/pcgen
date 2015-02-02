/*
 * Copyright (c) Thomas Parker, 2007, 2008.
 *  portions derived from CoreUtility.java
 *    Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 * 
 */
package pcgen.base.lang;

import java.util.Collection;
import java.util.Comparator;
import java.util.StringTokenizer;

/**
 * StringUtil is a utility class designed to provide utility methods when
 * working with java.lang.String Objects.
 */
public final class StringUtil
{

	/**
	 * Provides a Comparator for Strings that sorts in Case Sensitive Order.
	 * While this is generally the default for Strings (and thus this might seem
	 * extraneous), it is valuable as it can be used by other objects as a
	 * counterpart to String.CASE_INSENSITIVE_ORDER
	 */
	@SuppressWarnings("PMD.LongVariable")
	public static final Comparator<String> CASE_SENSITIVE_ORDER = new Comparator<String>()
	{
		@Override
		public int compare(String str1, String str2)
		{
			return str1.compareTo(str2);
		}

	};

	private StringUtil()
	{
		// Do not instantiate
	}

	/**
	 * Concatenates the Collection of Objects (converted to Strings using
	 * .toString()) into a String using the separator as the delimiter.
	 * 
	 * This method is value-semantic and will not modify or maintain a reference
	 * to the given Collection of Objects.
	 * 
	 * @param collection
	 *            An Collection of objects
	 * @param separator
	 *            The separating string
	 * @return A 'separator' separated String
	 */
	public static String join(Collection<?> collection, String separator)
	{
		return joinToStringBuilder(collection, separator).toString();
	}

	/**
	 * Concatenates the Collection of Objects (converted to Strings using
	 * .toString()) into a StringBuilder using the separator as the delimiter.
	 * 
	 * This method is value-semantic and will not modify or maintain a reference
	 * to the given Collection of Objects. Ownership of the returned
	 * StringBuilder is transferred to the calling object. No reference to the
	 * StringBuilder is maintained by StringUtil.
	 * 
	 * @param collection
	 *            An Collection of objects
	 * @param separator
	 *            The separating character
	 * @return A 'separator' separated StringBuilder
	 */
	public static StringBuilder joinToStringBuilder(Collection<?> collection,
			String separator)
	{
		if (collection == null)
		{
			return new StringBuilder();
		}

		StringBuilder result = new StringBuilder(collection.size() * 10);

		boolean needjoin = false;

		for (Object obj : collection)
		{
			if (needjoin)
			{
				result.append(separator);
			}
			needjoin = true;
			result.append(obj.toString());
		}

		return result;
	}

	/**
	 * Concatenates the Array of Strings into a String using the separator as
	 * the delimiter.
	 * 
	 * This method is value-semantic and will not modify or maintain a reference
	 * to the given Array of strings.
	 * 
	 * @param stringArray
	 *            An Array of strings
	 * @param separator
	 *            The separating string
	 * @return A 'separator' separated String
	 */
	public static String join(String[] stringArray, String separator)
	{
		if (stringArray == null)
		{
			return "";
		}

		StringBuilder result = new StringBuilder(stringArray.length * 10);

		boolean needjoin = false;

		for (String obj : stringArray)
		{
			if (needjoin)
			{
				result.append(separator);
			}
			needjoin = true;
			result.append(obj);
		}

		return result.toString();
	}

	/**
	 * Replaces all of the instances of the find String with newStr in the
	 * (first) given String.
	 * 
	 * @param original
	 *            The input String in which the String replacement should take
	 *            place
	 * @param find
	 *            The String to find within the input String, and which will be
	 *            replaced by the newStr
	 * @param replace
	 *            The new String that replaces the String to find within the
	 *            input String
	 * @return A new String created as a result of modifying the input String to
	 *         replace all instances of the String to find with the newStr
	 *         replacement String
	 */
	public static String replaceAll(String original, String find, String replace)
	{
		int startindex = original.indexOf(find);

		if (startindex < 0)
		{
			return original;
		}

		char[] working = original.toCharArray();
		StringBuilder sb = new StringBuilder(original.length()
			+ replace.length());

		int currindex = 0;

		while (startindex > -1)
		{
			for (int i = currindex; i < startindex; ++i)
			{
				sb.append(working[i]);
			}

			currindex = startindex;
			sb.append(replace);
			currindex += find.length();
			startindex = original.indexOf(find, currindex);
		}

		for (int i = currindex; i < working.length; ++i)
		{
			sb.append(working[i]);
		}

		return sb.toString();
	}

	/**
	 * Tests to see if the given String has balanced parenthesis. Balanced means
	 * that it has an equal number of open and close parenthesis, and also that
	 * the parenthesis are in a "sensible" format. "Sensible" means that a close
	 * parenthesis cannot appear before an open parenthesis.
	 * 
	 * @param str
	 *            The String to be tested to see if it has balanced parenthesis
	 * @return true if the given String has balanced parenthesis; false
	 *         otherwise
	 */
	public static boolean hasBalancedParens(String str)
	{
		int level = 0;
		StringTokenizer st = new StringTokenizer(str, "()", true);
		while (st.hasMoreTokens())
		{
			String tok = st.nextToken();
			if (")".equals(tok))
			{
				level--;
			}
			else if ("(".equals(tok))
			{
				level++;
			}
			if (level < 0)
			{
				return false;
			}
		}
		return level == 0;
	}

}
