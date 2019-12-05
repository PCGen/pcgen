/**
 * Copyright (c) 2008 Andrew Wilson <nuance@users.sourceforge.net>.
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * <p>
 * Created 05-Aug-2008 00:21:49
 */

package pcgen.core.term;

import java.util.ArrayList;
import java.util.List;

import pcgen.core.Equipment;
import pcgen.core.EquipmentUtilities;
import pcgen.core.PlayerCharacter;

public class PCCountEqTypeTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{

    final String[] types;
    private final int merge;

    public PCCountEqTypeTermEvaluator(String originalText, String[] types, final int merge)
    {
        this.originalText = originalText;
        this.types = types;
        this.merge = merge;
    }

    @Override
    public Float resolve(PlayerCharacter pc)
    {

        List<Equipment> aList = new ArrayList<>();

        int cur = 0;
        String aType = types[cur];

        if ("CONTAINER".equals(aType))
        {
            cur++;
            final List<Equipment> equipList = pc.getEquipmentListInOutputOrder(merge);
            for (Equipment eq : equipList)
            {
                if (eq.isContainer())
                {
                    aList.add(eq);
                }
            }
        } else if ("WEAPON".equalsIgnoreCase(aType))
        {
            cur++;
            aList = pc.getExpandedWeapons(merge);
        } else if ("ACITEM".equalsIgnoreCase(aType))
        {
            cur++;
            // Special check for ACITEM which is really anything with AC
            // in the bonus section, but is not type SHIELD or ARMOR
            final List<Equipment> equipList = pc.getEquipmentListInOutputOrder(merge);
            for (Equipment eq : equipList)
            {
                if (eq.altersAC(pc) && !eq.isArmor() && !eq.isShield())
                {
                    aList.add(eq);
                }
            }
        }

        // This is new, it's to deal with the fact that the code uses an array
        // now instead of the deprecated StringTokeniser class.  We can have
        // an empty tokeniser, but not an empty array.  To get around this we
        // create an array of one item, the empty string.
        else if ("".equalsIgnoreCase(aType))
        {
            cur++;
            aList = pc.getEquipmentOfTypeInOutputOrder(aType, 3, merge);
        } else
        {
            aList = pc.getEquipmentOfTypeInOutputOrder(aType, 3, merge);
        }

        while (cur < types.length)
        {
            final String curTok = types[cur];
            cur++;

            if ("NOT".equalsIgnoreCase(curTok))
            {
                aList = EquipmentUtilities.removeEqType(aList, types[cur]);
                cur++;
            } else if ("ADD".equalsIgnoreCase(curTok))
            {
                aList = pc.addEqType(aList, types[cur]);
                cur++;
            } else if ("IS".equalsIgnoreCase(curTok))
            {
                aList = EquipmentUtilities.removeNotEqType(aList, types[cur]);
                cur++;
            } else if ("EQUIPPED".equalsIgnoreCase(curTok) || "NOTEQUIPPED".equalsIgnoreCase(curTok))
            {
                final boolean eFlag = "EQUIPPED".equalsIgnoreCase(curTok);

                for (int ix = aList.size() - 1;ix >= 0;--ix)
                {
                    final Equipment anEquip = aList.get(ix);

                    if (anEquip.isEquipped() != eFlag)
                    {
                        aList.remove(anEquip);
                    }
                }
            }
        }

        return (float) aList.size();
    }

    @Override
    public boolean isSourceDependant()
    {
        return false;
    }

    public boolean isStatic()
    {
        return false;
    }
}
