/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.rules.persistence.token;

import java.util.Collection;

import pcgen.cdom.base.Loadable;
import pcgen.rules.context.LoadContext;

/**
 * A PostValidationToken is a token that is processed after LST file load is
 * complete, AFTER references are resolved. It is used when processing across
 * ALL objects of a given type are required.
 *
 * @param <T> The type of object upon which the PostValidationToken operates
 */
public interface PostValidationToken<T extends Loadable>
{
    /**
     * Processes the PostValidationToken with the given LoadContext and
     * collection of objects. Returns true if the processing was successful,
     * false if the processing indicates an LST load error.
     *
     * @param context The LoadContext around which this PostValidationToken is
     *                evaluated
     * @param c       The collection of objects to be processed in this
     *                PostValidationToken
     * @return true if the processing was successful; false otherwise
     */
    boolean process(LoadContext context, Collection<? extends T> c);

    /**
     * Returns the class of the object upon which this PostValidationToken
     * operates.
     *
     * @return The class of the object upon which this PostValidationToken
     * operates
     */
    Class<T> getValidationTokenClass();

    /**
     * Returns the priority of this PostValidationToken. PostValidationTokens
     * are supposed to be processed in the order in which they are required
     * (lowest first).
     *
     * @return The priority of this PostValidationToken
     */
    int getPriority();
}
