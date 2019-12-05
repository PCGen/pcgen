/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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
 */

package pcgen.rules.persistence.token;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.rules.context.LoadContext;

/**
 * Parses a token of the form: {@literal <Token Name>:<string>}
 *
 * @param <T> The type of object on which this AbstractStringToken can be used
 */
public abstract class AbstractStringToken<T extends CDOMObject> extends AbstractNonEmptyToken<T>
{
    /**
     * This must be overridden to specify the key.
     *
     * @return The key.
     */
    protected abstract StringKey stringKey();

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, T obj, String value)
    {
        context.getObjectContext().put(obj, stringKey(), value);
        return ParseResult.SUCCESS;
    }

    public String[] unparse(LoadContext context, T obj)
    {
        String value = context.getObjectContext().getString(obj, stringKey());
        if (value == null)
        {
            return null;
        }
        return new String[]{value};
    }
}
