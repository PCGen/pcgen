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

public class ListEvent<E> extends EventObject
{

    public static final int ELEMENT_ADDED = 0;
    public static final int ELEMENT_REMOVED = 1;
    public static final int ELEMENTS_CHANGED = 2;
    public static final int ELEMENT_MODIFIED = 3;
    private final E element;
    private final int type;
    private final int index;

    /**
     * This is a shortcut constructor for an ELEMENTS_CHANGED event
     *
     * @param source
     */
    public ListEvent(Object source)
    {
        this(source, ELEMENTS_CHANGED, null, -1);
    }

    public ListEvent(Object source, int type, E element, int index)
    {
        super(source);
        this.type = type;
        this.index = index;
        this.element = element;
    }

    public int getIndex()
    {
        return index;
    }

    public int getType()
    {
        return type;
    }

    public E getElement()
    {
        return element;
    }

}
