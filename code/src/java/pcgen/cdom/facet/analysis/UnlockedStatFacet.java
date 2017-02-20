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
package pcgen.cdom.facet.analysis;

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.CDOMObjectConsolidationFacet;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCStat;

/**
 * UnlockedStatFacet is a Facet that tracks the Unlocked Stats that have been
 * applied to a Player Character.
 * 
 */
public class UnlockedStatFacet extends AbstractSourcedListFacet<CharID, PCStat>
		implements DataFacetChangeListener<CharID, CDOMObject>
{

	private CDOMObjectConsolidationFacet consolidationFacet;

	/**
	 * Adds a PCStat to this facet if the PCStat was unlocked by a CDOMObject
	 * which has been added to a Player Character.
	 * 
	 * Triggered when one of the Facets to which UnlockedStatFacet listens fires
	 * a DataFacetChangeEvent to indicate a CDOMObject was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.event.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.event.DataFacetChangeEvent)
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		CDOMObject cdo = dfce.getCDOMObject();
		List<CDOMSingleRef<PCStat>> unlocked = cdo.getListFor(ListKey.UNLOCKED_STATS);
		if (unlocked != null)
		{
			CharID charID = dfce.getCharID();
			for (CDOMSingleRef<PCStat> ref : unlocked)
			{
				add(charID, ref.get(), cdo);
			}
		}
	}

	/**
	 * Removes a PCStat from this facet if the PCStat was unlocked by a
	 * CDOMObject which has been removed from a Player Character.
	 * 
	 * Triggered when one of the Facets to which UnlockedStatFacet listens fires
	 * a DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.event.DataFacetChangeListener#dataRemoved(pcgen.cdom.facet.event.DataFacetChangeEvent)
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		removeAll(dfce.getCharID(), dfce.getCDOMObject());
	}

	public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
	{
		this.consolidationFacet = consolidationFacet;
	}

	/**
	 * Initializes the connections for UnlockedStatFacet to other facets.
	 * 
	 * This method is automatically called by the Spring framework during
	 * initialization of the UnlockedStatFacet.
	 */
	public void init()
	{
		consolidationFacet.addDataFacetChangeListener(this);
	}
}
