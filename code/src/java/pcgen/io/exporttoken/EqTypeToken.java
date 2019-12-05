/*
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
 *
 *
 *
 */
package pcgen.io.exporttoken;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;

/**
 * Deal with EQTYPE Token
 */
public class EqTypeToken extends EqToken
{
    /**
     * Token Name
     */
    public static final String TOKEN_NAME = "EQTYPE";

    @Override
    public String getTokenName()
    {
        return TOKEN_NAME;
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        StringTokenizer aTok = new StringTokenizer(tokenSource, ".", false);
        aTok.nextToken();

        //Merge
        String token = aTok.nextToken();
        int merge = Constants.MERGE_ALL;
        if (token.contains("MERGE"))
        {
            merge = returnMergeType(token);
            token = aTok.nextToken();
        }

        //Get List
        List<Equipment> eqList = new ArrayList<>();
        if ("Container".equals(token))
        {
            for (Equipment eq : pc.getEquipmentListInOutputOrder(merge))
            {
                if (eq.isContainer())
                {
                    eqList.add(eq);
                }
            }
        } else
        {
            eqList = pc.getEquipmentOfTypeInOutputOrder(token, 3, merge);
        }

        int temp = -1;
        //Begin Not code...
        while (aTok.hasMoreTokens())
        {
            if ("NOT".equalsIgnoreCase(token))
            {
                eqList = listNotType(eqList, aTok.nextToken());
            } else if ("ADD".equalsIgnoreCase(token))
            {
                eqList = listAddType(pc, eqList, aTok.nextToken());
            } else if ("IS".equalsIgnoreCase(token))
            {
                eqList = listIsType(eqList, aTok.nextToken());
            } else
            {
                // In the end of the above, bString would
                // be valid token, that should go into temp.
                try
                {
                    temp = Integer.parseInt(token);
                } catch (NumberFormatException exc)
                {
                    // not an error!
                }
            }

            if (temp >= 0)
            {
                break;
            }
            token = aTok.nextToken();
        }

        String tempString = aTok.hasMoreTokens() ? aTok.nextToken() : "";

        if ((temp >= 0) && (temp < eqList.size()))
        {
            Equipment eq = eqList.get(temp);
            return getEqToken(pc, eq, tempString, aTok);
        }
        return "";
    }
}
