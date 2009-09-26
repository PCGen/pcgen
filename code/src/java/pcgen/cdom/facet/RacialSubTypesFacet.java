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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.core.PCTemplate;
import pcgen.core.Race;

/**
 * RacialSubTypesFacet is a Facet that tracks the Racial Sub Types of a
 * PlayerCharacter
 */
public class RacialSubTypesFacet
{
	private TemplateFacet templateFacet = FacetLibrary
			.getFacet(TemplateFacet.class);
	private RaceFacet raceFacet = FacetLibrary.getFacet(RaceFacet.class);

	/**
	 * Returns a Collection of the Racial Sub Types for the Player Character
	 * represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            Racial Sub Types should be returned
	 * @return A Collection of the Racial Sub Types for the Player Character
	 *         represented by the given CharID
	 */
	public Collection<RaceSubType> getRacialSubTypes(CharID id)
	{
		List<RaceSubType> racialSubTypes = new ArrayList<RaceSubType>();
		Race race = raceFacet.get(id);
		if (race != null)
		{
			for (RaceSubType st : race.getSafeListFor(ListKey.RACESUBTYPE))
			{
				racialSubTypes.add(st);
			}
		}
		Set<PCTemplate> templates = templateFacet.getSet(id);
		if (!templates.isEmpty())
		{
			List<RaceSubType> added = new ArrayList<RaceSubType>();
			List<RaceSubType> removed = new ArrayList<RaceSubType>();
			for (PCTemplate aTemplate : templates)
			{
				added.addAll(aTemplate.getSafeListFor(ListKey.RACESUBTYPE));
				removed.addAll(aTemplate
						.getSafeListFor(ListKey.REMOVED_RACESUBTYPE));
			}
			for (RaceSubType st : added)
			{
				racialSubTypes.add(st);
			}
			for (RaceSubType st : removed)
			{
				racialSubTypes.remove(st);
			}
		}

		return Collections.unmodifiableList(racialSubTypes);
	}

	public boolean contains(CharID id, RaceSubType st)
	{
		return getRacialSubTypes(id).contains(st);
	}

	public int getCount(CharID id)
	{
		return getRacialSubTypes(id).size();
	}

}
