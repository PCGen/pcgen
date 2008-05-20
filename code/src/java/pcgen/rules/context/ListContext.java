package pcgen.rules.context;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.base.util.TreeMapToList;
import pcgen.base.util.TripleKeyMap;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.SimpleAssociatedObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.rules.persistence.TokenUtilities;

public class ListContext
{

//	private static final CDOMReference<? extends CDOMList<CDOMObject>> GRANTED = new CDOMDirectSingleRef<CDOMList<CDOMObject>>(
//			new GrantedList());

	private final TrackingListCommitStrategy edits = new TrackingListCommitStrategy();

	private final ListCommitStrategy commit;

	public ListContext()
	{
		commit = new TrackingListCommitStrategy();
	}

	public ListContext(ListCommitStrategy commitStrategy)
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

	public <T extends CDOMObject> AssociatedPrereqObject addToMasterList(
			String tokenName, CDOMObject owner,
			CDOMReference<? extends CDOMList<T>> list, T allowed)
	{
		return edits.addToMasterList(tokenName, owner, list, allowed);
	}

	public void clearAllMasterLists(String tokenName, CDOMObject owner)
	{
		edits.clearAllMasterLists(tokenName, owner);
	}

	public <T extends CDOMObject> void clearMasterList(String tokenName,
			CDOMObject owner, CDOMReference<? extends CDOMList<T>> list)
	{
		edits.clearMasterList(tokenName, owner, list);
	}

	public <T extends CDOMObject> AssociatedPrereqObject addToList(
			String tokenName, CDOMObject owner,
			CDOMReference<? extends CDOMList<? super T>> list,
			CDOMReference<T> allowed)
	{
		return edits.addToList(tokenName, owner, list, allowed);
	}

	public <T extends CDOMObject> void removeFromList(String tokenName,
			CDOMObject owner,
			CDOMReference<? extends CDOMList<? super T>> list,
			CDOMReference<T> ref)
	{
		edits.removeFromList(tokenName, owner, list, ref);
	}

	public void removeAllFromList(String tokenName, CDOMObject owner,
			CDOMReference<? extends CDOMList<?>> swl)
	{
		edits.removeAllFromList(tokenName, owner, swl);
	}

//	public <T extends CDOMObject> AssociatedPrereqObject grant(
//			String sourceToken, CDOMObject obj, CDOMReference<T> pro)
//	{
//		return addToList(sourceToken, obj, GRANTED, pro);
//	}
//
//	public <T extends CDOMObject> void remove(String sourceToken,
//			CDOMObject obj, CDOMReference<T> pro)
//	{
//		removeFromList(sourceToken, obj, GRANTED, pro);
//	}
//
//	public void removeAll(String tokenName, CDOMObject obj)
//	{
//		removeAllFromList(tokenName, obj, GRANTED);
//	}

//	public <T extends PrereqObject> AssociatedChanges<CDOMReference<T>> getChangesFromToken(
//			String tokenName, CDOMObject source, Class<T> cl)
//	{
//		AssociatedChanges<CDOMReference<CDOMObject>> assoc = getChangesInList(
//				tokenName, source, GRANTED);
//		boolean globalClear = assoc.includesGlobalClear();
//		MapToList<CDOMReference<CDOMObject>, AssociatedPrereqObject> added = assoc
//				.getAddedAssociations();
//		MapToList<CDOMReference<T>, AssociatedPrereqObject> add = new TreeMapToList<CDOMReference<T>, AssociatedPrereqObject>(
//				TokenUtilities.REFERENCE_SORTER);
//		if (added != null)
//		{
//			for (CDOMReference<CDOMObject> key : added.getKeySet())
//			{
//				if (cl.equals(key.getReferenceClass()))
//				{
//					add.addAllToListFor((CDOMReference<T>) key, added.getListFor(key));
//				}
//			}
//		}
//
//		MapToList<CDOMReference<T>, AssociatedPrereqObject> remove = new TreeMapToList<CDOMReference<T>, AssociatedPrereqObject>(
//				TokenUtilities.REFERENCE_SORTER);
//		MapToList<CDOMReference<CDOMObject>, AssociatedPrereqObject> removed = assoc
//				.getRemovedAssociations();
//		if (removed != null)
//		{
//			for (CDOMReference<CDOMObject> key : removed.getKeySet())
//			{
//				if (cl.equals(key.getReferenceClass()))
//				{
//					remove.addAllToListFor((CDOMReference<T>) key, removed.getListFor(key));
//				}
//			}
//		}
//
//		return new AssociatedCollectionChanges<CDOMReference<T>>(add, remove, globalClear);
//	}

	public void commit()
	{
		for (CDOMReference list : edits.positiveMasterMap.getKeySet())
		{
			commitDirect(list);
		}
		for (URI uri : edits.globalClearSet.getKeySet())
		{
			for (CDOMObject owner : edits.globalClearSet
					.getSecondaryKeySet(uri))
			{
				for (CDOMReference<? extends CDOMList<?>> list : edits.globalClearSet
						.getListFor(uri, owner))
				{
					commit.removeAllFromList("FOO", owner, list);
				}
			}
		}
		for (URI uri : edits.negativeMap.getKeySet())
		{
			for (CDOMObject owner : edits.negativeMap.getSecondaryKeySet(uri))
			{
				CDOMObject neg = edits.negativeMap.get(uri, owner);
				Collection<CDOMReference<? extends CDOMList<? extends CDOMObject>>> modifiedLists = neg
						.getModifiedLists();
				for (CDOMReference list : modifiedLists)
				{
					remove(owner, neg, list);
				}
			}
		}
		for (URI uri : edits.positiveMap.getKeySet())
		{
			for (CDOMObject owner : edits.positiveMap.getSecondaryKeySet(uri))
			{
				CDOMObject neg = edits.positiveMap.get(uri, owner);
				Collection<CDOMReference<? extends CDOMList<? extends CDOMObject>>> modifiedLists = neg
						.getModifiedLists();
				for (CDOMReference list : modifiedLists)
				{
					add(owner, neg, list);
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
		decommit();
	}

	private <T extends CDOMObject> void commitDirect(
			CDOMReference<? extends CDOMList<T>> list)
	{
		for (OwnerURI ou : edits.positiveMasterMap.getSecondaryKeySet(list))
		{
			for (CDOMObject child : edits.positiveMasterMap.getTertiaryKeySet(
					list, ou))
			{
				AssociatedPrereqObject assoc = edits.positiveMasterMap.get(
						list, ou, child);
				AssociatedPrereqObject edge = commit.addToMasterList(assoc
						.getAssociation(AssociationKey.TOKEN), ou.owner, list,
						(T) child);
				Collection<AssociationKey<?>> associationKeys = assoc
						.getAssociationKeys();
				for (AssociationKey<?> ak : associationKeys)
				{
					setAssoc(assoc, edge, ak);
				}
				edge.addAllPrerequisites(assoc.getPrerequisiteList());
			}
		}
	}

	public void decommit()
	{
		edits.decommit();
	}

	public Collection<CDOMReference<? extends CDOMList<? extends CDOMObject>>> getChangedLists(
			CDOMObject owner, Class<? extends CDOMList<?>> cl)
	{
		return commit.getChangedLists(owner, cl);
	}

	public <T extends CDOMObject> AssociatedChanges<CDOMReference<T>> getChangesInList(
			String tokenName, CDOMObject owner,
			CDOMReference<? extends CDOMList<T>> swl)
	{
		return commit.getChangesInList(tokenName, owner, swl);
	}

	public <T extends CDOMObject> AssociatedChanges<T> getChangesInMasterList(
			String tokenName, CDOMObject owner,
			CDOMReference<? extends CDOMList<T>> swl)
	{
		return commit.getChangesInMasterList(tokenName, owner, swl);
	}

	public Changes<CDOMReference> getMasterListChanges(String tokenName,
			CDOMObject owner, Class<? extends CDOMList<?>> cl)
	{
		return commit.getMasterListChanges(tokenName, owner, cl);
	}

	public boolean hasMasterLists()
	{
		return commit.hasMasterLists();
	}

	private <BT extends CDOMObject> void remove(CDOMObject owner,
			CDOMObject neg, CDOMReference<CDOMList<BT>> list)
	{
		Collection<CDOMReference<BT>> mods = neg.getListMods(list);
		for (CDOMReference<BT> ref : mods)
		{
			for (AssociatedPrereqObject assoc : neg.getListAssociations(list,
					ref))
			{
				commit
						.removeFromList(assoc
								.getAssociation(AssociationKey.TOKEN), owner,
								list, ref);
			}
		}
	}

	private <BT extends CDOMObject> void add(CDOMObject owner, CDOMObject neg,
			CDOMReference<CDOMList<BT>> list)
	{
		Collection<CDOMReference<BT>> mods = neg.getListMods(list);
		for (CDOMReference<BT> ref : mods)
		{
			for (AssociatedPrereqObject assoc : neg.getListAssociations(list,
					ref))
			{
				String token = assoc.getAssociation(AssociationKey.TOKEN);
				AssociatedPrereqObject edge = commit.addToList(token, owner,
						list, ref);
				Collection<AssociationKey<?>> associationKeys = assoc
						.getAssociationKeys();
				for (AssociationKey<?> ak : associationKeys)
				{
					setAssoc(assoc, edge, ak);
				}
				edge.addAllPrerequisites(assoc.getPrerequisiteList());
			}
		}
	}

	private <T> void setAssoc(AssociatedPrereqObject assoc,
			AssociatedPrereqObject edge, AssociationKey<T> ak)
	{
		edge.setAssociation(ak, assoc.getAssociation(ak));
	}

	public class TrackingListCommitStrategy implements ListCommitStrategy
	{

		protected class CDOMShell extends CDOMObject
		{
			@Override
			public CDOMObject clone() throws CloneNotSupportedException
			{
				throw new CloneNotSupportedException();
			}

			@Override
			public boolean isType(String str)
			{
				return false;
			}
		}

		private URI sourceURI;

		private URI extractURI;

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

		/*
		 * TODO These maps (throughout this entire class) are probably problems
		 * because they are not using Identity characteristics
		 */
		private TripleKeyMap<CDOMReference<? extends CDOMList<?>>, OwnerURI, CDOMObject, AssociatedPrereqObject> positiveMasterMap = new TripleKeyMap<CDOMReference<? extends CDOMList<?>>, OwnerURI, CDOMObject, AssociatedPrereqObject>();

		private HashMapToList<CDOMReference<? extends CDOMList<?>>, OwnerURI> masterClearSet = new HashMapToList<CDOMReference<? extends CDOMList<?>>, OwnerURI>();

		private HashMapToList<String, OwnerURI> masterAllClear = new HashMapToList<String, OwnerURI>();

		public <T extends CDOMObject> AssociatedPrereqObject addToMasterList(
				String tokenName, CDOMObject owner,
				CDOMReference<? extends CDOMList<T>> list, T allowed)
		{
			SimpleAssociatedObject a = new SimpleAssociatedObject();
			a.setAssociation(AssociationKey.OWNER, owner);
			a.setAssociation(AssociationKey.TOKEN, tokenName);
			positiveMasterMap.put(list, new OwnerURI(sourceURI, owner),
					allowed, a);
			return a;
		}

		public Changes<CDOMReference> getMasterListChanges(String tokenName,
				CDOMObject owner, Class<? extends CDOMList<?>> cl)
		{
			OwnerURI lo = new OwnerURI(extractURI, owner);
			ArrayList<CDOMReference> list = new ArrayList<CDOMReference>();
			Set<CDOMReference<? extends CDOMList<?>>> set = positiveMasterMap
					.getKeySet();
			if (set != null)
			{
				LIST: for (CDOMReference<? extends CDOMList<?>> ref : set)
				{
					if (!cl.equals(ref.getReferenceClass()))
					{
						continue;
					}
					for (CDOMObject allowed : positiveMasterMap
							.getTertiaryKeySet(ref, lo))
					{
						AssociatedPrereqObject assoc = positiveMasterMap.get(
								ref, lo, allowed);
						if (owner.equals(assoc
								.getAssociation(AssociationKey.OWNER))
								&& tokenName.equals(assoc
										.getAssociation(AssociationKey.TOKEN)))
						{
							list.add(ref);
							continue LIST;
						}
					}
				}
			}
			return new CollectionChanges<CDOMReference>(list, null,
					masterAllClear.containsInList(tokenName, lo));
		}

		public <T extends CDOMObject> AssociatedChanges<T> getChangesInMasterList(
				String tokenName, CDOMObject owner,
				CDOMReference<? extends CDOMList<T>> swl)
		{
			MapToList<T, AssociatedPrereqObject> map = new TreeMapToList<T, AssociatedPrereqObject>(
					TokenUtilities.WRITEABLE_SORTER);
			OwnerURI lo = new OwnerURI(extractURI, owner);
			Set<CDOMObject> added = positiveMasterMap
					.getTertiaryKeySet(swl, lo);
			for (CDOMObject lw : added)
			{
				AssociatedPrereqObject apo = positiveMasterMap.get(swl, lo, lw);
				if (tokenName.equals(apo.getAssociation(AssociationKey.TOKEN)))
				{
					map.addToListFor((T) lw, apo);
				}
			}
			return new AssociatedCollectionChanges<T>(map, null, masterClearSet
					.containsInList(swl, lo));
		}

		public <T extends CDOMObject> void clearMasterList(String tokenName,
				CDOMObject owner, CDOMReference<? extends CDOMList<T>> list)
		{
			masterClearSet.addToListFor(list, new OwnerURI(sourceURI, owner));
		}

		public void clearAllMasterLists(String tokenName, CDOMObject owner)
		{
			masterAllClear.addToListFor(tokenName, new OwnerURI(sourceURI,
					owner));
		}

		private DoubleKeyMap<URI, CDOMObject, CDOMObject> positiveMap = new DoubleKeyMap<URI, CDOMObject, CDOMObject>();

		private DoubleKeyMap<URI, CDOMObject, CDOMObject> negativeMap = new DoubleKeyMap<URI, CDOMObject, CDOMObject>();

		private DoubleKeyMapToList<URI, CDOMObject, CDOMReference<? extends CDOMList<?>>> globalClearSet = new DoubleKeyMapToList<URI, CDOMObject, CDOMReference<? extends CDOMList<?>>>();

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

		public <T extends CDOMObject> AssociatedPrereqObject addToList(
				String tokenName, CDOMObject owner,
				CDOMReference<? extends CDOMList<? super T>> list,
				CDOMReference<T> allowed)
		{
			SimpleAssociatedObject a = new SimpleAssociatedObject();
			a.setAssociation(AssociationKey.TOKEN, tokenName);
			getPositive(sourceURI, owner).putToList(list, allowed, a);
			return a;
		}

		public <T extends CDOMObject> void removeFromList(String tokenName,
				CDOMObject owner,
				CDOMReference<? extends CDOMList<? super T>> list,
				CDOMReference<T> ref)
		{
			SimpleAssociatedObject a = new SimpleAssociatedObject();
			a.setAssociation(AssociationKey.TOKEN, tokenName);
			getNegative(sourceURI, owner).putToList(list, ref, a);
		}

		public Collection<CDOMReference<? extends CDOMList<? extends CDOMObject>>> getChangedLists(
				CDOMObject owner, Class<? extends CDOMList<?>> cl)
		{
			ArrayList<CDOMReference<? extends CDOMList<? extends CDOMObject>>> list = new ArrayList<CDOMReference<? extends CDOMList<? extends CDOMObject>>>();
			for (CDOMReference<? extends CDOMList<? extends CDOMObject>> ref : getPositive(
					extractURI, owner).getModifiedLists())
			{
				if (cl.equals(ref.getReferenceClass()))
				{
					list.add(ref);
				}
			}
			return list;
		}

		public void removeAllFromList(String tokenName, CDOMObject owner,
				CDOMReference<? extends CDOMList<?>> swl)
		{
			globalClearSet.addToListFor(sourceURI, owner, swl);
		}

		public <T extends CDOMObject> AssociatedChanges<CDOMReference<T>> getChangesInList(
				String tokenName, CDOMObject owner,
				CDOMReference<? extends CDOMList<T>> swl)
		{
			return new ListChanges<T>(tokenName,
					getPositive(extractURI, owner), getNegative(extractURI,
							owner), swl, globalClearSet.containsInList(
							extractURI, owner, swl));
		}

		public boolean hasMasterLists()
		{
			return !positiveMasterMap.isEmpty() && !masterClearSet.isEmpty()
					&& !masterAllClear.isEmpty();
		}

		public void decommit()
		{
			masterAllClear.clear();
			masterClearSet.clear();
			positiveMasterMap.clear();
			positiveMap.clear();
			negativeMap.clear();
			globalClearSet.clear();
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
}
