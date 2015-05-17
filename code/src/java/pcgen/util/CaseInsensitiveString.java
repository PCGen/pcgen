/*
 * Copyright 2006, 2007 (C) Tom Parker <thpr@sourceforge.net>
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
 * Created on October 16, 2006
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2005/06/19 19:07:49 $
 */
package pcgen.util;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 * 
 * CaseInsensitiveString is designed to be a String-like Object which is fast in
 * dealing with case sensitive comparisons in "consistent with equals"
 * situations.
 * 
 * Two CaseInsensitiveString objects are equal if the underlying String
 * (provided at object construction) would return true to the
 * .equalsIgnoreCase(String s) method of java.lang.String. This class therefore
 * also overrides the .hashCode() method of Object in order to maintain the
 * "consistent with equals" behavior that is appropriate (and expected by
 * certain classes, such as java.util.HashMap)
 */
public class CaseInsensitiveString
{

	/**
	 * The String that underlies this CaseInsensitiveString
	 */
	private final String string;

	/**
	 * The hashCode for this CaseInsensitiveString. Cached in order to provide
	 * fast response time to the use of .hashCode() [this is faster than storing
	 * a second copy of the String that is forced to one case]
	 */
	private int hashCode = 0;

	/**
	 * Create a new CaseInsensitiveString with the given String used for the
	 * basis of [case insensitive] equality of thsi object.
	 * 
	 * @param s
	 *            The underlying String of this CaseInsensitiveString
	 */
	public CaseInsensitiveString(String s)
	{
		string = s;
	}

	/**
	 * Compares two CaseInsensitiveStrings for equality. Returns true if the two
	 * CaseInsensitiveStrings are equal.
	 * 
	 * @return true if the given Object is a CaseInsensitiveString that is equal
	 *         to this CaseInsensitiveString.
	 */
	@Override
	public boolean equals(Object s)
	{
		/*
		 * DON'T DARE to make this function work for String or anything else...
		 * The way .equals works is that it casts to the class of the parent
		 * object - CaseInsensitiveStrings do NOT equals Strings. Please be
		 * consistent with the .equals interface as generally used in Java.
		 */
		return s instanceof CaseInsensitiveString &&
			string.equalsIgnoreCase(((CaseInsensitiveString) s).toString());
	}

	/**
	 * Returns a hash code of this CaseInsensitiveString. Provided to ensure
	 * behavior that is consistent with equals.
	 * 
	 * @return An integer has code of the CaseInsensitiveString
	 */
	@Override
	public int hashCode()
	{
		if (hashCode == 0)
		{
			hashCode = string.toUpperCase().hashCode();
		}
		return hashCode;
	}

	/**
	 * Returns a String representation of this CaseInsensitiveString.
	 * 
	 * @return A String representation of this CaseInsensitiveString.
	 */
	@Override
	public String toString()
	{
		return string;
	}
}
