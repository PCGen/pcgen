/*
 * Copyright 2007, 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
 */
package pcgen.cdom.helper;

/**
 * A Quality represents a name/value characteristic
 */
public class Quality
{

	/**
	 * The name of the quality stored in this Quality.
	 */
	private final String quality;

	/**
	 * The value of the quality stored in this Quality.
	 */
	private final String value;

	/**
	 * Constructs a new Quality with the given name and value.
	 * 
	 * @param name
	 *            The name of the Quality
	 * @param val
	 *            The value of this Quality
	 * @throws IllegalArgumentException
	 *             if the given name or value is null
	 */
	public Quality(String name, String val)
	{
		if (name == null)
		{
			throw new IllegalArgumentException(
					"Name for Quality cannot be null");
		}
		if (val == null)
		{
			throw new IllegalArgumentException(
					"Value for Quality cannot be null");
		}
		quality = name;
		value = val;
	}

	/**
	 * Returns the name of this Quality
	 * 
	 * @return the name of this Quality
	 */
	public String getName()
	{
		return quality;
	}

	/**
	 * Returns the value of this Quality
	 * 
	 * @return The value of this Quality
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Returns the consistent-with-equals hashCode for this Quality
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return quality.hashCode() ^ value.hashCode();
	}

	/**
	 * Returns true if this Quality is equal to the given Object. Equality is
	 * defined as being another Quality object with an equal name and value
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Quality)
		{
			Quality other = (Quality) o;
			return quality.equals(other.quality) && value.equals(other.value);
		}
		return false;
	}

	/**
	 * Returns a String representation of this Quality, primarily for purposes
	 * of debugging. It is strongly advised that no dependency on this method be
	 * created, as the return value may be changed without warning.
	 * 
	 * @return A String representation of this Quality
	 */
	@Override
	public String toString()
	{
		return quality + ": " + value;
	}
}
