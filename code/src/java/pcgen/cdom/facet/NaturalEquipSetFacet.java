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

import pcgen.cdom.base.Constants;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.EquipSet;

/**
 * EquipSetFacet is a Facet that tracks the EquipSets for a Player Character.
 */
public class NaturalEquipSetFacet implements DataFacetChangeListener<Equipment>
{
	private final PlayerCharacterTrackingFacet trackingFacet =
			FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

	private NaturalWeaponFacet naturalWeaponFacet;

	@Override
	public void dataAdded(DataFacetChangeEvent<Equipment> dfce)
	{
		PlayerCharacter pc = trackingFacet.getPC(dfce.getCharID());
		EquipSet eSet = pc.getEquipSetByIdPath("0.1");
		if (eSet != null)
		{
			Equipment eq = dfce.getCDOMObject();
			EquipSet es = pc.addEquipToTarget(eSet, null, "", eq, null);
			if (es == null)
			{
				pc.addEquipToTarget(eSet, null, Constants.EQUIP_LOCATION_CARRIED, eq, null);
			}
		}
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<Equipment> dfce)
	{
		// Ignore for now
	}

	public void setNaturalWeaponFacet(NaturalWeaponFacet naturalWeaponFacet)
	{
		this.naturalWeaponFacet = naturalWeaponFacet;
	}

	public void init()
	{
		naturalWeaponFacet.addDataFacetChangeListener(this);
	}
}
