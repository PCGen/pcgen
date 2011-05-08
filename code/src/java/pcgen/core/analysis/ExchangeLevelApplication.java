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
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.content.LevelExchange;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;

public class ExchangeLevelApplication
{

	public static void exchangeLevels(final PlayerCharacter aPC, PCClass newcl)
	{
		LevelExchange le = newcl.get(ObjectKey.EXCHANGE_LEVEL);
	
			try
			{
				PCClass cl = le.getExchangeClass().resolvesTo();
				int iMinLevel = le.getMinDonatingLevel();
				int iMaxDonation = le.getMaxDonatedLevels();
				int iLowest = le.getDonatingLowerLevelBound();
				final PCClass aClass = aPC.getClassKeyed(cl.getKeyName());
	
				if (aClass != null)
				{
					final int iLevel = aPC.getLevel(aClass);
	
					if (iLevel >= iMinLevel)
					{
						iMaxDonation = Math.min(iMaxDonation, iLevel - iLowest);
						if (newcl.hasMaxLevel())
						{
							iMaxDonation =
									Math.min(iMaxDonation, newcl.getSafe(IntegerKey.LEVEL_LIMIT) - 1);
						}
	
						if (iMaxDonation > 0)
						{
							//
							// Build the choice list
							//
							final List<String> choiceNames =
									new ArrayList<String>();
	
							for (int i = 0; i <= iMaxDonation; ++i)
							{
								choiceNames.add(Integer.toString(i));
							}
	
							//
							// Get number of levels to exchange for this class
							//
							final ChooserInterface c =
									ChooserFactory.getChooserInstance();
							c
								.setTitle("Select number of levels to convert from "
									+ aClass.getDisplayName()
									+ " to "
									+ newcl.getDisplayName());
							c.setTotalChoicesAvail(1);
							c.setPoolFlag(false);
							c.setAvailableList(choiceNames);
							c.setVisible(true);
	
							final List<String> selectedList =
									c.getSelectedList();
							int iLevels = 0;
	
							if (!selectedList.isEmpty())
							{
								iLevels = Integer.parseInt(selectedList.get(0));
							}
	
							if (iLevels > 0)
							{
								aPC.giveClassesAway(newcl, aClass, iLevels);
							}
						}
					}
				}
			}
			catch (NumberFormatException exc)
			{
				ShowMessageDelegate.showMessageDialog("levelExchange:"
					+ Constants.LINE_SEPARATOR + exc.getMessage(),
					Constants.APPLICATION_NAME, MessageType.ERROR);
			}
	}

}
