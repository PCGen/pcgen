/*
 * Copyright 2006 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * 
 * Created on October 16, 2006
 */
package pcgen.base.lang;

import java.util.Objects;

/**
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
 * 
 * NOTE: It is generally preferred to use new TreeMap(String.CASE_INSENSITIVE_ORDER)
 * when dealing with case insensitive Maps.  This should only be avoided in rare
 * cases where performance is an issue in fetching items from large maps. In reality,
 * it may be best to remove CaseInsensitiveString from use entirely.
 */
public class CaseInsensitiveString
{

	/**
	 * The String that underlies this CaseInsensitiveString.
	 */
	private final String string;

	/**
	 * The hashCode for this CaseInsensitiveString. Cached in order to provide
	 * fast response time to the use of .hashCode() [this is faster than storing
	 * a second copy of the String that is forced to one case]
	 */
	private int hash = 0;

	/**
	 * Create a new CaseInsensitiveString with the given String used for the
	 * basis of [case insensitive] equality of thsi object.
	 * 
	 * @param str
	 *            The underlying String of this CaseInsensitiveString
	 */
	public CaseInsensitiveString(String str)
	{
		string = Objects.requireNonNull(str);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof CaseInsensitiveString)
		{
			CaseInsensitiveString cis = (CaseInsensitiveString) obj;
			return (string == cis.string) || string.equalsIgnoreCase(cis.string);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		if (hash == 0)
		{
			int length = string.length();
			for (int i = 0; i < length; i++)
			{
				hash = hash * 29 + Character.toUpperCase(string.charAt(i));
			}
		}
		return hash;
	}

	/**
	 * Returns the underlying String for this CaseInsensitiveString.
	 * 
	 * @return The underlying String for this CaseInsensitiveString
	 */
	@Override
	public String toString()
	{
		return string;
	}
}
