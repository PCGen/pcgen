/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.rules.context;

import java.net.URI;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Set;

import pcgen.base.formula.Formula;
import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.HashMapToList;
import pcgen.base.util.TripleKeyMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.core.prereq.Prerequisite;

public abstract class AbstractObjectContext
{
	private final TrackingObjectCommitStrategy edits = new TrackingObjectCommitStrategy();

	public URI getSourceURI()
	{
		return edits.getSourceURI();
	}

	public void setSourceURI(URI sourceURI)
	{
		edits.setSourceURI(sourceURI);
		getCommitStrategy().setSourceURI(sourceURI);
	}

	public URI getExtractURI()
	{
		return edits.getExtractURI();
	}

	public void setExtractURI(URI extractURI)
	{
		edits.setExtractURI(extractURI);
		getCommitStrategy().setExtractURI(extractURI);
	}

	public <T> void addToList(CDOMObject cdo, ListKey<T> key, T value)
	{
		edits.addToList(cdo, key, value);
	}

	public <K, V> void put(CDOMObject cdo, MapKey<K, V> mk, K key, V value)
	{
		edits.put(cdo, mk, key, value);
	}

	public void put(CDOMObject cdo, FormulaKey fk, Formula f)
	{
		edits.put(cdo, fk, f);
	}

	public void put(ConcretePrereqObject cpo, Prerequisite p)
	{
		edits.put(cpo, p);
	}

	public void clearPrerequisiteList(ConcretePrereqObject cpo)
	{
		edits.clearPrerequisiteList(cpo);
	}

	public void put(CDOMObject cdo, IntegerKey ik, Integer i)
	{
		edits.put(cdo, ik, i);
	}

	public void remove(CDOMObject cdo, IntegerKey ik)
	{
		edits.remove(cdo, ik);
	}

	public <T> void put(CDOMObject cdo, ObjectKey<T> sk, T s)
	{
		edits.put(cdo, sk, s);
	}

	public void remove(CDOMObject cdo, ObjectKey<?> sk)
	{
		edits.remove(cdo, sk);
	}

	public void put(CDOMObject cdo, StringKey sk, String s)
	{
		edits.put(cdo, sk, s);
	}

	public void remove(CDOMObject cdo, StringKey sk)
	{
		edits.remove(cdo, sk);
	}

	public void put(CDOMObject cdo, VariableKey vk, Formula f)
	{
		edits.put(cdo, vk, f);
	}

	public <T> void removeFromList(CDOMObject cdo, ListKey<T> lk, T val)
	{
		edits.removeFromList(cdo, lk, val);
	}

	public void removeList(CDOMObject cdo, ListKey<?> lk)
	{
		edits.removeList(cdo, lk);
	}

	public <K, V> void remove(CDOMObject cdo, MapKey<K, V> mk, K key)
	{
		edits.remove(cdo, mk, key);
	}

	public void commit()
	{
		ObjectCommitStrategy commit = getCommitStrategy();
		for (URI uri : edits.preClearSet.getKeySet())
		{
			for (ConcretePrereqObject cpo : edits.preClearSet.getListFor(uri))
			{
				commit.clearPrerequisiteList(cpo);
			}
		}
		for (URI uri : edits.globalClearSet.getKeySet())
		{
			for (CDOMObject cdo : edits.globalClearSet.getSecondaryKeySet(uri))
			{
				for (ListKey<?> lk : edits.globalClearSet.getListFor(uri, cdo))
				{
					commit.removeList(cdo, lk);
				}
			}
		}
		for (URI uri : edits.negativeMap.getKeySet())
		{
			for (ConcretePrereqObject cpo : edits.negativeMap
					.getSecondaryKeySet(uri))
			{
				if (cpo instanceof CDOMObject)
				{
					CDOMObject cdo = (CDOMObject) cpo;
					CDOMObject neg = edits.negativeMap.get(uri, cdo);
					for (ObjectKey<?> ok : neg.getSafeListFor(ListKey.REMOVED_OBJECTKEY))
					{
						commit.remove(cdo, ok);
					}
					for (StringKey sk : neg.getSafeListFor(ListKey.REMOVED_STRINGKEY))
					{
						commit.remove(cdo, sk);
					}
					for (IntegerKey ik : neg.getSafeListFor(ListKey.REMOVED_INTEGERKEY))
					{
						commit.remove(cdo, ik);
					}
					for (ListKey<?> key : neg.getListKeys())
					{
						removeListKey(cdo, key, neg);
					}
					for (MapKey<?, ?> key1 : neg.getMapKeys())
					{
						removeMapKey(cdo, key1, neg);
					}
				}
			}
		}
		for (URI uri : edits.positiveMap.getKeySet())
		{
			for (ConcretePrereqObject cpo : edits.positiveMap
					.getSecondaryKeySet(uri))
			{
				CDOMObject pos = edits.positiveMap.get(uri, cpo);
				for (Prerequisite p : pos.getPrerequisiteList())
				{
					commit.put(cpo, p);
				}
				if (cpo instanceof CDOMObject)
				{
					CDOMObject cdo = (CDOMObject) cpo;
					for (StringKey key : pos.getStringKeys())
					{
						commit.put(cdo, key, pos.get(key));
					}
					for (IntegerKey key : pos.getIntegerKeys())
					{
						commit.put(cdo, key, pos.get(key));
					}
					for (FormulaKey key : pos.getFormulaKeys())
					{
						commit.put(cdo, key, pos.get(key));
					}
					for (VariableKey key : pos.getVariableKeys())
					{
						commit.put(cdo, key, pos.get(key));
					}
					for (ObjectKey<?> key : pos.getObjectKeys())
					{
						putObjectKey(cdo, key, pos);
					}
					for (ListKey<?> key : pos.getListKeys())
					{
						putListKey(cdo, key, pos);
					}
					for (MapKey<?, ?> key1 : pos.getMapKeys())
					{
						putMapKey(cdo, key1, pos);
					}
					/*
					 * No need to deal with ListMods because that's done in
					 * listContext
					 */
					/*
					 * TODO Deal with cloned objects
					 */
				}
			}
		}
		for (URI uri : edits.patternClearSet.getKeySet())
		{
			for (CDOMObject cdo : edits.patternClearSet.getSecondaryKeySet(uri))
			{
				for (ListKey<?> lk : edits.patternClearSet.getTertiaryKeySet(
						uri, cdo))
				{
					for (String s : edits.patternClearSet.getListFor(uri, cdo,
							lk))
					{
						commit.removePatternFromList(cdo, lk, s);
					}
				}
			}
		}
		rollback();
	}

	private <T> void removeListKey(CDOMObject cdo, ListKey<T> key,
			CDOMObject neg)
	{
		ObjectCommitStrategy commit = getCommitStrategy();
		for (T obj : neg.getListFor(key))
		{
			commit.removeFromList(cdo, key, obj);
		}
	}

	private <T> void putListKey(CDOMObject cdo, ListKey<T> key, CDOMObject neg)
	{
		ObjectCommitStrategy commit = getCommitStrategy();
		for (T obj : neg.getListFor(key))
		{
			commit.addToList(cdo, key, obj);
		}
	}

	private <T> void putObjectKey(CDOMObject cdo, ObjectKey<T> key,
			CDOMObject neg)
	{
		getCommitStrategy().put(cdo, key, neg.get(key));
	}

	private <K, V> void removeMapKey(CDOMObject cdo, MapKey<K, V> key1,
			CDOMObject neg)
	{
		ObjectCommitStrategy commit = getCommitStrategy();
		Set<K> secKeys = neg.getKeysFor(key1);
		for (K key2 : secKeys)
		{
			commit.remove(cdo, key1, key2);
		}
	}

	private <K, V> void putMapKey(CDOMObject cdo, MapKey<K, V> key1,
			CDOMObject pos)
	{
		ObjectCommitStrategy commit = getCommitStrategy();
		Set<K> secKeys = pos.getKeysFor(key1);
		for (K key2 : secKeys)
		{
			commit.put(cdo, key1, key2, pos.get(key1, key2));
		}
	}

	public void rollback()
	{
		edits.decommit();
	}

	public Formula getFormula(CDOMObject cdo, FormulaKey fk)
	{
		return getCommitStrategy().getFormula(cdo, fk);
	}

	public Integer getInteger(CDOMObject cdo, IntegerKey ik)
	{
		return getCommitStrategy().getInteger(cdo, ik);
	}

	public <T> Changes<T> getListChanges(CDOMObject cdo, ListKey<T> lk)
	{
		return getCommitStrategy().getListChanges(cdo, lk);
	}

	public <K, V> MapChanges<K, V> getMapChanges(CDOMObject cdo, MapKey<K, V> mk)
	{
		return getCommitStrategy().getMapChanges(cdo, mk);
	}

	// public <T> Changes<T> getGivenChanges(String sourceToken, CDOMObject cdo,
	// Class<T> cl)
	// {
	// return new GivenChanges<T>(cl, sourceToken, commit.getListChanges(cdo,
	// ListKey.GIVEN));
	// }
	//
	public <T> T getObject(CDOMObject cdo, ObjectKey<T> ik)
	{
		return getCommitStrategy().getObject(cdo, ik);
	}

	public String getString(CDOMObject cdo, StringKey sk)
	{
		return getCommitStrategy().getString(cdo, sk);
	}

	public Formula getVariable(CDOMObject obj, VariableKey key)
	{
		return getCommitStrategy().getVariable(obj, key);
	}

	public Set<VariableKey> getVariableKeys(CDOMObject obj)
	{
		return getCommitStrategy().getVariableKeys(obj);
	}

	<T extends CDOMObject> T cloneConstructedCDOMObject(T obj, String newName)
	{
		return edits.cloneConstructedCDOMObject(obj, newName);
	}

	private static class SimpleCDOMObject extends CDOMObject
	{
		@Override
		public boolean isType(String str)
		{
			return false;
		}
	}

	public static class TrackingObjectCommitStrategy implements
			ObjectCommitStrategy
	{
		private final DoubleKeyMap<URI, ConcretePrereqObject, CDOMObject> positiveMap = new DoubleKeyMap<URI, ConcretePrereqObject, CDOMObject>(
				HashMap.class, IdentityHashMap.class);

		private final DoubleKeyMap<URI, ConcretePrereqObject, CDOMObject> negativeMap = new DoubleKeyMap<URI, ConcretePrereqObject, CDOMObject>(
				HashMap.class, IdentityHashMap.class);

		private final DoubleKeyMapToList<URI, CDOMObject, ListKey<?>> globalClearSet = new DoubleKeyMapToList<URI, CDOMObject, ListKey<?>>(
				HashMap.class, IdentityHashMap.class);

		private final HashMapToList<URI, ConcretePrereqObject> preClearSet = new HashMapToList<URI, ConcretePrereqObject>();

		private final TripleKeyMapToList<URI, CDOMObject, ListKey<?>, String> patternClearSet = new TripleKeyMapToList<URI, CDOMObject, ListKey<?>, String>(
				HashMap.class, IdentityHashMap.class, HashMap.class);

		private URI sourceURI;

		private URI extractURI;

		private CDOMObject getNegative(URI source, CDOMObject cdo)
		{
			if (cdo == null)
			{
				throw new IllegalArgumentException(
						"Cannot remove contents from null object");
			}
			CDOMObject negative = negativeMap.get(source, cdo);
			if (negative == null)
			{
				negative = new SimpleCDOMObject();
				negativeMap.put(source, cdo, negative);
			}
			return negative;
		}

		@Override
		public void clearPrerequisiteList(ConcretePrereqObject cpo)
		{
			preClearSet.addToListFor(sourceURI, cpo);
		}

		@Override
		public void put(ConcretePrereqObject cpo, Prerequisite p)
		{
			getPositive(sourceURI, cpo).addPrerequisite(p);
		}

		private CDOMObject getPositive(URI source, ConcretePrereqObject cdo)
		{
			if (cdo == null)
			{
				throw new IllegalArgumentException(
						"Cannot assign contents to null object");
			}
			CDOMObject positive = positiveMap.get(source, cdo);
			if (positive == null)
			{
				positive = new SimpleCDOMObject();
				positiveMap.put(source, cdo, positive);
			}
			return positive;
		}

		@Override
		public void put(CDOMObject cdo, StringKey sk, String s)
		{
			if (s != null && s.startsWith(Constants.LST_DOT_CLEAR))
			{
				throw new IllegalArgumentException("Cannot set a value to " + s);
			}
			getPositive(sourceURI, cdo).put(sk, s);
		}

		@Override
		public void remove(CDOMObject cdo, StringKey sk)
		{
			getNegative(sourceURI, cdo).addToListFor(ListKey.REMOVED_STRINGKEY,
					sk);
		}

		@Override
		public <T> void put(CDOMObject cdo, ObjectKey<T> sk, T s)
		{
			getPositive(sourceURI, cdo).put(sk, s);
		}

		@Override
		public void remove(CDOMObject cdo, ObjectKey<?> sk)
		{
			getNegative(sourceURI, cdo).addToListFor(ListKey.REMOVED_OBJECTKEY,
					sk);
		}

		@Override
		public void put(CDOMObject cdo, IntegerKey ik, Integer i)
		{
			getPositive(sourceURI, cdo).put(ik, i);
		}

		@Override
		public void remove(CDOMObject cdo, IntegerKey ik)
		{
			getNegative(sourceURI, cdo).addToListFor(ListKey.REMOVED_INTEGERKEY,
					ik);
		}

		@Override
		public void put(CDOMObject cdo, FormulaKey fk, Formula f)
		{
			getPositive(sourceURI, cdo).put(fk, f);
		}

		@Override
		public void put(CDOMObject cdo, VariableKey vk, Formula f)
		{
			getPositive(sourceURI, cdo).put(vk, f);
		}

		@Override
		public boolean containsListFor(CDOMObject cdo, ListKey<?> key)
		{
			return cdo.containsListFor(key);
		}

		@Override
		public <T> void addToList(CDOMObject cdo, ListKey<T> key, T value)
		{
			getPositive(sourceURI, cdo).addToListFor(key, value);
		}

		@Override
		public void removeList(CDOMObject cdo, ListKey<?> lk)
		{
			globalClearSet.addToListFor(sourceURI, cdo, lk);
		}

		@Override
		public <T> void removeFromList(CDOMObject cdo, ListKey<T> lk, T val)
		{
			getNegative(sourceURI, cdo).addToListFor(lk, val);
		}

		// ==== MapKey manipulation functions ====

		@Override
		public <K, V> void put(CDOMObject cdo, MapKey<K, V> mk, K key, V value)
		{
			getPositive(sourceURI, cdo).addToMapFor(mk, key, value);
		}

		@Override
		public <K, V> void remove(CDOMObject cdo, MapKey<K, V> mk, K key)
		{
			getNegative(sourceURI, cdo).addToMapFor(mk, key, null);
		}

		@Override
		public <K, V> MapChanges<K, V> getMapChanges(CDOMObject cdo,
				MapKey<K, V> mk)
		{
			return new MapChanges<K, V>(getPositive(extractURI, cdo).getMapFor(
					mk), getNegative(extractURI, cdo).getMapFor(mk), false);
		}

		// ==== end of MapKey manipulation functions ====

		@Override
		public String getString(CDOMObject cdo, StringKey sk)
		{
			return getPositive(extractURI, cdo).get(sk);
		}

		@Override
		public Integer getInteger(CDOMObject cdo, IntegerKey ik)
		{
			return getPositive(extractURI, cdo).get(ik);
		}

		@Override
		public Formula getFormula(CDOMObject cdo, FormulaKey fk)
		{
			return getPositive(extractURI, cdo).get(fk);
		}

		@Override
		public Formula getVariable(CDOMObject cdo, VariableKey key)
		{
			return getPositive(extractURI, cdo).get(key);
		}

		@Override
		public Set<VariableKey> getVariableKeys(CDOMObject cdo)
		{
			return getPositive(extractURI, cdo).getVariableKeys();
		}

		@Override
		public <T> T getObject(CDOMObject cdo, ObjectKey<T> ik)
		{
			return getPositive(extractURI, cdo).get(ik);
		}

		@Override
		public <T> Changes<T> getListChanges(CDOMObject cdo, ListKey<T> lk)
		{
			return new CollectionChanges<T>(getPositive(extractURI, cdo)
					.getListFor(lk), getNegative(extractURI, cdo)
					.getListFor(lk), globalClearSet.containsInList(extractURI,
					cdo, lk));
		}

		@Override
		public <T> PatternChanges<T> getListPatternChanges(CDOMObject cdo,
				ListKey<T> lk)
		{
			return new PatternChanges<T>(getPositive(extractURI, cdo)
					.getListFor(lk), patternClearSet.getListFor(extractURI,
					cdo, lk), globalClearSet
					.containsInList(extractURI, cdo, lk));
		}

		public URI getExtractURI()
		{
			return extractURI;
		}

		@Override
		public void setExtractURI(URI extractURI)
		{
			this.extractURI = extractURI;
		}

		public URI getSourceURI()
		{
			return sourceURI;
		}

		@Override
		public void setSourceURI(URI sourceURI)
		{
			this.sourceURI = sourceURI;
		}

		public void decommit()
		{
			positiveMap.clear();
			negativeMap.clear();
			globalClearSet.clear();
			preClearSet.clear();
			patternClearSet.clear();
		}

		public <T extends CDOMObject> T cloneConstructedCDOMObject(T obj,
				String newName)
		{
			Class<T> cl = (Class<T>) obj.getClass();
			try
			{
				T newObj = cl.newInstance();
				newObj.setName(newName);
				/*
				 * TODO Need to store this clone somewhere
				 */
				return newObj;
			}
			catch (InstantiationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public Changes<Prerequisite> getPrerequisiteChanges(
				ConcretePrereqObject obj)
		{
			return new CollectionChanges<Prerequisite>(getPositive(extractURI,
					obj).getPrerequisiteList(), null, preClearSet
					.containsInList(extractURI, obj));
		}

		@Override
		public <T> void removePatternFromList(CDOMObject cdo, ListKey<T> lk,
				String pattern)
		{
			patternClearSet.addToListFor(sourceURI, cdo, lk, pattern);
		}

		@Override
		public boolean wasRemoved(CDOMObject cdo, ObjectKey<?> ok)
		{
			return getNegative(extractURI, cdo).containsInList(
					ListKey.REMOVED_OBJECTKEY, ok);
		}

		@Override
		public boolean wasRemoved(CDOMObject cdo, StringKey sk)
		{
			return getNegative(extractURI, cdo).containsInList(
					ListKey.REMOVED_STRINGKEY, sk);
		}

		@Override
		public boolean wasRemoved(CDOMObject cdo, IntegerKey ik)
		{
			return getNegative(extractURI, cdo).containsInList(
					ListKey.REMOVED_INTEGERKEY, ik);
		}

		public void purge(CDOMObject cdo)
		{
			positiveMap.remove(sourceURI, cdo);
			negativeMap.remove(sourceURI, cdo);
			globalClearSet.removeListFor(sourceURI, cdo);
			preClearSet.removeFromListFor(sourceURI, cdo);
			patternClearSet.removeListsFor(sourceURI, cdo);
		}
	}

	public Changes<Prerequisite> getPrerequisiteChanges(ConcretePrereqObject obj)
	{
		return getCommitStrategy().getPrerequisiteChanges(obj);
	}

	public boolean containsListFor(CDOMObject obj, ListKey<?> lk)
	{
		return getCommitStrategy().containsListFor(obj, lk);
	}

	// private class GivenChanges<T> implements Changes<T>
	// {
	//
	// private final ArrayList<T> added = new ArrayList<T>();
	// private final ArrayList<T> removed = new ArrayList<T>();
	// private final String token;
	// private final Class<T> targetClass;
	// private final boolean clear;
	//		
	// public GivenChanges(Class<T> cl, String sourceToken,
	// Changes<SourceWrapper> listChanges)
	// {
	// targetClass = cl;
	// token = sourceToken;
	// clear = listChanges.includesGlobalClear();
	// Collection<SourceWrapper> allAdded = listChanges.getAdded();
	// if (allAdded != null)
	// {
	// for (SourceWrapper add : allAdded)
	// {
	// PrereqObject target = add.getTarget();
	// if (targetClass.isAssignableFrom(target.getClass())
	// && token.equals(add.getSourceToken()))
	// {
	// added.add((T) target);
	// }
	// }
	// }
	// Collection<SourceWrapper> allRemoved = listChanges.getRemoved();
	// if (allRemoved != null)
	// {
	// for (SourceWrapper rem : allRemoved)
	// {
	// PrereqObject target = rem.getTarget();
	// if (targetClass.equals(target.getClass())
	// && token.equals(rem.getSourceToken()))
	// {
	// removed.add((T) target);
	// }
	// }
	// }
	// }
	//
	// public Collection<T> getAdded()
	// {
	// return Collections.unmodifiableList(added);
	// }
	//
	// public Collection<T> getRemoved()
	// {
	// return Collections.unmodifiableList(removed);
	// }
	//
	// public boolean hasAddedItems()
	// {
	// return !added.isEmpty();
	// }
	//
	// public boolean hasRemovedItems()
	// {
	// return !removed.isEmpty();
	// }
	//
	// public boolean includesGlobalClear()
	// {
	// return clear;
	// }
	//
	// public boolean isEmpty()
	// {
	// return added.isEmpty() && removed.isEmpty();
	// }
	//		
	// }

	public interface Remover<T>
	{
		public boolean matches(T obj);
	}

	public void removePatternFromList(CDOMObject cdo, ListKey<?> lk,
			String pattern)
	{
		edits.removePatternFromList(cdo, lk, pattern);
	}

	public <T> PatternChanges<T> getListPatternChanges(CDOMObject cdo,
			ListKey<T> lk)
	{
		return getCommitStrategy().getListPatternChanges(cdo, lk);
	}

	public boolean wasRemoved(CDOMObject cdo, ObjectKey<?> ok)
	{
		return getCommitStrategy().wasRemoved(cdo, ok);
	}

	public boolean wasRemoved(CDOMObject cdo, StringKey sk)
	{
		return getCommitStrategy().wasRemoved(cdo, sk);
	}

	public boolean wasRemoved(CDOMObject cdo, IntegerKey ik)
	{
		return getCommitStrategy().wasRemoved(cdo, ik);
	}

	protected abstract ObjectCommitStrategy getCommitStrategy();

}
