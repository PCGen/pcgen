/*
 * WeaponProfDataStore.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on April 21, 2001, 2:15 PM
 *
 * $Id$
 */
package pcgen.core;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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
public class WeaponProfDataStore
{
	private PObjectDataStore store = new PObjectDataStore("WeaponProf");

	/**
	 * Gets all weapons proficiencies of a particular type.
	 * 
	 * @see 		PObjectDataStore#getAllOfType(String)
	 * @param type	Type of proficency to get.
	 * @return		A collection of proficiencies.
	 */
	public final Collection getAllOfType(final String type)
	{
		return store.getAllOfType(type);
	}

	/**
	 * Retrieve a list of the types in the data store.
	 * 
	 * @see 	PObjectDataStore#getTypes()
	 * @return	Set of type names (as Strings)
	 */
	public Set getTypes()
	{
		return store.getTypes();
	}

	/**
	 * Get a sorted list of the contents.
	 *
	 * @see 	PObjectDataStore#getArrayCopy()
	 * @return	List of weapon proficiencies, sorted by name.
	 */
	public final List getArrayCopy()
	{
		return store.getArrayCopy();
	}

	/**
	 * Retrieve proficiency by a key.  This is a caseless compare.
	 *
	 * @see			PObjectDataStore#getKeyed(String)
	 * @param aKey	Key to seek.  This will be compared in uppercase.
	 * @return 		WeaponProf satisfying caseless equals with the key
	 */
	public final WeaponProf getKeyed(final String aKey)
	{
		return (WeaponProf) store.getKeyed(aKey);
	}

	/**
	 * Searches for an exact name match
	 * 
	 * @see			PObjectDataStore#getNamed(String)
	 * @param name	Name to seek.  Compare is caseless.
	 * @return 		WeaponProf matching the name.
	 */
	public final WeaponProf getNamed(final String name)
	{
		return (WeaponProf) store.getNamed(name);
	}

	/**
	 * Retrieve the names from the list, with optional delimiters.
	 * 
	 * @see						PObjectDataStore#getNames(String, boolean)
	 * @param delim				The delimiter to seperate the items.
	 * @param addArrayMarkers	If true, will add [ and ] to the output.
	 * @return 					String of names.
	 */
	public final String getNames(final String delim, final boolean addArrayMarkers)
	{
		return store.getNames(delim, addArrayMarkers);
	}

	/**
	 * Add a weapon proficiency to the list
	 * 
	 * @see			PObjectDataStore#add(PObject)
	 * @param wp	Weapon proficiency to add.
	 */
	public final void add(final PObject wp)
	{
		store.add(wp);
	}

	/**
	 * Add any items to this array which are not already there.
	 * 
	 * @see			PObjectDataStore#addUniqueAsStringTo(List)
	 * @param dest	Array to be augmented by the WeaponProfs.
	 */
	public final void addUniqueAsStringTo(final List dest)
	{
		store.addUniqueAsStringTo(dest);
	}

	/**
	 * Clear the data store.
	 * 
	 * @see	PObjectDataStore#clear()
	 */
	public final void clear()
	{
		store.clear();
	}

	/**
	 * Return true if any PObject in this list whose name is
	 * in collectionOfNames has a variable with the desired name
	 * 
	 * @see 					PObjectDataStore#hasVariableNamed(Collection, String)
	 * @param collectionOfNames	Collection of names to seek in the list.
	 * @param variableString	Variable to seek in the list.
	 * @return 					True if any PObject in this list whose name is
	 *        					in collectionOfNames has a variable with the desired name.
	 */
	public final boolean hasVariableNamed(final Collection collectionOfNames, final String variableString)
	{
		return store.hasVariableNamed(collectionOfNames, variableString);
	}

	/**
	 * Remove the object with the stated (compared without case) name.
	 *
	 * @see 		PObjectDataStore#removeNamed(String)
	 * @param name	Name to be looked up.
	 */
	public final void removeNamed(final String name)
	{
		store.removeNamed(name);
	}

	/**
	 * Get the number of elements contained.
	 *
	 * @see		PObjectDataStore#size()
	 * @return	The number of PObjects in the keyed lists.
	 */
	public final int size()
	{
		return store.size();
	}
}
