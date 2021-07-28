/*
 * Copyright (c) Thomas Parker, 2010.
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
package pcgen.cdom.facet;

import java.util.Collection;
import java.util.List;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.analysis.AgeSetFacet;
import pcgen.cdom.facet.base.AbstractStorageFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.BioSetFacet;
import pcgen.core.AgeSet;
import pcgen.core.BioSet;
import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;

/**
 * AgeSetKitFacet stores
 * 
 */
public class AgeSetKitFacet extends AbstractStorageFacet<CharID> implements DataFacetChangeListener<CharID, Integer>
{
	private final PlayerCharacterTrackingFacet trackingFacet =
			FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

	private AgeSetFacet ageSetFacet;

	private BioSetFacet bioSetFacet;

	/**
	 * Drives the selection of the AgeSet Kit for a Player Character when
	 * relevant changes (change to an AgeSet) are made to a Player Character.
	 * 
	 * Triggered when one of the Facets to which AgeSetKitFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CharID, Integer> dfce)
	{
		CharID id = dfce.getCharID();
		AgeSet ageSet = ageSetFacet.get(id);
		PlayerCharacter pc = trackingFacet.getPC(id);
		// TODO Is ageSet null check necessary?
		if (ageSet == null || pc.isImporting())
		{
			return;
		}
		int ageSetIndex = ageSetFacet.getAgeSetIndex(id);
		/*
		 * TODO The method of storing what AgeSets have had kit selections made
		 * should be converted to store the actual AgeSet rather than the index,
		 * in order to reduce the number of calls to ageSetFacet.getAgeSetIndex.
		 * This (of course) drives the move of the AgeSets for which a kit
		 * selection has been made into a Facet. It is possible that the
		 * CacheInfo of AgeSetKitFacet is actually a good place to store that
		 * information (or it may be implicit with the information already
		 * stored there??)
		 */
		if (!pc.hasMadeKitSelectionForAgeSet(ageSetIndex))
		{
			CacheInfo cache = getConstructingClassInfo(id);
			List<Kit> kits = cache.get(ageSet);
			if (kits != null)
			{
				// Need to do selection
				BioSet bioSet = bioSetFacet.get(id);
				for (TransitionChoice<Kit> kit : ageSet.getKits())
				{
					Collection<? extends Kit> choice = kit.driveChoice(pc);
					cache.put(ageSet, choice);
					kit.act(choice, bioSet, pc);
				}
			}
			pc.setHasMadeKitSelectionForAgeSet(ageSetIndex, true);
		}
	}

	/**
	 * Triggered when one of the Facets to which AgeSetKitFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<CharID, Integer> dfce)
	{
		/*
		 * CONSIDER Kits seem to be fire & forget - so nothing? Probably not, as
		 * if the age is changed downward, it's likely that the kits from the
		 * "older" ages should be removed...
		 */
	}

	/**
	 * Returns the Cached Info for this AgeSetKitFacet and the given CharID. May
	 * return null if no information has been set in this AgeSetKitFacet for the
	 * given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The CacheInfo is owned by
	 * AgeSetKitFacet, and since it can be modified, a reference to that object
	 * should not be exposed to any object other than AgeSetKitFacet.
	 * 
	 * @param id
	 *            The CharID for which the CacheInfo should be returned
	 * @return The CacheInfo for the Player Character represented by the given
	 *         CharID; null if no information has been set in this
	 *         AgeSetKitFacet for the Player Character.
	 */
	private CacheInfo getClassInfo(CharID id)
	{
		return (CacheInfo) getCache(id);
	}

	/**
	 * Returns a non-null CacheInfo for this AgeSetKitFacet and the given
	 * CharID. Will return a new, empty CacheInfo if no information has been set
	 * in this AgeSetKitFacet for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The CacheInfo object is owned
	 * by AgeSetKitFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than AgeSetKitFacet.
	 * 
	 * @param id
	 *            The CharID for which the CacheInfo should be returned
	 * @return The CacheInfo for the Player Character represented by the given
	 *         CharID.
	 */
	private CacheInfo getConstructingClassInfo(CharID id)
	{
		CacheInfo info = getClassInfo(id);
		if (info == null)
		{
			info = new CacheInfo();
			setCache(id, info);
		}
		return info;
	}

	/**
	 * CacheInfo is the data structure used to store information in
	 * AgeSetKitFacet. This class should not be exposed outside of
	 * AgeSetKitFacet.
	 * 
	 */
	private static class CacheInfo
	{

		private HashMapToList<AgeSet, Kit> kitMap = new HashMapToList<>();

		public List<Kit> get(AgeSet ageSet)
		{
			return kitMap.getListFor(ageSet);
		}

		public void put(AgeSet ageSet, Collection<? extends Kit> choice)
		{
			kitMap.addAllToListFor(ageSet, choice);
		}
	}

	public void setAgeSetFacet(AgeSetFacet ageSetFacet)
	{
		this.ageSetFacet = ageSetFacet;
	}

	public void setBioSetFacet(BioSetFacet bioSetFacet)
	{
		this.bioSetFacet = bioSetFacet;
	}

	/**
	 * Copies the contents of the AgeSetKitFacet from one Player Character to
	 * another Player Character, based on the given CharIDs representing those
	 * Player Characters.
	 * 
	 * This is a method in AgeSetKitFacet in order to avoid exposing the mutable
	 * Collection object to other classes. This should not be inlined, as the
	 * Collection is internal information to AgeSetKitFacet and should not be
	 * exposed to other classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the AgeSetKitFacet of one
	 * Player Character will only impact the Player Character where the
	 * AgeSetKitFacet was changed).
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
		CacheInfo ci = getClassInfo(source);
		if (ci != null)
		{
			CacheInfo copyci = getConstructingClassInfo(copy);
			copyci.kitMap.addAllLists(ci.kitMap);
		}
	}
}
