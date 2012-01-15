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
 */
public class UserEquipmentFacet extends AbstractSourcedListFacet<Equipment>
		implements DataFacetChangeListener<Equipment>
{
	@Override
	public void dataAdded(DataFacetChangeEvent<Equipment> dfce)
	{
		add(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<Equipment> dfce)
	{
		remove(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
	}

}
