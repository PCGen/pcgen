/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 *
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.SystemCollections;
import pcgen.core.character.EquipSlot;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public final class EquipSlotLoader extends LstLineFileLoader
{

    @Override
    public void parseLine(LoadContext context, String lstLine, URI sourceURI)
    {
        final EquipSlot eqSlot = new EquipSlot();

        final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

        Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(EquipSlotLstToken.class);
        while (colToken.hasMoreTokens())
        {
            final String colString = colToken.nextToken().trim();

            final int idxColon = colString.indexOf(':');
            String key = "";
            try
            {
                key = colString.substring(0, idxColon);
            } catch (StringIndexOutOfBoundsException e)
            {
                // TODO Handle Exception
            }
            EquipSlotLstToken token = (EquipSlotLstToken) tokenMap.get(key);

            //TODO: (DJ) Sick hack, remove in 5.11.x
            if (token != null && key.equals("NUMSLOTS"))
            {
                final String value = colString.substring(idxColon + 1);
                LstUtils.deprecationCheck(token, eqSlot.getSlotName(), sourceURI, value);
                if (!token.parse(eqSlot, lstLine, getGameMode()))
                {
                    Logging.errorPrint(
                            "Error parsing equip slots " + eqSlot.getSlotName() + ':' + sourceURI + ':' + colString + "\"");
                }
                break;
            } else if (token != null)
            {
                final String value = colString.substring(idxColon + 1);
                LstUtils.deprecationCheck(token, eqSlot.getSlotName(), sourceURI, value);
                if (!token.parse(eqSlot, value, getGameMode()))
                {
                    Logging.errorPrint(
                            "Error parsing equip slots " + eqSlot.getSlotName() + ':' + sourceURI + ':' + colString + "\"");
                }
            } else
            {
                Logging.errorPrint("Illegal slot info '" + lstLine + "' in " + sourceURI.toString());
            }
        }

        SystemCollections.addToEquipSlotsList(eqSlot, getGameMode());
    }
}
