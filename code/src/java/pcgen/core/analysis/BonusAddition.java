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

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.util.Logging;

public final class BonusAddition
{
    private BonusAddition()
    {
    }

    /**
     * Apply the bonus to a character. The bonus can optionally only be added
     * once no matter how many associated choices this object has. This is
     * normally used where a bonus is added for each associated choice.
     *
     * @param bonusString  The unparsed bonus to be added.
     * @param chooseString The choice to be added.
     * @param aPC          The character to apply thr bonus to.
     */
    public static void applyBonus(String bonusString, String chooseString, PlayerCharacter aPC, CDOMObject target)
    {
        bonusString = makeBonusString(bonusString, chooseString, aPC);

        final BonusObj aBonus = Bonus.newBonus(Globals.getContext(), bonusString);
        if (aBonus != null)
        {
            aPC.addSaveableBonus(aBonus, target);
        }
    }

    /**
     * Remove the bonus from this objects list of bonuses.
     *
     * @param bonusString The string representing the bonus
     * @param aPC         The player character to remove th bonus from.
     */
    public static void removeBonus(String bonusString, PlayerCharacter aPC, CDOMObject target)
    {
        BonusObj toRemove = null;
        BonusObj aBonus = Bonus.newBonus(Globals.getContext(), bonusString);
        String bonusStrRep = String.valueOf(aBonus);

        for (BonusObj listBonus : aPC.getSaveableBonusList(target))
        {
            if (listBonus.toString().equals(bonusStrRep))
            {
                toRemove = listBonus;
            }
        }

        if (toRemove != null)
        {
            aPC.removeSaveableBonus(toRemove, target);
        } else
        {
            Logging.errorPrint("removeBonus: Could not find bonus: " + bonusString + " in bonusList "
                    + aPC.getSaveableBonusList(target));
        }
    }

    private static String makeBonusString(String bonusString, String chooseString, PlayerCharacter aPC)
    {
        // assumption is that the chooseString is in the form
        // class/type[space]level
        int i = chooseString.lastIndexOf(' ');
        String classString = "";
        String levelString = "";

        if (bonusString.startsWith("BONUS:"))
        {
            bonusString = bonusString.substring(6);
        }

        boolean lockIt = bonusString.endsWith(".LOCK");

        if (lockIt)
        {
            bonusString = bonusString.substring(0, bonusString.lastIndexOf(".LOCK"));
        }

        if (i >= 0)
        {
            classString = chooseString.substring(0, i);

            if (i < chooseString.length())
            {
                levelString = chooseString.substring(i + 1);
            }
        }

        while (bonusString.lastIndexOf("TYPE=%") >= 0)
        {
            i = bonusString.lastIndexOf("TYPE=%");
            bonusString = bonusString.substring(0, i + 5) + classString + bonusString.substring(i + 6);
        }

        while (bonusString.lastIndexOf("CLASS=%") >= 0)
        {
            i = bonusString.lastIndexOf("CLASS=%");
            bonusString = bonusString.substring(0, i + 6) + classString + bonusString.substring(i + 7);
        }

        while (bonusString.lastIndexOf("LEVEL=%") >= 0)
        {
            i = bonusString.lastIndexOf("LEVEL=%");
            bonusString = bonusString.substring(0, i + 6) + levelString + bonusString.substring(i + 7);
        }

        if (lockIt)
        {
            i = bonusString.lastIndexOf('|');

            Float val = aPC.getVariableValue(bonusString.substring(i + 1), "");
            bonusString = bonusString.substring(0, i) + "|" + val;
        }

        return bonusString;
    }

}
