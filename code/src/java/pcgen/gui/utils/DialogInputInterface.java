/*
 * DialogInputInterface.java Copyright 2004 (C) Chris Ward
 * <frugal@users.sourceforge.net>
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
 *
 * Created on July 16, 2004
 */
package pcgen.gui.utils;

import pcgen.core.Globals;
import pcgen.core.utils.MessageType;
import pcgen.util.InputInterface;

import javax.swing.JOptionPane;
import java.awt.Component;

/**
 * Input interface for dialog box
 */
public class DialogInputInterface implements InputInterface {

    /*
     * (non-Javadoc)
     *
     * @see pcgen.util.InputInterface#showInputDialog(java.awt.Component,
     *      java.lang.Object, java.lang.String, int, javax.swing.Icon,
     *      java.lang.Object[], java.lang.Object)
     */
    public Object showInputDialog(Object parentComponent, Object message, String title, MessageType messageType,
            Object[] selectionValues, Object initialSelectionValue) {
        if (Globals.getUseGUI()) {
            int mt = 0;

            if (messageType.equals(MessageType.QUESTION)) {
                mt = JOptionPane.QUESTION_MESSAGE;
            }
            else if (messageType.equals(MessageType.ERROR)) {
                mt = JOptionPane.ERROR_MESSAGE;
            }
            else if (messageType.equals(MessageType.WARNING)) {
                mt = JOptionPane.WARNING_MESSAGE;
            }
            else  {
                mt = JOptionPane.INFORMATION_MESSAGE;
            }
            return JOptionPane.showInputDialog((Component) parentComponent, message, title, mt, null, selectionValues, initialSelectionValue);
        }
        //TODO: This should probably prompt, but not sure if that makes
        // sense on the command line
        throw new IllegalStateException("Cannot showInputDialog when getUseGUI returns false");
    }

}