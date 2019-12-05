/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package pcgen.gui2.facade;

import java.util.ArrayList;
import java.util.List;

import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.RuleConstants;
import pcgen.core.SizeAdjustment;
import pcgen.core.utils.CoreUtility;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

final class CharacterUtils
{
    private CharacterUtils()
    {
    }

    private static boolean isFreeClothing(Equipment eq, PlayerCharacter aPC, SizeAdjustment pcSizeAdj)
    {
        return !eq.isType("Magic")
                && (CoreUtility.doublesEqual(eq.getCost(aPC).doubleValue(), 0.0))
                && pcSizeAdj.equals(eq.getSizeAdjustment());
    }

    static void selectClothes(final PlayerCharacter aPC)
    {
        if (Globals.checkRule(RuleConstants.FREECLOTHES) && ((aPC.getDisplay().totalNonMonsterLevels()) == 1))
        {
            //
            // See what the PC is already carrying
            //
            List<Equipment> clothes = aPC.getEquipmentOfType("Clothing.Resizable", 3); //$NON-NLS-1$

            //
            // Check to see if any of the clothing the PC
            // is carrying will actually fit and
            // has a zero price attached
            //
            SizeAdjustment pcSizeAdj = aPC.getSizeAdjustment();

            boolean hasClothes = clothes.stream()
                    .anyMatch(eq -> isFreeClothing(eq, aPC, pcSizeAdj));


            // If the PC has no clothing items, or none that
            // are sized to fit, then allow them to pick
            // a free set

            if (!hasClothes)
            {
                clothes = EquipmentList.getEquipmentOfType("Clothing.Resizable.Starting", "Magic.Custom.Auto_Gen");
                if (clothes.isEmpty())
                {
                    clothes = EquipmentList.getEquipmentOfType("Clothing.Resizable", "Magic.Custom.Auto_Gen");
                }

                List<Equipment> selectedClothes = new ArrayList<>();
                selectedClothes =
                        Globals.getChoiceFromList(
                                LanguageBundle.getString("in_sumSelectAFreeSetOfClothing"), //$NON-NLS-1$
                                clothes, selectedClothes, 1, aPC);

                if (!selectedClothes.isEmpty())
                {
                    Equipment eq = selectedClothes.get(0);

                    if (eq != null)
                    {
                        eq = eq.clone();
                        eq.setQty(1.0);

                        //
                        // Need to resize to fit?
                        //
                        if (!pcSizeAdj.equals(eq.getSizeAdjustment()))
                        {
                            eq.resizeItem(aPC, pcSizeAdj);
                        }

                        eq.setCostMod('-' + eq.getCost(aPC).toString()); // make cost 0

                        if (aPC.getEquipmentNamed(eq.nameItemFromModifiers(aPC)) == null)
                        {
                            aPC.addEquipment(eq);
                        } else
                        {
                            Logging.errorPrint("Cannot add duplicate equipment to PC"); //$NON-NLS-1$
                        }
                    }
                }
            }
        }

    }
}
