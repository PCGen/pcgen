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
package pcgen.core.analysis;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Equipment;
import pcgen.core.EquipmentChoice;
import pcgen.core.EquipmentModifier;
import pcgen.core.PlayerCharacter;
import pcgen.core.chooser.CDOMChooserFacadeImpl;
import pcgen.facade.core.ChooserFacade.ChooserTreeViewType;
import pcgen.system.LanguageBundle;
import pcgen.util.SignedInteger;
import pcgen.util.chooser.ChooserFactory;

public final class EquipmentChoiceDriver
{
    private EquipmentChoiceDriver()
    {
    }

    /**
     * @param pool
     * @param parent
     * @param bAdd   being added
     * @return an integer where apparently (from how it's used) only 0 is significant
     */
    public static boolean getChoice(final int pool, final Equipment parent, EquipmentModifier eqMod, final boolean bAdd,
            PlayerCharacter pc)
    {
        String choiceString = eqMod.getSafe(StringKey.CHOICE_STRING);

        if (choiceString.isEmpty())
        {
            return true;
        }

        final boolean forEqBuilder = choiceString.startsWith("EQBUILDER.");

        if (bAdd && forEqBuilder)
        {
            return true;
        }

        List<Object> selectedList = new ArrayList<>(parent.getAssociationList(eqMod));

        final EquipmentChoice equipChoice =
                buildEquipmentChoice(pool, parent, eqMod, bAdd, forEqBuilder, selectedList.size(), pc);

        int effectiveChoices;
        if (equipChoice.isBAdd())
        {
            effectiveChoices = selectedList.size() + equipChoice.getMaxSelect();
        } else
        {
            effectiveChoices = selectedList.size();
        }

        String title = LanguageBundle.getFormattedString("in_equipChoiceMod", //$NON-NLS-1$
                equipChoice.getTitle(), eqMod.getDisplayName(), "|");
        CDOMChooserFacadeImpl<Object> chooserFacade =
                new CDOMChooserFacadeImpl<>(title, equipChoice.getAvailableList(), selectedList, effectiveChoices);
        chooserFacade.setDefaultView(ChooserTreeViewType.NAME);
        chooserFacade.setAllowsDups(equipChoice.isAllowDuplicates());
        ChooserFactory.getDelegate().showGeneralChooser(chooserFacade);

        selectedList = chooserFacade.getFinalSelected();

        setChoice(parent, eqMod, selectedList, equipChoice);

        return parent.hasAssociations(eqMod);
    }

    public static void setChoice(Equipment parent, EquipmentModifier eqMod, final String choice,
            final EquipmentChoice equipChoice)
    {
        final List<Object> tempList = new ArrayList<>();
        tempList.add(choice);
        setChoice(parent, eqMod, tempList, equipChoice);
    }

    private static void setChoice(Equipment parent, EquipmentModifier eqMod, final List<Object> selectedList,
            final EquipmentChoice equipChoice)
    {
        parent.removeAllAssociations(eqMod);

        for (Object o : selectedList)
        {
            String aString = String.valueOf(o);

            if (equipChoice.getMinValue() < equipChoice.getMaxValue())
            {
                final int idx = aString.indexOf('|');

                if (idx < 0)
                {
                    final List<SignedInteger> secondaryChoice = new ArrayList<>();

                    for (int j = equipChoice.getMinValue();j <= equipChoice.getMaxValue();j +=
                            equipChoice.getIncValue())
                    {
                        if (j != 0)
                        {
                            secondaryChoice.add(new SignedInteger(j));
                        }
                    }

                    String title = LanguageBundle.getFormattedString("in_equipChoiceSelectMod", aString); //$NON-NLS-1$
                    CDOMChooserFacadeImpl<SignedInteger> chooserFacade =
                            new CDOMChooserFacadeImpl<>(title, secondaryChoice, new ArrayList<>(), 1);
                    chooserFacade.setDefaultView(ChooserTreeViewType.NAME);
                    chooserFacade.setAllowsDups(equipChoice.isAllowDuplicates());
                    ChooserFactory.getDelegate().showGeneralChooser(chooserFacade);

                    List<SignedInteger> chosenList = chooserFacade.getFinalSelected();

                    if (chosenList.isEmpty())
                    {
                        continue;
                    }

                    aString += ('|' + chosenList.get(0).toString());
                }
            }

            if (equipChoice.isAllowDuplicates() || !parent.containsAssociated(eqMod, aString))
            {
                parent.addAssociation(eqMod, aString);
            }
        }
    }

    /**
     * Build up the details of a required choice
     *
     * @param pool
     * @param parent       the equipment this modifer will be applied to
     * @param bAdd         is a choice being added or removed
     * @param forEqBuilder
     * @param numSelected
     * @return A populated EquipmentChoice object
     */
    public static EquipmentChoice buildEquipmentChoice(final int pool, final Equipment parent, EquipmentModifier eqMod,
            final boolean bAdd, final boolean forEqBuilder, final int numSelected, PlayerCharacter pc)
    {
        final EquipmentChoice equipChoice = new EquipmentChoice(bAdd, pool);
        String choiceString = eqMod.getSafe(StringKey.CHOICE_STRING);

        if (choiceString.isEmpty())
        {
            return equipChoice;
        }

        equipChoice.constructFromChoiceString(choiceString, parent, pool, numSelected, forEqBuilder, pc);

        return equipChoice;
    }

}
