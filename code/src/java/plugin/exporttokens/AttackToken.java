/*
 * AttackToken.java
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

import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.AttackInfo;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.util.enumeration.AttackType;

/**
 * Class Deals with:
 * <p>
 * ATTACK.GRAPPLE.BASE
 * ATTACK.GRAPPLE.EPIC
 * ATTACK.GRAPPLE.MISC
 * ATTACK.GRAPPLE.SIZE
 * ATTACK.GRAPPLE.STAT
 * ATTACK.GRAPPLE.TOTAL
 * ATTACK.MELEE.BASE
 * ATTACK.MELEE.EPIC
 * ATTACK.MELEE.MISC
 * ATTACK.MELEE.SIZE
 * ATTACK.MELEE.STAT
 * ATTACK.MELEE.TOTAL
 * ATTACK.RANGED.BASE
 * ATTACK.RANGED.EPIC
 * ATTACK.RANGED.MISC
 * ATTACK.RANGED.SIZE
 * ATTACK.RANGED.STAT
 * ATTACK.RANGED.TOTAL
 * ATTACK.UNARMED.BASE
 * ATTACK.UNARMED.EPIC
 * ATTACK.UNARMED.SIZE
 * ATTACK.UNARMED.TOTAL
 */
public class AttackToken extends Token
{
    /**
     * Token name
     */
    public static final String TOKENNAME = "ATTACK";

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
            String attackTypeString = aTok.nextToken();
            String modifier = aTok.hasMoreTokens() ? aTok.nextToken() : "";
            String format = aTok.hasMoreTokens() ? aTok.nextToken() : "";

            AttackType attackType = AttackType.valueOf(attackTypeString);
            retString = AttackInfo.getAttackInfo(pc, attackType, modifier);

            // SHORT means we only return the first attack bonus
            if ("SHORT".equalsIgnoreCase(format))
            {
                int sepPos = retString.indexOf('/');
                if (sepPos >= 0)
                {
                    retString = retString.substring(0, sepPos);
                }
            }
        }

        return retString;
    }
}
