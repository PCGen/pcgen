/*
 * Copyright 2004-2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Represents a Map of objects to Lists. List management is done internally to
 * this class (while copies are accessible, the lists are kept private to this
 * class).
 * 
 * This class is reference-semantic. In appropriate cases (such as calling the
 * addToListFor method), HashMapToList will maintain a reference to the given
 * Object. HashMapToList will not modify any of the Objects it is passed;
 * however, it reserves the right to return references to Objects it contains to
 * other Objects.
 * 
 * However, when any method in which HashMapToList returns a Collection,
 * ownership of the Collection itself is transferred to the calling Object, but
 * the contents of the Collection (keys, values, etc.) are references whose
 * ownership should be respected.
 * 
 * Note: For purposes of containing and removing an object from a List, this
 * Class performs instance tests (meaning the use of == not .equals()).
 * 
 * CAUTION: This is a convenience method for use in Java 1.4 and is not
 * appropriate for use in Java 1.5 (Typed Collections are probably more
 * appropriate)
 * 
 * @param <K>
 *            The Class of the key for this HashMapToInstanceList
 * @param <V>
 *            The Class of the Value for this HashMapToInstanceList
 */
public class HashMapToInstanceList<K, V> extends AbstractMapToList<K, V>
{

	/**
	 * Creates a new HashMapToList.
	 */
	public HashMapToInstanceList()
	{
		super(new HashMap<>());
	}

	/**
	 * Creates a new HashSet for use by AbstractMapToList. It is intended that
	 * this will only be used by AbstractMapToList.
	 * 
	 * Ownership of the constructed Set is transferred to the calling object,
	 * and no reference to it is maintained by HashMapToInstanceList due to this
	 * method call.
	 */
	@Override
	protected Set<K> getEmptySet()
	{
		return new HashSet<>();
	}

	/**
	 * Returns true if this MapToList contains a List for the given key and that
	 * list contains the given object (this is an instance test against the
	 * specific object, not the value of the object). Note, this method returns
	 * false if the given key is not in this MapToList.
	 * 
	 * This method is value-semantic in that no changes are made to the objects
	 * passed into the method.
	 * 
	 * @param key
	 *            The key for the List being tested.
	 * @param valueElement
	 *            The object to find in the List for the given key.
	 * @return true if this MapToList contains a List for the given key AND that
	 *         list contains the given value; false otherwise.
	 */
	@Override
	@SuppressWarnings("PMD.CompareObjectsWithEquals")
	public boolean containsInList(K key, V valueElement)
	{
		List<V> list = getListFor(key);
		if (list == null)
		{
			return false;
		}
		for (V o : list)
		{
			if (o == valueElement)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Package - internal behavior. Actually remove an item from a list
	 */
	@SuppressWarnings("PMD.DefaultPackage")
	@Override
	boolean removeFromList(List<V> list, V valueElement)
	{
		for (Iterator<V> it = list.iterator(); it.hasNext();)
		{
			if (it.next() == valueElement)
			{
				it.remove();
				return true;
			}
		}
		return false;
	}
}
