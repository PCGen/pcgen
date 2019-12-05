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
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;

public final class OutputNameFormatting
{

    private OutputNameFormatting()
    {
    }

    public static String parseOutputName(CDOMObject po, PlayerCharacter aPC)
    {
        return parseOutputName(getOutputName(po), aPC);
    }

    /**
     * Parse the output name to get a useable Name token
     *
     * @param aString
     * @param aPC
     * @return the output name to get a useable Name token
     */
    private static String parseOutputName(final String aString, final PlayerCharacter aPC)
    {
        final int varIndex = aString.indexOf('|');

        if (varIndex <= 0)
        {
            return (aString);
        }

        final StringTokenizer varTokenizer = new StringTokenizer(aString, "|");

        final String preVarStr = varTokenizer.nextToken();

        final ArrayList<Float> varArray = new ArrayList<>();
        final ArrayList<String> tokenList = new ArrayList<>();

        while (varTokenizer.hasMoreElements())
        {
            final String token = varTokenizer.nextToken();
            tokenList.add(token.toUpperCase());
            varArray.add(aPC.getVariableValue(token, ""));
        }

        final StringBuilder result = new StringBuilder(50);
        int varCount = 0;
        int subIndex = preVarStr.indexOf('%');
        int lastIndex = 0;

        while (subIndex >= 0)
        {
            if (subIndex > 0)
            {
                result.append(preVarStr.substring(lastIndex, subIndex));
            }

            final String token = tokenList.get(varCount);
            final Float val = varArray.get(varCount);

            if (token.endsWith(".INTVAL"))
            {
                result.append(String.valueOf(val.intValue()));
            } else
            {
                result.append(val);
            }

            lastIndex = subIndex + 1;
            varCount++;
            subIndex = preVarStr.indexOf('%', lastIndex);
        }

        if (preVarStr.length() > lastIndex)
        {
            result.append(preVarStr.substring(lastIndex));
        }

        return (result.toString());
    }

    /**
     * Returns the Product Identity string
     *
     * @return the Product Identity string
     */
    public static String piString(CDOMObject po)
    {
        String aString = po.toString();

        if (SettingsHandler.guiUsesOutputNameEquipment())
        {
            aString = OutputNameFormatting.getOutputName(po);
        }

        if (po.getSafe(ObjectKey.NAME_PI))
        {
            return "<b><i>" + aString + "</i></b>";
        }

        return aString;
    }

    /**
     * rephrase parenthetical name components, if appropriate
     *
     * @return pre formatted output name
     */
    private static String getPreFormatedOutputName(String displayName)
    {
        //if there are no () to pull from, just return the name
        if (!displayName.contains("(") || !displayName.contains(")"))
        {
            return displayName;
        }

        //we just take from the first ( to the first ), typically there should only be one of each
        final String subName =
                displayName.substring(
                        displayName.indexOf('(') + 1, displayName.lastIndexOf(')')); //the stuff inside the ()
        final StringTokenizer tok = new StringTokenizer(subName, "/");
        final StringBuilder newNameBuff = new StringBuilder(subName.length());

        while (tok.hasMoreTokens())
        {
            //build this new string from right to left
            newNameBuff.insert(0, tok.nextToken());

            if (tok.hasMoreTokens())
            {
                newNameBuff.insert(0, " ");
            }
        }

        return newNameBuff.toString();
    }

    /**
     * Get the output name of the item
     *
     * @return the output name of the item
     */
    public static String getOutputName(CDOMObject po)
    {
        String outputName = po.get(StringKey.OUTPUT_NAME);
        String displayName = po.getDisplayName();
        // if no OutputName has been defined, just return the regular name
        if (outputName == null)
        {
            return displayName;
        } else if (outputName.equalsIgnoreCase("[BASE]") && (displayName.contains("(")))
        {
            outputName = displayName.substring(0, displayName.indexOf('(')).trim();
        }
        if (outputName.contains("[NAME]"))
        {
            outputName = outputName.replaceAll("\\[NAME\\]", getPreFormatedOutputName(displayName));
        }
        return outputName;
    }

}
