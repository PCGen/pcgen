/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * Derived from PCTemplate.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.analysis;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.DamageReduction;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;

public class TemplateModifier
{

	/**
	 * Generate a string that represents the changes this Template will apply.
	 * 
	 * @param aPC
	 *            the Pc we'd like the string generated with reference to
	 * 
	 * @return a string explaining the Template
	 */
	public static String modifierString(PCTemplate pct, PlayerCharacter aPC)
	{
		StringBuffer mods = new StringBuffer(50); // More likely to be
		// true than 16
		// (the default)

		for (int x = 0; x < SettingsHandler.getGame().getUnmodifiableStatList()
				.size(); ++x)
		{
			if (TemplateStat.isNonAbility(pct, x))
			{
				mods.append(SettingsHandler.getGame().s_ATTRIBSHORT[x]).append(":nonability ");
			}
			else
			{
				int statMod = pct.getStatMod(x, aPC);

				if (statMod != 0)
				{
					mods.append(SettingsHandler.getGame().s_ATTRIBSHORT[x]).append(':').append(
							statMod).append(' ');
				}
			}
		}

		List<DamageReduction> drList = new ArrayList<DamageReduction>();
		int totalLevels = aPC.getTotalLevels();
		int totalHitDice = aPC.totalHitDice();
		List<PCTemplate> templList = new ArrayList<PCTemplate>();
		templList.add(pct);
		pct.getConditionalTemplates(totalLevels, totalHitDice, templList);
		for (PCTemplate subt : templList)
		{
			List<DamageReduction> tList = subt
					.getListFor(ListKey.DAMAGE_REDUCTION);
			if (tList != null)
			{
				drList.addAll(tList);
			}
		}
		if (drList.size() != 0)
		{
			mods.append("DR:").append(DamageReduction.getDRString(aPC, drList));
		}

		int nat = (int) pct.bonusTo("COMBAT", "AC", aPC, aPC);

		if (nat != 0)
		{
			mods.append("AC BONUS:").append(nat);
		}

		if (pct.getCR(totalLevels, totalHitDice) != 0)
		{
			mods.append("CR:").append(
					pct.getCR(totalLevels, totalHitDice))
					.append(' ');
		}

		if (TemplateSR
				.getSR(pct, totalLevels, totalHitDice, aPC) != 0)
		{
			mods.append("SR:").append(
					TemplateSR.getSR(pct, totalLevels, totalHitDice, aPC)).append(' ');
		}

		// if (!getDR(aPC.getTotalLevels(), aPC.totalHitDice()).equals(""))
		// {
		// mods.append("DR:").append(getDR(aPC.getTotalLevels(),
		// aPC.totalHitDice()))
		// .append(' ');
		// }

		return mods.toString();
	}

}
