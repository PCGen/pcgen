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

import java.util.function.Predicate;

/**
 * ObjectUtilities are various utility methods for dealing with all
 * java.lang.Object objects.
 */
public final class ObjectUtil
{

	/**
	 * Private Constructor for Utility Class.
	 */
	private ObjectUtil()
	{
	}

	/**
	 * Returns a Predicate that indicates whether the object provided to the Predicate is
	 * equal (identity equal, meaning ==) to the object provided to this method.
	 * 
	 * @param object
	 *            The object to be checked by the Predicate to see if it is identify equal
	 *            to this object
	 * @return A Predicate that indicates whether the object provided to the Predicate is
	 *         equal (identity equal, meaning ==) to the object provided to this method
	 * @param <T>
	 *            The component type of the Object to be checked for equality
	 */
	public static <T> Predicate<? super T> identityEquals(T object)
	{
		return given -> (given == object);
	}

}
