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
package pcgen.cdom.facet.model;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;

/**
 * ActiveEqModFacet is a Facet that tracks the EqMods that are on Equipment
 * equipped by the PlayerCharacter.
 * 
 */
public class ActiveEqModFacet extends
		AbstractSourcedListFacet<CharID, EquipmentModifier> implements
		DataFacetChangeListener<CharID, Equipment>
{

	/**
	 * Adds the EqMods associated with a piece of Equipment which is equipped by
	 * a Player Character.
	 * 
	 * Triggered when one of the Facets to which ActiveEqModFacet listens fires
	 * a DataFacetChangeEvent to indicate a piece of Equipment was equipped by a
	 * Player Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CharID, Equipment> dfce)
	{
		/*
		 * In theory, this doesn't need to check for additions/removals from the
		 * EqMod list, because such changes can't happen to equipment that is
		 * currently equipped by the PC (new equipment is a clone, not the
		 * original item)
		 */
		CharID id = dfce.getCharID();
		Equipment eq = dfce.getCDOMObject();
		for (EquipmentModifier eqMod : eq.getEqModifierList(true))
		{
			add(id, eqMod, eq);
		}
		for (EquipmentModifier eqMod : eq.getEqModifierList(false))
		{
			add(id, eqMod, eq);
		}
	}

	/**
	 * Removes the EqMods associated with a piece of Equipment which is
	 * unequipped by a Player Character.
	 * 
	 * Triggered when one of the Facets to which ActiveEqModFacet listens fires
	 * a DataFacetChangeEvent to indicate a piece of Equipment was unequipped by
	 * a Player Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<CharID, Equipment> dfce)
	{
		CharID id = dfce.getCharID();
		Equipment eq = dfce.getCDOMObject();
		for (EquipmentModifier eqMod : eq.getEqModifierList(true))
		{
			remove(id, eqMod, eq);
		}
		for (EquipmentModifier eqMod : eq.getEqModifierList(false))
		{
			remove(id, eqMod, eq);
		}
	}
}
