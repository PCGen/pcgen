/*
 * Copyright (c) Thomas Parker, 2012.
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

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractListFacet;
import pcgen.core.Equipment;

/**
 * PrimaryWeaponFacet contains the list of weapons that are Primary Weapons for
 * a Player Character.
 * 
 * Theoretically, this list of Primary weapons is only a single weapon, while
 * this list may contain multiple entries to handle both melee and ranged
 * instances of that Equipment.
 * 
 */
public class PrimaryWeaponFacet extends AbstractListFacet<CharID, Equipment>
{

	@Override
	protected Collection<Equipment> getCopyForNewOwner(Collection<Equipment> componentSet)
	{
		List<Equipment> newCopies = new ArrayList<>();
		for (Equipment entry : componentSet)
		{
			newCopies.add(entry.clone());
		}
		return newCopies;
	}

}
