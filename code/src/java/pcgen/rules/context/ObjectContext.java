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
import pcgen.base.util.TripleKeyMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.core.prereq.Prerequisite;

public class ObjectContext
{
	private final TrackingObjectCommitStrategy edits = new TrackingObjectCommitStrategy();

	private final ObjectCommitStrategy commit;

	public ObjectContext()
	{
		commit = new TrackingObjectCommitStrategy();
	}

	public ObjectContext(ObjectCommitStrategy commitStrategy)
	{
		if (commitStrategy == null)
		{
			throw new IllegalArgumentException("Commit Strategy cannot be null");
		}
		commit = commitStrategy;
	}

	public URI getSourceURI()
	{
		return edits.getSourceURI();
	}

	public void setSourceURI(URI sourceURI)
	{
		edits.setSourceURI(sourceURI);
		commit.setSourceURI(sourceURI);
	}

	public URI getExtractURI()
	{
		return edits.getExtractURI();
	}

	public void setExtractURI(URI extractURI)
	{
		edits.setExtractURI(extractURI);
		commit.setExtractURI(extractURI);
	}

	public <T> void addToList(CDOMObject cdo, ListKey<T> key, T value)
	{
		edits.addToList(cdo, key, value);
	}

	public void put(CDOMObject cdo, FormulaKey fk, Formula f)
	{
		edits.put(cdo, fk, f);
	}

	public void put(ConcretePrereqObject cpo, Prerequisite p)
	{
		edits.put(cpo, p);
	}

	public void put(CDOMObject cdo, IntegerKey ik, Integer i)
	{
		edits.put(cdo, ik, i);
	}

	public <T> void put(CDOMObject cdo, ObjectKey<T> sk, T s)
	{
		edits.put(cdo, sk, s);
	}

	public void put(CDOMObject cdo, StringKey sk, String s)
	{
		edits.put(cdo, sk, s);
	}

	public void put(CDOMObject cdo, VariableKey vk, Formula f)
	{
		edits.put(cdo, vk, f);
	}

//	public void give(String sourceToken, CDOMObject cdo, PrereqObject target)
//	{
//		addToList(cdo, ListKey.GIVEN, new SourceWrapper(target, sourceToken));
//	}
//	
//	public void revoke(String sourceToken, CDOMObject cdo, PrereqObject target)
//	{
//		removeFromList(cdo, ListKey.GIVEN, new SourceWrapper(target, sourceToken));
//	}
//
//	public void revokeAll(final String tokenName, CDOMObject cdo)
//	{
//		/*
//		 * TODO This is broken for the ConsolidatedObjectCommitStrategy, as it 
//		 * doesn't properly filter by tokenName...
//		 */
//		removeList(cdo, ListKey.GIVEN);
//	}

	public <T> void removeFromList(CDOMObject cdo, ListKey<T> lk, T val)
	{
		edits.removeFromList(cdo, lk, val);
	}

	public void removeList(CDOMObject cdo, ListKey<?> lk)
	{
		edits.removeList(cdo, lk);
	}

	public void commit()
	{
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
					for (StringKey key : neg.getStringKeys())
					{
						commit.put(cdo, key, null);
					}
					for (ListKey<?> key : neg.getListKeys())
					{
						removeListKey(cdo, key, neg);
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
					/*
					 * TODO CDOM List Mods
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
				for (ListKey<?> lk : edits.patternClearSet.getTertiaryKeySet(uri, cdo))
				{
					for (String s : edits.patternClearSet.getListFor(uri, cdo, lk))
					{
						commit.removePatternFromList(cdo, lk, s);
					}
				}
			}
		}
		decommit();
	}

	private <T> void removeListKey(CDOMObject cdo, ListKey<T> key,
			CDOMObject neg)
	{
		for (T obj : neg.getListFor(key))
		{
			commit.removeFromList(cdo, key, obj);
		}
	}

	private <T> void putListKey(CDOMObject cdo, ListKey<T> key, CDOMObject neg)
	{
		for (T obj : neg.getListFor(key))
		{
			commit.addToList(cdo, key, obj);
		}
	}

	private <T> void putObjectKey(CDOMObject cdo, ObjectKey<T> key,
			CDOMObject neg)
	{
		commit.put(cdo, key, neg.get(key));
	}

	public void decommit()
	{
		edits.decommit();
	}

	public Formula getFormula(CDOMObject cdo, FormulaKey fk)
	{
		return commit.getFormula(cdo, fk);
	}

	public Integer getInteger(CDOMObject cdo, IntegerKey ik)
	{
		return commit.getInteger(cdo, ik);
	}

	public <T> Changes<T> getListChanges(CDOMObject cdo, ListKey<T> lk)
	{
		return commit.getListChanges(cdo, lk);
	}

//	public <T> Changes<T> getGivenChanges(String sourceToken, CDOMObject cdo, Class<T> cl)
//	{
//		return new GivenChanges<T>(cl, sourceToken, commit.getListChanges(cdo, ListKey.GIVEN));
//	}
//
	public <T> T getObject(CDOMObject cdo, ObjectKey<T> ik)
	{
		return commit.getObject(cdo, ik);
	}

	public String getString(CDOMObject cdo, StringKey sk)
	{
		return commit.getString(cdo, sk);
	}

	public Formula getVariable(CDOMObject obj, VariableKey key)
	{
		return commit.getVariable(obj, key);
	}

	public Set<VariableKey> getVariableKeys(CDOMObject obj)
	{
		return commit.getVariableKeys(obj);
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
		private static String CLEAR = ".CLEAR";

		private DoubleKeyMap<URI, ConcretePrereqObject, CDOMObject> positiveMap = new DoubleKeyMap<URI, ConcretePrereqObject, CDOMObject>(
				HashMap.class, IdentityHashMap.class);

		private DoubleKeyMap<URI, ConcretePrereqObject, CDOMObject> negativeMap = new DoubleKeyMap<URI, ConcretePrereqObject, CDOMObject>(
				HashMap.class, IdentityHashMap.class);

		private DoubleKeyMapToList<URI, CDOMObject, ListKey<?>> globalClearSet = new DoubleKeyMapToList<URI, CDOMObject, ListKey<?>>(
				HashMap.class, IdentityHashMap.class);

		private TripleKeyMapToList<URI, CDOMObject, ListKey<?>, String> patternClearSet = new TripleKeyMapToList<URI, CDOMObject, ListKey<?>, String>(
				HashMap.class, IdentityHashMap.class, HashMap.class);

		private URI sourceURI;

		private URI extractURI;

		private CDOMObject getNegative(URI source, CDOMObject cdo)
		{
			CDOMObject negative = negativeMap.get(source, cdo);
			if (negative == null)
			{
				negative = new SimpleCDOMObject();
				negativeMap.put(source, cdo, negative);
			}
			return negative;
		}

		public void put(ConcretePrereqObject cpo, Prerequisite p)
		{
			getPositive(sourceURI, cpo).addPrerequisite(p);
		}

		private CDOMObject getPositive(URI source, ConcretePrereqObject cdo)
		{
			CDOMObject positive = positiveMap.get(source, cdo);
			if (positive == null)
			{
				positive = new SimpleCDOMObject();
				positiveMap.put(source, cdo, positive);
			}
			return positive;
		}

		public void put(CDOMObject cdo, StringKey sk, String s)
		{
			if (s == null)
			{
				getNegative(sourceURI, cdo).put(sk, Constants.LST_DOT_CLEAR);
				cdo.remove(sk);
			}
			else if (s.startsWith(Constants.LST_DOT_CLEAR))
			{
				throw new IllegalArgumentException("Cannot set a value to " + s);
			}
			else
			{
				getPositive(sourceURI, cdo).put(sk, s);
			}
		}

		public <T> void put(CDOMObject cdo, ObjectKey<T> sk, T s)
		{
			getPositive(sourceURI, cdo).put(sk, s);
		}

		public void put(CDOMObject cdo, IntegerKey ik, Integer i)
		{
			getPositive(sourceURI, cdo).put(ik, i);
		}

		public void put(CDOMObject cdo, FormulaKey fk, Formula f)
		{
			getPositive(sourceURI, cdo).put(fk, f);
		}

		public void put(CDOMObject cdo, VariableKey vk, Formula f)
		{
			getPositive(sourceURI, cdo).put(vk, f);
		}

		public boolean containsListFor(CDOMObject cdo, ListKey<?> key)
		{
			return cdo.containsListFor(key);
		}

		public <T> void addToList(CDOMObject cdo, ListKey<T> key, T value)
		{
			getPositive(sourceURI, cdo).addToListFor(key, value);
		}

		public void removeList(CDOMObject cdo, ListKey<?> lk)
		{
			globalClearSet.addToListFor(sourceURI, cdo, lk);
		}

		public <T> void removeFromList(CDOMObject cdo, ListKey<T> lk, T val)
		{
			getNegative(sourceURI, cdo).addToListFor(lk, val);
		}

		public String getString(CDOMObject cdo, StringKey sk)
		{
			String added = getPositive(extractURI, cdo).get(sk);
			boolean hasClear = CLEAR.equals(getNegative(extractURI, cdo)
					.get(sk));
			if (hasClear)
			{
				if (added == null)
				{
					return Constants.LST_DOT_CLEAR;
				}
				else
				{
					return null;
				}
			}
			else
			{
				if (added == null)
				{
					return null;
				}
				else
				{
					return added;
				}
			}
		}

		public Integer getInteger(CDOMObject cdo, IntegerKey ik)
		{
			return getPositive(extractURI, cdo).get(ik);
		}

		public Formula getFormula(CDOMObject cdo, FormulaKey fk)
		{
			return getPositive(extractURI, cdo).get(fk);
		}

		public Formula getVariable(CDOMObject cdo, VariableKey key)
		{
			return getPositive(extractURI, cdo).get(key);
		}

		public Set<VariableKey> getVariableKeys(CDOMObject cdo)
		{
			return getPositive(extractURI, cdo).getVariableKeys();
		}

		public <T> T getObject(CDOMObject cdo, ObjectKey<T> ik)
		{
			return getPositive(extractURI, cdo).get(ik);
		}

		public <T> Changes<T> getListChanges(CDOMObject cdo, ListKey<T> lk)
		{
			return new CollectionChanges<T>(getPositive(extractURI, cdo)
					.getListFor(lk), getNegative(extractURI, cdo)
					.getListFor(lk), globalClearSet.containsInList(extractURI,
					cdo, lk));
		}

		public URI getExtractURI()
		{
			return extractURI;
		}

		public void setExtractURI(URI extractURI)
		{
			this.extractURI = extractURI;
		}

		public URI getSourceURI()
		{
			return sourceURI;
		}

		public void setSourceURI(URI sourceURI)
		{
			this.sourceURI = sourceURI;
		}

		public void decommit()
		{
			positiveMap.clear();
			negativeMap.clear();
			globalClearSet.clear();
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

		public Changes<Prerequisite> getPrerequisiteChanges(
				ConcretePrereqObject obj)
		{
			return new CollectionChanges<Prerequisite>(getPositive(extractURI,
					obj).getPrerequisiteList(), null, false);
		}

		public <T> void removePatternFromList(CDOMObject cdo, ListKey<T> lk,
				String pattern)
		{
			patternClearSet.addToListFor(sourceURI, cdo, lk, pattern);
		}
	}

	public Changes<Prerequisite> getPrerequisiteChanges(ConcretePrereqObject obj)
	{
		return commit.getPrerequisiteChanges(obj);
	}

	public boolean containsListFor(CDOMObject obj, ListKey<?> lk)
	{
		return commit.containsListFor(obj, lk);
	}

//	private class GivenChanges<T> implements Changes<T>
//	{
//
//		private final ArrayList<T> added = new ArrayList<T>();
//		private final ArrayList<T> removed = new ArrayList<T>();
//		private final String token;
//		private final Class<T> targetClass;
//		private final boolean clear;
//		
//		public GivenChanges(Class<T> cl, String sourceToken,
//				Changes<SourceWrapper> listChanges)
//		{
//			targetClass = cl;
//			token = sourceToken;
//			clear = listChanges.includesGlobalClear();
//			Collection<SourceWrapper> allAdded = listChanges.getAdded();
//			if (allAdded != null)
//			{
//				for (SourceWrapper add : allAdded)
//				{
//					PrereqObject target = add.getTarget();
//					if (targetClass.isAssignableFrom(target.getClass())
//							&& token.equals(add.getSourceToken()))
//					{
//						added.add((T) target);
//					}
//				}
//			}
//			Collection<SourceWrapper> allRemoved = listChanges.getRemoved();
//			if (allRemoved != null)
//			{
//				for (SourceWrapper rem : allRemoved)
//				{
//					PrereqObject target = rem.getTarget();
//					if (targetClass.equals(target.getClass())
//							&& token.equals(rem.getSourceToken()))
//					{
//						removed.add((T) target);
//					}
//				}
//			}
//		}
//
//		public Collection<T> getAdded()
//		{
//			return Collections.unmodifiableList(added);
//		}
//
//		public Collection<T> getRemoved()
//		{
//			return Collections.unmodifiableList(removed);
//		}
//
//		public boolean hasAddedItems()
//		{
//			return !added.isEmpty();
//		}
//
//		public boolean hasRemovedItems()
//		{
//			return !removed.isEmpty();
//		}
//
//		public boolean includesGlobalClear()
//		{
//			return clear;
//		}
//
//		public boolean isEmpty()
//		{
//			return added.isEmpty() && removed.isEmpty();
//		}
//		
//	}
	
	public interface Remover<T>
	{
		public boolean matches(T obj);
	}

	public void removePatternFromList(CDOMObject cdo,
			ListKey<?> lk, String pattern)
	{
		edits.removePatternFromList(cdo, lk, pattern);
	}
}
