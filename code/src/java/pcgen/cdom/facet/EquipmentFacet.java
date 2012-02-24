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

import pcgen.core.Equipment;

/**
 * EquipmentFacet is a Facet that tracks the Equipment possessed by a Player
 * Character. Possessed in this case does not mean active, it could be carried
 * or owned. Therefore, items granted or allowed by the Equipment in this facet
 * are not part of the Player Character (the equipment must be equipped first).
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class EquipmentFacet extends AbstractSourcedListFacet<Equipment>
		implements DataFacetChangeListener<Equipment>
{
	/**
	 * Adds all of the Equipment that is possessed by a Player Character.
	 * 
	 * Triggered when one of the Facets to which EquipmentFacet listens fires a
	 * DataFacetChangeEvent to indicate Equipment was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<Equipment> dfce)
	{
		add(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
	}

	/**
	 * Removes all of the Equipment that is no longer possessed by a Player
	 * Character.
	 * 
	 * Triggered when one of the Facets to which EquipmentFacet listens fires a
	 * DataFacetChangeEvent to indicate Equipment was removed from a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataRemoved(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<Equipment> dfce)
	{
		remove(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
	}

}
