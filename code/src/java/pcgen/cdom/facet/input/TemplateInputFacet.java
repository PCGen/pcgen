/*
 * Copyright (c) Thomas Parker, 2012.
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
package pcgen.cdom.facet.input;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.content.Selection;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.PlayerCharacterTrackingFacet;
import pcgen.cdom.facet.model.TemplateSelectionFacet;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.ChooseActivation;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.ChooserUtilities;

/**
 * TemplateInputFacet is a Facet that handles addition of PCTempaltes to a
 * Player Character.
 */
public class TemplateInputFacet
{

	private TemplateSelectionFacet templateSelectionFacet;

	private final PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
		.getFacet(PlayerCharacterTrackingFacet.class);

	public boolean add(CharID id, PCTemplate obj)
	{
		if (ChooseActivation.hasChooseToken(obj))
		{
			PlayerCharacter pc = trackingFacet.getPC(id);
			ChoiceManagerList<?> aMan =
					ChooserUtilities.getChoiceManager(obj, pc);
			return processChoice(id, pc, obj, aMan);
		}
		else
		{
			directAdd(id, obj, null);
		}
		return true;
	}

	private <T> boolean processChoice(CharID id, PlayerCharacter pc,
		PCTemplate obj, ChoiceManagerList<T> aMan)
	{
		List<T> selectedList = new ArrayList<T>();
		List<T> availableList = new ArrayList<T>();
		aMan.getChoices(pc, availableList, selectedList);

		if (availableList.isEmpty())
		{
			return false;
		}
		if (!selectedList.isEmpty())
		{
			//Error?
		}
		final List<T> newSelections =
				aMan.doChooser(pc, availableList, selectedList,
					new ArrayList<String>());
		if (newSelections.size() != 1)
		{
			//Error?
			return false;
		}
		for (T sel : newSelections)
		{
			directAdd(id, obj, sel);
		}
		return true;
	}

	public void importSelection(CharID id, PCTemplate obj, String choice)
	{
		PlayerCharacter pc = trackingFacet.getPC(id);
		if (ChooseActivation.hasChooseToken(obj))
		{
			ChoiceManagerList<?> aMan =
					ChooserUtilities.getChoiceManager(obj, pc);
			processImport(id, pc, obj, aMan, choice);
		}
		else
		{
			directAdd(id, obj, null);
		}
	}

	private <T> void processImport(CharID id, PlayerCharacter pc,
		PCTemplate obj, ChoiceManagerList<T> aMan, String choice)
	{
		directAdd(id, obj, aMan.decodeChoice(choice));
	}

	private <T> void directAdd(CharID id, PCTemplate obj, T sel)
	{
		Selection<PCTemplate, T> rs =
				new Selection<PCTemplate, T>(obj, sel);
		templateSelectionFacet.add(id, rs, obj);
	}

	public void remove(CharID id, PCTemplate obj)
	{
		templateSelectionFacet.removeAll(id, obj);
	}

	public void setTemplateSelectionFacet(
		TemplateSelectionFacet templateSelectionFacet)
	{
		this.templateSelectionFacet = templateSelectionFacet;
	}

}
