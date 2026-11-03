/*
 * Copyright 2004-2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Created on Aug 29, 2004
 * Imported into PCGen on June 18, 2005.
 */
package pcgen.base.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Map of objects to Lists. List management is done internally to
 * this class (while copies are accessible, the lists are kept private to this
 * class).
 * 
 * This class is both value-semantic and reference-semantic.
 * 
 * In appropriate cases (such as calling the addToListFor method), HashMapToList
 * will maintain a reference to the given Object. HashMapToList will not modify
 * any of the Objects it is passed; however, it reserves the right to return
 * references to Objects it contains to other Objects.
 * 
 * However, HashMapToList also protects its internal structure (the internal
 * structure is not exposed) ... when any method in which HashMapToList returns
 * a Collection, ownership of the Collection itself is transferred to the
 * calling Object, but the contents of the Collection (keys, values, etc.) are
 * references whose ownership should be respected. Also, when any method in
 * which HashMapToList receives a Collection as a parameter, the ownership of
 * the given Collection is not transferred to HashMapToList (in other words, the
 * Collection will not be modified, and no references to the Collection will be
 * maintain by HashMapToList). HashMapToList will obviously retain references to
 * the contents of any Collections it may be passed.
 * 
 * CAUTION: If you are not looking for the value-semantic protection of this
 * class (of preventing accidental modification, then this is a convenience
 * method and is not appropriate for use in Java 1.5 (Typed Collections are
 * probably more appropriate).
 * 
 * @param <K>
 *            The Class of the key for this HashMapToList
 * @param <V>
 *            The Class of the Value for this HashMapToList
 */
public class HashMapToList<K, V> extends AbstractMapToList<K, V>
{

	/**
	 * Creates a new HashMapToList.
	 */
	public HashMapToList()
	{
		super(new HashMap<>());
	}

	/**
	 * Creates a new HashSet for use by AbstractMapToList. It is intended that
	 * this will only be used by AbstractMapToList.
	 * 
	 * Ownership of the constructed Set is transferred to the calling object,
	 * and no reference to it is maintained by HashMapToList due to this method
	 * call.
	 */
	@Override
	protected Set<K> getEmptySet()
	{
		return new HashSet<>();
	}
}
