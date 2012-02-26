/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.base;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.base.util.DoubleKeyMapToList;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.util.ListKeyMapToList;
import pcgen.cdom.util.MapKeyMap;
import pcgen.core.AssociationStore;
import pcgen.core.Description;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.BonusActivation;
import pcgen.core.bonus.BonusObj;
import pcgen.core.facade.TempBonusFacade;

public abstract class CDOMObject extends ConcretePrereqObject implements
		Cloneable, BonusContainer, Loadable, TempBonusFacade
{

	private URI sourceURI = null;
	
	private String displayName = Constants.EMPTY_STRING;

	/*
	 * CONSIDER This should be a NumberMap - not Integer, but allow Double as
	 * well, in one HashMap... this will control the size of CDOMObject.
	 */
	/** A map to hold items keyed by Integers for the object */
	// TODO make this final once clone() is no longer required...
	private Map<IntegerKey, Integer> integerChar = new HashMap<IntegerKey, Integer>();

	/** A map to hold items keyed by Strings for the object */
	// TODO make this final once clone() is no longer required...
	private Map<StringKey, String> stringChar = new HashMap<StringKey, String>();

	/** A map to hold items keyed by Strings for the object */
	// TODO make this final once clone() is no longer required...
	private Map<FormulaKey, Formula> formulaChar = new HashMap<FormulaKey, Formula>();

	/** A map to hold items keyed by Strings for the object */
	// TODO make this final once clone() is no longer required...
	private Map<VariableKey, Formula> variableChar = new HashMap<VariableKey, Formula>();

	/** A map to hold items keyed by Strings for the object */
	// TODO make this final once clone() is no longer required...
	private Map<ObjectKey<?>, Object> objectChar = new HashMap<ObjectKey<?>, Object>();

	/** A map of Lists for the object */
	// TODO make this final once clone() is no longer required...
	// TODO Make this private once PObject is cleaned up
	protected ListKeyMapToList listChar = new ListKeyMapToList();

	/** A map of Maps for the object */
	// TODO make this final once clone() is no longer required...
	private MapKeyMap mapChar = new MapKeyMap();
	
	// TODO make this final once clone() is no longer required...
	/*
	 * CONSIDER This is currently order enforcing the reference fetching to
	 * match the integration tests that we perform, and their current behavior.
	 * Not sure if this is really tbe best solution?
	 */
	private DoubleKeyMapToList<CDOMReference<? extends CDOMList<? extends PrereqObject>>, CDOMReference<?>, AssociatedPrereqObject> cdomListMods = new DoubleKeyMapToList<CDOMReference<? extends CDOMList<? extends PrereqObject>>, CDOMReference<?>, AssociatedPrereqObject>(HashMap.class, LinkedHashMap.class);

	public final boolean containsKey(IntegerKey key)
	{
		return integerChar.containsKey(key);
	}

	public final Integer get(IntegerKey key)
	{
		return integerChar.get(key);
	}

	public final int getSafe(IntegerKey key)
	{
		Integer intValue = integerChar.get(key);
		return intValue == null ? key.getDefault() : intValue;
	}

	public final Integer put(IntegerKey key, Integer intValue)
	{
		return integerChar.put(key, intValue);
	}

	public final Integer remove(IntegerKey key)
	{
		return integerChar.remove(key);
	}

	public final Set<IntegerKey> getIntegerKeys()
	{
		return new HashSet<IntegerKey>(integerChar.keySet());
	}

	public final boolean containsKey(StringKey key)
	{
		return stringChar.containsKey(key);
	}

	public final String get(StringKey key)
	{
		return stringChar.get(key);
	}

	public final String getSafe(StringKey key)
	{
		String str = stringChar.get(key);
		return str == null ? "" : str;
	}

	public final String put(StringKey key, String value)
	{
		return stringChar.put(key, value);
	}

	public final String remove(StringKey key)
	{
		return stringChar.remove(key);
	}

	public final Set<StringKey> getStringKeys()
	{
		return new HashSet<StringKey>(stringChar.keySet());
	}

	public final boolean containsKey(FormulaKey key)
	{
		return formulaChar.containsKey(key);
	}

	public final Formula get(FormulaKey key)
	{
		return formulaChar.get(key);
	}

	public final Formula getSafe(FormulaKey key)
	{
		Formula formula = get(key);
		return formula == null ? key.getDefault() : formula;
	}

	public final Formula put(FormulaKey key, Formula value)
	{
		return formulaChar.put(key, value);
	}

	public final Formula remove(FormulaKey key)
	{
		return formulaChar.remove(key);
	}

	public final Set<FormulaKey> getFormulaKeys()
	{
		return new HashSet<FormulaKey>(formulaChar.keySet());
	}

	public final boolean containsKey(VariableKey key)
	{
		return variableChar.containsKey(key);
	}

	public final Formula get(VariableKey key)
	{
		return variableChar.get(key);
	}

	public final Set<VariableKey> getVariableKeys()
	{
		return new HashSet<VariableKey>(variableChar.keySet());
	}

	public final Formula put(VariableKey key, Formula value)
	{
		return variableChar.put(key, value);
	}

	public final Formula remove(VariableKey key)
	{
		return variableChar.remove(key);
	}

	public final void removeAllVariables()
	{
		variableChar.clear();
	}

	public final boolean containsKey(ObjectKey<?> key)
	{
		return objectChar.containsKey(key);
	}

	public final <OT> OT get(ObjectKey<OT> key)
	{
		return key.cast(objectChar.get(key));
	}

	public final <OT> OT getSafe(ObjectKey<OT> key)
	{
		OT obj = key.cast(objectChar.get(key));
		return obj == null ? key.getDefault() : obj;
	}

	public final <OT> OT put(ObjectKey<OT> key, OT value)
	{
		return key.cast(objectChar.put(key, value));
	}

	public final <OT> OT remove(ObjectKey<OT> key)
	{
		return key.cast(objectChar.remove(key));
	}

	public final Set<ObjectKey<?>> getObjectKeys()
	{
		return new HashSet<ObjectKey<?>>(objectChar.keySet());
	}

	public final boolean containsListFor(ListKey<?> key)
	{
		return listChar.containsListFor(key);
	}

	public final <T> void addToListFor(ListKey<T> key, T element)
	{
		listChar.addToListFor(key, element);
	}

	public final <T> void addAllToListFor(ListKey<T> key, Collection<T> elementCollection)
	{
		listChar.addAllToListFor(key, elementCollection);
	}

	public final <T> List<T> getListFor(ListKey<T> key)
	{
		return listChar.getListFor(key);
	}

	public final <T> List<T> getSafeListFor(ListKey<T> key)
	{
		return listChar.containsListFor(key) ? listChar.getListFor(key)
				: new ArrayList<T>();
	}
	
	public final String getListAsString(ListKey<?> key)
	{
		return StringUtil.join(getListFor(key), ", ");
	}

	public final int getSizeOfListFor(ListKey<?> key)
	{
		return listChar.sizeOfListFor(key);
	}

	public final int getSafeSizeOfListFor(ListKey<?> key)
	{
		return listChar.containsListFor(key) ? listChar.sizeOfListFor(key) : 0;
	}

	public final <T> boolean containsInList(ListKey<T> key, T element)
	{
		return listChar.containsInList(key, element);
	}

	public final <T> boolean containsAnyInList(ListKey<T> key, Collection<T> elementCollection)
	{
		return listChar.containsAnyInList(key, elementCollection);
	}

	public final <T> T getElementInList(ListKey<T> key, int index)
	{
		return listChar.getElementInList(key, index);
	}

	public final <T> List<T> removeListFor(ListKey<T> key)
	{
		return listChar.removeListFor(key);
	}

	public final <T> boolean removeFromListFor(ListKey<T> key, T element)
	{
		return listChar.removeFromListFor(key, element);
	}

	public final Set<ListKey<?>> getListKeys()
	{
		return listChar.getKeySet();
	}

	// ===== MapKeyMap Methods =====
	
	/**
	 * Add a value to the map of maps.
	 * 
	 * @param mapKey The MapKey we are adding an entry to
	 * @param key The key to assign against
	 * @param value The value to be stored.
	 */
	public final <K, V> V addToMapFor(MapKey<K, V> mapKey, K key, V value)
	{
		return mapChar.addToMapFor(mapKey, key, value);
	}

	/**
	 * Remove a value from the map of maps.
	 * 
	 * @param mapKey The MapKey we are removing an entry from
	 * @param key The key to eject
	 */
	public final <K, V> void removeFromMapFor(MapKey<K, V> mapKey, K key)
	{
		mapChar.removeFromMapFor(mapKey, key);
	}

	/**
	 * Remove a map from the map of maps.
	 * 
	 * @param mapKey The MapKey we are removing
	 */
	public final <K, V> void removeMapFor(MapKey<K, V> mapKey)
	{
		mapChar.removeMapFor(mapKey);
	}

	/**
	 * Retrieve the map of keys and values for the MapKey.
	 * 
	 * @param mapKey The MapKey we are retrieving
	 * @return The map of keys and values.
	 */
	public final <K, V> Map<K, V> getMapFor(MapKey<K, V> mapKey)
	{
		return mapChar.getMapFor(mapKey);
	}

	/**
	 * Retrieve the set of keys for the MapKey.
	 * 
	 * @param mk The MapKey we are retrieving
	 * @return The set of keys.
	 */
	public final <K, V> Set<K> getKeysFor(MapKey<K, V> mapKey)
	{
		return mapChar.getKeysFor(mapKey);
	}

	/**
	 * Get the value for the given MapKey and secondary key. If there is 
	 * not a mapping for the given keys, null is returned.
	 * 
	 * @param mk
	 *            The MapKey for retrieving the given value
	 * @param key2
	 *            The secondary key for retrieving the given value
	 * @return Object The value stored for the given keys
	 */
	public final <K, V> V get(MapKey<K, V> mapKey, K key2)
	{
		return mapChar.get(mapKey, key2);
	}

	/**
	 * Remove the value associated with the primary and secondary keys 
	 * from the map.
	 *  
	 * @param mk The MapKey of the entry we are removing
	 * @param key2 The secondary key of the entry we are removing
	 * @return true if the key and its associated value were successfully removed 
	 *         from the map; false otherwise
	 */
	public final <K, V> boolean removeFromMap(MapKey<K, V> mapKey, K key2)
	{
		return mapChar.removeFromMapFor(mapKey, key2);
	}

	/**
	 * Retrieve the set of mapkeys held.
	 * 
	 * @return The set of mapkeys.
	 */
	public final Set<MapKey<?, ?>> getMapKeys()
	{
		return mapChar.getKeySet();
	}
	
	@Override
	public String getKeyName()
	{
		// FIXME TODO Patched for now to avoid NPEs, but this is wrong
		// TODO Auto-generated method stub
		String returnKey = this.get(StringKey.KEY_NAME);
		if (returnKey == null)
		{
			returnKey = this.getDisplayName();
			//returnKey = this.get(StringKey.NAME);
		}
		return returnKey;
	}

	public void setKeyName(String key)
	{
		put(StringKey.KEY_NAME, key);
	}

	public final int getSafeSizeOfMapFor(MapKey<?, ?> mapKey)
	{
		return mapChar.containsMapFor(mapKey) ? mapChar.getKeysFor(mapKey).size() : 0;
	}

	@Override
	public void setName(String name)
	{
		displayName = name;
	}

	public boolean isCDOMEqual(CDOMObject cdo)
	{
		if (cdo == this)
		{
			return true;
		}
		if (!equalsPrereqObject(cdo))
		{
			return false;
		}
		if (!integerChar.equals(cdo.integerChar))
		{
			// System.err.println("CDOM Inequality Integer");
			// System.err.println(integerChar + " " + cdo.integerChar);
			return false;
		}
		if (!stringChar.equals(cdo.stringChar))
		{
			// System.err.println("CDOM Inequality String");
			// System.err.println(stringChar + " " + cdo.stringChar);
			return false;
		}
		if (!formulaChar.equals(cdo.formulaChar))
		{
			// System.err.println("CDOM Inequality Formula");
			// System.err.println(formulaChar + " " + cdo.formulaChar);
			return false;
		}
		if (!variableChar.equals(cdo.variableChar))
		{
			// System.err.println("CDOM Inequality Variable");
			// System.err.println(variableChar + " " + cdo.variableChar);
			return false;
		}
		if (!objectChar.equals(cdo.objectChar))
		{
			// System.err.println("CDOM Inequality Object");
			// System.err.println(objectChar + " " + cdo.objectChar);
			return false;
		}
		if (!listChar.equals(cdo.listChar))
		{
//			 System.err.println("CDOM Inequality List");
//			 System.err.println(listChar + " " + cdo.listChar);
//			 System.err.println(listChar.getKeySet() + " "
//			 + cdo.listChar.getKeySet());
			return false;
		}
		if (!mapChar.equals(cdo.mapChar))
		{
			return false;
		}
		if (!cdomListMods.equals(cdo.cdomListMods))
		{
			// System.err.println("CDOM Inequality ListMods");
			// System.err.println(cdomListMods + " " + cdo.cdomListMods);
			// System.err.println(cdomListMods.getKeySet() + " "
			// + cdo.cdomListMods.getKeySet());
			// for (CDOMReference<? extends CDOMList<? extends PrereqObject>>
			// key : cdomListMods
			// .getKeySet())
			// {
			// System.err.println(cdomListMods.getSecondaryKeySet(key));
			// System.err.println(cdo.cdomListMods.getSecondaryKeySet(key));
			// }
			return false;
		}
		return true;
	}

	public final <T extends PrereqObject> void putToList(
			CDOMReference<? extends CDOMList<? extends PrereqObject>> listRef,
			CDOMReference<T> granted, AssociatedPrereqObject associations)
	{
		cdomListMods.addToListFor(listRef, granted, associations);
	}

	public final <T extends PrereqObject> void removeFromList(
			CDOMReference<? extends CDOMList<? extends PrereqObject>> listRef,
			CDOMReference<T> granted)
	{
		cdomListMods.removeListFor(listRef, granted);
	}

	public final boolean hasListMods(
			CDOMReference<? extends CDOMList<? extends PrereqObject>> listRef)
	{
		return cdomListMods.containsListFor(listRef);
	}

	// TODO Is there a way to get type safety here?
	public final <BT extends PrereqObject> Collection<CDOMReference<BT>> getListMods(
			CDOMReference<? extends CDOMList<BT>> listRef)
	{
		Set set = cdomListMods.getSecondaryKeySet(listRef);
		if (set == null || set.isEmpty())
		{
			return null;
		}
		return set;
	}

	public final <BT extends CDOMObject> Collection<CDOMReference<BT>> getSafeListMods(
			CDOMReference<? extends CDOMList<BT>> listRef)
	{
		Collection<CDOMReference<BT>> set = getListMods(listRef);
		if (set == null)
		{
			return Collections.emptySet();
		}
		return set;
	}

	public final Collection<AssociatedPrereqObject> getListAssociations(
			CDOMReference<? extends CDOMList<? extends PrereqObject>> listRef,
			CDOMReference<?> key)
	{
		return cdomListMods.getListFor(listRef, key);
	}

	/**
	 * @return A list of references to the global lists that this CDOM Object has modified
	 */
	public final Collection<CDOMReference<? extends CDOMList<? extends PrereqObject>>> getModifiedLists()
	{
		return cdomListMods.getKeySet();
	}

	@Override
	public final String getLSTformat()
	{
		String abb = get(StringKey.ABB);
		if (abb == null)
		{
			return getKeyName();
		}
		return abb;
	}

	public final void overlayCDOMObject(CDOMObject cdo)
	{
		addAllPrerequisites(cdo.getPrerequisiteList());
		integerChar.putAll(cdo.integerChar);
		stringChar.putAll(cdo.stringChar);
		formulaChar.putAll(cdo.formulaChar);
		objectChar.putAll(cdo.objectChar);
		variableChar.putAll(cdo.variableChar);
		listChar.addAllLists(cdo.listChar);
		mapChar.putAll(cdo.mapChar);
		cdomListMods.addAll(cdo.cdomListMods);
	}

	@Override
	public CDOMObject clone() throws CloneNotSupportedException
	{
		CDOMObject clone = (CDOMObject) super.clone();
		clone.integerChar = new HashMap<IntegerKey, Integer>(integerChar);
		clone.stringChar = new HashMap<StringKey, String>(stringChar);
		clone.formulaChar = new HashMap<FormulaKey, Formula>(formulaChar);
		clone.variableChar = new HashMap<VariableKey, Formula>(variableChar);
		clone.objectChar = new HashMap<ObjectKey<?>, Object>(objectChar);
		clone.listChar = new ListKeyMapToList();
		clone.listChar.addAllLists(listChar);
		clone.mapChar = new MapKeyMap();
		clone.mapChar.putAll(mapChar);
		clone.cdomListMods = cdomListMods.clone();
		clone.ownBonuses(clone);
		return clone;
	}

	public void removeAllFromList(CDOMReference<? extends CDOMList<?>> listRef)
	{
		cdomListMods.removeListsFor(listRef);
	}

	@Override
	public abstract boolean isType(String type);

	public <T extends CDOMObject> boolean hasObjectOnList(
			CDOMReference<? extends CDOMList<T>> list, T element)
	{
		if (element == null)
		{
			return false;
		}
		Collection<CDOMReference<T>> references = getListMods(list);
		if (references == null)
		{
			return false;
		}
		for (CDOMReference<T> ref : references)
		{
			if (ref.contains(element))
			{
				return true;
			}
		}
		return false;
	}

	public ListKey<Description> getDescriptionKey()
	{
		return ListKey.DESCRIPTION;
	}

	/**
	 * Set's all the BonusObj's to this creator
	 * 
	 * Hopefully this is a temporary import - thpr Oct 9, 2008
	 * @throws CloneNotSupportedException 
	 */
	public void ownBonuses(Object owner) throws CloneNotSupportedException
	{
		List<BonusObj> bonusList = getListFor(ListKey.BONUS);
		if (bonusList != null)
		{
			removeListFor(ListKey.BONUS);
			for (BonusObj orig : bonusList)
			{
				BonusObj bonus = orig.clone();
				addToListFor(ListKey.BONUS, bonus);
			}
		}
	}

	/**
	 * Hopefully this is a temporary import - thpr Oct 11, 2008
	 * 
	 * Return the qualified key, ususally used as the source in a 
	 * getVariableValue call. Always returns an empty string, but 
	 * may be overridden by subclasses to return a required value.
	 * 
	 * @return The qualified name of the object
	 */
	public String getQualifiedKey()
	{
		return Constants.EMPTY_STRING;
	}
	
	/**
	 * Get the list of bonuses for this object
	 * @param as TODO
	 * @return the list of bonuses for this object
	 */
	public List<BonusObj> getRawBonusList(PlayerCharacter pc)
	{
		List<BonusObj> bonusList = getSafeListFor(ListKey.BONUS);
		if (pc != null)
		{
			bonusList.addAll(pc.getAddedBonusList(this));
		}
		return bonusList;
	}

	/**
	 * returns all BonusObj's that are "active"
	 * @param pc A PlayerCharacter object.
	 * @return active bonuses
	 */
	@Override
	public List<BonusObj> getActiveBonuses(final PlayerCharacter pc)
	{
		final List<BonusObj> aList = new ArrayList<BonusObj>();

		for ( BonusObj bonus : getRawBonusList(pc) )
		{
			if (pc.isApplied(bonus))
			{
				aList.add(bonus);
			}
		}

		return aList;
	}

	public List<BonusObj> getBonusList(AssociationStore assocStore)
	{
		if (assocStore instanceof PlayerCharacter)
		{
			return getRawBonusList((PlayerCharacter) assocStore);
		}
		else
		{
			return getRawBonusList(null);
		}
	}

	/**
	 * Set the source file for this object
	 * @param sourceFile
	 */
	@Override
	public final void setSourceURI(URI source)
	{
		sourceURI = source;
	}

	/**
	 * Get the source file for this object
	 * @return the source file for this object
	 */
	@Override
	public final URI getSourceURI()
	{
		return sourceURI;
	}

	/**
	 * Get name
	 * @return name
	 */
	@Override
	public final String getDisplayName()
	{
		return displayName;
	}

	/**
	 * Sets all the BonusObj's to "active"
	 * @param pc
	 */
	@Override
	public void activateBonuses(final PlayerCharacter pc)
	{
		BonusActivation.activateBonuses(this, pc);
	}
	
	@Override
	public boolean isInternal()
	{
		return getSafe(ObjectKey.INTERNAL);
	}
}
