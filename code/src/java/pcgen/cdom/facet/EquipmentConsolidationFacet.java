/*
 * Copyright (c) Thomas Parker, 2010.
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
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;

/**
 * EquipmentConsolidationFacet consolidates all of the CDOMObjects that are not
 * part of a Player Character. This only includes CDOMObjects not natively part
 * of the Player Character, meaning they are part of the Equipment equipped by
 * the Player Character.
 * 
 * By consolidating all of the CDOMObjects into one location, behaviors which
 * are consistent across all CDOMObjects related to the equipment on a Player
 * Character can be performed based on events from a single source Facet.
 * 
 * If you are looking for a Facet that consolidates all of the CDOMObjects
 * granted to a Player Character, including those from Equipment and those
 * natively part of the Player Character, then use CDOMObjectConsolidationFacet
 * 
 * @see pcgen.cdom.facet.CDOMObjectConsolidationFacet
 * 
 */
public class EquipmentConsolidationFacet extends AbstractSourcedListFacet<CharID, CDOMObject>
		implements DataFacetChangeListener<CharID, CDOMObject>
{

	/**
	 * Adds all of the CDOMObjects that are part of a Player Character's
	 * Equipment. Listens for CDOMObjects not natively part of the Player
	 * Character, and thus part of the Equipment equipped by the Player
	 * Character.
	 * 
	 * Triggered when one of the Facets to which AvailableSpellFacet listens
	 * fires a DataFacetChangeEvent to indicate a CDOMObject was added to a
	 * Player Character.
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
		add(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
	}

	/**
	 * Removes all of the CDOMObjects that are not part of a Player Character
	 * when they are removed from the Player Character. Listens for CDOMObjects
	 * not natively part of the Player Character, and thus part of the Equipment
	 * equipped by the Player Character.
	 * 
	 * Triggered when one of the Facets to which CharacterConsolidationFacet
	 * listens fires a DataFacetChangeEvent to indicate a CDOMObject was removed
	 * from a Player Character.
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
		remove(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
	}

}
