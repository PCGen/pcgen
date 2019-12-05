/*
 * Copyright James Dempsey, 2014
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
 *
 */
package pcgen.pluginmgr.messages;

import javax.swing.JMenu;

import pcgen.pluginmgr.PCGenMessage;

/**
 * The Class {@code FocusOrStateChangeOccurredMessage} encapsulates a message
 * that the user has switched from gmgen to pcgen or vice versa.
 */
@SuppressWarnings("serial")
public class FocusOrStateChangeOccurredMessage extends PCGenMessage
{

    private final JMenu editMenu;

    /**
     * Create a new instance of FocusOrStateChangeOccurredMessage
     *
     * @param source
     * @param editMenu
     */
    public FocusOrStateChangeOccurredMessage(Object source, JMenu editMenu)
    {
        super(source);
        this.editMenu = editMenu;
    }

    public JMenu getEditMenu()
    {
        return editMenu;
    }

}
