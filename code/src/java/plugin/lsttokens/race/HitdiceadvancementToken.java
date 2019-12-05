/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.race;

import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Race;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * Class deals with HITDICEADVANCEMENT Token
 */
public class HitdiceadvancementToken extends AbstractTokenWithSeparator<Race> implements CDOMPrimaryToken<Race>
{

    @Override
    public String getTokenName()
    {
        return "HITDICEADVANCEMENT";
    }

    @Override
    protected char separator()
    {
        return ',';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, Race race, String value)
    {
        final StringTokenizer commaTok = new StringTokenizer(value, Constants.COMMA);

        context.getObjectContext().removeList(race, ListKey.HITDICE_ADVANCEMENT);
        int last = 0;
        while (commaTok.hasMoreTokens())
        {
            String tok = commaTok.nextToken();
            int hd;
            if ("*".equals(tok))
            {
                if (commaTok.hasMoreTokens())
                {
                    return new ParseResult.Fail("Found * in " + getTokenName() + " but was not at end of list");
                }

                hd = Integer.MAX_VALUE;
            } else
            {
                try
                {
                    hd = Integer.parseInt(tok);
                    if (hd < last)
                    {
                        return new ParseResult.Fail("Found " + hd + " in " + getTokenName() + " but was < 1 "
                                + "or the previous value in the list: " + value);
                    }
                    last = hd;
                } catch (NumberFormatException nfe)
                {
                    return new ParseResult.Fail(nfe.getMessage());
                }
            }
            context.getObjectContext().addToList(race, ListKey.HITDICE_ADVANCEMENT, hd);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Race race)
    {
        Changes<Integer> changes = context.getObjectContext().getListChanges(race, ListKey.HITDICE_ADVANCEMENT);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean needsComma = false;
        int last = 0;
        Collection<Integer> list = changes.getAdded();
        for (Iterator<Integer> it = list.iterator();it.hasNext();)
        {
            if (needsComma)
            {
                sb.append(',');
            }
            needsComma = true;
            Integer hd = it.next();
            if (hd == Integer.MAX_VALUE)
            {
                if (it.hasNext())
                {
                    context.addWriteMessage(
                            "Integer MAX_VALUE found in " + getTokenName() + " was not at the end of the array.");
                    return null;
                }
                sb.append('*');
            } else
            {
                if (hd < last)
                {
                    Logging.errorPrint("Found " + hd + " in " + getTokenName() + " but was <= zero "
                            + "or the previous value in the list: " + list);
                    return null;
                }
                last = hd;
                sb.append(hd);
            }
        }
        return new String[]{sb.toString()};
    }

    @Override
    public Class<Race> getTokenClass()
    {
        return Race.class;
    }
}
