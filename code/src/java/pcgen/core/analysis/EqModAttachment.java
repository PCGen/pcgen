/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.List;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.helper.EqModRef;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;

public final class EqModAttachment
{

    private EqModAttachment()
    {
    }

    public static void finishEquipment(Equipment eq)
    {
        for (int i = 1;i <= 2;i++)
        {
            EquipmentHead head = eq.getEquipmentHeadReference(i);
            if (head == null)
            {
                continue;
            }
            List<EqModRef> modInfoList = head.getListFor(ListKey.EQMOD_INFO);
            if (modInfoList == null)
            {
                continue;
            }
            for (EqModRef modRef : modInfoList)
            {
                List<EquipmentModifier> modlist = head.getListFor(ListKey.EQMOD);
                EquipmentModifier eqMod = modRef.getRef().get();
                String eqModKey = eqMod.getKeyName();
                EquipmentModifier curMod = null;
                if (modlist != null)
                {
                    for (EquipmentModifier mod : modlist)
                    {
                        if (mod.getKeyName().equals(eqModKey))
                        {
                            curMod = mod;
                            break;
                        }
                    }
                }

                // If not already attached, then add a new one
                if (curMod == null)
                {
                    // only make a copy if we need to
                    // add qualifiers to modifier
                    if (!eqMod.getSafe(StringKey.CHOICE_STRING).isEmpty())
                    {
                        eqMod = eqMod.clone();
                    }

                    eq.addToEqModifierList(eqMod, i == 1);
                } else
                {
                    eqMod = curMod;
                }

                // Add the associated choices
                if (!eqMod.getSafe(StringKey.CHOICE_STRING).isEmpty())
                {
                    List<String> choices = modRef.getChoices();
                    for (String x : choices)
                    {
                        Integer min = eqMod.get(IntegerKey.MIN_CHARGES);
                        if (min != null && min > 0 || (eqMod.getSafe(StringKey.CHOICE_STRING).startsWith("EQBUILDER")))
                        {
                            // We clear the associated info to avoid a
                            // buildup of info
                            // like number of charges.
                            eq.removeAllAssociations(eqMod);
                        }
                        eq.addAssociation(eqMod, x);
                    }
                }
            }
        }
    }

}
