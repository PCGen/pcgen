/*
 * Copyright (c) 2009 Mark Jeffries <motorviper@users.sourceforge.net>
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
package pcgen.rules.persistence.token;

import pcgen.rules.context.LoadContext;

/**
 * Does initial parsing of a token which should not be empty.
 *
 * @param <T> The type of object on which this AbstractNonEmptyToken can be used
 */
public abstract class AbstractNonEmptyToken<T> extends AbstractToken implements CDOMToken<T>
{
    /**
     * Checks to make sure the value is non-empty before continuing parsing.
     */
    @Override
    @SuppressWarnings("PMD.StringInstantiation")
    public ParseResult parseToken(LoadContext context, T obj, String value)
    {
        ParseResult pr = checkNonEmpty(value);
        if (pr.passed())
        {
            // new String() just in case because this seems to be where a lot of the substrings pass through
            pr = parseNonEmptyToken(context, obj, new String(value));
        }
        return pr;
    }

    /**
     * Must be overridden to continue parsing.
     */
    protected abstract ParseResult parseNonEmptyToken(LoadContext context, T obj, String value);
}
