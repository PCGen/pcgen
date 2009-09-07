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

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.LevelFacet.LevelChangeEvent;
import pcgen.cdom.facet.LevelFacet.LevelChangeListener;
import pcgen.core.PCTemplate;

public class ConditionalTemplateFacet extends AbstractListFacet<PCTemplate>
		implements DataFacetChangeListener<PCTemplate>, LevelChangeListener
{
	private TemplateFacet templateFacet = FacetLibrary
			.getFacet(TemplateFacet.class);
	private LevelFacet levelFacet = FacetLibrary.getFacet(LevelFacet.class);

	public void dataAdded(DataFacetChangeEvent<PCTemplate> dfce)
	{
		CharID id = dfce.getCharID();
		int totalLevels = levelFacet.getTotalLevels(id);
		int totalHitDice = levelFacet.getMonsterLevelCount(id);
		PCTemplate source = dfce.getCDOMObject();
		addAll(id, source.getConditionalTemplates(totalLevels, totalHitDice));
	}

	public void dataRemoved(DataFacetChangeEvent<PCTemplate> dfce)
	{
		CharID id = dfce.getCharID();
		int totalLevels = levelFacet.getTotalLevels(id);
		int totalHitDice = levelFacet.getMonsterLevelCount(id);
		PCTemplate source = dfce.getCDOMObject();
		removeAll(dfce.getCharID(), source.getConditionalTemplates(totalLevels,
				totalHitDice));
	}

	public void levelChanged(LevelChangeEvent lce)
	{
		CharID id = lce.getCharID();
		Set<PCTemplate> oldSet = getSet(id);
		int totalLevels = levelFacet.getTotalLevels(id);
		int totalHitDice = levelFacet.getMonsterLevelCount(id);
		Map<PCTemplate, PCTemplate> newMap = new IdentityHashMap<PCTemplate, PCTemplate>();
		for (PCTemplate sourceTempl : templateFacet.getSet(id))
		{
			List<PCTemplate> conditionalTemplates = sourceTempl
					.getConditionalTemplates(totalLevels, totalHitDice);
			for (PCTemplate condTempl : conditionalTemplates)
			{
				newMap.put(condTempl, sourceTempl);
			}
		}

		// Delete items that the PC no longer has
		for (PCTemplate a : oldSet)
		{
			if (!newMap.containsKey(a))
			{
				remove(id, a);
			}
		}
		//Add new items
		for (Map.Entry<PCTemplate, PCTemplate> me : newMap.entrySet())
		{
			PCTemplate a = me.getKey();
			if (!oldSet.contains(a))
			{
				add(id, a);
			}
		}
	}
}
