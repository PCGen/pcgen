/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.HashMapToList;
import pcgen.base.util.ListSet;
import pcgen.base.util.MapToList;
import pcgen.base.util.TreeMapToList;
import pcgen.base.util.TripleKeyMap;
import pcgen.base.util.TripleKeyMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.MasterListInterface;
import pcgen.cdom.base.SimpleAssociatedObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.SettingsHandler;

public abstract class AbstractListContext
{

	private final TrackingListCommitStrategy edits = new TrackingListCommitStrategy();

	void setSourceURI(URI sourceURI)
	{
		edits.setSourceURI(sourceURI);
		getCommitStrategy().setSourceURI(sourceURI);
	}

	void setExtractURI(URI extractURI)
	{
		edits.setExtractURI(extractURI);
		getCommitStrategy().setExtractURI(extractURI);
	}

	public <T extends CDOMObject> AssociatedPrereqObject addToMasterList(String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> list, T allowed)
	{
		return edits.addToMasterList(tokenName, owner, list, allowed);
	}

	public <T extends CDOMObject> void removeFromMasterList(String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> list, T allowed)
	{
		edits.removeFromMasterList(tokenName, owner, list, allowed);
	}

	public void clearAllMasterLists(String tokenName, CDOMObject owner)
	{
		edits.clearAllMasterLists(tokenName, owner);
	}

	public <T extends CDOMObject> AssociatedPrereqObject addToList(String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<? super T>> list, CDOMReference<T> allowed)
	{
		return edits.addToList(tokenName, owner, list, allowed);
	}

	public <T extends CDOMObject> AssociatedPrereqObject removeFromList(String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<? super T>> list, CDOMReference<T> ref)
	{
		return edits.removeFromList(tokenName, owner, list, ref);
	}

	public void removeAllFromList(String tokenName, CDOMObject owner, CDOMReference<? extends CDOMList<?>> swl)
	{
		edits.removeAllFromList(tokenName, owner, swl);
	}

	void commit()
	{
		ListCommitStrategy commit = getCommitStrategy();
		for (CDOMReference<? extends CDOMList<?>> list : edits.positiveMasterMap.getKeySet())
		{
			//Note: Intentional Generics Violation due to Sun Compiler
			commitDirect((CDOMReference) list);
		}
		for (CDOMReference<? extends CDOMList<?>> list : edits.negativeMasterMap.getKeySet())
		{
			//Note: Intentional Generics Violation due to Sun Compiler
			removeDirect((CDOMReference) list);
		}
		for (URI uri : edits.globalClearSet.getKeySet())
		{
			for (CDOMObject owner : edits.globalClearSet.getSecondaryKeySet(uri))
			{
				for (String tokenName : edits.globalClearSet.getTertiaryKeySet(uri, owner))
				{
					for (CDOMReference<? extends CDOMList<?>> list : edits.globalClearSet.getListFor(uri, owner,
						tokenName))
					{
						commit.removeAllFromList(tokenName, owner, list);
					}
				}
			}
		}
		for (URI uri : edits.negativeMap.getKeySet())
		{
			for (CDOMObject owner : edits.negativeMap.getSecondaryKeySet(uri))
			{
				CDOMObject neg = edits.negativeMap.get(uri, owner);
				Collection<CDOMReference<? extends CDOMList<?>>> modifiedLists = neg.getModifiedLists();
				for (CDOMReference<? extends CDOMList<?>> list : modifiedLists)
				{
					//Note: Intentional Generics Violation due to Sun Compiler
					remove(owner, neg, (CDOMReference) list);
				}
			}
		}
		for (URI uri : edits.positiveMap.getKeySet())
		{
			for (CDOMObject owner : edits.positiveMap.getSecondaryKeySet(uri))
			{
				CDOMObject neg = edits.positiveMap.get(uri, owner);
				Collection<CDOMReference<? extends CDOMList<?>>> modifiedLists = neg.getModifiedLists();
				for (CDOMReference<? extends CDOMList<?>> list : modifiedLists)
				{
					//Note: Intentional Generics Violation due to Sun Compiler
					add(owner, neg, (CDOMReference) list);
				}
			}
		}
		for (String token : edits.masterAllClear.getKeySet())
		{
			for (OwnerURI ou : edits.masterAllClear.getListFor(token))
			{
				commit.clearAllMasterLists(token, ou.owner);
			}
		}
		rollback();
	}

	private <T extends CDOMObject, L extends CDOMList<T>> void commitDirect(CDOMReference<L> list)
	{
		ListCommitStrategy commit = getCommitStrategy();
		for (OwnerURI ou : edits.positiveMasterMap.getSecondaryKeySet(list))
		{
			for (CDOMObject child : edits.positiveMasterMap.getTertiaryKeySet(list, ou))
			{
				AssociatedPrereqObject assoc = edits.positiveMasterMap.get(list, ou, child);
				AssociatedPrereqObject edge =
						commit.addToMasterList(assoc.getAssociation(AssociationKey.TOKEN), ou.owner, list, (T) child);
				Collection<AssociationKey<?>> associationKeys = assoc.getAssociationKeys();
				for (AssociationKey<?> ak : associationKeys)
				{
					setAssoc(assoc, edge, ak);
				}
				edge.addAllPrerequisites(assoc.getPrerequisiteList());
			}
		}
	}

	private <T extends CDOMObject, U extends CDOMList<T>> void removeDirect(CDOMReference<U> list)
	{
		ListCommitStrategy commit = getCommitStrategy();
		for (OwnerURI ou : edits.negativeMasterMap.getSecondaryKeySet(list))
		{
			for (CDOMObject child : edits.negativeMasterMap.getTertiaryKeySet(list, ou))
			{
				AssociatedPrereqObject assoc = edits.negativeMasterMap.get(list, ou, child);
				commit.removeFromMasterList(assoc.getAssociation(AssociationKey.TOKEN), ou.owner, list, (T) child);
			}
		}
	}

	void rollback()
	{
		edits.decommit();
	}

	public Collection<CDOMReference<? extends CDOMList<?>>> getChangedLists(CDOMObject owner,
		Class<? extends CDOMList<?>> cl)
	{
		return getCommitStrategy().getChangedLists(owner, cl);
	}

	public <T extends CDOMObject> AssociatedChanges<CDOMReference<T>> getChangesInList(String tokenName,
		CDOMObject owner, CDOMReference<? extends CDOMList<T>> swl)
	{
		return getCommitStrategy().getChangesInList(tokenName, owner, swl);
	}

	public <T extends CDOMObject> AssociatedChanges<T> getChangesInMasterList(String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> swl)
	{
		return getCommitStrategy().getChangesInMasterList(tokenName, owner, swl);
	}

	public <T extends CDOMList<?>> Changes<CDOMReference<T>> getMasterListChanges(String tokenName, CDOMObject owner,
		Class<T> cl)
	{
		return getCommitStrategy().getMasterListChanges(tokenName, owner, cl);
	}

	public boolean hasMasterLists()
	{
		return getCommitStrategy().hasMasterLists();
	}

	private <BT extends CDOMObject, L extends CDOMList<BT>> void remove(CDOMObject owner, CDOMObject neg,
		CDOMReference<L> list)
	{
		ListCommitStrategy commit = getCommitStrategy();
		Collection<CDOMReference<BT>> mods = neg.getListMods(list);
		for (CDOMReference<BT> ref : mods)
		{
			for (AssociatedPrereqObject assoc : neg.getListAssociations(list, ref))
			{
				String token = assoc.getAssociation(AssociationKey.TOKEN);
				AssociatedPrereqObject edge = commit.removeFromList(token, owner, list, ref);
				Collection<AssociationKey<?>> associationKeys = assoc.getAssociationKeys();
				for (AssociationKey<?> ak : associationKeys)
				{
					setAssoc(assoc, edge, ak);
				}
				edge.addAllPrerequisites(assoc.getPrerequisiteList());
			}
		}
	}

	private <BT extends CDOMObject, L extends CDOMList<BT>> void add(CDOMObject owner, CDOMObject neg,
		CDOMReference<L> list)
	{
		ListCommitStrategy commit = getCommitStrategy();
		Collection<CDOMReference<BT>> mods = neg.getListMods(list);
		for (CDOMReference<BT> ref : mods)
		{
			for (AssociatedPrereqObject assoc : neg.getListAssociations(list, ref))
			{
				String token = assoc.getAssociation(AssociationKey.TOKEN);
				AssociatedPrereqObject edge = commit.addToList(token, owner, list, ref);
				Collection<AssociationKey<?>> associationKeys = assoc.getAssociationKeys();
				for (AssociationKey<?> ak : associationKeys)
				{
					setAssoc(assoc, edge, ak);
				}
				edge.addAllPrerequisites(assoc.getPrerequisiteList());
			}
		}
	}

	private <T> void setAssoc(AssociatedPrereqObject assoc, AssociatedPrereqObject edge, AssociationKey<T> ak)
	{
		edge.setAssociation(ak, assoc.getAssociation(ak));
	}

	public static class TrackingListCommitStrategy implements ListCommitStrategy
	{

		private final DoubleKeyMap<URI, CDOMObject, CDOMObject> positiveMap =
				new DoubleKeyMap<>(HashMap.class, IdentityHashMap.class);

		private final DoubleKeyMap<URI, CDOMObject, CDOMObject> negativeMap =
				new DoubleKeyMap<>(HashMap.class, IdentityHashMap.class);

		private final TripleKeyMapToList<URI, CDOMObject, String, CDOMReference<? extends CDOMList<?>>> globalClearSet =
				new TripleKeyMapToList<>(HashMap.class, IdentityHashMap.class, HashMap.class);

		/*
		 * TODO These maps (throughout this entire class) are probably problems
		 * because they are not using Identity characteristics
		 */
		private final TripleKeyMap<CDOMReference<? extends CDOMList<?>>, OwnerURI, CDOMObject, AssociatedPrereqObject> positiveMasterMap =
				new TripleKeyMap<>(); //HashMap.class, HashMap.class, IdentityHashMap.class);

		private final TripleKeyMap<CDOMReference<? extends CDOMList<?>>, OwnerURI, CDOMObject, AssociatedPrereqObject> negativeMasterMap =
				new TripleKeyMap<>(); //HashMap.class, HashMap.class, IdentityHashMap.class);

		private final HashMapToList<CDOMReference<? extends CDOMList<?>>, OwnerURI> masterClearSet =
				new HashMapToList<>();

		private final HashMapToList<String, OwnerURI> masterAllClear = new HashMapToList<>();

		private URI sourceURI;

		private URI extractURI;

		protected static class CDOMShell extends CDOMObject implements Cloneable
		{
			@Override
			public CDOMShell clone() throws CloneNotSupportedException
			{
				throw new CloneNotSupportedException();
			}

			@Override
			public boolean isType(String str)
			{
				return false;
			}
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

		@Override
		public <T extends CDOMObject> AssociatedPrereqObject addToMasterList(String tokenName, CDOMObject owner,
			CDOMReference<? extends CDOMList<T>> list, T allowed)
		{
			SimpleAssociatedObject a = new SimpleAssociatedObject();
			a.setAssociation(AssociationKey.OWNER, owner);
			a.setAssociation(AssociationKey.TOKEN, tokenName);
			positiveMasterMap.put(list, new OwnerURI(sourceURI, owner), allowed, a);
			return a;
		}

		@Override
		public <T extends CDOMObject> void removeFromMasterList(String tokenName, CDOMObject owner,
			CDOMReference<? extends CDOMList<T>> list, T allowed)
		{
			SimpleAssociatedObject a = new SimpleAssociatedObject();
			a.setAssociation(AssociationKey.OWNER, owner);
			a.setAssociation(AssociationKey.TOKEN, tokenName);
			negativeMasterMap.put(list, new OwnerURI(sourceURI, owner), allowed, a);
		}

		@Override
		public <T extends CDOMList<?>> Changes<CDOMReference<T>> getMasterListChanges(String tokenName,
			CDOMObject owner, Class<T> cl)
		{
			OwnerURI lo = new OwnerURI(extractURI, owner);
			TreeSet<CDOMReference<T>> list = new TreeSet<>(ReferenceUtilities.REFERENCE_SORTER);
			Set<CDOMReference<? extends CDOMList<?>>> set = positiveMasterMap.getKeySet();
			if (set != null)
			{
				LIST: for (CDOMReference<? extends CDOMList<?>> ref : set)
				{
					if (!cl.equals(ref.getReferenceClass()))
					{
						continue;
					}
					@SuppressWarnings("unchecked")
					CDOMReference<T> tr = (CDOMReference<T>) ref;
					for (CDOMObject allowed : positiveMasterMap.getTertiaryKeySet(tr, lo))
					{
						AssociatedPrereqObject assoc = positiveMasterMap.get(tr, lo, allowed);
						if (owner.equals(assoc.getAssociation(AssociationKey.OWNER))
							&& tokenName.equals(assoc.getAssociation(AssociationKey.TOKEN)))
						{
							list.add(tr);
							continue LIST;
						}
					}
				}
			}
			set = negativeMasterMap.getKeySet();
			ArrayList<CDOMReference<T>> removelist = new ArrayList<>();
			if (set != null)
			{
				LIST: for (CDOMReference<? extends CDOMList<?>> ref : set)
				{
					if (!cl.equals(ref.getReferenceClass()))
					{
						continue;
					}
					@SuppressWarnings("unchecked")
					CDOMReference<T> tr = (CDOMReference<T>) ref;
					for (CDOMObject allowed : negativeMasterMap.getTertiaryKeySet(tr, lo))
					{
						AssociatedPrereqObject assoc = negativeMasterMap.get(tr, lo, allowed);
						if (owner.equals(assoc.getAssociation(AssociationKey.OWNER))
							&& tokenName.equals(assoc.getAssociation(AssociationKey.TOKEN)))
						{
							removelist.add(tr);
							continue LIST;
						}
					}
				}
			}
			return new CollectionChanges<>(list, removelist, masterAllClear.containsInList(tokenName, lo));
		}

		@Override
		public <T extends CDOMObject> AssociatedChanges<T> getChangesInMasterList(String tokenName, CDOMObject owner,
			CDOMReference<? extends CDOMList<T>> swl)
		{
			MapToList<T, AssociatedPrereqObject> map = new TreeMapToList<>(CDOMObjectUtilities::compareKeys);
			OwnerURI lo = new OwnerURI(extractURI, owner);
			Set<CDOMObject> added = positiveMasterMap.getTertiaryKeySet(swl, lo);
			for (CDOMObject lw : added)
			{
				AssociatedPrereqObject apo = positiveMasterMap.get(swl, lo, lw);
				if (tokenName.equals(apo.getAssociation(AssociationKey.TOKEN)))
				{
					map.addToListFor((T) lw, apo);
				}
			}
			MapToList<T, AssociatedPrereqObject> rmap = new TreeMapToList<>(CDOMObjectUtilities::compareKeys);
			Set<CDOMObject> removed = negativeMasterMap.getTertiaryKeySet(swl, lo);
			for (CDOMObject lw : removed)
			{
				AssociatedPrereqObject apo = negativeMasterMap.get(swl, lo, lw);
				if (tokenName.equals(apo.getAssociation(AssociationKey.TOKEN)))
				{
					rmap.addToListFor((T) lw, apo);
				}
			}
			return new AssociatedCollectionChanges<>(map, rmap, masterClearSet.containsInList(swl, lo));
		}

		public <T extends CDOMObject> void clearMasterList(String tokenName, CDOMObject owner,
			CDOMReference<? extends CDOMList<T>> list)
		{
			masterClearSet.addToListFor(list, new OwnerURI(sourceURI, owner));
		}

		@Override
		public void clearAllMasterLists(String tokenName, CDOMObject owner)
		{
			masterAllClear.addToListFor(tokenName, new OwnerURI(sourceURI, owner));
		}

		private CDOMObject getPositive(URI source, CDOMObject cdo)
		{
			CDOMObject positive = positiveMap.get(source, cdo);
			if (positive == null)
			{
				positive = new CDOMShell();
				positiveMap.put(source, cdo, positive);
			}
			return positive;
		}

		private CDOMObject getNegative(URI source, CDOMObject cdo)
		{
			CDOMObject negative = negativeMap.get(source, cdo);
			if (negative == null)
			{
				negative = new CDOMShell();
				negativeMap.put(source, cdo, negative);
			}
			return negative;
		}

		@Override
		public <T extends CDOMObject> AssociatedPrereqObject addToList(String tokenName, CDOMObject owner,
			CDOMReference<? extends CDOMList<? super T>> list, CDOMReference<T> allowed)
		{
			SimpleAssociatedObject a = new SimpleAssociatedObject();
			a.setAssociation(AssociationKey.TOKEN, tokenName);
			getPositive(sourceURI, owner).putToList(list, allowed, a);
			return a;
		}

		@Override
		public <T extends CDOMObject> AssociatedPrereqObject removeFromList(String tokenName, CDOMObject owner,
			CDOMReference<? extends CDOMList<? super T>> list, CDOMReference<T> ref)
		{
			SimpleAssociatedObject a = new SimpleAssociatedObject();
			a.setAssociation(AssociationKey.TOKEN, tokenName);
			getNegative(sourceURI, owner).putToList(list, ref, a);
			return a;
		}

		@Override
		public Collection<CDOMReference<? extends CDOMList<?>>> getChangedLists(CDOMObject owner,
			Class<? extends CDOMList<?>> cl)
		{
			Set<CDOMReference<? extends CDOMList<?>>> list = new ListSet<>();
			for (CDOMReference<? extends CDOMList<?>> ref : getPositive(extractURI, owner).getModifiedLists())
			{
				if (cl.equals(ref.getReferenceClass()))
				{
					list.add(ref);
				}
			}
			for (CDOMReference<? extends CDOMList<?>> ref : getNegative(extractURI, owner).getModifiedLists())
			{
				if (cl.equals(ref.getReferenceClass()))
				{
					list.add(ref);
				}
			}

			Set<String> globalClearTokenKeys = globalClearSet.getTertiaryKeySet(extractURI, owner);
			for (String key : globalClearTokenKeys)
			{
				List<CDOMReference<? extends CDOMList<?>>> globalClearList =
						globalClearSet.getListFor(extractURI, owner, key);
				if (globalClearList != null)
				{
					for (CDOMReference<? extends CDOMList<?>> ref : globalClearList)
					{
						if (cl.equals(ref.getReferenceClass()))
						{
							list.add(ref);
						}
					}
				}
			}
			return list;
		}

		@Override
		public void removeAllFromList(String tokenName, CDOMObject owner, CDOMReference<? extends CDOMList<?>> swl)
		{
			globalClearSet.addToListFor(sourceURI, owner, tokenName, swl);
		}

		@Override
		public <T extends CDOMObject> AssociatedChanges<CDOMReference<T>> getChangesInList(String tokenName,
			CDOMObject owner, CDOMReference<? extends CDOMList<T>> swl)
		{
			boolean hasGlobalClear = globalClearSet.containsListFor(extractURI, owner, tokenName)
				&& globalClearSet.getListFor(extractURI, owner, tokenName).contains(swl);

			return new ListChanges<>(tokenName, getPositive(extractURI, owner), getNegative(extractURI, owner), swl,
				hasGlobalClear);
		}

		@Override
		public boolean hasMasterLists()
		{
			return !positiveMasterMap.isEmpty() && !masterClearSet.isEmpty() && !masterAllClear.isEmpty();
		}

		public void decommit()
		{
			masterAllClear.clear();
			masterClearSet.clear();
			positiveMasterMap.clear();
			negativeMasterMap.clear();
			positiveMap.clear();
			negativeMap.clear();
			globalClearSet.clear();
		}

		@Override
		public boolean equalsTracking(ListCommitStrategy obj)
		{
			if (obj instanceof TrackingListCommitStrategy)
			{
				TrackingListCommitStrategy other = (TrackingListCommitStrategy) obj;
				return other.masterAllClear.equals(this.masterAllClear)
					&& other.masterClearSet.equals(this.masterClearSet)
					&& other.positiveMasterMap.equals(this.positiveMasterMap)
					&& other.negativeMasterMap.equals(this.negativeMasterMap);
			}
			return false;
		}

		public void purge(CDOMObject cdo)
		{
			positiveMap.remove(sourceURI, cdo);
			negativeMap.remove(sourceURI, cdo);
			globalClearSet.removeListsFor(sourceURI, cdo);
		}
	}

	private static class OwnerURI
	{
		public final CDOMObject owner;
		public final URI source;

		public OwnerURI(URI sourceURI, CDOMObject cdo)
		{
			source = sourceURI;
			owner = cdo;
		}

		@Override
		public int hashCode()
		{
			return owner.hashCode();
		}

		@Override
		public boolean equals(Object o)
		{
			if (o instanceof OwnerURI)
			{
				OwnerURI other = (OwnerURI) o;
				if (source == null)
				{
					if (other.source != null)
					{
						return false;
					}
				}
				else
				{
					if (!source.equals(other.source))
					{
						return false;
					}
				}
				return owner.equals(other.owner);
			}
			return false;
		}
	}

	public boolean masterListsEqual(AbstractListContext lc)
	{
		return getCommitStrategy().equalsTracking(lc.getCommitStrategy());
	}

	protected abstract ListCommitStrategy getCommitStrategy();

	/**
	 * Create a copy of any associations to the original object and link them 
	 * to the new object. This will scan lists such as ClassSpellLists and 
	 * DomainSpellLists which may link to the original object. For each 
	 * association found, a new association will be created linking to the new object 
	 * and the association will be added to the list.
	 * 
	 * @param <T>    The type of CDOMObject being copied (e.g. Spell, Domain etc)
	 * @param cdoOld The original object being copied. 
	 * @param cdoNew The new object to be linked in.
	 */
	@SuppressWarnings("unchecked")
	<T extends CDOMObject> void cloneInMasterLists(T cdoOld, T cdoNew)
	{
		MasterListInterface masterLists = SettingsHandler.getGameAsProperty().get().getMasterLists();
		for (CDOMReference ref : masterLists.getActiveLists())
		{
			Collection<AssociatedPrereqObject> assocs = masterLists.getAssociations(ref, cdoOld);
			if (assocs != null)
			{
				for (AssociatedPrereqObject apo : assocs)
				{
					//					Logging.debugPrint("Found assoc from " + ref + " to "
					//							+ apo.getAssociationKeys() + " / "
					//							+ apo.getAssociation(AssociationKey.OWNER));
					AssociatedPrereqObject newapo = getCommitStrategy()
						.addToMasterList(apo.getAssociation(AssociationKey.TOKEN), cdoNew, ref, cdoNew);
					newapo.addAllPrerequisites(apo.getPrerequisiteList());
					for (AssociationKey assocKey : apo.getAssociationKeys())
					{
						if ((assocKey != AssociationKey.TOKEN) && (assocKey != AssociationKey.OWNER))
						{
							newapo.setAssociation(assocKey, apo.getAssociation(assocKey));
						}
					}
				}
			}
		}
	}
}
