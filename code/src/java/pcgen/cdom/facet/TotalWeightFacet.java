/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.cdom.enumeration.CharID;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.RuleConstants;

public class TotalWeightFacet
{
	private PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
			.getFacet(PlayerCharacterTrackingFacet.class);
	private EquipmentFacet equipmentFacet = FacetLibrary
			.getFacet(EquipmentFacet.class);

	public Float getTotalWeight(CharID id)
	{
		float totalWeight = 0;
		final Float floatZero = Float.valueOf(0);
		boolean firstClothing = ! Globals.checkRule(RuleConstants.CLOTHINGENCUMBRANCE);
		
		PlayerCharacter pc = trackingFacet.getPC(id);
		for (Equipment eq : equipmentFacet.getSet(id))
		{
			// Loop through the list of top
			if ((eq.getCarried().compareTo(floatZero) > 0)
					&& (eq.getParent() == null))
			{
				if (eq.getChildCount() > 0)
				{
					totalWeight += (eq.getWeightAsDouble(pc) + eq
							.getContainedWeight(pc).floatValue());
				}
				else
				{
					if (firstClothing && eq.isEquipped()
							&& eq.isType("CLOTHING"))
					{
						// The first equipped set of clothing should have a
						// weight of 0. Feature #437410
						firstClothing = false;
						totalWeight += (eq.getWeightAsDouble(pc) * Math.max(eq
								.getCarried().floatValue() - 1, 0));
					}
					else
					{
						totalWeight += (eq.getWeightAsDouble(pc) * eq
								.getCarried().floatValue());
					}
				}
			}
		}

		return Float.valueOf(totalWeight);
	}

}
