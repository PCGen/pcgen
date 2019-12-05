/*
 * Copyright 2015 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package pcgen.rules.persistence.token;

import pcgen.cdom.base.Loadable;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * This Token converts a list (what was likely a ListKey based token storing
 * strings or TypeSafeConstants in the past) into a FACTSET.
 *
 * @param <T> The type of object on which this AbstractToFactSetToken can
 *            operate.
 */
public abstract class AbstractToFactSetToken<T extends Loadable> extends AbstractNonEmptyToken<T>
        implements CDOMCompatibilityToken<T>
{

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, T obj, String value)
    {
        if (!context.processToken(obj, "FACTSET", getTokenName() + '|' + value))
        {
            Logging.replayParsedMessages();
            return new ParseResult.Fail("Delegation Error to FACTSET");
        }

        return ParseResult.SUCCESS;
    }

    @Override
    public int compatibilityLevel()
    {
        return 6;
    }

    @Override
    public int compatibilitySubLevel()
    {
        return 4;
    }

    @Override
    public int compatibilityPriority()
    {
        return 0;
    }

}
