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
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;

/**
 * AddedTemplateFacet is a Facet that tracks the Templates that have been added
 * to a Player Character.
 */
public class AddedTemplateFacet extends AbstractSourcedListFacet<PCTemplate>
		implements DataFacetChangeListener<CDOMObject>
{

	private PrerequisiteFacet prereqFacet = FacetLibrary
			.getFacet(PrerequisiteFacet.class);

	private PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
		.getFacet(PlayerCharacterTrackingFacet.class);

	public Collection<PCTemplate> select(CharID id, CDOMObject po)
	{
		List<PCTemplate> list = new ArrayList<PCTemplate>();
		// older version of this cleared the
		// templateAdded list, so this may have to do that as well?
		FacetCache.remove(id, getClass());
		PlayerCharacter pc = trackingFacet.getPC(id);
		if (!pc.isImporting())
		{
			for (CDOMReference<PCTemplate> ref : po
					.getSafeListFor(ListKey.TEMPLATE))
			{
				for (PCTemplate pct : ref.getContainedObjects())
				{
					add(id, pct, po);
					list.add(pct);
				}
			}
			List<PCTemplate> added = new ArrayList<PCTemplate>();
			for (CDOMReference<PCTemplate> ref : po
					.getSafeListFor(ListKey.TEMPLATE_ADDCHOICE))
			{
				added.addAll(ref.getContainedObjects());
			}
			for (CDOMReference<PCTemplate> ref : po
					.getSafeListFor(ListKey.TEMPLATE_CHOOSE))
			{
				List<PCTemplate> chooseList = new ArrayList<PCTemplate>(added);
				chooseList.addAll(ref.getContainedObjects());
				PCTemplate selected = chooseTemplate(po, chooseList, true, id);
				if (selected != null)
				{
					add(id, selected, po);
					list.add(selected);
				}
			}
		}
		return list;
	}

	public Collection<PCTemplate> remove(CharID id, CDOMObject po)
	{
		List<PCTemplate> list = new ArrayList<PCTemplate>();
		PlayerCharacter pc = trackingFacet.getPC(id);
		if (!pc.isImporting())
		{
			for (CDOMReference<PCTemplate> ref : po
					.getSafeListFor(ListKey.REMOVE_TEMPLATES))
			{
				for (PCTemplate pct : ref.getContainedObjects())
				{
					list.add(pct);
				}
			}
		}
		return list;
	}

	public PCTemplate chooseTemplate(CDOMObject anOwner, List<PCTemplate> list,
			boolean forceChoice, CharID id)
	{
		final List<PCTemplate> availableList = new ArrayList<PCTemplate>();
		for (PCTemplate pct : list)
		{
			if (prereqFacet.qualifies(id, pct, anOwner))
			{
				availableList.add(pct);
			}
		}
		if (availableList.size() == 1)
		{
			return availableList.get(0);
		}
		// If we are left without a choice, don't show the chooser.
		if (availableList.size() < 1)
		{
			return null;
		}
		final List<PCTemplate> selectedList = new ArrayList<PCTemplate>(1);
		String title = "Template Choice";
		if (anOwner != null)
		{
			title += " (" + anOwner.getDisplayName() + ")";
		}
		Globals.getChoiceFromList(title, availableList, selectedList, 1,
				forceChoice);
		if (selectedList.size() == 1)
		{
			return selectedList.get(0);
		}

		return null;
	}

	public Collection<PCTemplate> getFromSource(CharID id, CDOMObject cdo)
	{
		List<PCTemplate> list = new ArrayList<PCTemplate>();
		Map<PCTemplate, Set<Object>> map = getCachedMap(id);
		if (map != null)
		{
			for (Map.Entry<PCTemplate, Set<Object>> me : map.entrySet())
			{
				Set<Object> sourceSet = me.getValue();
				if (sourceSet.contains(cdo))
				{
					list.add(me.getKey());
				}
			}
		}
		return list;
	}

	@Override
	public void dataAdded(DataFacetChangeEvent<CDOMObject> dfce)
	{
		CharID id = dfce.getCharID();
		CDOMObject cdo = dfce.getCDOMObject();
		PlayerCharacter pc = trackingFacet.getPC(id);
		Collection<PCTemplate> list = getFromSource(id, cdo);
		/*
		 * If someone pre-set the list, then we use the preset list. If not, we
		 * need to do selections
		 */
		if (list.isEmpty())
		{
			for (PCTemplate pct : select(id, cdo))
			{
				pc.addTemplate(pct);
			}
			for (PCTemplate pct : remove(id, cdo))
			{
				pc.removeTemplate(pct);
			}
		}
		else
		{
			for (PCTemplate pct : list)
			{
				pc.addTemplate(pct);
			}
		}
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<CDOMObject> dfce)
	{
		CDOMObject cdo = dfce.getCDOMObject();
		CharID id = dfce.getCharID();
		PlayerCharacter pc = trackingFacet.getPC(id);
		Collection<PCTemplate> list = getFromSource(id, cdo);
		if (list != null)
		{
			for (PCTemplate pct : list)
			{
				pc.removeTemplate(pct);
			}
		}

		Collection<CDOMReference<PCTemplate>> refList =
				cdo.getListFor(ListKey.TEMPLATE);
		if (refList != null)
		{
			for (CDOMReference<PCTemplate> pctr : refList)
			{
				for (PCTemplate pct : pctr.getContainedObjects())
				{
					pc.removeTemplate(pct);
				}
			}
		}

	}
}
