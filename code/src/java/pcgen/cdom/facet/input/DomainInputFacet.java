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

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.DomainSelectionFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.PlayerCharacterTrackingFacet;
import pcgen.cdom.facet.model.DomainFacet;
import pcgen.cdom.helper.ClassSource;
import pcgen.core.Domain;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.ChooseActivation;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.util.Logging;

/**
 * DomainInputFacet is a Facet that handles addition of Domains to a Player
 * Character.
 */
public class DomainInputFacet
{

	private DomainFacet domainFacet;

	private DomainSelectionFacet domainSelectionFacet;

	private final PlayerCharacterTrackingFacet trackingFacet =
			FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

	public boolean add(CharID id, Domain obj, ClassSource source)
	{
		PlayerCharacter pc = trackingFacet.getPC(id);
		if (pc.isAllowInteraction() && ChooseActivation.hasNewChooseToken(obj))
		{
			ChoiceManagerList<?> aMan = ChooserUtilities.getChoiceManager(obj, pc);
			return processChoice(id, pc, obj, aMan, source);
		}
		else
		{
			directSet(id, obj, null, source);
		}
		return true;
	}

	private <T> boolean processChoice(CharID id, PlayerCharacter pc, Domain obj, ChoiceManagerList<T> aMan,
		ClassSource source)
	{
		List<T> selectedList = new ArrayList<>();
		List<T> availableList = new ArrayList<>();
		aMan.getChoices(pc, availableList, selectedList);

		if (availableList.isEmpty())
		{
			return false;
		}
		if (!selectedList.isEmpty())
		{
			//Error?
			Logging.log(Logging.INFO, "Selected List is not emtpy, it contains : " + selectedList.size() + " domains");
		}
		final List<T> newSelections = aMan.doChooser(pc, availableList, selectedList, new ArrayList<>());
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

	public void importSelection(CharID id, Domain obj, String choice, ClassSource source)
	{
		PlayerCharacter pc = trackingFacet.getPC(id);
		if (ChooseActivation.hasNewChooseToken(obj))
		{
			ChoiceManagerList<?> aMan = ChooserUtilities.getChoiceManager(obj, pc);
			String[] assoc = choice.split(",", -1);
			for (String string : assoc)
			{
				if (string.startsWith("FEAT?"))
				{
					int openloc = string.indexOf('(');
					int closeloc = string.lastIndexOf(')');
					string = string.substring(openloc + 1, closeloc);
				}
				processImport(id, obj, aMan, string, source);
			}
		}
		else
		{
			directSet(id, obj, null, source);
		}
	}

	private <T> void processImport(CharID id, Domain obj, ChoiceManagerList<T> aMan, String choice, ClassSource source)
	{
		directSet(id, obj, aMan.decodeChoice(choice), source);
	}

	public <T> void directSet(CharID id, Domain obj, T sel, ClassSource source)
	{
		domainFacet.add(id, obj, source);
		if (sel != null)
		{
			domainSelectionFacet.set(id, obj, sel);
		}
	}

	public void remove(CharID id, Domain obj)
	{
		PlayerCharacter pc = trackingFacet.getPC(id);
		/*
		 * TODO This order of operations differs from Race and Template - is
		 * there a reason selection is first here and second there? Arguably
		 * this is correct since directSet is doing the selection last, so
		 * first-in first-out implies avoiding that issue
		 */
		if (pc.isAllowInteraction())
		{
			domainSelectionFacet.remove(id, obj);
		}
		domainFacet.remove(id, obj);
	}

	public void setDomainSelectionFacet(DomainSelectionFacet domainSelectionFacet)
	{
		this.domainSelectionFacet = domainSelectionFacet;
	}

	public void setDomainFacet(DomainFacet domainFacet)
	{
		this.domainFacet = domainFacet;
	}
}
