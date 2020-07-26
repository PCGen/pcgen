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
package pcgen.cdom.facet.analysis;

import java.util.List;
import java.util.Optional;

import pcgen.cdom.base.ItemFacet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.Region;
import pcgen.cdom.facet.base.AbstractItemFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.fact.RegionFacet;
import pcgen.cdom.facet.model.BioSetFacet;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.util.CControl;
import pcgen.core.AgeSet;
import pcgen.core.BioSet;
import pcgen.core.Race;
import pcgen.output.channel.ChannelUtilities;
import pcgen.output.publish.OutputDB;

/**
 * AgeSetFacet stores the AgeSet for the Player Character.
 * 
 */
public class AgeSetFacet extends AbstractItemFacet<CharID, AgeSet>
		implements DataFacetChangeListener<CharID, Object>, ItemFacet<CharID, AgeSet>
{
	private RegionFacet regionFacet;

	private RaceFacet raceFacet;

	private BioSetFacet bioSetFacet;

	/**
	 * Drives the identification of the active AgeSet for a Player Character
	 * when certain changes are made to a Player Character.
	 * 
	 * Triggered when one of the Facets to which AgeSetFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CharID, Object> dfce)
	{
		update(dfce.getCharID());
	}

	/**
	 * This method simply drives a global update of the active AgeSet of a
	 * PlayerCharacter. This is asserted to be simpler to comprehend than
	 * attempting to do a change-by-change matrix of possible moves between
	 * AgeSets (which would have to be built from the BioSet).
	 * 
	 * Since the processing to determine the AgeSet is reasonably simple, since
	 * this facet will only throw an event if the AgeSet actually changes, and
	 * since there are a disparate set of objects that can cause a change in the
	 * AgeSet (e.g. change of Race, change of Age, change of Region), the
	 * quantity of extra facets to listen to those changes in a unique fashion
	 * and the complex storage of AgeSets would seem an unreasonable trade in
	 * complexity vs. this global update.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            active AgeSet should be established (and updated if necessary)
	 */
	private void update(CharID id)
	{
		Optional<Region> region = regionFacet.getRegion(id);
		AgeSet ageSet = bioSetFacet.get(id).getAgeSet(region, getAgeSetIndex(id));
		if (ageSet == null)
		{
			remove(id);
		}
		else
		{
			set(id, ageSet);
		}
	}

	/**
	 * Drives the identification of the active AgeSet for a Player Character
	 * when certain changes are made to a Player Character.
	 * 
	 * Triggered when one of the Facets to which AgeSetFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<CharID, Object> dfce)
	{
		update(dfce.getCharID());
	}

	/**
	 * Returns the index of the active AgeSet on the Player Character.
	 * 
	 * In general, use of this method outside of AgeSetFacet is discouraged. If
	 * this method is being used, a serious analysis should be taking place to
	 * determine if the AgeSet itself (in other words, the get method of
	 * AgeSetFacet) can be used instead.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            index of the active AgeSet should be returned.
	 * @return The index of the active AgeSet on the Player Character.
	 */
	public int getAgeSetIndex(CharID id)
	{
		BioSet bioSet = bioSetFacet.get(id);
		Optional<Region> region = regionFacet.getRegion(id);
		Race race = raceFacet.get(id);
		String raceName = race == null ? "" : race.getKeyName().trim();
		List<String> values = bioSet.getValueInMaps(region, raceName, "BASEAGE");
		if (values == null)
		{
			return 0;
		}

		int pcAge = (Integer) ChannelUtilities.readControlledChannel(id, CControl.AGEINPUT);
		int ageSet = -1;

		for (String s : values)
		{
			int setBaseAge = Integer.parseInt(s);

			if (pcAge < setBaseAge)
			{
				break;
			}

			++ageSet;
		}

		//
		// Check to see if character is younger than earliest age group
		//
		if (ageSet < 0)
		{
			ageSet = 0;
		}

		return ageSet;
	}

	public void setRegionFacet(RegionFacet regionFacet)
	{
		this.regionFacet = regionFacet;
	}

	public void setRaceFacet(RaceFacet raceFacet)
	{
		this.raceFacet = raceFacet;
	}

	public void setBioSetFacet(BioSetFacet bioSetFacet)
	{
		this.bioSetFacet = bioSetFacet;
	}

	/**
	 * Initializes the connections for AgeSetFacet to other facets.
	 * 
	 * This method is automatically called by the Spring framework during
	 * initialization of the AgeSetFacet.
	 */
	public void init()
	{
		raceFacet.addDataFacetChangeListener(this);
		regionFacet.addDataFacetChangeListener(this);
		bioSetFacet.addDataFacetChangeListener(this);
		OutputDB.register("ageset", this);
	}
}
