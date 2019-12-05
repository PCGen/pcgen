/*
 * BaseMovementToken.java
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

import pcgen.cdom.enumeration.MovementType;
import pcgen.core.Globals;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;
import pcgen.util.enumeration.Load;

/**
 * BASEMOVEMENT related stuff
 * possible tokens are
 * BASEMOVEMENT.type.load.flag
 * where
 * type    := "WALK" and other Movement Types|a numeric value
 * so 0 is the first movement type etc.
 * load     := "LIGHT"|"MEDIUM"|"HEAVY"|"OVERLOAD"
 * flag     := "TRUE"|"FALSE"
 * TRUE = Add Movement Measurement type to String.
 * FALSE = Dont Add Movement Measurement type to String
 * del     := "."
 * <p>
 * i.e. BASEMOVEMENT.0.LIGHT.TRUE
 * Would output 30' for a normal human
 * and    BASEMOVEMENT.0.LIGHT.FALSE
 * Would output 30 for the same human.
 * <p>
 */
public class BaseMovementToken extends AbstractExportToken
{
    @Override
    public String getTokenName()
    {
        return "BASEMOVEMENT";
    }

    @Override
    public String getToken(String tokenSource, CharacterDisplay display, ExportHandler eh)
    {
        String retString = "";
        if ((display.getRace() != null) && !display.getRace().isUnselected())
        {
            StringTokenizer aTok = new StringTokenizer(tokenSource, ".", false);
            aTok.nextToken(); //clear BASEMOVEMENT Token
            String moveType = "WALK";
            Load load = Load.LIGHT;
            boolean flag = true;

            //Move Type
            if (aTok.hasMoreElements())
            {
                moveType = aTok.nextToken();
                try
                {
                    int movNum = Integer.parseInt(moveType);
                    if (movNum < display.getNumberOfMovements())
                    {
                        moveType = display.getMovementValues().get(movNum).getName();
                    }
                } catch (NumberFormatException e)
                {
                    // Delibrately ignore exception, means movetype is not an index
                }
            }

            //Encumberance Level
            if (aTok.hasMoreElements())
            {
                String loadName = aTok.nextToken();
                for (Load aLoad : Load.values())
                {
                    if (loadName.equals(aLoad.toString()))
                    {
                        load = aLoad;
                    }
                }
            }

            //Display Movement Measurement type?
            if (aTok.hasMoreElements())
            {
                flag = "TRUE".equalsIgnoreCase(aTok.nextToken());
            }
            retString = getBaseMovementToken(display, MovementType.getConstant(moveType), load, flag);
        }
        return retString;
    }

    /**
     * Get the base movement token
     *
     * @param display
     * @param moveType
     * @param load
     * @param displayFlag
     * @return The base movement token
     */
    public static String getBaseMovementToken(CharacterDisplay display, MovementType moveType, Load load, boolean displayFlag)
    {
        if (!display.hasMovement(moveType))
        {
            return "";
        }
        int baseMovement = display.getBaseMovement(moveType, load);
        if (displayFlag)
        {
            return moveType.toString() + ' ' + Globals.getGameModeUnitSet().displayDistanceInUnitSet(baseMovement)
                    + Globals.getGameModeUnitSet().getDistanceUnit();
        }
        return Globals.getGameModeUnitSet().displayDistanceInUnitSet(baseMovement);
    }
}
