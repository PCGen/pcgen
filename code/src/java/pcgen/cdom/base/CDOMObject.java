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
import pcgen.core.Description;
import pcgen.core.PlayerCharacter;
import pcgen.core.bonus.BonusObj;

public abstract class CDOMObject extends ConcretePrereqObject implements
		Cloneable
{

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

	public final boolean containsKey(IntegerKey arg0)
	{
		return integerChar.containsKey(arg0);
	}

	public final Integer get(IntegerKey arg0)
	{
		return integerChar.get(arg0);
	}

	public final int getSafe(IntegerKey arg0)
	{
		Integer ic = integerChar.get(arg0);
		return ic == null ? arg0.getDefault() : ic;
	}

	public final Integer put(IntegerKey arg0, Integer arg1)
	{
		return integerChar.put(arg0, arg1);
	}

	public final Integer remove(IntegerKey arg0)
	{
		return integerChar.remove(arg0);
	}

	public final Set<IntegerKey> getIntegerKeys()
	{
		return new HashSet<IntegerKey>(integerChar.keySet());
	}

	public final boolean containsKey(StringKey arg0)
	{
		return stringChar.containsKey(arg0);
	}

	public final String get(StringKey arg0)
	{
		return stringChar.get(arg0);
	}

	public final String getSafe(StringKey arg0)
	{
		String str = stringChar.get(arg0);
		return str == null ? "" : str;
	}

	public final String put(StringKey arg0, String arg1)
	{
		return stringChar.put(arg0, arg1);
	}

	public final String remove(StringKey arg0)
	{
		return stringChar.remove(arg0);
	}

	public final Set<StringKey> getStringKeys()
	{
		return new HashSet<StringKey>(stringChar.keySet());
	}

	public final boolean containsKey(FormulaKey arg0)
	{
		return formulaChar.containsKey(arg0);
	}

	public final Formula get(FormulaKey arg0)
	{
		return formulaChar.get(arg0);
	}

	public final Formula getSafe(FormulaKey fk)
	{
		Formula f = get(fk);
		return f == null ? fk.getDefault() : f;
	}

	public final Formula put(FormulaKey arg0, Formula arg1)
	{
		return formulaChar.put(arg0, arg1);
	}

	public final Formula remove(FormulaKey arg0)
	{
		return formulaChar.remove(arg0);
	}

	public final Set<FormulaKey> getFormulaKeys()
	{
		return new HashSet<FormulaKey>(formulaChar.keySet());
	}

	public final boolean containsKey(VariableKey arg0)
	{
		return variableChar.containsKey(arg0);
	}

	public final Formula get(VariableKey arg0)
	{
		return variableChar.get(arg0);
	}

	public final Set<VariableKey> getVariableKeys()
	{
		return new HashSet<VariableKey>(variableChar.keySet());
	}

	public final Formula put(VariableKey arg0, Formula arg1)
	{
		return variableChar.put(arg0, arg1);
	}

	public final Formula remove(VariableKey arg0)
	{
		return variableChar.remove(arg0);
	}

	public final void removeAllVariables()
	{
		variableChar.clear();
	}

	public final boolean containsKey(ObjectKey<?> arg0)
	{
		return objectChar.containsKey(arg0);
	}

	public final <OT> OT get(ObjectKey<OT> arg0)
	{
		return arg0.cast(objectChar.get(arg0));
	}

	public final <OT> OT getSafe(ObjectKey<OT> arg0)
	{
		OT obj = arg0.cast(objectChar.get(arg0));
		return obj == null ? arg0.getDefault() : obj;
	}

	public final <OT> OT put(ObjectKey<OT> arg0, OT arg1)
	{
		return arg0.cast(objectChar.put(arg0, arg1));
	}

	public final <OT> OT remove(ObjectKey<OT> arg0)
	{
		return arg0.cast(objectChar.remove(arg0));
	}

	public final Set<ObjectKey<?>> getObjectKeys()
	{
		return new HashSet<ObjectKey<?>>(objectChar.keySet());
	}

	public final boolean containsListFor(ListKey<?> key)
	{
		return listChar.containsListFor(key);
	}

	public final <T> void addToListFor(ListKey<T> key, T value)
	{
		listChar.addToListFor(key, value);
	}

	public final <T> void addAllToListFor(ListKey<T> key, Collection<T> value)
	{
		listChar.addAllToListFor(key, value);
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

	public final int getSizeOfListFor(ListKey<?> key)
	{
		return listChar.sizeOfListFor(key);
	}

	public final int getSafeSizeOfListFor(ListKey<?> key)
	{
		return listChar.containsListFor(key) ? listChar.sizeOfListFor(key) : 0;
	}

	public final <T> boolean containsInList(ListKey<T> key, T value)
	{
		return listChar.containsInList(key, value);
	}

	public final <T> T getElementInList(ListKey<T> key, int i)
	{
		return listChar.getElementInList(key, i);
	}

	public final <T> List<T> removeListFor(ListKey<T> key)
	{
		return listChar.removeListFor(key);
	}

	public final <T> boolean removeFromListFor(ListKey<T> key, T obj)
	{
		return listChar.removeFromListFor(key, obj);
	}

	public final Set<ListKey<?>> getListKeys()
	{
		return listChar.getKeySet();
	}

	// ===== MapKeyMap Methods =====
	
	/**
	 * Add a value to the map of maps.
	 * 
	 * @param mk The MapKey we are adding an entry to
	 * @param key The key to assign against
	 * @param value The value to be stored.
	 */
	public final <K, V> void addToMapFor(MapKey<K, V> mk, K key, V value)
	{
		mapChar.addToMapFor(mk, key, value);
	}

	/**
	 * Remove a value from the map of maps.
	 * 
	 * @param mk The MapKey we are removing an entry from
	 * @param key The key to eject
	 */
	public final <K, V> void removeFromMapFor(MapKey<K, V> mk, K key)
	{
		mapChar.removeFromMapFor(mk, key);
	}

	/**
	 * Remove a map from the map of maps.
	 * 
	 * @param mk The MapKey we are removing
	 */
	public final <K, V> void removeMapFor(MapKey<K, V> mk)
	{
		mapChar.removeMapFor(mk);
	}

	/**
	 * Retrieve the map of keys and values for the MapKey.
	 * 
	 * @param mk The MapKey we are retrieving
	 * @return The map of keys and values.
	 */
	public final <K, V> Map<K, V> getMapFor(MapKey<K, V> mk)
	{
		return mapChar.getMapFor(mk);
	}

	/**
	 * Retrieve the set of keys for the MapKey.
	 * 
	 * @param mk The MapKey we are retrieving
	 * @return The set of keys.
	 */
	public final <K, V> Set<K> getKeysFor(MapKey<K, V> mk)
	{
		return mapChar.getKeysFor(mk);
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
	public final <K, V> V get(MapKey<K, V> mk, K key2)
	{
		return mapChar.get(mk, key2);
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
	public final <K, V> boolean removeFromMap(MapKey<K, V> mk, K key2)
	{
		return mapChar.removeFromMapFor(mk, key2);
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

	public final int getSafeSizeOfMapFor(MapKey<?, ?> key)
	{
		return mapChar.containsMapFor(key) ? mapChar.getKeysFor(key).size() : 0;
	}

	public String getDisplayName()
	{
		return this.get(StringKey.NAME);
	}

	public void setName(String name)
	{
		this.put(StringKey.NAME, name);
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
			CDOMReference<? extends CDOMList<? extends PrereqObject>> list,
			CDOMReference<T> granted, AssociatedPrereqObject associations)
	{
		cdomListMods.addToListFor(list, granted, associations);
	}

	public final <T extends PrereqObject> void removeFromList(
			CDOMReference<? extends CDOMList<? extends PrereqObject>> list,
			CDOMReference<T> granted)
	{
		cdomListMods.removeListFor(list, granted);
	}

	public final boolean hasListMods(
			CDOMReference<? extends CDOMList<? extends PrereqObject>> list)
	{
		return cdomListMods.containsListFor(list);
	}

	// TODO Is there a way to get type safety here?
	public final <BT extends PrereqObject> Collection<CDOMReference<BT>> getListMods(
			CDOMReference<? extends CDOMList<BT>> list)
	{
		Set set = cdomListMods.getSecondaryKeySet(list);
		if (set == null || set.isEmpty())
		{
			return null;
		}
		return set;
	}

	public final <BT extends CDOMObject> Collection<CDOMReference<BT>> getSafeListMods(
			CDOMReference<? extends CDOMList<BT>> list)
	{
		Collection<CDOMReference<BT>> set = getListMods(list);
		if (set == null)
		{
			return Collections.emptySet();
		}
		return set;
	}

	public final Collection<AssociatedPrereqObject> getListAssociations(
			CDOMReference<? extends CDOMList<? extends PrereqObject>> list,
			CDOMReference<?> key)
	{
		return cdomListMods.getListFor(list, key);
	}

	/**
	 * @return A list of references to the global lists that this CDOM Object has modified
	 */
	public final Collection<CDOMReference<? extends CDOMList<? extends PrereqObject>>> getModifiedLists()
	{
		return cdomListMods.getKeySet();
	}

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

	public void removeAllFromList(CDOMReference<? extends CDOMList<?>> swl)
	{
		cdomListMods.removeListsFor(swl);
	}

	public abstract boolean isType(String str);

	public <T extends CDOMObject> boolean hasObjectOnList(
			CDOMReference<? extends CDOMList<T>> list, T obj)
	{
		if (obj == null)
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
			if (ref.contains(obj))
			{
				return true;
			}
		}
		return false;
	}

	public final void addAdds(final PlayerCharacter aPC)
	{
		List<PersistentTransitionChoice<?>> addList = getListFor(ListKey.ADD);
		if (addList != null)
		{
			for (PersistentTransitionChoice<?> tc : addList)
			{
				driveChoice(tc, aPC);
			}
		}
	}

	public final void removeAdds(final PlayerCharacter aPC)
	{
		List<PersistentTransitionChoice<?>> addList = getListFor(ListKey.ADD);
		if (addList != null)
		{
			for (PersistentTransitionChoice<?> tc : addList)
			{
				tc.remove(this, aPC);
			}
		}
	}

	public final void checkRemovals(final PlayerCharacter aPC)
	{
		List<PersistentTransitionChoice<?>> removeList = getListFor(ListKey.REMOVE);
		if (removeList != null)
		{
			for (PersistentTransitionChoice<?> tc : removeList)
			{
				driveChoice(tc, aPC);
			}
		}
	}

	private <T> void driveChoice(TransitionChoice<T> tc, final PlayerCharacter aPC)
	{
		tc.act(tc.driveChoice(aPC), this, aPC);
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
				bonus.setCreatorObject(owner);
			}
		}
	}

	/**
	 * Hopefully this is a temporary import - thpr Oct 11, 2008
	 */
	public String bonusStringPrefix()
	{
		return "";
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
	
}
