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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.Indirect;
import pcgen.base.util.MapToList;
import pcgen.base.util.ObjectContainer;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.enumeration.FactSetKey;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.util.FactSetKeyMapToList;
import pcgen.cdom.util.ListKeyMapToList;
import pcgen.cdom.util.MapKeyMap;
import pcgen.core.Description;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.BonusActivation;
import pcgen.core.bonus.BonusObj;

public abstract class CDOMObject extends ConcretePrereqObject implements
		Cloneable, BonusContainer, Loadable
{

	private URI sourceURI = null;
	
	private String displayName = Constants.EMPTY_STRING;

	/*
	 * CONSIDER This should be a NumberMap - not Integer, but allow Double as
	 * well, in one HashMap... this will control the size of CDOMObject.
	 */
	/** A map to hold items keyed by Integers for the object */
	// TODO make this final once clone() is no longer required...
	private Map<IntegerKey, Integer> integerChar = null;

	/** A map to hold items keyed by Strings for the object */
	// TODO make this final once clone() is no longer required...
	private Map<StringKey, String> stringChar = null;

	/** A map to hold items keyed by Strings for the object */
	// TODO make this final once clone() is no longer required...
	private Map<FormulaKey, Formula> formulaChar = null;

	/** A map to hold items keyed by Strings for the object */
	// TODO make this final once clone() is no longer required...
	private Map<VariableKey, Formula> variableChar = null;

	/** A map to hold items keyed by Strings for the object */
	// TODO make this final once clone() is no longer required...
	private Map<ObjectKey<?>, Object> objectChar = null;

	/** A map to hold items keyed by Strings for the object */
	// TODO make this final once clone() is no longer required...
	private Map<FactKey<?>, Object> factChar = null;

	/** A map to hold items keyed by Strings for the object */
	// TODO make this final once clone() is no longer required...
	private FactSetKeyMapToList factSetChar = null;

	/** A map of Lists for the object */
	// TODO make this final once clone() is no longer required...
	private ListKeyMapToList listChar = null;

	/** A map of Maps for the object */
	// TODO make this final once clone() is no longer required...
	private MapKeyMap mapChar = null;
	
	// TODO make this final once clone() is no longer required...
	/*
	 * CONSIDER This is currently order enforcing the reference fetching to
	 * match the integration tests that we perform, and their current behavior.
	 * Not sure if this is really the best solution?
	 */
	private DoubleKeyMapToList<CDOMReference<? extends CDOMList<? extends PrereqObject>>, CDOMReference<?>, AssociatedPrereqObject> cdomListMods =
			null;

	public final boolean containsKey(IntegerKey key)
	{
		return integerChar == null ? false : integerChar.containsKey(key);
	}

	public final Integer get(IntegerKey key)
	{
		return integerChar == null ? null : integerChar.get(key);
	}

	public final int getSafe(IntegerKey key)
	{
		Integer intValue = integerChar == null ? null : integerChar.get(key);
		return intValue == null ? key.getDefault() : intValue.intValue();
	}

	public final Integer put(IntegerKey key, Integer intValue)
	{
		if (integerChar == null)
		{
			integerChar = new HashMap<IntegerKey, Integer>();
		}
		return integerChar.put(key, intValue);
	}

	public final Integer remove(IntegerKey key)
	{
		Integer out = integerChar == null ? null : integerChar.remove(key);
		if (out != null && integerChar.isEmpty())
		{
			integerChar = null;
		}
		return out;
	}

	public final Set<IntegerKey> getIntegerKeys()
	{
		return integerChar == null ? Collections.<IntegerKey> emptySet()
			: new HashSet<IntegerKey>(integerChar.keySet());
	}

	public final boolean containsKey(StringKey key)
	{
		return stringChar == null ? false : stringChar.containsKey(key);
	}

	public final String get(StringKey key)
	{
		return stringChar == null ? null : stringChar.get(key);
	}

	public final String getSafe(StringKey key)
	{
		String str = stringChar == null ? null : stringChar.get(key);
		return str == null ? "" : str;
	}

	public final String put(StringKey key, String value)
	{
		if (stringChar == null)
		{
			stringChar = new HashMap<StringKey, String>();
		}
		return stringChar.put(key, value);
	}

	public final String remove(StringKey key)
	{
		String out = stringChar == null ? null : stringChar.remove(key);
		if (out != null && stringChar.isEmpty())
		{
			stringChar = null;
		}
		return out;
	}

	public final Set<StringKey> getStringKeys()
	{
		return stringChar == null ? Collections.<StringKey> emptySet()
			: new HashSet<StringKey>(stringChar.keySet());
	}

	public final boolean containsKey(FormulaKey key)
	{
		return formulaChar == null ? false : formulaChar.containsKey(key);
	}

	public final Formula get(FormulaKey key)
	{
		return formulaChar == null ? null : formulaChar.get(key);
	}

	public final Formula getSafe(FormulaKey key)
	{
		Formula formula = get(key);
		return formula == null ? key.getDefault() : formula;
	}

	public final Formula put(FormulaKey key, Formula value)
	{
		if (formulaChar == null)
		{
			formulaChar = new HashMap<FormulaKey, Formula>();
		}
		return formulaChar.put(key, value);
	}

	public final Formula remove(FormulaKey key)
	{
		Formula out = formulaChar == null ? null : formulaChar.remove(key);
		if (out != null && formulaChar.isEmpty())
		{
			formulaChar = null;
		}
		return out;
	}

	public final Set<FormulaKey> getFormulaKeys()
	{
		return formulaChar == null ? Collections.<FormulaKey> emptySet()
			: new HashSet<FormulaKey>(formulaChar.keySet());
	}

	public final boolean containsKey(VariableKey key)
	{
		return variableChar == null ? false : variableChar.containsKey(key);
	}

	public final Formula get(VariableKey key)
	{
		return variableChar == null ? null : variableChar.get(key);
	}

	public final Set<VariableKey> getVariableKeys()
	{
		return variableChar == null ? Collections.<VariableKey> emptySet()
			: new HashSet<VariableKey>(variableChar.keySet());
	}

	public final Formula put(VariableKey key, Formula value)
	{
		if (variableChar == null)
		{
			variableChar = new HashMap<VariableKey, Formula>();
		}
		return variableChar.put(key, value);
	}

	public final Formula remove(VariableKey key)
	{
		Formula out = variableChar == null ? null : variableChar.remove(key);
		if (out != null && variableChar.isEmpty())
		{
			variableChar = null;
		}
		return out;
	}

	public final void removeAllVariables()
	{
		variableChar = null;
	}

	public final boolean containsKey(ObjectKey<?> key)
	{
		return objectChar == null ? false : objectChar.containsKey(key);
	}

	public final <OT> OT get(ObjectKey<OT> key)
	{
		return objectChar == null ? null : key.cast(objectChar.get(key));
	}

	public final <OT> OT getSafe(ObjectKey<OT> key)
	{
		OT obj = get(key);
		return obj == null ? key.getDefault() : obj;
	}

	public final <OT> OT put(ObjectKey<OT> key, OT value)
	{
		if (objectChar == null)
		{
			objectChar = new HashMap<ObjectKey<?>, Object>();
		}
		return key.cast(objectChar.put(key, value));
	}

	public final <OT> OT remove(ObjectKey<OT> key)
	{
		OT out = objectChar == null ? null : key.cast(objectChar.remove(key));
		if (out != null && objectChar.isEmpty())
		{
			objectChar = null;
		}
		return out;
	}

	public final Set<ObjectKey<?>> getObjectKeys()
	{
		return objectChar == null ? Collections.<ObjectKey<?>>emptySet() : new HashSet<ObjectKey<?>>(objectChar.keySet());
	}

	public final boolean containsKey(FactKey<?> key)
	{
		return factChar == null ? false : factChar.containsKey(key);
	}

	public final <FT> Indirect<FT> get(FactKey<FT> key)
	{
		if (factChar == null)
		{
			return null;
		}
		return (Indirect<FT>) factChar.get(key);
	}

	public final <FT> FT getResolved(FactKey<FT> key)
	{
		if (factChar == null)
		{
			return null;
		}
		Indirect<FT> indirect = (Indirect<FT>) factChar.get(key);
		if (indirect == null)
		{
			return null;
		}
		return indirect.resolvesTo();
	}

	public final <FT> FT put(FactKey<FT> key, Indirect<FT> value)
	{
		if (factChar == null)
		{
			factChar = new HashMap<FactKey<?>, Object>();
		}
		return key.cast(factChar.put(key, value));
	}

	public final <FT> FT remove(FactKey<FT> key)
	{
		FT out = factChar == null ? null : key.cast(factChar.remove(key));
		if (out != null && factChar.isEmpty())
		{
			factChar = null;
		}
		return out;
	}

	public final Set<FactKey<?>> getFactKeys()
	{
		return factChar == null ? Collections.<FactKey<?>>emptySet() : new HashSet<FactKey<?>>(factChar.keySet());
	}

	public final boolean containsSetFor(FactSetKey<?> key)
	{
		return factSetChar == null ? false : factSetChar.containsListFor(key);
	}

	public final <T> void addToSetFor(FactSetKey<T> key, ObjectContainer<T> element)
	{
		if (factSetChar == null)
		{
			factSetChar = new FactSetKeyMapToList();
		}
		factSetChar.addToListFor(key, element);
	}

	public final <T> void addAllToSetFor(FactSetKey<T> key, Collection<ObjectContainer<T>> elementCollection)
	{
		if (factSetChar == null)
		{
			factSetChar = new FactSetKeyMapToList();
		}
		factSetChar.addAllToListFor(key, elementCollection);
	}

	public final <T> List<ObjectContainer<T>> getSetFor(FactSetKey<T> key)
	{
		return factSetChar == null ? null : factSetChar.getListFor(key);
	}

	public final <T> List<ObjectContainer<T>> getSafeSetFor(FactSetKey<T> key)
	{
		return factSetChar != null && factSetChar.containsListFor(key) ? factSetChar.getListFor(key)
				: new ArrayList<ObjectContainer<T>>();
	}
	
	public final String getSetAsString(FactSetKey<?> key)
	{
		return StringUtil.join(getSetFor(key), ", ");
	}

	public final int getSizeOfSetFor(FactSetKey<?> key)
	{
		// The javadoc says throw NPE, but the code returns 0, so I also return 0 here
		return factSetChar == null ? 0 : factSetChar.sizeOfListFor(key);
	}

	public final int getSafeSizeOfSetFor(FactSetKey<?> key)
	{
		return factSetChar == null ? 0 : factSetChar.containsListFor(key) ? factSetChar.sizeOfListFor(key) : 0;
	}

	public final <T> boolean containsInSet(FactSetKey<T> key, ObjectContainer<T> element)
	{
		return factSetChar == null ? false : factSetChar.containsInList(key, element);
	}

	public final <T> boolean containsAnyInSet(FactSetKey<T> key, Collection<ObjectContainer<T>> elementCollection)
	{
		return factSetChar == null ? false : factSetChar.containsAnyInList(key, elementCollection);
	}

	public final <T> List<ObjectContainer<T>> removeSetFor(FactSetKey<T> key)
	{
		List<ObjectContainer<T>> out = factSetChar == null ? null : factSetChar.removeListFor(key);
		if (out != null && factSetChar.isEmpty())
		{
			factSetChar = null;
		}
		return out;
	}

	public final <T> boolean removeFromSetFor(FactSetKey<T> key, ObjectContainer<T> element)
	{
		boolean removed = factSetChar == null ? false : factSetChar.removeFromListFor(key, element);
		if (removed && factSetChar.isEmpty())
		{
			factSetChar = null;
		}
		return removed;
	}

	public final Set<FactSetKey<?>> getFactSetKeys()
	{
		return factSetChar == null ? Collections.<FactSetKey<?>>emptySet() : factSetChar.getKeySet();
	}

	public final boolean containsListFor(ListKey<?> key)
	{
		return listChar == null ? false : listChar.containsListFor(key);
	}

	public final <T> void addToListFor(ListKey<T> key, T element)
	{
		if (listChar == null)
		{
			listChar = new ListKeyMapToList();
		}
		listChar.addToListFor(key, element);
	}

	public final <T> void addAllToListFor(ListKey<T> key, Collection<T> elementCollection)
	{
		if (listChar == null)
		{
			listChar = new ListKeyMapToList();
		}
		listChar.addAllToListFor(key, elementCollection);
	}

	/**
	 * Returns a copy of the list of objects stored in this CDOMObject for the
	 * given ListKey.
	 * 
	 * No order is guaranteed, and the returned List may contain duplicates.
	 * There is no guarantee that duplicate items are sequential items in the
	 * returned List.
	 * 
	 * This method is value-semantic in that no changes are made to the key
	 * passed into the method and ownership of the returned List is transferred
	 * to the class calling this method.
	 * 
	 * @param key
	 *            The ListKey for which a copy of the list should be returned.
	 * @return A copy of the List contained in this CDOMObject for the given
	 *         key; null if the given key is not a ListKey in this CDOMObject.
	 */
	public final <T> List<T> getListFor(ListKey<T> key)
	{
		return listChar == null ? null : listChar.getListFor(key);
	}

	/**
	 * Returns a non-null copy of the list of objects stored in this CDOMObject
	 * for the given ListKey.
	 * 
	 * No order is guaranteed, and the returned List may contain duplicates.
	 * There is no guarantee that duplicate items are sequential items in the
	 * returned List.
	 * 
	 * This method is value-semantic in that no changes are made to the key
	 * passed into the method and ownership of the returned List is transferred
	 * to the class calling this method.
	 * 
	 * @param key
	 *            The ListKey for which a copy of the list should be returned.
	 * @return A copy of the List contained in this CDOMObject for the given
	 *         key.
	 */
	public final <T> List<T> getSafeListFor(ListKey<T> key)
	{
		return listChar != null && listChar.containsListFor(key) ? listChar.getListFor(key)
				: new ArrayList<T>();
	}
	
	/**
	 * Returns a non-null Set of the objects stored in this CDOMObject for the
	 * given ListKey. The List is converted to a Set to ensure that each entry
	 * in the List is only occurs once.
	 * 
	 * This is used because the loading system cannot guarantee "Set" behavior
	 * (cannot guarantee uniqueness), and a specific infrastructure for a Set
	 * (vs a List) is considered overkill for the few use cases that require it.
	 * 
	 * No order of the objects is guaranteed.
	 * 
	 * This method is value-semantic in that no changes are made to the key
	 * passed into the method and ownership of the returned Set is transferred
	 * to the class calling this method.
	 * 
	 * @param key
	 *            The ListKey for which a Set of the objects stored in this
	 *            CDOMObject for the given ListKey should be returned.
	 * @return A Set of objects in the List contained in this CDOMObject for the
	 *         given key.
	 */
	public final <T extends Comparable<T>> Set<T> getUniqueListFor(
		ListKey<T> key)
	{
		if (listChar == null)
		{
			return new HashSet<T>();
		}
		List<T> list = listChar.getListFor(key);
		if (list == null)
		{
			return new HashSet<T>();
		}
		return new LinkedHashSet<T>(list);
	}
	
	public final String getListAsString(ListKey<?> key)
	{
		return StringUtil.join(getListFor(key), ", ");
	}

	public final int getSizeOfListFor(ListKey<?> key)
	{
		// The javadoc says throw NPE, but the code returns 0, so I also return 0 here
		return listChar == null ? 0 : listChar.sizeOfListFor(key);
	}

	public final int getSafeSizeOfListFor(ListKey<?> key)
	{
		return listChar == null ? 0 : listChar.containsListFor(key) ? listChar.sizeOfListFor(key) : 0;
	}

	public final <T> boolean containsInList(ListKey<T> key, T element)
	{
		return listChar == null ? false : listChar.containsInList(key, element);
	}

	public final <T> boolean containsAnyInList(ListKey<T> key, Collection<T> elementCollection)
	{
		return listChar == null ? false : listChar.containsAnyInList(key, elementCollection);
	}

	public final <T> T getElementInList(ListKey<T> key, int index)
	{
		return listChar == null ? null : listChar.getElementInList(key, index);
	}

	public final <T> List<T> removeListFor(ListKey<T> key)
	{
		List<T> out = listChar == null ? null : listChar.removeListFor(key);
		if (out != null && listChar.isEmpty())
		{
			listChar = null;
		}
		return out;
	}

	public final <T> boolean removeFromListFor(ListKey<T> key, T element)
	{
		boolean removed = listChar == null ? false : listChar.removeFromListFor(key, element);
		if (removed && listChar.isEmpty())
		{
			listChar = null;
		}
		return removed;
	}

	public final Set<ListKey<?>> getListKeys()
	{
		return listChar == null ? Collections.<ListKey<?>>emptySet() : listChar.getKeySet();
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
		if (mapChar == null)
		{
			mapChar = new MapKeyMap();
		}
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
		if (mapChar != null)
		{
			boolean removed = mapChar.removeFromMapFor(mapKey, key);
			if (removed && mapChar.isEmpty())
			{
				mapChar = null;
			}
		}
	}

	/**
	 * Remove a map from the map of maps.
	 * 
	 * @param mapKey The MapKey we are removing
	 */
	public final <K, V> void removeMapFor(MapKey<K, V> mapKey)
	{
		if (mapChar != null)
		{
			Map<K, V> removed = mapChar.removeMapFor(mapKey);
			if (removed != null && mapChar.isEmpty())
			{
				mapChar = null;
			}
		}
	}

	/**
	 * Retrieve the map of keys and values for the MapKey.
	 * 
	 * @param mapKey The MapKey we are retrieving
	 * @return The map of keys and values.
	 */
	public final <K, V> Map<K, V> getMapFor(MapKey<K, V> mapKey)
	{
		// The javadoc for getMapFor() says that it returns null, but the implementation does NOT
		// This caused an NPE in AspectToken.parseNonEmptyToken because it assumed a non-null map
		return mapChar == null ? Collections.<K, V>emptyMap() : mapChar.getMapFor(mapKey);
	}

	/**
	 * Retrieve the set of keys for the MapKey.
	 * 
	 * @param mapKey The MapKey we are retrieving
	 * @return The set of keys.
	 */
	public final <K, V> Set<K> getKeysFor(MapKey<K, V> mapKey)
	{
		return mapChar == null ? Collections.<K>emptySet() : mapChar.getKeysFor(mapKey);
	}

	/**
	 * Get the value for the given MapKey and secondary key. If there is 
	 * not a mapping for the given keys, null is returned.
	 * 
	 * @param mapKey
	 *            The MapKey for retrieving the given value
	 * @param key2
	 *            The secondary key for retrieving the given value
	 * @return Object The value stored for the given keys
	 */
	public final <K, V> V get(MapKey<K, V> mapKey, K key2)
	{
		return mapChar == null ? null : mapChar.get(mapKey, key2);
	}

	/**
	 * Remove the value associated with the primary and secondary keys 
	 * from the map.
	 *  
	 * @param mapKey The MapKey of the entry we are removing
	 * @param key2 The secondary key of the entry we are removing
	 * @return true if the key and its associated value were successfully removed 
	 *         from the map; false otherwise
	 */
	public final <K, V> boolean removeFromMap(MapKey<K, V> mapKey, K key2)
	{
		boolean removed = mapChar == null ? false : mapChar.removeFromMapFor(mapKey, key2);
		if (removed && mapChar.isEmpty())
		{
			mapChar = null;
		}
		return removed;
	}

	/**
	 * Retrieve the set of mapkeys held.
	 * 
	 * @return The set of mapkeys.
	 */
	public final Set<MapKey<?, ?>> getMapKeys()
	{
		return mapChar == null ? Collections.<MapKey<?, ?>>emptySet() : mapChar.getKeySet();
	}
	
	@Override
	public String getKeyName()
	{
		// FIXME TODO Patched for now to avoid NPEs, but this is wrong
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
		return mapChar != null && mapChar.containsMapFor(mapKey) ? mapChar.getKeysFor(mapKey).size() : 0;
	}

	@Override
	public void setName(String name)
	{
		setDisplayName(name);
	}

	public void setDisplayName(String name)
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
		if (integerChar == null ? cdo.integerChar != null : !integerChar.equals(cdo.integerChar))
		{
			// System.err.println("CDOM Inequality Integer");
			// System.err.println(integerChar + " " + cdo.integerChar);
			return false;
		}
		if (stringChar == null ? cdo.stringChar != null : !stringChar.equals(cdo.stringChar))
		{
			// System.err.println("CDOM Inequality String");
			// System.err.println(stringChar + " " + cdo.stringChar);
			return false;
		}
		if (formulaChar == null ? cdo.formulaChar != null : !formulaChar.equals(cdo.formulaChar))
		{
			// System.err.println("CDOM Inequality Formula");
			// System.err.println(formulaChar + " " + cdo.formulaChar);
			return false;
		}
		if (variableChar == null ? cdo.variableChar != null : !variableChar.equals(cdo.variableChar))
		{
			// System.err.println("CDOM Inequality Variable");
			// System.err.println(variableChar + " " + cdo.variableChar);
			return false;
		}
		if (objectChar == null ? cdo.objectChar != null : !objectChar.equals(cdo.objectChar))
		{
			// System.err.println("CDOM Inequality Object");
			// System.err.println(objectChar + " " + cdo.objectChar);
			return false;
		}
		if (factChar == null ? cdo.factChar != null : !factChar.equals(cdo.factChar))
		{
			// System.err.println("CDOM Inequality Object");
			// System.err.println(objectChar + " " + cdo.objectChar);
			return false;
		}
		if (listChar == null ? cdo.listChar != null : !listChar.equals(cdo.listChar))
		{
//			 System.err.println("CDOM Inequality List");
//			 System.err.println(listChar + " " + cdo.listChar);
//			 System.err.println(listChar.getKeySet() + " "
//			 + cdo.listChar.getKeySet());
			return false;
		}
		if (mapChar == null ? cdo.mapChar != null : !mapChar.equals(cdo.mapChar))
		{
			return false;
		}
		if (cdomListMods == null ? cdo.cdomListMods != null : !cdomListMods.equals(cdo.cdomListMods))
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
		if (cdomListMods == null)
		{
			cdomListMods =
					new DoubleKeyMapToList<CDOMReference<? extends CDOMList<? extends PrereqObject>>, CDOMReference<?>, AssociatedPrereqObject>(
						HashMap.class, LinkedHashMap.class);
		}
		cdomListMods.addToListFor(listRef, granted, associations);
	}

	public final <T extends PrereqObject> void removeFromList(
			CDOMReference<? extends CDOMList<? extends PrereqObject>> listRef,
			CDOMReference<T> granted)
	{
		if (cdomListMods != null)
		{
			List<AssociatedPrereqObject> removed =
					cdomListMods.removeListFor(listRef, granted);
			if (removed != null && cdomListMods.isEmpty())
			{
				cdomListMods = null;
			}
		}
	}

	public final boolean hasListMods(
			CDOMReference<? extends CDOMList<? extends PrereqObject>> listRef)
	{
		return cdomListMods == null ? false : cdomListMods.containsListFor(listRef);
	}

	// TODO Is there a way to get type safety here?
	public final <BT extends PrereqObject> Collection<CDOMReference<BT>> getListMods(
			CDOMReference<? extends CDOMList<BT>> listRef)
	{
		Set set = cdomListMods == null ? null : cdomListMods.getSecondaryKeySet(listRef);
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
		return cdomListMods == null ? null : cdomListMods.getListFor(listRef, key);
	}

	/**
	 * @return A list of references to the global lists that this CDOM Object has modified
	 */
	public final Collection<CDOMReference<? extends CDOMList<? extends PrereqObject>>> getModifiedLists()
	{
		return cdomListMods == null
			? Collections
				.<CDOMReference<? extends CDOMList<? extends PrereqObject>>> emptySet()
			: cdomListMods.getKeySet();
	}

	@Override
	public final String getLSTformat()
	{
		return getKeyName();
	}

	public final void overlayCDOMObject(CDOMObject cdo)
	{
		addAllPrerequisites(cdo.getPrerequisiteList());
		if (cdo.integerChar != null)
		{
			if (integerChar == null)
			{
				integerChar = new HashMap<IntegerKey, Integer>();
			}
			integerChar.putAll(cdo.integerChar);
		}
		if (cdo.stringChar != null)
		{
			if (stringChar == null)
			{
				stringChar = new HashMap<StringKey, String>();
			}
			stringChar.putAll(cdo.stringChar);
		}
		if (cdo.formulaChar != null)
		{
			if (formulaChar == null)
			{
				formulaChar = new HashMap<FormulaKey, Formula>();
			}
			formulaChar.putAll(cdo.formulaChar);
		}
		if (cdo.objectChar != null)
		{
			if (objectChar == null)
			{
				objectChar = new HashMap<ObjectKey<?>, Object>();
			}
			objectChar.putAll(cdo.objectChar);
		}
		if (cdo.factChar != null)
		{
			if (factChar == null)
			{
				factChar = new HashMap<FactKey<?>, Object>();
			}
			factChar.putAll(cdo.factChar);
		}
		if (cdo.variableChar != null)
		{
			if (variableChar == null)
			{
				variableChar = new HashMap<VariableKey, Formula>();
			}
			variableChar.putAll(cdo.variableChar);
		}
		if (cdo.listChar != null)
		{
			if (listChar == null)
			{
				listChar = new ListKeyMapToList();
			}
			listChar.addAllLists(cdo.listChar);
		}
		if (cdo.factSetChar != null)
		{
			if (factSetChar == null)
			{
				factSetChar = new FactSetKeyMapToList();
			}
			factSetChar.addAllLists(cdo.factSetChar);
		}
		if (cdo.mapChar != null)
		{
			if (mapChar == null)
			{
				mapChar = new MapKeyMap();
			}
			mapChar.putAll(cdo.mapChar);
		}
		if (cdo.cdomListMods != null)
		{
			if (cdomListMods == null)
			{
				cdomListMods =
						new DoubleKeyMapToList<CDOMReference<? extends CDOMList<? extends PrereqObject>>, CDOMReference<?>, AssociatedPrereqObject>(
							HashMap.class, LinkedHashMap.class);
			}
			cdomListMods.addAll(cdo.cdomListMods);
		}
	}

	@Override
	public CDOMObject clone() throws CloneNotSupportedException
	{
		CDOMObject clone = (CDOMObject) super.clone();
		clone.integerChar = integerChar == null ? null : new HashMap<IntegerKey, Integer>(integerChar);
		clone.stringChar = stringChar == null ? null : new HashMap<StringKey, String>(stringChar);
		clone.formulaChar = formulaChar == null ? null : new HashMap<FormulaKey, Formula>(formulaChar);
		clone.variableChar = variableChar == null ? null : new HashMap<VariableKey, Formula>(variableChar);
		clone.objectChar = objectChar == null ? null : new HashMap<ObjectKey<?>, Object>(objectChar);
		clone.factChar = factChar == null ? null : new HashMap<FactKey<?>, Object>(factChar);
		if (listChar != null)
		{
			clone.listChar = new ListKeyMapToList();
			clone.listChar.addAllLists(listChar);
		}
		if (factSetChar != null)
		{
			clone.factSetChar = new FactSetKeyMapToList();
			clone.factSetChar.addAllLists(factSetChar);
		}
		if (mapChar != null)
		{
			clone.mapChar = new MapKeyMap();
			clone.mapChar.putAll(mapChar);
		}
		clone.cdomListMods = cdomListMods == null ? null : cdomListMods.clone();
		clone.ownBonuses(clone);
		return clone;
	}

	public void removeAllFromList(CDOMReference<? extends CDOMList<?>> listRef)
	{
		if (cdomListMods != null)
		{
			MapToList<CDOMReference<?>, AssociatedPrereqObject> removed =
					cdomListMods.removeListsFor(listRef);
			if (removed != null && cdomListMods.isEmpty())
			{
				cdomListMods = null;
			}
		}
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
	 * @param pc the current player character
	 * @return the list of bonuses for this object
	 */
	public List<BonusObj> getRawBonusList(PlayerCharacter pc)
	{
		List<BonusObj> bonusList = getSafeListFor(ListKey.BONUS);
		if (pc != null)
		{
			bonusList.addAll(pc.getAddedBonusList(this));
			bonusList.addAll(pc.getSaveableBonusList(this));
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

		for (BonusObj bonus : getRawBonusList(pc))
		{
			if (pc.isApplied(bonus))
			{
				aList.add(bonus);
			}
		}

		return aList;
	}

	public List<BonusObj> getBonusList(PlayerCharacter assocStore)
	{
		return getRawBonusList(assocStore);
	}
	
	public List<BonusObj> getBonusList(Equipment e)
	{
		return getRawBonusList(null);
	}

	/**
	 * Set the source file for this object
	 * @param source
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
		return getSafe(ObjectKey.INTERNAL).booleanValue();
	}
}
