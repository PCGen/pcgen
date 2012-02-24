/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.CharID;
import pcgen.core.Domain;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.ChooseActivation;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.ChooserUtilities;

/**
 * ChooseDriverFacet is a Facet that drives the selection of a CHOOSE on a
 * CDOMObject.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class ChooseDriverFacet extends
		AbstractSingleSourceListFacet<CDOMObject, String> implements
		DataFacetChangeListener<CDOMObject>
{
	/*
	 * Note this is a BIT of a hack in using the "source" to hold the
	 * "associated data"
	 * 
	 * TODO This needs to use AbstractAssociationFacet
	 */

	private final PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
			.getFacet(PlayerCharacterTrackingFacet.class);

	/**
	 * Triggered when one of the Facets to which ChooseDriverFacet listens fires
	 * a DataFacetChangeEvent to indicate a CDOMObject was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CDOMObject> dfce)
	{
		CharID id = dfce.getCharID();
		PlayerCharacter pc = trackingFacet.getPC(id);
		CDOMObject cdo = dfce.getCDOMObject();
		if (pc.isImporting())
		{
			String fullassoc = getSource(id, cdo);
			if (fullassoc != null)
			{
				processAssociations(pc, cdo, fullassoc);
			}
		}
		else
		{
			if (ChooseActivation.hasChooseToken(cdo))
			{
				ChooserUtilities.modChoices(cdo, new ArrayList<Object>(),
						new ArrayList<Object>(), true, pc, true, null);
			}
		}
	}

	private void processAssociations(PlayerCharacter pc, CDOMObject cdo,
			String fullassoc)
	{
		if (cdo instanceof Domain)
		{
			processDomainAssocs(pc, cdo, fullassoc);
		}
		else
		{
			processAssocs(pc, cdo, fullassoc);
		}
	}

	private void processAssocs(PlayerCharacter pc, CDOMObject cdo,
			String fullassoc)
	{
		ChoiceManagerList<Object> controller = ChooserUtilities
				.getConfiguredController(cdo, pc, null, new ArrayList<String>());
		String[] assoc = fullassoc.split("\\|", -1);
		for (String string : assoc)
		{
			controller.restoreChoice(pc, cdo, string);
		}
	}

	private void processDomainAssocs(PlayerCharacter pc, CDOMObject cdo,
			String fullassoc)
	{
		ChoiceManagerList<Object> controller = ChooserUtilities
				.getConfiguredController(cdo, pc, null, new ArrayList<String>());
		String[] assoc = fullassoc.split(Constants.COMMA, -1);
		for (String string : assoc)
		{
			if (string.startsWith("FEAT?"))
			{
				int openloc = string.indexOf('(');
				int closeloc = string.lastIndexOf(')');
				string = string.substring(openloc + 1, closeloc);
			}
			controller.restoreChoice(pc, cdo, string);
		}
	}

	/**
	 * Triggered when one of the Facets to which ChooseDriverFacet listens fires
	 * a DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataRemoved(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<CDOMObject> dfce)
	{
		/*
		 * TODO Consider whether this needs to be symmetric to add (remove
		 * associations)
		 */
	}

	/**
	 * Directly adds an association (the result of a CHOOSE) - available for the
	 * I/O system.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character to which the
	 *            association should be added
	 * @param cdo
	 *            The CDOMObject for which the association should be added
	 * @param fullassoc
	 *            The association to be added to the CDOMObject for the Player
	 *            Character identified by the given CharID
	 */
	public void addAssociation(CharID id, CDOMObject cdo, String fullassoc)
	{
		add(id, cdo, fullassoc);
	}
}
