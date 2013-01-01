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

import pcgen.cdom.content.SourcedSelection;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.PlayerCharacterTrackingFacet;
import pcgen.cdom.facet.model.DomainSelectionFacet;
import pcgen.cdom.helper.ClassSource;
import pcgen.core.Domain;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.ChooseActivation;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.ChooserUtilities;

/**
 * DomainInputFacet is a Facet that handles addition of Domains to a Player
 * Character.
 */
public class DomainInputFacet 
{

	private DomainSelectionFacet domainSelectionFacet;

	private final PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
		.getFacet(PlayerCharacterTrackingFacet.class);

	public boolean add(CharID id, Domain obj, ClassSource source)
	{
		if (ChooseActivation.hasChooseToken(obj))
		{
			PlayerCharacter pc = trackingFacet.getPC(id);
			ChoiceManagerList<?> aMan =
					ChooserUtilities.getChoiceManager(obj, pc);
			return processChoice(id, pc, obj, aMan, source);
		}
		else
		{
			directSet(id, obj, null, source);
		}
		return true;
	}

	private <T> boolean processChoice(CharID id, PlayerCharacter pc,
		Domain obj, ChoiceManagerList<T> aMan, ClassSource source)
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
			directSet(id, obj, sel, source);
		}
		return true;
	}

	public void importSelection(CharID id, Domain obj, String choice,
		ClassSource source)
	{
		PlayerCharacter pc = trackingFacet.getPC(id);
		if (ChooseActivation.hasChooseToken(obj))
		{
			ChoiceManagerList<?> aMan =
					ChooserUtilities.getChoiceManager(obj, pc);
			String[] assoc = choice.split(",", -1);
			for (String string : assoc)
			{
				if (string.startsWith("FEAT?"))
				{
					int openloc = string.indexOf('(');
					int closeloc = string.lastIndexOf(')');
					string = string.substring(openloc + 1, closeloc);
				}
				processImport(id, pc, obj, aMan, string, source);
			}
		}
		else
		{
			directSet(id, obj, null, source);
		}
	}

	private <T> void processImport(CharID id, PlayerCharacter pc, Domain obj,
		ChoiceManagerList<T> aMan, String choice, ClassSource source)
	{
		directSet(id, obj, aMan.decodeChoice(choice), source);
	}

	private <T> void directSet(CharID id, Domain obj, T sel,
		ClassSource source)
	{
		domainSelectionFacet.add(id,
			SourcedSelection.getSelection(obj, sel, source), obj);
	}

	public void remove(CharID id, Domain obj)
	{
		domainSelectionFacet.removeAll(id, obj);
	}

	public void setDomainSelectionFacet(
		DomainSelectionFacet domainSelectionFacet)
	{
		this.domainSelectionFacet = domainSelectionFacet;
	}

}
