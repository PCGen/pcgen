/*
 * Copyright 2012 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.core.display;

import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.util.Delta;

public final class UnarmedDamageDisplay
{

    private UnarmedDamageDisplay()
    {
    }

    /**
     * Get the unarmed damage string for this PC as adjusted by the booleans
     * passed in.
     *
     * @param pc
     * @param includeStrBonus
     * @param adjustForPCSize
     * @return the unarmed damage string
     */
    public static String getUnarmedDamageString(PlayerCharacter pc, final boolean includeStrBonus,
            final boolean adjustForPCSize)
    {
        CharacterDisplay display = pc.getDisplay();
        String retString = "2|1d2";

        for (PCClass pcClass : display.getClassSet())
        {
            retString = getBestUDamString(retString,
                    pcClass.getUdamForLevel(display.getLevel(pcClass), pc, adjustForPCSize));
        }

        int sizeInt = adjustForPCSize ? pc.sizeInt() : pc.racialSizeInt();
        for (List<String> unarmedDamage : display.getUnarmedDamage())
        {
            String aDamage;
            if (unarmedDamage.size() == 1)
            {
                aDamage = unarmedDamage.get(0);
            } else
            {
                aDamage = unarmedDamage.get(sizeInt);
            }
            retString = UnarmedDamageDisplay.getBestUDamString(retString, aDamage);
        }
        //Test against the default for the race
        String pObjDamage = display.getUDamForRace();
        retString = getBestUDamString(retString, pObjDamage);

        // string is in form sides|damage, just return damage portion
        StringBuilder ret = new StringBuilder(retString.substring(retString.indexOf('|') + 1));
        if (includeStrBonus)
        {
            int sb = (int) display.getStatBonusTo("DAMAGE", "TYPE.MELEE");
            sb += (int) display.getStatBonusTo("DAMAGE", "TYPE=MELEE");
            if (sb != 0)
            {
                ret.append(Delta.toString(sb));
            }
        }
        return ret.toString();
    }

    /**
     * Picks the biggest die size from two strings in the form V|WdX, YdZ (where
     * the WdX represents W X sided dice).  If Z is larger than X, returns
     * V|YdZ, otherwise it returns V|WdX
     *
     * @param oldString 2|1d3
     * @param newString 1d4
     * @return in the example parameters given, will return 2|1d4 (because the
     * 4 is bigger than the 3). If the last figure in the new string
     * isn't larger, it returns the original string.
     */
    public static String getBestUDamString(final String oldString, final String newString)
    {
        if ((newString == null) || (newString.length() < 2))
        {
            return oldString;
        }
        if (oldString == null)
        {
            StringTokenizer aTok = new StringTokenizer(newString, " dD+-(x)");
            aTok.nextToken();
            return Integer.parseInt(aTok.nextToken()) + "|" + newString;
        }

        StringTokenizer aTok = new StringTokenizer(oldString, "|");
        int sides = Integer.parseInt(aTok.nextToken());
        String retString = oldString;

        aTok = new StringTokenizer(newString, " dD+-(x)");

        if (aTok.countTokens() > 1)
        {
            aTok.nextToken();

            final int i = Integer.parseInt(aTok.nextToken());

            if (sides < i)
            {
                sides = i;
                retString = sides + "|" + newString;
            }
        }

        return retString;
    }

}
