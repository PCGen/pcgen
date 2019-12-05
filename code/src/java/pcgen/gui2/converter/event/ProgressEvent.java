/*
 * Copyright (c) 2006, 2009.
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
 *
 */
package pcgen.gui2.converter.event;

import java.util.EventObject;

public class ProgressEvent extends EventObject
{

    public static final int NOT_ALLOWED = 1;

    public static final int ALLOWED = 0;

    public static final int AUTO_ADVANCE = 2;

    private final int ident;

    public ProgressEvent(Object arg0, int id)
    {
        super(arg0);
        ident = id;
    }

    public int getID()
    {
        return ident;
    }
}
