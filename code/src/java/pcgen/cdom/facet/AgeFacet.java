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
import pcgen.core.BioSet;

public class AgeFacet extends AbstractItemFacet<Integer>
{
	private final RegionFacet regionFacet = FacetLibrary
			.getFacet(RegionFacet.class);
	private final RaceFacet raceFacet = FacetLibrary.getFacet(RaceFacet.class);
	private final BioSetFacet bioSetFacet = FacetLibrary
			.getFacet(BioSetFacet.class);

	public int getAgeSet(CharID id)
	{
		BioSet bioSet = bioSetFacet.get(id);
		List<String> values = bioSet.getValueInMaps(regionFacet.getRegion(id),
				raceFacet.get(id).getKeyName().trim(), "BASEAGE");
		if (values == null)
		{
			return 0;
		}

		Integer age = get(id);
		int pcAge = (age == null) ? 0 : age;
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
}
