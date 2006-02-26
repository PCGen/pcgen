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
 * Current Version: $Revision: 1.9 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/22 19:33:55 $
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core;

import java.util.*;

/**
 * Implements a storage facility for objects which implement the Categorisable
 * interface.
 *
 * @author   Andrew Wilson <nuance@sourceforge.net>
 * @version  $Revision: 1.9 $
 */
public class CategorisableStore implements Cloneable
{
	private static final Comparator catKeyComp = new Comparator()
		{
			public int compare(final Object o1, final Object o2)
			{
				return ((Categorisable) o1).getKeyName().compareToIgnoreCase(
						((Categorisable) o2).getKeyName());
			}
		};

	private static final Comparator catNameComp = new Comparator()
		{
			public int compare(final Object o1, final Object o2)
			{
				return ((Categorisable) o1).getName().compareToIgnoreCase(
					    ((Categorisable) o2).getName());
			}
		};

	protected Map categoryMap = new HashMap();

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
	 * @param   aCatObj  the ability to add
	 *
	 * @return  true if the object was added correctly
	 */

	public boolean addNewCategory(final Categorisable aCatObj)
	{
		Map objMap = (HashMap) categoryMap.get(aCatObj.getCategory());

		if (objMap == null)
		{
			objMap = new HashMap();
			categoryMap.put(aCatObj.getCategory(), objMap);
		}

		/* Keys absolutely must be unique */
		if (objMap.get(aCatObj.getKeyName()) != null)
		{
			return false;
		}

		objMap.put(aCatObj.getKeyName(), aCatObj);

		if (objMap.get(aCatObj.getKeyName()) == aCatObj)
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
		categoryMap = new HashMap();
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

		Iterator it = this.getKeyIterator(Constants.ALL_CATEGORIES);

		while (it.hasNext())
		{
			final Categorisable ab = (Categorisable) it.next();
			clone.addNewCategory(ab);
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

	public Categorisable getKeyed(final String aCategory, final String aKey)
	{
		final HashMap objMap = (HashMap) categoryMap.get(aCategory);

		// nothing in this category?
		if (objMap == null)
		{
			return null;
		}

		return (Categorisable) objMap.get(aKey);
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
	public Iterator getKeyIterator(String aCategory)
	{
		TreeSet sortedAggregate = getSortedSet(aCategory, catKeyComp);

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

	public Categorisable getNamed(final String aCategory, String aName)
	{
		final Iterator it = getNameIterator(aCategory);

		// nothing in this category?
		if (it == null)
		{
			return null;
		}

		String strippedName = AbilityUtilities.removeChoicesFromName(aName);

		while (it.hasNext())
		{
			final Categorisable itCatObj = (Categorisable) it.next();

			if (
			    itCatObj.getName().equalsIgnoreCase(aName) ||
			    itCatObj.getName().equalsIgnoreCase(strippedName))
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
	public Iterator getNameIterator(String aCategory)
	{
		TreeSet sortedAggregate = getSortedSet(aCategory, catNameComp);

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
	public Iterator getCategoryIterator()
	{
		TreeSet sortedAggregate = getSortedCategorySet(catNameComp);

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
	private TreeSet getSortedSet(
	    final String     aCategory,
	    final Comparator aComp)
	{
		final HashSet aggregate;

		if (Constants.ALL_CATEGORIES.equals(aCategory))
		{
			aggregate = makeAggregateSet();
		}
		else
		{
			HashMap aggregateMap = (HashMap) categoryMap.get(aCategory);

			if (aggregateMap == null)
			{
				return null;
			}

			aggregate = new HashSet(aggregateMap.values());
		}

		TreeSet sortedAggregate = new TreeSet(aComp);
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
	private TreeSet getSortedCategorySet(final Comparator aComp)
	{
		TreeSet sortedAggregate = new TreeSet(aComp);
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
	public List getUnmodifiableList(String aCategory)
	{
		TreeSet sortedAggregate = getSortedSet(aCategory, catNameComp);

		if (sortedAggregate == null)
		{
			return Collections.EMPTY_LIST;
		}

		ArrayList aList = new ArrayList(sortedAggregate.size());
		aList.addAll(sortedAggregate);

		return Collections.unmodifiableList(aList);
	}

	/**
	 * Make a HashSet of all the objects in the warehouse.
	 *
	 * @return  a HashSet containing all the objects
	 */
	private HashSet makeAggregateSet()
	{
		final HashSet aggregate = new HashSet();

		for (Iterator it = categoryMap.values().iterator(); it.hasNext();)
		{
			aggregate.addAll(((HashMap) it.next()).values());
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
		final HashMap objMap = (HashMap) categoryMap.get(aCategory);

		// nothing in this category?
		if (objMap == null)
		{
			return false;
		}

		final Categorisable aCatObj = (Categorisable) objMap.remove(aKey);

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
		final Iterator it = getNameIterator(aCategory);

		// nothing in this category?
		if (it == null)
		{
			return false;
		}

		String strippedName = AbilityUtilities.removeChoicesFromName(aName);

		while (it.hasNext())
		{
			final Categorisable itCatObj = (Categorisable) it.next();

			if (
			    itCatObj.getName().equalsIgnoreCase(aName) ||
			    itCatObj.getName().equalsIgnoreCase(strippedName))
			{
				final String        aKey    = itCatObj.getKeyName();
				final HashMap       objMap  = (HashMap) categoryMap.get(aCategory);
				final Categorisable aCatObj = (Categorisable) objMap.remove(aKey);

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
		Iterator it   = categoryMap.values().iterator();

		while (it.hasNext())
		{
			size = size + ((HashMap) it.next()).size();
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
