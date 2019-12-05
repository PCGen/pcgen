/*
 * AlignmentToken.java
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
import pcgen.cdom.util.CControl;
import pcgen.core.PCAlignment;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.output.channel.compat.AlignmentCompat;

/**
 * Class deals with ALIGNMENT and ALIGNMENT.SHORT Token
 */
public class AlignmentToken extends Token
{
    @Override
    public String getTokenName()
    {
        return "ALIGNMENT";
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        String retString = "";

        if (!pc.getDisplay().getSuppressBioField(BiographyField.ALIGNMENT))
        {
            if ("ALIGNMENT".equals(tokenSource))
            {
                retString = getAlignmentDisplay(pc);
            } else if ("ALIGNMENT.SHORT".equals(tokenSource))
            {
                retString = getShortToken(pc);
            }
        }

        return retString;
    }

    private String getAlignmentDisplay(PlayerCharacter pc)
    {
        if (!pc.isFeatureEnabled(CControl.ALIGNMENTFEATURE))
        {
            return "";
        }
        final PCAlignment alignment = AlignmentCompat.getCurrentAlignment(pc.getCharID());
        return alignment == null ? "None" : alignment.getDisplayName();
    }

    /**
     * Get Alignment Short Token
     *
     * @param display
     * @return Alignment Short Token
     */
    public static String getShortToken(PlayerCharacter pc)
    {
        if (!pc.isFeatureEnabled(CControl.ALIGNMENTFEATURE))
        {
            return "";
        }

        final PCAlignment alignment = AlignmentCompat.getCurrentAlignment(pc.getCharID());
        return alignment == null ? "None" : alignment.getKeyName();
    }
}
