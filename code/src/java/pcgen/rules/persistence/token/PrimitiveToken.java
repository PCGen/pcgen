/*
 * Copyright 2009-2010 (C) Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.cdom.base.PrimitiveCollection;
import pcgen.persistence.lst.LstToken;
import pcgen.rules.context.LoadContext;

/**
 * A PrimitiveToken is an object that can select zero or more objects of a
 * specific type of object.
 * <p>
 * This is typically processed as part of a CHOOSE. The syntax of a Primitive is
 * Key=Value[Args]. The Key is returned from the LstToken interface, the value
 * and args are provided to the initialize method.
 *
 * @param <T> The Type of object processed by the PrimitiveToken
 */
public interface PrimitiveToken<T> extends LstToken, PrimitiveCollection<T>
{

    /**
     * Initializes the PrimitiveToken with the content of the
     * PrimitiveCollection as defined by the arguments.
     * <p>
     * This method returns true if initialization was successful. If
     * initialization is not successful, then it should not be used as a
     * PrimitiveCollection.
     * <p>
     * Note that any qualifier may or may not support args; that is up to the
     * implementation. However, any non-support should be identified by
     * returning false, rather than throwing an exception.
     *
     * @param context The LoadContext to be used to get necessary information to
     *                initialize the PrimitiveToken
     * @param cl      The Class of object on which this PrimitiveToken is operating
     * @param value   The value of the primitive
     * @param args    The arguments to the primitive
     * @return true if initialization was successful; false otherwise
     */
    boolean initialize(LoadContext context, Class<T> cl, String value, String args);
}
