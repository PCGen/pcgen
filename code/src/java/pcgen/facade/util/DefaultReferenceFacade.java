/*
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.facade.util;

import java.util.Objects;

public class DefaultReferenceFacade<E> extends AbstractReferenceFacade<E>
        implements WriteableReferenceFacade<E>
{

    protected E object;

    public DefaultReferenceFacade()
    {
        this(null);
    }

    public DefaultReferenceFacade(E object)
    {
        this.object = object;
    }

    @Override
    public E get()
    {
        return object;
    }

    @Override
    public void set(E object)
    {
        if (Objects.equals(this.object, object))
        {
            return;
        }
        E old = this.object;
        this.object = object;
        fireReferenceChangedEvent(this, old, object);
    }

    @Override
    public String toString()
    {
        return String.valueOf(object);
    }
}
