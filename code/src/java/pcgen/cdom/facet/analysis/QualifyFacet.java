/*
 * Copyright (c) Thomas Parker, 2009.
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
package pcgen.cdom.facet.analysis;

import java.util.List;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ClassIdentity;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.CDOMObjectConsolidationFacet;
import pcgen.cdom.facet.base.AbstractStorageFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.reference.Qualifier;

/**
 * QualifyFacet is a Facet that tracks the objects to which the Player Character
 * should Qualify.
 * 
 */
public class QualifyFacet extends AbstractStorageFacet<CharID> implements DataFacetChangeListener<CharID, CDOMObject>
{

	private CDOMObjectConsolidationFacet consolidationFacet;

	/**
	 * Adds the list of items a Player Character should qualify for to this
	 * QualifyFacet when a CDOMObject which grants such a qualify is added to a
	 * Player Character.
	 * 
	 * Triggered when one of the Facets to which QualifyFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		CDOMObject cdo = dfce.getCDOMObject();
		List<Qualifier> qualList = cdo.getListFor(ListKey.QUALIFY);
		CacheInfo ci = getConstructingCacheInfo(dfce.getCharID());

		if (qualList != null)
		{
			for (Qualifier q : qualList)
			{
				ci.add(q, cdo);
			}
		}
	}

	/**
	 * Removes the list of items a Player Character should qualify for from this
	 * QualifyFacet when a CDOMObject which grants such a qualify is removed
	 * from a Player Character.
	 * 
	 * Triggered when one of the Facets to which QualifyFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		CharID id = dfce.getCharID();
		CacheInfo ci = (CacheInfo) getCache(id);

		if (ci != null)
		{
			ci.removeAll(dfce.getCDOMObject());
		}
	}

	/**
	 * Returns a CacheInfo for this QualifyFacet and the PlayerCharacter
	 * represented by the given CharID. Will return a new, empty CacheInfo if no
	 * information has been set in this QualifyFacet for the given CharID. Will
	 * not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The CacheInfo object is owned
	 * by QualifyFacet, and since it can be modified, a reference to that object
	 * should not be exposed to any object other than QualifyFacet.
	 * 
	 * @param id
	 *            The CharID for which the Set should be returned
	 * @return The CacheInfo for the given object and Player Character
	 *         represented by the given CharID.
	 */
	private CacheInfo getConstructingCacheInfo(CharID id)
	{
		CacheInfo ci = (CacheInfo) getCache(id);
		if (ci == null)
		{
			ci = new CacheInfo();
			setCache(id, ci);
		}
		return ci;
	}

	/**
	 * Returns a CacheInfo for this QualifyFacet and the PlayerCharacter
	 * represented by the given CharID. Will return a null if no information has
	 * been set in this QualifyFacet for the given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The CacheInfo object is owned
	 * by QualifyFacet, and since it can be modified, a reference to that object
	 * should not be exposed to any object other than QualifyFacet.
	 * 
	 * @param id
	 *            The CharID for which the Set should be returned
	 * @return The CacheInfo for the given object and Player Character
	 *         represented by the given CharID.
	 */
	private CacheInfo getCacheInfo(CharID id)
	{
		return (CacheInfo) getCache(id);
	}

	/**
	 * Data structure used to store information for QualifyFacet (stores
	 * Qualifier objects and the sources of those Qualifier objects)
	 */
	private static class CacheInfo
	{
		private HashMapToList<String, Qualifier> hml = new HashMapToList<>();
		private HashMapToList<CDOMObject, Qualifier> sourceMap = new HashMapToList<>();

		/**
		 * Adds the given Qualifier to the CacheInfo, with the given source.
		 * 
		 * @param q
		 *            The Qualifier to be added to this CacheInfo
		 * @param source
		 *            The source for the Qualifier being added to this CacheInfo
		 */
		public void add(Qualifier q, CDOMObject source)
		{
			hml.addToListFor(q.getQualifiedReference().getPersistentFormat(), q);
			sourceMap.addToListFor(source, q);
		}

		/**
		 * Removes all Qualifier objects from this CacheInfo which have been
		 * granted from the given source CDOMObject.
		 * 
		 * @param object
		 *            The source CDOMObject for which all Qualifier objects will
		 *            be removed from this CacheInfo
		 */
		public void removeAll(CDOMObject object)
		{
			List<Qualifier> list = sourceMap.removeListFor(object);
			if (list != null)
			{
				for (Qualifier q : list)
				{
					hml.removeFromListFor(q.getQualifiedReference().getPersistentFormat(), q);
				}
			}
		}

		/**
		 * Returns true if the Player Character has been granted qualification
		 * for the given CDOMObject.
		 * 
		 * @param qualTestObject
		 *            The CDOMObject to check if the Player Character has been
		 *            granted qualification for the object
		 * @return true if the Player Character has been granted qualification
		 *         for the given CDOMObject; false otherwise
		 */
		public boolean isQualified(Loadable qualTestObject)
		{
			ClassIdentity<? extends Loadable> identity = qualTestObject.getClassIdentity();
			List<Qualifier> list = hml.getListFor(identity.getPersistentFormat());
			if (list != null)
			{
				for (Qualifier q : list)
				{
					CDOMReference qRef = q.getQualifiedReference();
					if (qRef.contains(qualTestObject))
					{
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public int hashCode()
		{
			return hml.hashCode();
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == this)
			{
				return true;
			}
			if (o instanceof CacheInfo ci)
			{
				return ci.hml.equals(hml) && ci.sourceMap.equals(sourceMap);
			}
			return false;
		}
	}

	/**
	 * Returns true if the Player Character identified by the given CharID has
	 * been granted qualification for the given CDOMObject.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            given CDOMObject will be checked to see if the Player
	 *            Character qualifies.
	 * @param qualTestObject
	 *            The CDOMObject to check if the Player Character has been
	 *            granted qualification for the object
	 * @return true if the Player Character identified by the given CharID has
	 *         been granted qualification for the given CDOMObject; false
	 *         otherwise
	 */
	public boolean grantsQualify(CharID id, CDOMObject qualTestObject)
	{
		CacheInfo ci = getCacheInfo(id);
		return (ci != null) && ci.isQualified(qualTestObject);
	}

	public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
	{
		this.consolidationFacet = consolidationFacet;
	}

	/**
	 * Initializes the connections for QualifyFacet to other facets.
	 * 
	 * This method is automatically called by the Spring framework during
	 * initialization of the QualifyFacet.
	 */
	public void init()
	{
		consolidationFacet.addDataFacetChangeListener(this);
	}

	public int getCount(CharID id)
	{
		CacheInfo ci = (CacheInfo) getCache(id);
		return (ci == null) ? 0 : ci.hml.size();
	}

	/**
	 * Copies the contents of the QualifyFacet from one Player Character to
	 * another Player Character, based on the given CharIDs representing those
	 * Player Characters.
	 * 
	 * This is a method in QualifyFacet in order to avoid exposing the mutable
	 * Map object to other classes. This should not be inlined, as the Map is
	 * internal information to QualifyFacet and should not be exposed to other
	 * classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the QualifyFacet of one Player
	 * Character will only impact the Player Character where the QualifyFacet
	 * was changed).
	 * 
	 * @param source
	 *            The CharID representing the Player Character from which the
	 *            information should be copied
	 * @param copy
	 *            The CharID representing the Player Character to which the
	 *            information should be copied
	 */
	@Override
	public void copyContents(CharID source, CharID copy)
	{
		CacheInfo ci = getCacheInfo(source);
		if (ci != null)
		{
			CacheInfo copyci = getConstructingCacheInfo(copy);
			copyci.hml.addAllLists(ci.hml);
			copyci.sourceMap.addAllLists(ci.sourceMap);
		}
	}
}
