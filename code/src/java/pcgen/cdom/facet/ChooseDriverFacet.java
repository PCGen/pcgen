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
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.event.ScopeFacetChangeEvent;
import pcgen.cdom.facet.event.ScopeFacetChangeListener;
import pcgen.cdom.facet.model.DomainSelectionFacet;
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
public class ChooseDriverFacet
{

	private final PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
		.getFacet(PlayerCharacterTrackingFacet.class);

	private RaceSelectionFacet raceSelectionFacet;

	private DomainSelectionFacet domainSelectionFacet;

	private TemplateSelectionFacet templateSelectionFacet;

	private Adder adder = new Adder();
	private Remover remover = new Remover();

	private class Adder implements DataFacetChangeListener<Selection<?, ?>>,
			ScopeFacetChangeListener<CDOMObject, Object>
	{
		/**
		 * Triggered when one of the Facets to which ChooseDriverFacet listens
		 * fires a DataFacetChangeEvent to indicate a CDOMObject was added to a
		 * Player Character.
		 * 
		 * @param dfce
		 *            The DataFacetChangeEvent containing the information about
		 *            the change
		 * 
		 * @see pcgen.cdom.facet.event.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.event.DataFacetChangeEvent)
		 */
		@Override
		public void dataAdded(DataFacetChangeEvent<Selection<?, ?>> dfce)
		{
			Selection<?, ?> sel = dfce.getCDOMObject();
			CDOMObject obj = sel.getObject();
			if (ChooseActivation.hasNewChooseToken(obj))
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
		 * Triggered when one of the Facets to which ChooseDriverFacet listens
		 * fires a DataFacetChangeEvent to indicate a CDOMObject was removed
		 * from a Player Character.
		 * 
		 * @param dfce
		 *            The DataFacetChangeEvent containing the information about
		 *            the change
		 * 
		 * @see pcgen.cdom.facet.event.DataFacetChangeListener#dataRemoved(pcgen.cdom.facet.event.DataFacetChangeEvent)
		 */
		@Override
		public void dataRemoved(DataFacetChangeEvent<Selection<?, ?>> dfce)
		{
			//ignore
		}

		public void dataAdded(ScopeFacetChangeEvent<CDOMObject, Object> dfce)
		{
			CDOMObject obj = dfce.getScope();
			Object sel = dfce.getCDOMObject();
			if (ChooseActivation.hasNewChooseToken(obj))
			{
				PlayerCharacter pc = trackingFacet.getPC(dfce.getCharID());
				addAssoc(ChooserUtilities.getChoiceManager(obj, pc), pc, obj, sel);
			}
		}

		private <T> void addAssoc(ChoiceManagerList<T> aMan, PlayerCharacter pc,
			CDOMObject obj, T sel)
		{
			aMan.applyChoice(pc, obj, sel);
		}

		public void dataRemoved(ScopeFacetChangeEvent<CDOMObject, Object> dfce)
		{
			//ignore
		}
	}

	private class Remover implements DataFacetChangeListener<Selection<?, ?>>,
			ScopeFacetChangeListener<CDOMObject, Object>
	{
		/**
		 * Triggered when one of the Facets to which ChooseDriverFacet listens
		 * fires a DataFacetChangeEvent to indicate a CDOMObject was added to a
		 * Player Character.
		 * 
		 * @param dfce
		 *            The DataFacetChangeEvent containing the information about
		 *            the change
		 * 
		 * @see pcgen.cdom.facet.event.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.event.DataFacetChangeEvent)
		 */
		@Override
		public void dataAdded(DataFacetChangeEvent<Selection<?, ?>> dfce)
		{
			//ignore
		}

		/**
		 * Triggered when one of the Facets to which ChooseDriverFacet listens
		 * fires a DataFacetChangeEvent to indicate a CDOMObject was removed
		 * from a Player Character.
		 * 
		 * @param dfce
		 *            The DataFacetChangeEvent containing the information about
		 *            the change
		 * 
		 * @see pcgen.cdom.facet.event.DataFacetChangeListener#dataRemoved(pcgen.cdom.facet.event.DataFacetChangeEvent)
		 */
		@Override
		public void dataRemoved(DataFacetChangeEvent<Selection<?, ?>> dfce)
		{
			Selection<?, ?> sel = dfce.getCDOMObject();
			CDOMObject obj = sel.getObject();
			if (ChooseActivation.hasNewChooseToken(obj))
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

		public void dataAdded(ScopeFacetChangeEvent<CDOMObject, Object> dfce)
		{
			//ignore
		}

		public void dataRemoved(ScopeFacetChangeEvent<CDOMObject, Object> dfce)
		{
			Object assoc = dfce.getCDOMObject();
			CDOMObject cdo = dfce.getScope();
			if (ChooseActivation.hasNewChooseToken(cdo))
			{
				PlayerCharacter pc = trackingFacet.getPC(dfce.getCharID());
				removeAssoc(ChooserUtilities.getChoiceManager(cdo, pc), pc, cdo, assoc);
			}
		}

		private <T> void removeAssoc(ChoiceManagerList<T> aMan, PlayerCharacter pc,
			CDOMObject obj, T sel)
		{
			aMan.removeChoice(pc, obj, sel);
		}

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
		raceSelectionFacet.addScopeFacetChangeListener(1000, adder);
		domainSelectionFacet.addDataFacetChangeListener(1000, adder);
		templateSelectionFacet.addScopeFacetChangeListener(1000, adder);
		raceSelectionFacet.addScopeFacetChangeListener(-1000, remover);
		domainSelectionFacet.addDataFacetChangeListener(-1000, remover);
		templateSelectionFacet.addScopeFacetChangeListener(-1000, remover);
	}
}
