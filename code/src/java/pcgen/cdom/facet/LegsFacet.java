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
package pcgen.cdom.facet;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCTemplate;
import pcgen.core.Race;

public class LegsFacet
{

	private TemplateFacet templateFacet = FacetLibrary
			.getFacet(TemplateFacet.class);
	private RaceFacet raceFacet = FacetLibrary.getFacet(RaceFacet.class);

	public int getLegs(CharID id)
	{
		final Race aRace = raceFacet.get(id);
		int legs = 0;
		if (aRace != null)
		{
			legs = aRace.getSafe(IntegerKey.LEGS);
		}

		// Scan templates for any overrides
		for (PCTemplate template : templateFacet.getSet(id))
		{
			Integer l = template.get(IntegerKey.LEGS);
			if (l != null)
			{
				legs = l;
			}
		}
		return legs;
	}

}
