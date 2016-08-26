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
import pcgen.base.util.Indirect;
import pcgen.base.util.TripleKeyMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.enumeration.FactSetKey;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.core.prereq.Prerequisite;
import pcgen.util.Logging;

public abstract class AbstractObjectContext implements ObjectCommitStrategy
{
	private final TrackingObjectCommitStrategy edits = new TrackingObjectCommitStrategy();

	URI getSourceURI()
	{
		return edits.getSourceURI();
	}

	@Override
	public void setSourceURI(URI sourceURI)
	{
		edits.setSourceURI(sourceURI);
		getCommitStrategy().setSourceURI(sourceURI);
	}

	URI getExtractURI()
	{
		return edits.getExtractURI();
	}

	@Override
	public void setExtractURI(URI extractURI)
	{
		edits.setExtractURI(extractURI);
		getCommitStrategy().setExtractURI(extractURI);
	}

	@Override
	public <T> void addToList(CDOMObject cdo, ListKey<T> key, T value)
	{
		edits.addToList(cdo, key, value);
	}

	@Override
	public <T> void addToSet(CDOMObject cdo, FactSetKey<T> key, Indirect<T> value)
	{
		edits.addToSet(cdo, key, value);
	}

	@Override
	public <K, V> void put(CDOMObject cdo, MapKey<K, V> mk, K key, V value)
	{
		edits.put(cdo, mk, key, value);
	}

	@Override
	public void put(CDOMObject cdo, FormulaKey fk, Formula f)
	{
		edits.put(cdo, fk, f);
	}

	@Override
	public void put(ConcretePrereqObject cpo, Prerequisite p)
	{
		edits.put(cpo, p);
	}

	@Override
	public void clearPrerequisiteList(ConcretePrereqObject cpo)
	{
		edits.clearPrerequisiteList(cpo);
	}

	@Override
	public void put(CDOMObject cdo, IntegerKey ik, Integer i)
	{
		edits.put(cdo, ik, i);
	}

	@Override
	public void remove(CDOMObject cdo, IntegerKey ik)
	{
		edits.remove(cdo, ik);
	}

	@Override
	public <T> void put(CDOMObject cdo, ObjectKey<T> sk, T s)
	{
		edits.put(cdo, sk, s);
	}

	@Override
	public void remove(CDOMObject cdo, ObjectKey<?> sk)
	{
		edits.remove(cdo, sk);
	}

	@Override
	public <T> void put(CDOMObject cdo, FactKey<T> sk, Indirect<T> s)
	{
		edits.put(cdo, sk, s);
	}

	@Override
	public void remove(CDOMObject cdo, FactKey<?> sk)
	{
		edits.remove(cdo, sk);
	}

	@Override
	public void put(CDOMObject cdo, StringKey sk, String s)
	{
		edits.put(cdo, sk, s);
	}

	@Override
	public void remove(CDOMObject cdo, StringKey sk)
	{
		edits.remove(cdo, sk);
	}

	@Override
	public void put(CDOMObject cdo, VariableKey vk, Formula f)
	{
		edits.put(cdo, vk, f);
	}

	@Override
	public <T> void removeFromList(CDOMObject cdo, ListKey<T> lk, T val)
	{
		edits.removeFromList(cdo, lk, val);
	}

	@Override
	public void removeList(CDOMObject cdo, ListKey<?> lk)
	{
		edits.removeList(cdo, lk);
	}

	@Override
	public <T> void removeFromSet(CDOMObject cdo, FactSetKey<T> lk, Indirect<T> val)
	{
		edits.removeFromSet(cdo, lk, val);
	}

	@Override
	public void removeSet(CDOMObject cdo, FactSetKey<?> lk)
	{
		edits.removeSet(cdo, lk);
	}

	@Override
	public <K, V> void remove(CDOMObject cdo, MapKey<K, V> mk, K key)
	{
		edits.remove(cdo, mk, key);
	}

	void commit()
	{
		ObjectCommitStrategy commit = getCommitStrategy();
		edits.preClearSet.getKeySet().forEach(uri ->
		{
			edits.preClearSet.getListFor(uri).forEach(commit::clearPrerequisiteList);
		});
		edits.globalClearSet.getKeySet().forEach(uri ->
		{
			edits.globalClearSet.getSecondaryKeySet(uri).forEach(cdo ->
			{
				edits.globalClearSet.getListFor(uri, cdo).forEach(lk ->
				{
					commit.removeList(cdo, lk);
				});
			});
		});
		edits.negativeMap.getKeySet().forEach(uri ->
		{
			edits.negativeMap
					.getSecondaryKeySet(uri).stream().filter(cpo -> cpo instanceof CDOMObject).forEach(cpo ->
			{
				CDOMObject cdo = (CDOMObject) cpo;
				CDOMObject neg = edits.negativeMap.get(uri, cdo);
				neg.getSafeListFor(ListKey.REMOVED_OBJECTKEY).forEach(ok ->
				{
					commit.remove(cdo, ok);
				});
				neg.getSafeListFor(ListKey.REMOVED_FACTKEY).forEach(ok ->
				{
					commit.remove(cdo, ok);
				});
				neg.getSafeListFor(ListKey.REMOVED_STRINGKEY).forEach(sk ->
				{
					commit.remove(cdo, sk);
				});
				neg.getSafeListFor(ListKey.REMOVED_INTEGERKEY).forEach(ik ->
				{
					commit.remove(cdo, ik);
				});
				neg.getFactSetKeys().forEach(key ->
				{
					removeFactSetKey(cdo, key, neg);
				});
				neg.getListKeys().forEach(key ->
				{
					removeListKey(cdo, key, neg);
				});
				neg.getMapKeys().forEach(key1 ->
				{
					removeMapKey(cdo, key1, neg);
				});
			});
		});
		/*
		 * No need to deal with ListMods because that's done in
		 * listContext
		 *//*
		  * TODO Deal with cloned objects
		  *//*
		   * No need to deal with ListMods because that's done in
		   * listContext
		   *//*
		    * TODO Deal with cloned objects
		    */
		edits.positiveMap.getKeySet().forEach(uri ->
		{
			/*
			 * No need to deal with ListMods because that's done in
			 * listContext
			 *//*
			  * TODO Deal with cloned objects
			  */
			edits.positiveMap
					.getSecondaryKeySet(uri).forEach(cpo ->
			{
				CDOMObject pos = edits.positiveMap.get(uri, cpo);
				pos.getPrerequisiteList().forEach(p ->
				{
					commit.put(cpo, p);
				});
				if (cpo instanceof CDOMObject)
				{
					CDOMObject cdo = (CDOMObject) cpo;
					pos.getStringKeys().forEach(key ->
					{
						commit.put(cdo, key, pos.get(key));
					});
					pos.getIntegerKeys().forEach(key ->
					{
						commit.put(cdo, key, pos.get(key));
					});
					pos.getFormulaKeys().forEach(key ->
					{
						commit.put(cdo, key, pos.get(key));
					});
					pos.getVariableKeys().forEach(key ->
					{
						commit.put(cdo, key, pos.get(key));
					});
					pos.getObjectKeys().forEach(key ->
					{
						putObjectKey(cdo, key, pos);
					});
					pos.getFactKeys().forEach(key ->
					{
						putFactKey(cdo, key, pos);
					});
					pos.getListKeys().forEach(key ->
					{
						putListKey(cdo, key, pos);
					});
					pos.getFactSetKeys().forEach(key ->
					{
						putFactSetKey(cdo, key, pos);
					});
					pos.getMapKeys().forEach(key1 ->
					{
						putMapKey(cdo, key1, pos);
					});
					/*
					 * No need to deal with ListMods because that's done in
					 * listContext
					 */
					/*
					 * TODO Deal with cloned objects
					 */
				}
			});
		});
		edits.patternClearSet.getKeySet().forEach(uri ->
		{
			edits.patternClearSet.getSecondaryKeySet(uri).forEach(cdo ->
			{
				edits.patternClearSet.getTertiaryKeySet(
						uri, cdo).forEach(lk ->
				{
					edits.patternClearSet.getListFor(uri, cdo,
							lk).forEach(s ->
					{
						commit.removePatternFromList(cdo, lk, s);
					});
				});
			});
		});
		rollback();
	}

	private <T> void removeListKey(CDOMObject cdo, ListKey<T> key, CDOMObject neg)
	{
		ObjectCommitStrategy commit = getCommitStrategy();
		neg.getListFor(key).forEach(obj ->
		{
			commit.removeFromList(cdo, key, obj);
		});
	}

	private <T> void removeFactSetKey(CDOMObject cdo, FactSetKey<T> key, CDOMObject neg)
	{
		ObjectCommitStrategy commit = getCommitStrategy();
		neg.getSetFor(key).forEach(obj ->
		{
			commit.removeFromSet(cdo, key, obj);
		});
	}

	private <T> void putListKey(CDOMObject cdo, ListKey<T> key, CDOMObject neg)
	{
		ObjectCommitStrategy commit = getCommitStrategy();
		neg.getListFor(key).forEach(obj ->
		{
			commit.addToList(cdo, key, obj);
		});
	}

	private <T> void putFactSetKey(CDOMObject cdo, FactSetKey<T> key, CDOMObject neg)
	{
		ObjectCommitStrategy commit = getCommitStrategy();
		neg.getSetFor(key).forEach(obj ->
		{
			commit.addToSet(cdo, key, obj);
		});
	}

	private <T> void putObjectKey(CDOMObject cdo, ObjectKey<T> key, CDOMObject neg)
	{
		getCommitStrategy().put(cdo, key, neg.get(key));
	}

	private <T> void putFactKey(CDOMObject cdo, FactKey<T> key, CDOMObject neg)
	{
		getCommitStrategy().put(cdo, key, neg.get(key));
	}

	private <K, V> void removeMapKey(CDOMObject cdo, MapKey<K, V> key1,
			CDOMObject neg)
	{
		ObjectCommitStrategy commit = getCommitStrategy();
		Set<K> secKeys = neg.getKeysFor(key1);
		secKeys.forEach(key2 ->
		{
			commit.remove(cdo, key1, key2);
		});
	}

	private <K, V> void putMapKey(CDOMObject cdo, MapKey<K, V> key1,
			CDOMObject pos)
	{
		ObjectCommitStrategy commit = getCommitStrategy();
		Set<K> secKeys = pos.getKeysFor(key1);
		secKeys.forEach(key2 ->
		{
			commit.put(cdo, key1, key2, pos.get(key1, key2));
		});
	}

	void rollback()
	{
		edits.decommit();
	}

	@Override
	public Formula getFormula(CDOMObject cdo, FormulaKey fk)
	{
		return getCommitStrategy().getFormula(cdo, fk);
	}

	@Override
	public Integer getInteger(CDOMObject cdo, IntegerKey ik)
	{
		return getCommitStrategy().getInteger(cdo, ik);
	}

	@Override
	public <T> Changes<T> getListChanges(CDOMObject cdo, ListKey<T> lk)
	{
		return getCommitStrategy().getListChanges(cdo, lk);
	}

	@Override
	public <T> Changes<Indirect<T>> getSetChanges(CDOMObject cdo, FactSetKey<T> lk)
	{
		return getCommitStrategy().getSetChanges(cdo, lk);
	}

	@Override
	public <K, V> MapChanges<K, V> getMapChanges(CDOMObject cdo, MapKey<K, V> mk)
	{
		return getCommitStrategy().getMapChanges(cdo, mk);
	}

	@Override
	public <T> T getObject(CDOMObject cdo, ObjectKey<T> ik)
	{
		return getCommitStrategy().getObject(cdo, ik);
	}

	@Override
	public <T> Indirect<T> getFact(CDOMObject cdo, FactKey<T> ik)
	{
		return getCommitStrategy().getFact(cdo, ik);
	}

	@Override
	public String getString(CDOMObject cdo, StringKey sk)
	{
		return getCommitStrategy().getString(cdo, sk);
	}

	@Override
	public Formula getVariable(CDOMObject obj, VariableKey key)
	{
		return getCommitStrategy().getVariable(obj, key);
	}

	@Override
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
		private final DoubleKeyMap<URI, ConcretePrereqObject, CDOMObject> positiveMap = new DoubleKeyMap<>(
                HashMap.class, IdentityHashMap.class);

		private final DoubleKeyMap<URI, ConcretePrereqObject, CDOMObject> negativeMap = new DoubleKeyMap<>(
                HashMap.class, IdentityHashMap.class);

		private final DoubleKeyMapToList<URI, CDOMObject, ListKey<?>> globalClearSet = new DoubleKeyMapToList<>(
                HashMap.class, IdentityHashMap.class);

		private final DoubleKeyMapToList<URI, CDOMObject, FactSetKey<?>> globalClearFactSet = new DoubleKeyMapToList<>(
                HashMap.class, IdentityHashMap.class);

		private final HashMapToList<URI, ConcretePrereqObject> preClearSet = new HashMapToList<>();

		private final TripleKeyMapToList<URI, CDOMObject, ListKey<?>, String> patternClearSet = new TripleKeyMapToList<>(
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
		public <T> void put(CDOMObject cdo, FactKey<T> sk, Indirect<T> s)
		{
			getPositive(sourceURI, cdo).put(sk, s);
		}

		@Override
		public void remove(CDOMObject cdo, FactKey<?> sk)
		{
			getNegative(sourceURI, cdo).addToListFor(ListKey.REMOVED_FACTKEY,
					sk);
		}

		@Override
		public boolean containsSetFor(CDOMObject cdo, FactSetKey<?> key)
		{
			return cdo.containsSetFor(key);
		}

		@Override
		public <T> void addToSet(CDOMObject cdo, FactSetKey<T> key, Indirect<T> value)
		{
			getPositive(sourceURI, cdo).addToSetFor(key, value);
		}

		@Override
		public void removeSet(CDOMObject cdo, FactSetKey<?> lk)
		{
			globalClearFactSet.addToListFor(sourceURI, cdo, lk);
		}

		@Override
		public <T> void removeFromSet(CDOMObject cdo, FactSetKey<T> lk, Indirect<T> val)
		{
			getNegative(sourceURI, cdo).addToSetFor(lk, val);
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
			return new MapChanges<>(getPositive(extractURI, cdo).getMapFor(
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
		public <T> Indirect<T> getFact(CDOMObject cdo, FactKey<T> ik)
		{
			return getPositive(extractURI, cdo).get(ik);
		}

		@Override
		public <T> Changes<T> getListChanges(CDOMObject cdo, ListKey<T> lk)
		{
			return new CollectionChanges<>(getPositive(extractURI, cdo)
                    .getListFor(lk), getNegative(extractURI, cdo)
                    .getListFor(lk), globalClearSet.containsInList(extractURI,
                    cdo, lk));
		}

		@Override
		public <T> Changes<Indirect<T>> getSetChanges(CDOMObject cdo, FactSetKey<T> lk)
		{
			return new CollectionChanges<>(getPositive(extractURI, cdo).getSetFor(lk),
                    getNegative(extractURI, cdo).getSetFor(lk),
                    globalClearFactSet.containsInList(extractURI, cdo, lk));
		}

		@Override
		public <T> PatternChanges<T> getListPatternChanges(CDOMObject cdo,
				ListKey<T> lk)
		{
			return new PatternChanges<>(getPositive(extractURI, cdo)
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
				Logging.errorPrint("Error instantiating " + cl.getSimpleName(), e);
			}
			catch (IllegalAccessException e)
			{
				Logging.errorPrint("Error instantiating " + cl.getSimpleName(), e);
			}
			return null;
		}

		@Override
		public Changes<Prerequisite> getPrerequisiteChanges(
				ConcretePrereqObject obj)
		{
			return new CollectionChanges<>(getPositive(extractURI,
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
		public boolean wasRemoved(CDOMObject cdo, FactKey<?> ok)
		{
			return getNegative(extractURI, cdo).containsInList(
					ListKey.REMOVED_FACTKEY, ok);
		}

		@Override
		public boolean wasRemoved(CDOMObject cdo, FactSetKey<?> ok)
		{
			return getNegative(extractURI, cdo).containsInList(
					ListKey.REMOVED_FACTSETKEY, ok);
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

	@Override
	public Changes<Prerequisite> getPrerequisiteChanges(ConcretePrereqObject obj)
	{
		return getCommitStrategy().getPrerequisiteChanges(obj);
	}

	@Override
	public boolean containsListFor(CDOMObject obj, ListKey<?> lk)
	{
		return getCommitStrategy().containsListFor(obj, lk);
	}

	@Override
	public boolean containsSetFor(CDOMObject obj, FactSetKey<?> lk)
	{
		return getCommitStrategy().containsSetFor(obj, lk);
	}

	@Override
	public <T> void removePatternFromList(CDOMObject cdo, ListKey<T> lk,
			String pattern)
	{
		edits.removePatternFromList(cdo, lk, pattern);
	}

	@Override
	public <T> PatternChanges<T> getListPatternChanges(CDOMObject cdo,
			ListKey<T> lk)
	{
		return getCommitStrategy().getListPatternChanges(cdo, lk);
	}

	@Override
	public boolean wasRemoved(CDOMObject cdo, ObjectKey<?> ok)
	{
		return getCommitStrategy().wasRemoved(cdo, ok);
	}

	@Override
	public boolean wasRemoved(CDOMObject cdo, FactKey<?> sk)
	{
		return getCommitStrategy().wasRemoved(cdo, sk);
	}

	@Override
	public boolean wasRemoved(CDOMObject cdo, FactSetKey<?> sk)
	{
		return getCommitStrategy().wasRemoved(cdo, sk);
	}

	@Override
	public boolean wasRemoved(CDOMObject cdo, StringKey sk)
	{
		return getCommitStrategy().wasRemoved(cdo, sk);
	}

	@Override
	public boolean wasRemoved(CDOMObject cdo, IntegerKey ik)
	{
		return getCommitStrategy().wasRemoved(cdo, ik);
	}

	protected abstract ObjectCommitStrategy getCommitStrategy();

}
