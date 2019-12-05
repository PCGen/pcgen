/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.testsupport;

import java.net.URI;
import java.util.StringTokenizer;

import pcgen.cdom.base.Loadable;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.util.Logging;

public class CDOMTokenLoader<T extends Loadable> implements CDOMLoader<T>
{

    @Override
    public boolean parseLine(LoadContext context, T obj, String val, URI source)
    {
        if (val == null)
        {
            return true;
        }
        boolean returnValue = true;
        StringTokenizer st = new StringTokenizer(val, "\t");
        while (st.hasMoreTokens())
        {
            String token = st.nextToken().trim();
            int colonLoc = token.indexOf(':');
            if (colonLoc == -1)
            {
                Logging.errorPrint("Invalid Token - does not contain a colon: "
                        + token);
                returnValue = false;
                continue;
            } else if (colonLoc == 0)
            {
                Logging.errorPrint("Invalid Token - starts with a colon: "
                        + token);
                returnValue = false;
            }
            String key = token.substring(0, colonLoc);
            String value = (colonLoc == (token.length() - 1)) ? null : token
                    .substring(colonLoc + 1);
            if (context.processToken(obj, key.intern(), value.intern()))
            {
                context.commit();
            } else
            {
                context.rollback();
                Logging.replayParsedMessages();
                returnValue = false;
            }
            Logging.clearParseMessages();
        }
        return returnValue;
    }
}
