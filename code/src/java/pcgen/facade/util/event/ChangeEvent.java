/*
 * Copyright James Dempsey, 2012
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
package pcgen.facade.util.event;

import java.util.EventObject;

/**
 * The Class {@code ChangeEvent} indicates the source object, or
 * associated data, was modified in some way.
 */
public class ChangeEvent extends EventObject
{

    /**
     * ID for serialization.
     */
    private static final long serialVersionUID = 4689320734592481155L;

    /**
     * Create a new instance of ChangeEvent
     *
     * @param source The object being changed
     */
    public ChangeEvent(Object source)
    {
        super(source);
    }

}
