/*
 * Copyright 2013 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.lang;

/**
 * ObjectUtilities are various utility methods for dealing with all
 * java.lang.Object objects
 */
public final class ObjectUtil
{

	private ObjectUtil()
	{
		//Do not instantiate a utility class
	}

	/**
	 * Returns true if the two given objects are identical as reported by
	 * .equals()
	 * 
	 * This is tolerant of one or both of the values being null, and will return
	 * true if both are null (and false if only one is null).
	 * 
	 * @param o1
	 *            The first object to be compared for equality
	 * @param o2
	 *            The second object to be compared for equality
	 * @return true if the two objects are equal; false otherwise
	 */
	@SuppressWarnings("PMD.CompareObjectsWithEquals")
	public static <T> boolean compareWithNull(T o1, T o2)
	{
		return (o1 == o2) || ((o1 != null) && o1.equals(o2));
	}

}
