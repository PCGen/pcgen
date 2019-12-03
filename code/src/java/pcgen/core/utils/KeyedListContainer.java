/*
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
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

import java.util.List;

import pcgen.cdom.enumeration.ListKey;

/**
 *
 * An object that implements KeyedListContainer safely encapsulates a ListKeyMap
 */
public interface KeyedListContainer
{
	/**
	 * Returns true if it contains a list for that key
	 * @param key
	 * @return true if it contains a list for that key
	 */
    boolean containsListFor(ListKey<?> key);

	/**
	 * Retrieves a list based on key
	 * @param key
	 * @return list
	 */
    <T> List<T> getListFor(ListKey<T> key);

	/**
	 * Get the size of a list based off a key
	 * @param key
	 * @return The size of a list based off a key
	 */
    int getSizeOfListFor(ListKey<?> key);

	/**
	 * Returns true if a value is in a list for that key
	 * @param key
	 * @param value
	 * @return true if a value is in a list for that key
	 */
    <T> boolean containsInList(ListKey<T> key, T value);

	/**
	 * Gets an element from the list
	 * @param key
	 * @param i
	 * @return An element from the list
	 */
    <T> T getElementInList(ListKey<T> key, int i);

	/**
	 * Get safe list
	 * @param key
	 * @return safe list
	 */
    <T> List<T> getSafeListFor(ListKey<T> key);

	/**
	 * Get the size of a safe list
	 * @param key
	 * @return The size of a safe list
	 */
    int getSafeSizeOfListFor(ListKey<?> key);
}
