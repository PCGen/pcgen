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

import pcgen.cdom.content.HitDie;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;

public class HitPointFacet extends
		AbstractAssociationFacet<PCClassLevel, Integer>
{

	private PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
		.getFacet(PlayerCharacterTrackingFacet.class);

	private LevelFacet levelFacet = FacetLibrary.getFacet(LevelFacet.class);

	private HitDieFacet hitDieFacet = FacetLibrary.getFacet(HitDieFacet.class);

	private ClassFacet classFacet = FacetLibrary.getFacet(ClassFacet.class);

	private BonusCheckingFacet bonusFacet = FacetLibrary
		.getFacet(BonusCheckingFacet.class);

	public void rollHP(CharID id, PCClass pcc, int level, boolean first)
	{
		int roll = 0;

		HitDie lvlDie = hitDieFacet.getLevelHitDie(id, pcc, level);
		if ((lvlDie == null) || (lvlDie.getDie() == 0))
		{
			roll = 0;
		}
		else
		{
			final int min =
					1
						+ (int) bonusFacet.getBonus(id, "HD", "MIN")
						+ (int) bonusFacet.getBonus(id, "HD", "MIN;CLASS."
							+ pcc.getKeyName());
			final int max =
					hitDieFacet.getLevelHitDie(id, pcc, level).getDie()
						+ (int) bonusFacet.getBonus(id, "HD", "MAX")
						+ (int) bonusFacet.getBonus(id, "HD", "MAX;CLASS."
							+ pcc.getKeyName());

			if (Globals.getGameModeHPFormula().length() == 0)
			{
				if (first
					&& level == 1
					&& SettingsHandler.isHPMaxAtFirstLevel()
					&& (!SettingsHandler.isHPMaxAtFirstPCClassLevelOnly() || pcc
						.isType("PC")))
				{
					roll = max;
				}
				else
				{
					PlayerCharacter pc = trackingFacet.getPC(id);
					if (!pc.isImporting())
					{
						roll =
								Globals.rollHP(min, max, pcc.getDisplayName(),
									level, levelFacet.getTotalLevels(id));
					}
				}
			}

			roll += ((int) bonusFacet.getBonus(id, "HP", "CURRENTMAXPERLEVEL"));
		}
		PCClassLevel classLevel =  classFacet.getClassLevel(id, pcc, level - 1);
		set(id, classLevel, Integer.valueOf(roll));
	}

}
