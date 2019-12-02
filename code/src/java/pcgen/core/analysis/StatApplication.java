/*
 * Copyright 2009 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.RuleConstants;
import pcgen.core.SettingsHandler;
import pcgen.core.chooser.CDOMChooserFacadeImpl;
import pcgen.facade.core.ChooserFacade.ChooserTreeViewType;
import pcgen.gui2.facade.Gui2InfoFactory;
import pcgen.gui2.util.PrettyIntegerFormat;
import pcgen.system.LanguageBundle;
import pcgen.util.chooser.ChooserFactory;

public final class StatApplication
{

	private StatApplication()
	{
	}

	//
	// Ask user to select a stat to increment. This can happen before skill
	// points
	// are calculated, so an increase to the appropriate stat can give more
	// skill points
	//
	public static int askForStatIncrease(final PlayerCharacter aPC, final int statsToChoose, final boolean isPre)
	{
		//
		// If 1st time here (checks for preincrement), then will only ask if
		// want to ask before level up
		// If 2nd time here, will ask if there are any remaining points
		// unassigned.
		// So, hitting cancel on the 1st popup will cause the 2nd popup to ask
		// again.
		// This is to handle cases where the user is adding multiple levels, so
		// the SKILL point total
		// won't be too messed up
		//
		if (isPre)
		{
			if (!Globals.checkRule(RuleConstants.INTBEFORE))
			{
				return statsToChoose;
			}
		}

		String titleKey = "in_statTitle";
		if (isPre && !Globals.checkRule(RuleConstants.RETROSKILL))
		{
			titleKey = "in_statTitleWithSkill";
		}

		int iCount = 0;
		Set<PCStat> statsAlreadyBonused = new HashSet<>();
		boolean allowStacks = SettingsHandler.getGame().isBonusStatAllowsStack();
		DecimalFormat formatter = PrettyIntegerFormat.getFormat();

		for (int ix = 0; ix < statsToChoose; ++ix)
		{
			final List<String> selectableStats = new ArrayList<>();

			for (PCStat aStat : aPC.getDisplay().getStatSet())
			{
				final StringBuilder sStats = new StringBuilder(100);
				final int iAdjStat = aPC.getTotalStatFor(aStat);
				final int iCurStat = aPC.getBaseStatFor(aStat);
				sStats.append(aStat.getDisplayName()).append(":  ").append(iCurStat);

				if (iCurStat != iAdjStat)
				{
					sStats.append(" adjusted: ").append(iAdjStat);
				}

				sStats.append(" (").append(formatter.format(aPC.getStatModFor(aStat))).append(")");

                if (!allowStacks && statsAlreadyBonused.contains(aStat)) {
                    sStats.append(" * Already incremented.");
                }
                selectableStats.add(sStats.toString());
            }

			CDOMChooserFacadeImpl<String> chooserFacade = new CDOMChooserFacadeImpl<>(
				LanguageBundle.getString(titleKey), selectableStats, new ArrayList<>(), 1);
			chooserFacade.setDefaultView(ChooserTreeViewType.NAME);
			chooserFacade.setPreferRadioSelection(true);
			chooserFacade.setInfoFactory(new Gui2InfoFactory(aPC));
			ChooserFactory.getDelegate().showGeneralChooser(chooserFacade);
			final List<String> selectedValues = chooserFacade.getFinalSelected();
			final String selectedValue = selectedValues.isEmpty() ? null : selectedValues.get(0);

			if (selectedValue != null)
			{
				for (PCStat aStat : aPC.getStatSet())
				{
					if (selectedValue.startsWith(aStat.getDisplayName()))
					{
						aPC.saveStatIncrease(aStat, 1, isPre);
						aPC.setStat(aStat, aPC.getStat(aStat) + 1);
						aPC.setPoolAmount(aPC.getPoolAmount() - 1);
						statsAlreadyBonused.add(aStat);
						++iCount;

						break;
					}
				}
			}
		}

		return statsToChoose - iCount;
	}

}
