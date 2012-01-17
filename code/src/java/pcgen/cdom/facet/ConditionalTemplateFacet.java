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

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.LevelFacet.LevelChangeEvent;
import pcgen.cdom.facet.LevelFacet.LevelChangeListener;
import pcgen.core.PCTemplate;

/**
 * ConditionalTemplateFacet is a Facet that tracks the Conditional Templates
 * granted to the PlayerCharacter. Conditional Templates are those items that
 * are are set by HD, LEVEL, and REPEATLEVEL tokens in PCTemplates.
 */
public class ConditionalTemplateFacet extends AbstractListFacet<PCTemplate>
		implements DataFacetChangeListener<PCTemplate>, LevelChangeListener
{
	private TemplateFacet templateFacet;
	private LevelFacet levelFacet;

	/**
	 * Triggered when one of the Facets to which ConditionalTemplateFacet
	 * listens fires a DataFacetChangeEvent to indicate a PCTemplate was added
	 * to a Player Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<PCTemplate> dfce)
	{
		CharID id = dfce.getCharID();
		int totalLevels = levelFacet.getTotalLevels(id);
		int totalHitDice = levelFacet.getMonsterLevelCount(id);
		PCTemplate source = dfce.getCDOMObject();
		addAll(id, source.getConditionalTemplates(totalLevels, totalHitDice));
	}

	/**
	 * Triggered when one of the Facets to which ConditionalTemplateFacet
	 * listens fires a DataFacetChangeEvent to indicate a PCTemplate was removed
	 * from a Player Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<PCTemplate> dfce)
	{
		CharID id = dfce.getCharID();
		int totalLevels = levelFacet.getTotalLevels(id);
		int totalHitDice = levelFacet.getMonsterLevelCount(id);
		PCTemplate source = dfce.getCDOMObject();
		removeAll(dfce.getCharID(), source.getConditionalTemplates(totalLevels,
				totalHitDice));
	}

	/**
	 * Triggered when the Level of the Player Character changes.
	 * 
	 * @param lce
	 *            The LevelChangeEvent containing the information about the
	 *            level change
	 * 
	 * @see pcgen.cdom.facet.LevelFacet.LevelChangeListener#levelChanged(pcgen.cdom.facet.LevelFacet.LevelChangeEvent)
	 */
	@Override
	public void levelChanged(LevelChangeEvent lce)
	{
		CharID id = lce.getCharID();
		Collection<PCTemplate> oldSet = getSet(id);
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

	public void setTemplateFacet(TemplateFacet templateFacet)
	{
		this.templateFacet = templateFacet;
	}

	public void setLevelFacet(LevelFacet levelFacet)
	{
		this.levelFacet = levelFacet;
	}

}
