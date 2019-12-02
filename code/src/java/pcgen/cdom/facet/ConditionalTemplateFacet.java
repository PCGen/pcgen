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
import pcgen.cdom.facet.analysis.LevelFacet;
import pcgen.cdom.facet.analysis.LevelFacet.LevelChangeEvent;
import pcgen.cdom.facet.analysis.LevelFacet.LevelChangeListener;
import pcgen.cdom.facet.base.AbstractListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCTemplate;

/**
 * ConditionalTemplateFacet is a Facet that tracks the Conditional Templates
 * granted to the PlayerCharacter. Conditional Templates are those items that
 * are set by HD, LEVEL, and REPEATLEVEL tokens in PCTemplates.
 * 
 */
public class ConditionalTemplateFacet extends AbstractListFacet<CharID, PCTemplate>
		implements DataFacetChangeListener<CharID, PCTemplate>, LevelChangeListener
{
	private TemplateFacet templateFacet;
	private LevelFacet levelFacet;

	/**
	 * Adds all of the conditional Templates available to the Player Character
	 * to this ConditionalTemplateFacet.
	 * 
	 * Triggered when one of the Facets to which ConditionalTemplateFacet
	 * listens fires a DataFacetChangeEvent to indicate a PCTemplate was added
	 * to a Player Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CharID, PCTemplate> dfce)
	{
		CharID id = dfce.getCharID();
		int totalLevels = levelFacet.getTotalLevels(id);
		int totalHitDice = levelFacet.getMonsterLevelCount(id);
		PCTemplate source = dfce.getCDOMObject();
		addAll(id, source.getConditionalTemplates(totalLevels, totalHitDice));
	}

	/**
	 * Removes all of the conditional Templates granted by the object removed
	 * from the Player Character.
	 * 
	 * Triggered when one of the Facets to which ConditionalTemplateFacet
	 * listens fires a DataFacetChangeEvent to indicate a PCTemplate was removed
	 * from a Player Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<CharID, PCTemplate> dfce)
	{
		CharID id = dfce.getCharID();
		int totalLevels = levelFacet.getTotalLevels(id);
		int totalHitDice = levelFacet.getMonsterLevelCount(id);
		PCTemplate source = dfce.getCDOMObject();
		removeAll(dfce.getCharID(), source.getConditionalTemplates(totalLevels, totalHitDice));
	}

	/**
	 * Adds (or removes) all of the conditional Templates available to the
	 * Player Character to this ConditionalTemplateFacet. This requires a global
	 * check of all Templates granted to the Player Character, since this is
	 * occurring when the Player Character level changes.
	 * 
	 * Triggered when the Level of the Player Character changes.
	 * 
	 * @param lce
	 *            The LevelChangeEvent containing the information about the
	 *            level change
	 */
	@Override
	public void levelChanged(LevelChangeEvent lce)
	{
		CharID id = lce.getCharID();
		Collection<PCTemplate> oldSet = getSet(id);
		int totalLevels = levelFacet.getTotalLevels(id);
		int totalHitDice = levelFacet.getMonsterLevelCount(id);
		Map<PCTemplate, PCTemplate> newMap = new IdentityHashMap<>();
		for (PCTemplate sourceTempl : templateFacet.getSet(id))
		{
			List<PCTemplate> conditionalTemplates = sourceTempl.getConditionalTemplates(totalLevels, totalHitDice);
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
			// We need to check for presence by object identity (==) rather than equality (.equals)
			boolean found = false;
			for (PCTemplate pcTemplate : oldSet)
			{
                if (a == pcTemplate) {
                    found = true;
                    break;
                }
			}
			if (!found)
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

	public void init()
	{
		addDataFacetChangeListener(templateFacet);
		templateFacet.addDataFacetChangeListener(this);
	}
}
