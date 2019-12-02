/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.core.display;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.content.DamageReduction;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.DamageReductionFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.model.StatFacet;
import pcgen.cdom.util.CControl;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.BonusCalc;

public final class TemplateModifier
{

	private static DamageReductionFacet drFacet = FacetLibrary.getFacet(DamageReductionFacet.class);
	private static StatFacet statFacet = FacetLibrary.getFacet(StatFacet.class);

	private TemplateModifier()
	{
	}

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
		StringBuilder mods = new StringBuilder(50); // More likely to be
		// true than 16
		// (the default)

		CharID id = aPC.getCharID();
		for (PCStat stat : statFacet.getSet(id))
		{
			if (NonAbilityDisplay.isNonAbilityForObject(stat, pct))
			{
				mods.append(stat.getKeyName()).append(":nonability ");
			}
			else
			{
				int statMod = BonusCalc.getStatMod(pct, stat, aPC);

				if (statMod != 0)
				{
					mods.append(stat.getKeyName()).append(':').append(statMod).append(' ');
				}
			}
		}

		Map<DamageReduction, Set<Object>> drMap = new IdentityHashMap<>();
		CharacterDisplay display = aPC.getDisplay();
		int totalLevels = display.getTotalLevels();
		int totalHitDice = display.totalHitDice();
		List<PCTemplate> templList = new ArrayList<>();
		templList.add(pct);
		templList.addAll(pct.getConditionalTemplates(totalLevels, totalHitDice));
		for (PCTemplate subt : templList)
		{
			List<DamageReduction> tList = subt.getListFor(ListKey.DAMAGE_REDUCTION);
			if (tList != null)
			{
				for (DamageReduction dr : tList)
				{
                    Set<Object> set = drMap.computeIfAbsent(dr, k -> new HashSet<>());
                    set.add(pct);
				}
			}
		}
		if (!drMap.isEmpty())
		{
			mods.append("DR:").append(drFacet.getDRString(id, drMap));
		}

		if (!aPC.hasControl(CControl.ACVARTOTAL))
		{
			int nat = (int) BonusCalc.charBonusTo(pct, "COMBAT", "AC", aPC);

			if (nat != 0)
			{
				mods.append("AC BONUS:").append(nat);
			}
		}

		float cr = pct.getCR(totalLevels, totalHitDice);
		if (cr != 0)
		{
			mods.append("CR:").append(cr).append(' ');
		}

		if (display.getTemplateSR(pct, totalLevels, totalHitDice) != 0)
		{
			mods.append("SR:").append(display.getTemplateSR(pct, totalLevels, totalHitDice)).append(' ');
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
