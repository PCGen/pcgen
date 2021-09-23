/*
 * Copyright 2017 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.util;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * ListUtilities is a utility class designed to provide utility methods when working with
 * java.util.List Objects.
 */
public final class ListUtilities
{

	private ListUtilities()
	{
		//Do not construct utility class
	}

	/**
	 * Provides a Consumer that removes the given object from the List provided to the
	 * Consumer. Intended to be used in a Stream.
	 * 
	 * @param object
	 *            The object to be removed from the List provided to the Consumer
	 * @return A Consumer that removes the given object from the List provided to the
	 *         Consumer
	 * @param <T>
	 *            The type of the object contained in the List managed by the Consumer
	 */
	public static <T> Consumer<? super List<T>> removeFromList(T object)
	{
		return list -> list.remove(object);
	}

	/**
	 * Provides a Predicate that tests if the given list does NOT contain the object
	 * provided to the Predicate. Intended to be used in a Stream.
	 * 
	 * @param list
	 *            The List to be checked to see if it does NOT contain the object provided
	 *            to the Predicate
	 * @return A Predicate that tests if the given list does NOT contain the object
	 *         provided to the Predicate
	 * @param <T>
	 *            The type of the object contained in the List checked by the Predicate
	 */
	public static <T> Predicate<? super T> notContainedBy(List<T> list)
	{
		return node -> !list.contains(node);
	}

	/**
	 * Returns true if the given (non-null) List contains null as one of its values.
	 * 
	 * @param list
	 *            The list to be checked to see if it contains null
	 * @return true if the given List contains null as one of its values; false otherwise
	 */
	public static boolean containsNull(List<?> list)
	{
		for (Object o : list)
		{
			if (o == null)
			{
				return true;
			}
		}
		return false;
	}

}
