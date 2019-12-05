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
 * Does initial parsing of a token which should not be empty and is expected to
 * have separators.
 *
 * @param <T> The type of object on which this AbstractTokenWithSeparator can be
 *            used
 */
public abstract class AbstractTokenWithSeparator<T> extends AbstractNonEmptyToken<T>
{
    /**
     * Checks to make sure the value has valid separators before continuing parsing.
     */
    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, T obj, String value)
    {
        ParseResult pr = checkForIllegalSeparator(separator(), value);
        if (pr.passed())
        {
            pr = parseTokenWithSeparator(context, obj, value);
        }
        return pr;
    }

    /**
     * Override this to set the standard separator.
     *
     * @return the defined separator character
     */
    protected abstract char separator();

    /**
     * Must be overridden to continue parsing.
     */
    protected abstract ParseResult parseTokenWithSeparator(LoadContext context, T obj, String value);
}
