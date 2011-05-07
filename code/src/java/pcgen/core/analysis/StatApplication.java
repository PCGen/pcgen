/*
 * Copyright 2009 (C) Tom Parker <thpr@users.sourceforge.net>
 * Derived from PCClass.java
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.RuleConstants;
import pcgen.core.SettingsHandler;
import pcgen.core.utils.MessageType;
import pcgen.util.InputFactory;
import pcgen.util.InputInterface;

public class StatApplication
{

	//
	// Ask user to select a stat to increment. This can happen before skill
	// points
	// are calculated, so an increase to the appropriate stat can give more
	// skill points
	//
	public static final int askForStatIncrease(final PlayerCharacter aPC, 
		final int statsToChoose, final boolean isPre)
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
	
		String extraMsg = "";
	
		if (isPre)
		{
			extraMsg = "\nRaising a stat here may award more skill points.";
		}
	
		int iCount = 0;
		Set<PCStat> statsAlreadyBonused = new HashSet<PCStat>();
		boolean allowStacks = SettingsHandler.getGame().isBonusStatAllowsStack();
		for (int ix = 0; ix < statsToChoose; ++ix)
		{
			final StringBuffer sStats = new StringBuffer();
			final List<String> selectableStats = new ArrayList<String>();
	
			for (PCStat aStat : aPC.getStatSet())
			{
				final int iAdjStat =
						StatAnalysis.getTotalStatFor(aPC, aStat);
				final int iCurStat =
						StatAnalysis.getBaseStatFor(aPC, aStat);
				sStats.append(aStat.getAbb()).append(": ").append(iCurStat);
	
				if (iCurStat != iAdjStat)
				{
					sStats.append(" adjusted: ").append(iAdjStat);
				}
	
				sStats.append(" (").append(
					StatAnalysis.getStatModFor(aPC, aStat)).append(
					")");
	
				if (allowStacks || !statsAlreadyBonused.contains(aStat))
				{
					sStats.append("\n");
					selectableStats.add(aStat.getDisplayName());
				}
				else
				{
					sStats.append(" * Already incremented.\n");
				}
			}
	
			final String[] selectionValues = selectableStats.toArray(new String[]{});
			final InputInterface ii = InputFactory.getInputInstance();
			final Object selectedValue =
					ii
						.showInputDialog(
							null,
							"Choose stat to increment or select Cancel to increment stat on the Summary tab."
								+ extraMsg
								+ "\n\n"
								+ "Current Stats:\n"
								+ sStats + "\n", Constants.APPLICATION_NAME,
							MessageType.INFORMATION,
							selectionValues,
							selectionValues[0]);
	
			if (selectedValue != null)
			{
				for (PCStat aStat : aPC.getStatSet())
				{
					if (aStat.getDisplayName().equalsIgnoreCase(
						selectedValue.toString()))
					{
						aPC.saveStatIncrease(aStat, 1, isPre);
						aPC.setAssoc(aStat, AssociationKey.STAT_SCORE, aPC.getAssoc(aStat, AssociationKey.STAT_SCORE) + 1);
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
