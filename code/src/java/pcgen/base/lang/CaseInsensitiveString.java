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
 * NOTE: It is generally preferred to use new TreeMap<String,
 * blah>(String.CASE_INSENSITIVE_ORDER) when dealing with case insensitive Maps.
 * This should only be avoided in rare cases where performance is an issue in
 * fetching items from large maps. In reality, it may be best to remove
 * CaseInsensitiveString from use entirely.
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
	 * @throws IllegalArgumentException
	 *             if the given String is null
	 */
	public CaseInsensitiveString(String str)
	{
		if (str == null)
		{
			throw new IllegalArgumentException(
					"Cannot make a Case Insensitive String for null");
		}
		string = str;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof CaseInsensitiveString)
		{
			CaseInsensitiveString cis = (CaseInsensitiveString) obj;
			return string == cis.string || string.equalsIgnoreCase(cis.string);
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		/*
		 * This method makes a rather rash assumption for the sake of
		 * performance: The given string in this CaseInsensitiveString can be
		 * appropriately hashed using only a subset of its values, and
		 * converting those to upper case using Character.toUpperCase().
		 * 
		 * This may be possible due to the contract of a hash: It must be
		 * identical if .equals() would return an identical value, but no
		 * guarantee is made that if the hashCode is the same then .equals()
		 * will return true. Locales may break this assumption.
		 * 
		 * This assumption is made on the grounds that a full .toUpperCase() on
		 * a String is an expensive operation, and using only a few characters
		 * can be much faster, at the expense of having only slightly greater
		 * risk of a hash collision.
		 */
		if (hash == 0)
		{
			int length = string.length();
			for (int i = 3; i < length && i < 16; i += 3)
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
