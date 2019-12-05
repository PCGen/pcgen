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
package pcgen.rules.persistence.token;

import pcgen.cdom.base.Loadable;
import pcgen.rules.context.LoadContext;

/**
 * A PostDeferredToken is a token that is processed after LST file load is
 * complete, AFTER references are resolved. It is generally used for
 * compatibility with older syntax, and use of this interface is discouraged.
 *
 * @param <T> The type of object upon which the PostDeferredToken operates
 */
public interface PostDeferredToken<T extends Loadable>
{
    /**
     * Processes the PostDeferredToken with the given LoadContext and object.
     * Returns true if the processing was successful, false if the processing
     * indicates an LST load error.
     *
     * @param context The LoadContext around which this PostDeferredToken is
     *                evaluated
     * @param obj     The object to be processed in this PostDeferredToken
     * @return true if the processing was successful; false otherwise
     */
    boolean process(LoadContext context, T obj);

    /**
     * Returns the class of the object upon which this PostDeferredToken
     * operates.
     *
     * @return The class of the object upon which this PostDeferredToken
     * operates
     */
    Class<T> getDeferredTokenClass();

    /**
     * Returns the priority of this PostDeferredToken. PostDeferredTokens are
     * supposed to be processed in the order in which they are required (lowest
     * first).
     *
     * @return The priority of this PostDeferredToken
     */
    int getPriority();
}
