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
package pcgen.facade.util.event;

import java.util.EventObject;

public class ReferenceEvent<E> extends EventObject
{

    private final E newObject;
    private final E oldObject;

    public ReferenceEvent(Object source, E oldObject, E newObject)
    {
        super(source);
        this.newObject = newObject;
        this.oldObject = oldObject;
    }

    public E getOldReference()
    {
        return oldObject;
    }

    public E getNewReference()
    {
        return newObject;
    }

}
