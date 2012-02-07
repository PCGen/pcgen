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

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.enumeration.CharID;
import pcgen.core.Equipment;

/**
 * EquippedEquipmentFacet is a Facet that tracks the Equipment that is Equipped
 * by a Player Character.
 */
public class EquippedEquipmentFacet extends AbstractDataFacet<Equipment>
{
	private EquipmentFacet equipmentFacet;

	private final Class<?> thisClass = getClass();

	/**
	 * Triggered ("manually") when the equipped equipment on a Player Character
	 * has changed
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            equipped Equipment should be updated
	 */
	public void reset(CharID id)
	{
		Set<Equipment> oldEquipped =
				(Set<Equipment>) removeCache(id, thisClass);
		Set<Equipment> currentEquipment = equipmentFacet.getSet(id);
		Set<Equipment> newEquipped = new WrappedMapSet<Equipment>(
				IdentityHashMap.class);
		setCache(id, thisClass, newEquipped);
		if (oldEquipped != null)
		{
			// Delete items that the PC no longer has at all
			for (Equipment e : oldEquipped)
			{
				if (!currentEquipment.contains(e))
				{
					fireDataFacetChangeEvent(id, e,
							DataFacetChangeEvent.DATA_REMOVED);
				}
			}
		}
		for (Equipment e : currentEquipment)
		{
			if (e.isEquipped())
			{
				newEquipped.add(e);
				// If not old, it's added
				if (oldEquipped == null || !oldEquipped.contains(e))
				{
					fireDataFacetChangeEvent(id, e,
							DataFacetChangeEvent.DATA_ADDED);
				}
			}
			else
			{
				// If old, it's removed
				if (oldEquipped != null && oldEquipped.contains(e))
				{
					fireDataFacetChangeEvent(id, e,
							DataFacetChangeEvent.DATA_REMOVED);
				}
			}
		}
	}

	/**
	 * Returns the Set of Equipment in this EquippedEquipmentFacet for the
	 * Player Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            items in this AbstractListFacet should be returned.
	 * @return A non-null Set of Equipment in this EquippedEquipmentFacet for
	 *         the Player Character represented by the given CharID
	 */
	public Set<Equipment> getSet(CharID id)
	{
		Set<Equipment> set = (Set<Equipment>) getCache(id, thisClass);
		if (set == null)
		{
			return Collections.emptySet();
		}
		return set;
	}

	public void setEquipmentFacet(EquipmentFacet equipmentFacet)
	{
		this.equipmentFacet = equipmentFacet;
	}

	@Override
	public void copyContents(CharID source, CharID copy)
	{
		Set<Equipment> set = (Set<Equipment>) getCache(source, thisClass);
		if (set != null)
		{
			Set<Equipment> newEquipped = new WrappedMapSet<Equipment>(
					IdentityHashMap.class);
			newEquipped.addAll(set);
			setCache(copy, thisClass, newEquipped);
		}
	}

}
