/*
 * DefenseToken.java
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.exporttokens;

import java.util.StringTokenizer;

import pcgen.cdom.util.CControl;
import pcgen.core.PlayerCharacter;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/**
 * Deal with tokens:
 * <p>
 * DEFENSE.TOTAL
 * DEFENSE.FLATFOOTED
 * DEFENSE.TOUCH
 * DEFENSE.BASE
 * DEFENSE.ABILITY
 * DEFENSE.CLASS
 * DEFENSE.DODGE
 * DEFENSE.EQUIPMENT
 * DEFENSE.MISC
 * DEFENSE.NATURAL
 * DEFENSE.SIZE
 */
public class DefenseToken extends Token
{
    /**
     * Token Name
     */
    public static final String TOKENNAME = "DEFENSE";

    @Override
    public String getTokenName()
    {
        return TOKENNAME;
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        String retString = "";
        StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
        aTok.nextToken();

        if (aTok.hasMoreTokens())
        {
            String defenseType = aTok.nextToken();
            CharacterDisplay display = pc.getDisplay();

            String solverValue = pc.getControl("ACVAR" + defenseType);
            if (solverValue != null)
            {
                Object val = pc.getGlobal(solverValue);
                int intValue = ((Number) val).intValue();
                if ("EQUIPMENT".equals(defenseType))
                {
                    val = pc.getGlobal(pc.getControl(CControl.ACVARARMOR));
                    intValue += ((Number) val).intValue();
                }
                retString = Integer.toString(intValue);
            } else if (defenseType.equals("TOTAL"))
            {
                retString = Integer.toString(display.calcACOfType("Total"));
            } else if (defenseType.equals("FLATFOOTED"))
            {
                retString = Integer.toString(display.calcACOfType("Flatfooted"));
            } else if (defenseType.equals("TOUCH"))
            {
                retString = Integer.toString(display.calcACOfType("Touch"));
            } else if (defenseType.equals("BASE"))
            {
                retString = Integer.toString(display.calcACOfType("Base"));
            } else if (defenseType.equals("ABILITY"))
            {
                retString = Integer.toString(display.calcACOfType("Ability"));
            } else if (defenseType.equals("CLASS"))
            {
                retString = Integer.toString(display.calcACOfType("ClassDefense"));
            } else if (defenseType.equals("DODGE"))
            {
                retString = Integer.toString(display.calcACOfType("Dodge"));
            } else if (defenseType.equals("EQUIPMENT"))
            {
                retString = Integer.toString(display.calcACOfType("Equipment") + display.calcACOfType("Armor"));
            } else if (defenseType.equals("MISC"))
            {
                retString = Integer.toString(display.calcACOfType("Misc"));
            } else if (defenseType.equals("NATURAL"))
            {
                retString = Integer.toString(display.calcACOfType("NaturalArmor"));
            } else if (defenseType.equals("SIZE"))
            {
                retString = Integer.toString(display.calcACOfType("Size"));
            }
        }

        return retString;
    }
}
