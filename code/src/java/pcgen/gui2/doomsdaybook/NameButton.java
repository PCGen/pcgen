/*
 * Copyright 2003 (C) Devon Jones
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
package pcgen.gui2.doomsdaybook;

import pcgen.core.doomsdaybook.DataElement;

class NameButton extends javax.swing.JButton
{
    private final DataElement element;

    /**
     * Creates a new instance of NameButton
     *
     * @param element
     */
    NameButton(DataElement element)
    {
        this.element = element;
        super.setText(element.getTitle());
    }

    /**
     * Get the data element for the name button
     *
     * @return the data element for the name button
     */
    DataElement getDataElement()
    {
        return element;
    }
}
