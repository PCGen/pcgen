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
import pcgen.core.AgeSet;
import pcgen.core.BioSet;
import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;

public class AgeSetKitFacet implements DataFacetChangeListener<Integer>
{
	private final Class<?> thisClass = getClass();

	private final PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
			.getFacet(PlayerCharacterTrackingFacet.class);

	private AgeFacet ageFacet;

	private AgeSetFacet ageSetFacet;
	
	private BioSetFacet bioSetFacet;

	@Override
	public void dataAdded(DataFacetChangeEvent<Integer> dfce)
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

	@Override
	public void dataRemoved(DataFacetChangeEvent<Integer> dfce)
	{
		// CONSIDER Kits seem to be fire & forget - so nothing?
	}

	private CacheInfo getClassInfo(CharID id)
	{
		return (CacheInfo) FacetCache.get(id, thisClass);
	}

	private CacheInfo getConstructingClassInfo(CharID id)
	{
		CacheInfo info = getClassInfo(id);
		if (info == null)
		{
			info = new CacheInfo();
			FacetCache.set(id, thisClass, info);
		}
		return info;
	}

	private static class CacheInfo
	{

		private HashMapToList<AgeSet, Kit> kitMap = new HashMapToList<AgeSet, Kit>();

		public List<Kit> get(AgeSet ageSet)
		{
			return kitMap.getListFor(ageSet);
		}

		public void put(AgeSet ageSet, Collection<? extends Kit> choice)
		{
			kitMap.addAllToListFor(ageSet, choice);
		}
	}

	public void setAgeFacet(AgeFacet ageFacet)
	{
		this.ageFacet = ageFacet;
	}

	public void setAgeSetFacet(AgeSetFacet ageSetFacet)
	{
		this.ageSetFacet = ageSetFacet;
	}

	public void setBioSetFacet(BioSetFacet bioSetFacet)
	{
		this.bioSetFacet = bioSetFacet;
	}
	
	public void init()
	{
		ageFacet.addDataFacetChangeListener(this);
	}
}
