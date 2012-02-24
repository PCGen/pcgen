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

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Equipment;

/**
 * NaturalWeaponFacet is a Facet that tracks the NaturalWeapons that have been
 * granted to a Player Character.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class NaturalWeaponFacet extends AbstractSourcedListFacet<Equipment>
		implements DataFacetChangeListener<CDOMObject>
{

	/**
	 * Adds any Natural Attacks (TYPE=Natural Equipment) which are granted by a
	 * CDOMObject which was added to a Player Character.
	 * 
	 * Triggered when one of the Facets to which NaturalWeaponFacet listens
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
		List<Equipment> weapons = cdo.getListFor(ListKey.NATURAL_WEAPON);
		if (weapons != null)
		{
			CharID id = dfce.getCharID();
			for (Equipment e : weapons)
			{
				add(id, e, cdo);
			}
		}
	}

	/**
	 * Removes any Natural Attacks (TYPE=Natural Equipment) which are granted by
	 * a CDOMObject which was removed from a Player Character.
	 * 
	 * Triggered when one of the Facets to which NaturalEquipmentFacet listens
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
		CDOMObject cdo = dfce.getCDOMObject();
		List<Equipment> weapons = cdo.getListFor(ListKey.NATURAL_WEAPON);
		if (weapons != null)
		{
			CharID id = dfce.getCharID();
			for (Equipment e : weapons)
			{
				remove(id, e, cdo);
			}
		}
	}
}
