/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.Selection;
import pcgen.cdom.facet.model.DomainSelectionFacet;
import pcgen.cdom.facet.model.RaceSelectionFacet;
import pcgen.cdom.facet.model.TemplateSelectionFacet;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.ChooseActivation;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.ChooserUtilities;

/**
 * ChooseDriverFacet is a Facet that drives the application of a CHOOSE on a
 * CDOMObject.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class ChooseDriverFacet implements
		DataFacetChangeListener<Selection<?, ?>>
{

	private final PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
		.getFacet(PlayerCharacterTrackingFacet.class);

	private RaceSelectionFacet raceSelectionFacet;

	private DomainSelectionFacet domainSelectionFacet;

	private TemplateSelectionFacet templateSelectionFacet;

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
	public void dataAdded(DataFacetChangeEvent<Selection<?, ?>> dfce)
	{
		Selection<?, ?> sel = dfce.getCDOMObject();
		CDOMObject obj = sel.getObject();
		if (ChooseActivation.hasChooseToken(obj))
		{
			PlayerCharacter pc = trackingFacet.getPC(dfce.getCharID());
			add(ChooserUtilities.getChoiceManager(obj, pc), pc, obj, sel);
		}
	}

	private <T> void add(ChoiceManagerList<T> aMan, PlayerCharacter pc,
		CDOMObject obj, Selection<?, T> sel)
	{
		aMan.applyChoice(pc, obj, sel.getSelection());
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
	public void dataRemoved(DataFacetChangeEvent<Selection<?, ?>> dfce)
	{
		Selection<?, ?> sel = dfce.getCDOMObject();
		CDOMObject obj = sel.getObject();
		if (ChooseActivation.hasChooseToken(obj))
		{
			PlayerCharacter pc = trackingFacet.getPC(dfce.getCharID());
			remove(ChooserUtilities.getChoiceManager(obj, pc), pc, obj, sel);
		}
	}

	private <T> void remove(ChoiceManagerList<T> aMan, PlayerCharacter pc,
		CDOMObject obj, Selection<?, T> sel)
	{
		aMan.removeChoice(pc, obj, sel.getSelection());
	}

	public void setDomainSelectionFacet(
		DomainSelectionFacet domainSelectionFacet)
	{
		this.domainSelectionFacet = domainSelectionFacet;
	}

	public void setRaceSelectionFacet(RaceSelectionFacet raceSelectionFacet)
	{
		this.raceSelectionFacet = raceSelectionFacet;
	}

	public void setTemplateSelectionFacet(
		TemplateSelectionFacet templateSelectionFacet)
	{
		this.templateSelectionFacet = templateSelectionFacet;
	}

	public void init()
	{
		raceSelectionFacet.addDataFacetChangeListener(1000, this);
		domainSelectionFacet.addDataFacetChangeListener(1000, this);
		templateSelectionFacet.addDataFacetChangeListener(1000, this);
	}
}
