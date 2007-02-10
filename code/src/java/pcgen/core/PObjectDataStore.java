/*
 * PObjectDataStore.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * $Id$
 */
package pcgen.core;

import pcgen.util.Logging;

import java.util.*;

/**
 * Stores WeaponProfs in ways that make it easy to retrieve by name, caseless name
 * key, and type.  It assumes that the name, key, and type will not change - if
 * this assumption is violated, we will need an API to change the data store.<p>
 *
 * Consider PropertyChangeListener as an API for notifications.
 *
 * @author ???
 * @version $Revision$
 *
 */
public class PObjectDataStore<T extends PObject>
{
	private Map<String, T> byKey = new HashMap<String, T>(100);
	private Map<String, Map<String, T>> byType = new HashMap<String, Map<String, T>>(4);
	private Set<String> nameSet;
	private SortedMap<String, T> byUpperName = new TreeMap<String, T>();
	private String containedType;

	/** This should perhaps be protected, but I wanted composition to be allowed.
	 * @param inContainedType
	 */
	public PObjectDataStore(final String inContainedType)
	{
		containedType = inContainedType;
	}

	public Collection<T> getAll()
	{
		return byKey.values();
	}

	/**
	 * Get every entry for a given type.
	 *
	 * @param type value for the type field to be sought out.  Caseless compare.
	 * @return Collection of all values from the list that meet the requirements.
	 * TODO convert to an unmodifiable collection, so that we do not need the extra array copy.
	 */
	public Collection<T> getAllOfType(final String type)
	{
		final String upperType = type.toUpperCase();
		List<T> list = null;

		final Map<String, T> typedByName = byType.get(upperType);

		if (typedByName != null)
		{
			list = new ArrayList<T>(typedByName.values());
		}

		return list;
	}

	/**
	 * Retrieve a list of the types in the data store.
	 * @return Set of type names (as Strings)
	 */
	public Set<String> getTypes()
	{
		return Collections.unmodifiableSet(byType.keySet());
	}

	/**
	 * Get a sorted list of the contents.
	 *
	 * @return list of PObjects, sorted by name.
	 */
	public List<T> getArrayCopy()
	{
		return new ArrayList<T>(byUpperName.values());
	}

	/** Retrieve by the key.  This is a caseless compare.
	 *
	 * @param aKey key to seek.  This will be compared in uppercase.
	 * @return PObject satisfying caseless .equals with the key
	 */
	public T getKeyed(final String aKey)
	{
		return byKey.get(aKey.toUpperCase());
	}

	/**
	 * Find the variable with the requested name
	 *
	 * @param name Name to seek.  Compare is caseless.
	 * @return PObject meeting this requirement.
	 */
	public T getNamed(final String name)
	{
		return byUpperName.get(name.toUpperCase());
	}

	/**
	 * Retrieve the names from the list, with optional delimiters.
	 *
	 * @param delim the delimiter to seperate the items
	 * @param addArrayMarkers if true, will add [ and ] to the output
	 * @return objects in name order
	 */
	public String getNames(final String delim, final boolean addArrayMarkers)
	{
		final StringBuffer ret = new StringBuffer();
		boolean first = true;

		if (addArrayMarkers)
		{
			ret.append("[");
		}

		for (Iterator<T> ii = byUpperName.values().iterator(); ii.hasNext();)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				ret.append(delim);
			}

			final T object = ii.next();
			ret.append(object.getDisplayName());
		}

		if (addArrayMarkers)
		{
			ret.append("]");
		}

		return ret.toString();
	}

	/** Add a PObject to the list.
	 *
	 * We do not report an error if duplicate names or keys are sent.  This should be checked.
	 * @param obj object to add to the list.
	 * TODO if the object already exists in the list, it should be removed.
	 */
	public void add(final T obj)
	{
		final String key = obj.getKeyName().toUpperCase();
		byKey.put(key, obj);

		final String upperName = obj.getDisplayName().toUpperCase();
		byUpperName.put(upperName, obj);

		// We used to add all types together here.
		// Modifying to add each one seperately, so we can
		// treat correctly the Weapon Proficiency Types
		for (Iterator<String> e = obj.getTypeList(false).iterator(); e.hasNext();)
		{
			final String aString = e.next();
			Map<String, T> typedByName = byType.get(aString);

			if (typedByName == null)
			{
				typedByName = new TreeMap<String, T>();
				byType.put(aString, typedByName);
			}

			typedByName.put(upperName, obj);
		}

//		String upperType = obj.getType().toUpperCase();
//		SortedMap typedByName = (TreeMap) byType.get(upperType);
//		if (typedByName == null)
//		{
//			typedByName = new TreeMap();
//			byType.put(upperType, typedByName);
//		}
//		was = typedByName.put(upperName, obj);
		clearVariableNameCache();
	}

	/**
	 * Add any items to this array which are not already there.
	 *
	 * @param dest array to be augmented by the WeaponProfs
	 */
	public void addUniqueAsStringTo(final List<String> dest)
	{
		for (Iterator<T> ii = byUpperName.values().iterator(); ii.hasNext();)
		{
			final T object = ii.next();

			if (!dest.contains(object.toString()))
			{
				dest.add(object.toString());
			}
		}

		clearVariableNameCache();
	}

	/** Clear the data store.*/
	public void clear()
	{
		byKey.clear();
		byType.clear();
		byUpperName.clear();
		clearVariableNameCache();
	}

	/**
	 * Return true if any PObject in this list whose name is
	 * in collectionOfNames has a variable with the desired name
	 *
	 * @param subList collection of objects to seek in the list
	 * @param variableName variable to seek in the list
	 * @return true if any PObject in this list whose name is
	 *        in collectionOfNames has a variable with the desired name
	 */
	public boolean hasVariableNamed(final Collection<T> subList, final String variableName)
	{
		if (subList.isEmpty())
		{
			return false;
		}

		// See if _any_ variable in this list has the requested variable before checking the specific list
		if (!hasVariableNamed(variableName))
		{
			return false;
		}

		for (Iterator<T> e = subList.iterator(); e.hasNext();)
		{
			final T obj = e.next();

			if (obj == null)
			{
				// error or debugging? XXX
				Logging.debugPrint("No PObject for " + containedType + " " + obj.getDisplayName());

				continue;
			}

			if (obj.hasVariableNamed(variableName))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Remove the object with the stated (compared without case) name.
	 *
	 * @param name name to be looked up
	 * TODO change to a caseless lookup
	 */
	public void removeNamed(final String name)
	{
		final T object = getNamed(name);
		String upperName = object.getDisplayName().toUpperCase();
		byUpperName.remove(upperName);
		byKey.remove(object.getKeyName().toUpperCase());

		final Map<String, T> typedByName = byType.get(object.getType().toUpperCase());

		if (typedByName != null)
		{
			typedByName.remove(upperName);
		}

		clearVariableNameCache();
	}

	/**
	 * Get the number of elements contained.
	 *
	 * @return the number of PObjects in the keyed lists.
	 */
	public int size()
	{
		return byUpperName.size();
	}

	public Collection<T> retainAll(final Collection<T> other)
	{
		ArrayList<T> ret = new ArrayList<T>(this.size());
		for ( T entry : byKey.values() )
		{
			if ( other.contains(entry) )
			{
				ret.add( entry );
			}
		}
		return ret;
	}

	private final void cacheVariableNames()
	{
		nameSet = new HashSet<String>();

		for (Iterator<T> i = byUpperName.values().iterator(); i.hasNext();)
		{
			final T object = i.next();
			final Set<String> variableNames = object.getVariableNamesAsUnmodifiableSet();

			if (variableNames != null)
			{
				nameSet.addAll(variableNames);
			}
		}
	}

	private final void clearVariableNameCache()
	{
		nameSet = null;
	}

	public boolean hasVariableNamed(final String variableName)
	{
		final String upperName = variableName.toUpperCase();

		if (nameSet == null)
		{
			cacheVariableNames();
		}

		return nameSet.contains(upperName);
	}
}
