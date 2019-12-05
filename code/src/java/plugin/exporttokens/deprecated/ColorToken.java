/*
 * ColorToken.java
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
package plugin.exporttokens.deprecated;

import pcgen.cdom.enumeration.BiographyField;
import pcgen.cdom.enumeration.PCStringKey;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;

/**
 * Deal with tokens:
 * COLOR.EYE
 * COLOR.HAIR
 * COLOR.SKIN
 */
public class ColorToken extends AbstractExportToken
{
    @Override
    public String getTokenName()
    {
        return "COLOR";
    }

    @Override
    public String getToken(String tokenSource, CharacterDisplay display, ExportHandler eh)
    {
        String retString = "";

        if ("COLOR.EYE".equals(tokenSource))
        {
            retString = getEyeToken(display);
        } else if ("COLOR.HAIR".equals(tokenSource))
        {
            retString = getHairToken(display);
        } else if ("COLOR.SKIN".equals(tokenSource))
        {
            retString = getSkinToken(display);
        }

        return retString;
    }

    /**
     * Get the eye colour token
     *
     * @param display the display for the character being exported
     * @return eye colour
     */
    private static String getEyeToken(CharacterDisplay display)
    {
        if (display.getSuppressBioField(BiographyField.EYE_COLOR))
        {
            return "";
        }
        return display.getSafeStringFor(PCStringKey.EYECOLOR);
    }

    /**
     * Get the Hair token
     *
     * @param display the display for the character being exported
     * @return hair color
     */
    private static String getHairToken(CharacterDisplay display)
    {
        if (display.getSuppressBioField(BiographyField.HAIR_COLOR))
        {
            return "";
        }
        return display.getSafeStringFor(PCStringKey.HAIRCOLOR);
    }

    /**
     * Get the skin token
     *
     * @param display the display of the character being exported
     * @return skin color
     */
    private static String getSkinToken(CharacterDisplay display)
    {
        if (display.getSuppressBioField(BiographyField.SKIN_TONE))
        {
            return "";
        }
        return display.getSafeStringFor(PCStringKey.SKINCOLOR);
    }

}
