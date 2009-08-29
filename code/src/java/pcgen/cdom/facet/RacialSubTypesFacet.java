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

public class RacialSubTypesFacet
{
	private TemplateFacet templateFacet = FacetLibrary
			.getFacet(TemplateFacet.class);
	private RaceFacet raceFacet = FacetLibrary.getFacet(RaceFacet.class);

	public Collection<String> getRacialSubTypes(CharID id)
	{
		final ArrayList<String> racialSubTypes = new ArrayList<String>();
		for (RaceSubType st : raceFacet.get(id).getSafeListFor(
				ListKey.RACESUBTYPE))
		{
			racialSubTypes.add(st.toString());
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
				racialSubTypes.add(st.toString());
			}
			for (RaceSubType st : removed)
			{
				racialSubTypes.remove(st.toString());
			}
		}

		return Collections.unmodifiableList(racialSubTypes);
	}

}
