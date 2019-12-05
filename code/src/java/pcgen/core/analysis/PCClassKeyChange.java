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

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.rules.context.LoadContext;

public final class PCClassKeyChange
{
    private PCClassKeyChange()
    {
    }

    public static void changeReferences(String oldClass, PCClass pcc)
    {
        String newClass = pcc.getKeyName();

        // Don't traverse the list if the names are the same
        if (oldClass.equals(newClass))
        {
            return;
        }

        renameVariables(oldClass, pcc, newClass);
        renameBonusTarget(pcc, oldClass, newClass);

        // Repeat for Class Levels
        for (PCClassLevel pcl : pcc.getOriginalClassLevelCollection())
        {
            renameVariables(oldClass, pcl, newClass);
            renameBonusTarget(pcl, oldClass, newClass);
        }
    }

    private static void renameVariables(String oldClass, CDOMObject pcc, String newClass)
    {
        //
        // Go through the variable list (DEFINE) and adjust the class to the new
        // name
        //
        for (VariableKey vk : pcc.getVariableKeys())
        {
            pcc.put(vk,
                    FormulaFactory.getFormulaFor(pcc.get(vk).toString().replaceAll("=" + oldClass, "=" + newClass)));
        }
    }

    private static void renameBonusTarget(CDOMObject cdo, String oldClass, String newClass)
    {
        //
        // Go through the bonus list (BONUS) and adjust the class to the new
        // name
        //
        List<BonusObj> bonusList = cdo.getListFor(ListKey.BONUS);
        if (bonusList != null)
        {
            for (BonusObj bonusObj : bonusList)
            {
                final String bonus = bonusObj.toString();
                int offs = -1;

                for (;;)
                {
                    offs = bonus.indexOf('=' + oldClass, offs + 1);

                    if (offs < 0)
                    {
                        break;
                    }
                    LoadContext context = Globals.getContext();
                    final BonusObj aBonus = Bonus.newBonus(context,
                            bonus.substring(0, offs + 1) + newClass + bonus.substring(offs + oldClass.length() + 1));

                    if (aBonus != null)
                    {
                        cdo.addToListFor(ListKey.BONUS, aBonus);
                    }
                    cdo.removeFromListFor(ListKey.BONUS, bonusObj);
                }
            }
        }
    }

}
