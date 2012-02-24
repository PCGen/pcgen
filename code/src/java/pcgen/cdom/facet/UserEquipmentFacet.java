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
 * UserEquipmentFacet is a Facet that tracks the Equipment possessed by a Player
 * Character. Possessed in this case does not mean active, it could be carried
 * or owned.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class UserEquipmentFacet extends AbstractSourcedListFacet<Equipment>
		implements DataFacetChangeListener<Equipment>
{
	/**
	 * Consolidates Equipment into this facet when that Equipment has been added
	 * to a Player Character.
	 * 
	 * Triggered when one of the Facets to which UserEquipmentFacet listens
	 * fires a DataFacetChangeEvent to indicate a piece of Equipment was added
	 * to a Player Character.
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
	 * Removes a piece of Equipment from this facet when the Equipment has been
	 * removed from a Player Character.
	 * 
	 * Triggered when one of the Facets to which UnlockedStatFacet listens fires
	 * a DataFacetChangeEvent to indicate a piece of Equipment was removed from
	 * a Player Character.
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
