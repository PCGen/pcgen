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

import java.util.List;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.Region;
import pcgen.core.AgeSet;
import pcgen.core.BioSet;
import pcgen.core.Race;

public class AgeSetFacet extends AbstractItemFacet<AgeSet> implements
		DataFacetChangeListener<Object>
{
	private AgeFacet ageFacet;

	private RegionFacet regionFacet;

	private RaceFacet raceFacet;

	private BioSetFacet bioSetFacet;

	@Override
	public void dataAdded(DataFacetChangeEvent<Object> dfce)
	{
		update(dfce.getCharID());
	}

	private void update(CharID id)
	{
		Region region = Region.getConstant(regionFacet.getRegion(id));
		set(id, bioSetFacet.get(id).getAgeSet(region, getAgeSetIndex(id)));
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<Object> dfce)
	{
		update(dfce.getCharID());
	}

	public int getAgeSetIndex(CharID id)
	{
		BioSet bioSet = bioSetFacet.get(id);
		String region = regionFacet.getRegion(id);
		Race race = raceFacet.get(id);
		String raceName = race == null ? "" : race.getKeyName().trim();
		List<String> values = bioSet.getValueInMaps(region, raceName, "BASEAGE");
		if (values == null)
		{
			return 0;
		}

		int pcAge = ageFacet.getAge(id);
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

	public void setAgeFacet(AgeFacet ageFacet)
	{
		this.ageFacet = ageFacet;
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
	
}
