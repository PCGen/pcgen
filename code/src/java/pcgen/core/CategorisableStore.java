/*
 * CategorisableStore.java
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
 * Current Version: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core;

import java.util.*;

/**
 * Implements a storage facility for objects which implement the Categorisable
 * interface.
 *
 * @author   Andrew Wilson <nuance@sourceforge.net>
 * @version  $Revision$
 */
public class CategorisableStore<T extends Categorisable> implements Cloneable
{
	private static final Comparator<Categorisable> catKeyComp = new Comparator<Categorisable>()
		{
			public int compare(final Categorisable o1, final Categorisable o2)
			{
				return o1.getKeyName().compareToIgnoreCase(o2.getKeyName());
			}
		};

	private static final Comparator<Categorisable> catNameComp = new Comparator<Categorisable>()
		{
			public int compare(final Categorisable o1, final Categorisable o2)
			{
				final String s1 = o1.getDisplayName();
				final String s2 = o2.getDisplayName();
				return (s1.compareToIgnoreCase(s2));
			}
		};

	private static final Comparator<String> stringComp = new Comparator<String>()
		{
			public int compare(final String o1, final String o2)
			{
				return (o1.compareToIgnoreCase(o2));
			}
		};


		protected Map<String, Map<String, T>> categoryMap = new HashMap<String, Map<String, T>>();

	/**
	 * Make a new WareHouse
	 */
	public CategorisableStore()
	{
		super();
	}

	/**
	 * Add a Categorisable Object to this Collection
	 *
	 * @param   aCatObj  the categorisable object to add
	 *
	 * @return  true if the object was added correctly
	 */

	public boolean addCategorisable(final T aCatObj)
	{
		Map<String, T> objMap = categoryMap.get(aCatObj.getCategory());

		if (objMap == null)
		{
			objMap = new HashMap<String, T>();
			categoryMap.put(aCatObj.getCategory(), objMap);
		}

		final String key = aCatObj.getKeyName().toLowerCase();

		/* Keys absolutely must be unique */
		if (objMap.get(key) != null)
		{
			return false;
		}

		objMap.put(key, aCatObj);

		if (objMap.get(key) == aCatObj)
		{
			return true;
		}

		return false;
	}

	/**
	 * Clear out the store of objects
	 */
	public void clear()
	{
		categoryMap = new HashMap<String, Map<String, T>>();
	}

	/**
	 * Make a new store that has all of the objects in the the same categories
	 * as this store
	 *
	 * @return  the new store
	 */
	public Object clone()
	{
		CategorisableStore clone = new CategorisableStore();

		Iterator<T> it = this.getKeyIterator(Constants.ALL_CATEGORIES);

		while (it.hasNext())
		{
			final T ab = it.next();
			clone.addCategorisable(ab);
		}

		return clone;
	}

	/**
	 * Get the Categorisable Object whose Category and Key match the Strings
	 * passed in
	 *
	 * @param   aCategory  The category to search for the object
	 * @param   aKey       the key of the object to return
	 *
	 * @return  the Categorisable Object whose Category and Key match
	 */

	public T getKeyed(final String aCategory, final String aKey)
	{
		final Map<String, T> objMap = categoryMap.get(aCategory);

		// nothing in this category?
		if (objMap == null)
		{
			return null;
		}

		return objMap.get(aKey.toLowerCase());
	}

	/**
	 * Get an iterator for the objects in the chosen category.  If passed the
	 * string "ALL", will construct an iterator for all objects in the
	 * warehouse.  They will be sorted in Key order.
	 *
	 * @param   aCategory  the Category to return an iterator for
	 *
	 * @return  An Iterator
	 */
	public Iterator<T> getKeyIterator(String aCategory)
	{
		SortedSet<T> sortedAggregate = getSortedSet(aCategory, catKeyComp);

		if (sortedAggregate == null)
		{
			return Collections.EMPTY_SET.iterator();
		}

		return Collections.unmodifiableSortedSet(sortedAggregate).iterator();
	}

	/**
	 * Get the Categorisable Object whose Category and Name match the Strings
	 * passed in
	 *
	 * @param   aCategory  the category to search
	 * @param   aName      the Name of the object to return
	 *
	 * @return  the Categorisable Object whose Name matches the String passed in
	 */

	public T getNamed(final String aCategory, String aName)
	{
		final Iterator<T> it = getNameIterator(aCategory);

		// nothing in this category?
		if (it == null)
		{
			return null;
		}

		String strippedName = EquipmentUtilities.removeChoicesFromName(aName);

		while (it.hasNext())
		{
			final T itCatObj = it.next();

			if (
				itCatObj.getKeyName().equalsIgnoreCase(aName) ||
				itCatObj.getKeyName().equalsIgnoreCase(strippedName))
			{
				return itCatObj;
			}
		}

		return null;
	}

	/**
	 * Get an iterator for the objects in the chosen category.  If passed the
	 * string "ALL", will construct an iterator for all objects in the
	 * warehouse.  They will be sorted in Name order.
	 *
	 * @param   aCategory  the Category to return an iterator for
	 *
	 * @return  An Iterator
	 */
	public Iterator<T> getNameIterator(String aCategory)
	{
		TreeSet<T> sortedAggregate = getSortedSet(aCategory, catNameComp);

		if (sortedAggregate == null)
		{
			return Collections.EMPTY_SET.iterator();
		}

		return Collections.unmodifiableSortedSet(sortedAggregate).iterator();
	}


	/**
	 * Get an iterator for the categories.  They will be sorted in Name order.
	 *
	 * @return  An Iterator
	 */
	public Iterator<String> getCategoryIterator()
	{
		TreeSet<String> sortedAggregate = getSortedCategorySet(stringComp);

		if (sortedAggregate == null)
		{
			return Collections.EMPTY_SET.iterator();
		}

		return Collections.unmodifiableSortedSet(sortedAggregate).iterator();
	}


	/**
	 * Get an iterator for the objects in the chosen category.  If passed the
	 * string "ALL", will construct an iterator for all objects in the
	 * warehouse.  They will be sorted as per the comparator object passed in
	 *
	 * @param   aCategory  the Category to return an iterator for
	 * @param   aComp      a Comparator
	 *
	 * @return  An Iterator
	 */
	private TreeSet<T> getSortedSet(
		final String     aCategory,
		final Comparator<Categorisable> aComp)
	{
		final Set<T> aggregate;

		if (Constants.ALL_CATEGORIES.equals(aCategory))
		{
			aggregate = makeAggregateSet();
		}
		else
		{
			Map<String, T> aggregateMap = categoryMap.get(aCategory);

			if (aggregateMap == null)
			{
				return null;
			}

			aggregate = new HashSet<T>(aggregateMap.values());
		}

		TreeSet<T> sortedAggregate = new TreeSet<T>(aComp);
		sortedAggregate.addAll(aggregate);

		return sortedAggregate;
	}


	/**
	 * Get an iterator for the categories. They will be sorted as per the
	 * comparator object passed in
	 *
	 * @param   aComp      a Comparator
	 *
	 * @return  An Iterator
	 */
	private TreeSet<String> getSortedCategorySet(final Comparator<String> aComp)
	{
		TreeSet<String> sortedAggregate = new TreeSet<String>(aComp);
		sortedAggregate.addAll(categoryMap.keySet());

		return sortedAggregate;
	}


	/**
	 * For the rare method that does actually need a list of Categorisable
	 * Objects rather than an iterator.
	 *
	 * @param   aCategory  the category of object to return
	 *
	 * @return  an unmodifiable list of the Objects in the given category
	 */
	public List<T> getUnmodifiableList(String aCategory)
	{
		TreeSet<T> sortedAggregate = getSortedSet(aCategory, catNameComp);

		if (sortedAggregate == null)
		{
			return Collections.emptyList();
		}

		ArrayList<T> aList = new ArrayList<T>(sortedAggregate.size());
		aList.addAll(sortedAggregate);

		return Collections.unmodifiableList(aList);
	}

	/**
	 * Make a HashSet of all the objects in the warehouse.
	 *
	 * @return  a HashSet containing all the objects
	 */
	private Set<T> makeAggregateSet()
	{
		final Set<T> aggregate = new HashSet<T>();

		for (Iterator<Map<String, T>> it = categoryMap.values().iterator(); it.hasNext();)
		{
			aggregate.addAll(it.next().values());
		}

		return aggregate;
	}

	/**
	 * Remove the Categorisable object whose Category and Key match the Strings
	 * passed in.
	 *
	 * @param   aCategory  The category to search for the object to remove
	 * @param   aKey       The key of the object to remove
	 *
	 * @return  true if the object was removed (false may mean it was never
	 *          there)
	 */
	public boolean removeKeyed(final String aCategory, final String aKey)
	{
		final Map<String, T> objMap = categoryMap.get(aCategory);

		// nothing in this category?
		if (objMap == null)
		{
			return false;
		}

		String key = aKey.toLowerCase();

		final Categorisable aCatObj = objMap.remove(key);

		return (aCatObj != null);
	}

	/**
	 * Remove the Categorisable Object whose Category and Name match the Strings
	 * passed in.
	 *
	 * @param   aCategory  the category to search for the object to remove
	 * @param   aName      The name of the object to remove
	 *
	 * @return  a boolean representing whether the Categorisable Object was
	 *          removed. If the Categorisable Object was never there, this will
	 *          return false (since it was not removed).
	 */

	public boolean removeNamed(final String aCategory, String aName)
	{
		final Iterator<T> it = getNameIterator(aCategory);

		// nothing in this category?
		if (it == null)
		{
			return false;
		}

		String strippedName = EquipmentUtilities.removeChoicesFromName(aName);

		while (it.hasNext())
		{
			final Categorisable itCatObj = it.next();

			if (
				itCatObj.getKeyName().equalsIgnoreCase(aName) ||
				itCatObj.getKeyName().equalsIgnoreCase(strippedName))
			{
				final String        aKey    = itCatObj.getKeyName().toLowerCase();
				final Map<String, T>       objMap  = categoryMap.get(aCategory);
				final Categorisable aCatObj = objMap.remove(aKey);

				return (aCatObj != null);
			}
		}

		return false;
	}

	/**
	 * @return  the number of objects in the store
	 */
	public int size()
	{
		int      size = 0;
		Iterator<Map<String, T>> it   = categoryMap.values().iterator();

		while (it.hasNext())
		{
			size = size + it.next().size();
		}

		return size;
	}

	/**
	 * @return  true if there is nothing stored
	 */

	public boolean isEmpty()
	{
		return this.size() == 0;
	}
}
