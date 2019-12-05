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

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.content.LevelExchange;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.chooser.CDOMChooserFacadeImpl;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.facade.core.ChooserFacade.ChooserTreeViewType;
import pcgen.system.LanguageBundle;
import pcgen.util.chooser.ChooserFactory;

public final class ExchangeLevelApplication
{

    private ExchangeLevelApplication()
    {
    }

    public static void exchangeLevels(final PlayerCharacter aPC, PCClass newcl)
    {
        LevelExchange le = newcl.get(ObjectKey.EXCHANGE_LEVEL);

        try
        {
            PCClass cl = le.getExchangeClass().get();
            int iMinLevel = le.getMinDonatingLevel();
            int iMaxDonation = le.getMaxDonatedLevels();
            int iLowest = le.getDonatingLowerLevelBound();
            final PCClass aClass = aPC.getClassKeyed(cl.getKeyName());

            if (aClass != null)
            {
                final int iNumOrigClassLevel = aPC.getLevel(aClass);

                if (iNumOrigClassLevel >= iMinLevel)
                {
                    iMaxDonation = Math.min(iMaxDonation, iNumOrigClassLevel - iLowest + 1);
                    if (newcl.hasMaxLevel())
                    {
                        iMaxDonation =
                                Math.min(iMaxDonation, newcl.getSafe(IntegerKey.LEVEL_LIMIT) - aPC.getLevel(newcl));
                    }

                    if (iMaxDonation > 0)
                    {
                        //
                        // Build the choice list
                        //
                        final List<Integer> choiceNames = new ArrayList<>();

                        for (int i = 0;i <= iMaxDonation;++i)
                        {
                            choiceNames.add(i);
                        }

                        //
                        // Get number of levels to exchange for this class
                        //
                        String title = LanguageBundle.getFormattedString("in_exchangeLevelsChoice",
                                aClass.getDisplayName(), newcl.getDisplayName());
                        CDOMChooserFacadeImpl<Integer> chooserFacade =
                                new CDOMChooserFacadeImpl<>(title, choiceNames, new ArrayList<>(), 1);
                        chooserFacade.setDefaultView(ChooserTreeViewType.NAME);
                        ChooserFactory.getDelegate().showGeneralChooser(chooserFacade);
                        final List<Integer> selectedList = chooserFacade.getFinalSelected();

                        int iLevels = 0;

                        if (!selectedList.isEmpty())
                        {
                            iLevels = selectedList.get(0);
                        }

                        if (iLevels > 0)
                        {
                            aPC.giveClassesAway(newcl, aClass, iLevels);
                        }
                    }
                }
            }
        } catch (NumberFormatException exc)
        {
            ShowMessageDelegate.showMessageDialog("levelExchange:" + Constants.LINE_SEPARATOR + exc.getMessage(),
                    Constants.APPLICATION_NAME, MessageType.ERROR);
        }
    }

}
