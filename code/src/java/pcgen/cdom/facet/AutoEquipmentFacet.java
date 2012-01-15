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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Equipment;
import pcgen.core.QualifiedObject;

/**
 * AutoEquipmentFacet is a Facet that tracks the Automatic Equipment objects
 * that are contained in a Player Character.
 */
public class AutoEquipmentFacet extends
		AbstractQualifiedListFacet<QualifiedObject<CDOMReference<Equipment>>>
		implements DataFacetChangeListener<CDOMObject>
{

	/**
	 * Triggered when one of the Facets to which AutoEquipmentFacet listens
	 * fires a DataFacetChangeEvent to indicate a CDOMObject was added to a
	 * Player Character.
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
		CDOMObject cdo = dfce.getCDOMObject();
		List<QualifiedObject<CDOMReference<Equipment>>> list = cdo
				.getSafeListFor(ListKey.EQUIPMENT);
		if (list != null)
		{
			addAll(dfce.getCharID(), list, cdo);
		}
	}

	/**
	 * Triggered when one of the Facets to which AutoEquipmentFacet listens
	 * fires a DataFacetChangeEvent to indicate a CDOMObject was removed from a
	 * Player Character.
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
		removeAll(dfce.getCharID(), dfce.getCDOMObject());
	}

	public List<Equipment> getAutoEquipment(CharID id)
	{
		List<Equipment> list = new ArrayList<Equipment>();
		for (QualifiedObject<CDOMReference<Equipment>> qo : getQualifiedSet(id))
		{
			Collection<Equipment> equipList = qo.getRawObject().getContainedObjects();
			for (Equipment e : equipList)
			{
				e = e.clone();
				e.setQty(1);
				e.setAutomatic(true);
				list.add(e);
			}
		}
		return list;
	}
}
