/*
 * DomainToken.java
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

import java.util.ArrayList;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.Domain;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.display.DescriptionFormatting;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/**
 * Deals with tokens:
 * <p>
 * DOMAIN.x
 * DOMAIN.x.POWER
 */
public class DomainToken extends Token
{
    /**
     * Token Name
     */
    public static final String TOKENNAME = "DOMAIN";

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
            int domainIndex = 0;

            try
            {
                domainIndex = Math.max(0, Integer.parseInt(aTok.nextToken()) - 1);
            } catch (Exception e)
            {
                // TODO - This exception needs to be handled
            }

            if (aTok.hasMoreTokens())
            {
                String subToken = aTok.nextToken();

                if ("POWER".equals(subToken))
                {
                    retString = getPowerToken(pc, domainIndex);
                }
            } else
            {
                retString = getDomainToken(pc.getDisplay(), domainIndex);
            }
        }

        return retString;
    }

    /**
     * Get the DOMAIN token
     *
     * @param domainIndex
     * @return token
     */
    public String getDomainToken(CharacterDisplay display, int domainIndex)
    {
        try
        {
            Domain domain = new ArrayList<>(display.getSortedDomainSet()).get(domainIndex);

            return OutputNameFormatting.getOutputName(domain);
        } catch (Exception e)
        {
            return Constants.EMPTY_STRING;
        }
    }

    /**
     * Get the POWER sub token
     *
     * @param pc
     * @param domainIndex
     * @return POWER sub token
     */
    public static String getPowerToken(PlayerCharacter pc, int domainIndex)
    {
        try
        {
            Domain domain = new ArrayList<>(pc.getDisplay().getSortedDomainSet()).get(domainIndex);

            return DescriptionFormatting.piWrapDesc(domain, pc.getDescription(domain), true);
        } catch (Exception e)
        {
            return Constants.EMPTY_STRING;
        }
    }
}
