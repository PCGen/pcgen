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
package pcgen.cdom.facet.analysis;

import java.util.List;

import pcgen.cdom.content.ACControl;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.PlayerCharacterTrackingFacet;
import pcgen.cdom.facet.PrerequisiteFacet;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.exporttoken.BonusToken;
import pcgen.util.Logging;

/**
 * ArmorClassFacet calculates the Armor Class (actually types of armor class,
 * such as defense against a touch attack) of a Player Character.
 * 
 */
public class ArmorClassFacet
{

	/**
	 * Facet used to calculate Prerequisites
	 */
	private PrerequisiteFacet prerequisiteFacet;

	private PlayerCharacterTrackingFacet trackingFacet = FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

	/**
	 * Calculates the Armor Class of a certain type. These types are defined in
	 * the game mode; there are no hardcoded Armor Class types here - (and none
	 * should be added!)
	 * 
	 * @param id
	 *            The CharId identifying the Player Character for which a
	 *            specific type of Armor Class should be calculated
	 * @param type
	 *            The type of the Armor Class to be calculated. Defined in the
	 *            Game Mode files
	 * @return The armor class of the given type for the Player Character
	 *         identified by the given CharID
	 */
	@Deprecated
	public int calcACOfType(CharID id, String type)
	{
		/*
		 * CONSIDER should AC types be a type safe list?
		 */
		final List<ACControl> addList = SettingsHandler.getGameAsProperty().get().getACTypeAddString(type);
		final List<ACControl> removeList = SettingsHandler.getGameAsProperty().get().getACTypeRemoveString(type);

		if ((addList == null) && (removeList == null))
		{
			Logging.errorPrint("Invalid ACType: " + type);
			return 0;
		}

		int armorClass = 0;

		if (addList != null)
		{
			PlayerCharacter pc = trackingFacet.getPC(id);
			for (ACControl acc : addList)
			{
				if (prerequisiteFacet.qualifies(id, acc, null))
				{
					armorClass += Integer.parseInt(BonusToken.getBonusToken("BONUS.COMBAT.AC." + acc.getType(), pc));
				}
			}
		}

		if (removeList != null)
		{
			PlayerCharacter pc = trackingFacet.getPC(id);
			for (ACControl acc : removeList)
			{
				if (prerequisiteFacet.qualifies(id, acc, null))
				{
					armorClass -= Integer.parseInt(BonusToken.getBonusToken("BONUS.COMBAT.AC." + acc.getType(), pc));
				}
			}
		}

		return armorClass;
	}

	public void setPrerequisiteFacet(PrerequisiteFacet prerequisiteFacet)
	{
		this.prerequisiteFacet = prerequisiteFacet;
	}

}
