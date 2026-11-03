/*
 * Copyright 2004, 2005 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Created on Aug 29, 2004 Imported into PCGen on June 18, 2005.
 */
package pcgen.base.util;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Represents a Map of objects to Lists. List management is done internally to
 * this class (while copies are accessible, the lists are kept private to this
 * class).
 * 
 * This class is reference-semantic. In appropriate cases (such as calling the
 * addToListFor method), TreeMapToList will maintain a reference to the given
 * Object. TreeMapToList will not modify any of the Objects it is passed;
 * however, it reserves the right to return references to Objects it contains to
 * other Objects.
 * 
 * However, when any method in which TreeMapToList returns a Collection,
 * ownership of the Collection itself is transferred to the calling Object, but
 * the contents of the Collection (keys, values, etc.) are references whose
 * ownership should be respected.
 * 
 * Note that this sorts keys in the same fashion as TreeMap: according to the
 * natural order for the key's class (see Comparable), or by the comparator
 * provided at construction. Thus, the keys in the TreeMapToList must be
 * Comparable, or a Comparator must be provided. All association cautions with
 * TreeMap (such as consistent-with-equals operation) must also be observed in
 * TreeMapToList (see TreeMap)
 * 
 * CAUTION: This is a convenience method for use in Java 1.4 and is not
 * appropriate for use in Java 1.5 (Typed Collections are probably more
 * appropriate)
 * 
 * @param <K>
 *            The Class of the key for this TreeMapToList
 * @param <V>
 *            The Class of the Value for this TreeMapToList
 */
public class TreeMapToList<K, V> extends AbstractMapToList<K, V>
{

	/**
	 * The Comparator used to order the key elements of this TreeMapToList.
	 */
	private final Comparator<? super K> comparator;

	/**
	 * Creates a new TreeMapToList.
	 */
	public TreeMapToList()
	{
		super(new TreeMap<>());
		comparator = null;
	}

	/**
	 * Creates a new TreeMapToList using the given Comparator as the Comparator
	 * for the underlying TreeSet.
	 * 
	 * @param comp
	 *            The Comparator to be used as the Comparator for the keys in
	 *            this TreeMapToList
	 */
	public TreeMapToList(Comparator<? super K> comp)
	{
		super(new TreeMap<>(comp));
		comparator = comp;
	}

	/**
	 * Creates a new TreeSet for use by AbstractMapToList. It is intended that
	 * this will only be used by AbstractMapToList.
	 * 
	 * Ownership of the constructed Set is transferred to the calling object,
	 * and no reference to it is maintained by TreeMapToList due to this method
	 * call.
	 */
	@Override
	protected Set<K> getEmptySet()
	{
		return new TreeSet<>(comparator);
	}
}
