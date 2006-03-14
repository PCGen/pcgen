/*
 * ShowMessageGuiObserver.java
 *
 * Copyright 2004 (C) Frugal <frugal@purplewombat.co.uk>
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
 * Created on 18-Dec-2003
 *
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package pcgen.gui.utils;

import pcgen.core.utils.MessageType;
import pcgen.core.utils.MessageWrapper;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.util.Observable;
import java.util.Observer;

public class ShowMessageGuiObserver implements Observer {

    /*
     * (non-Javadoc)
     *
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
        if (arg instanceof MessageWrapper) {
            showMessageDialog((MessageWrapper) arg);
        }
    }

    public static void showMessageDialog(MessageWrapper messageWrapper) {
        if (messageWrapper.getTitle() == null) {
            JOptionPane.showMessageDialog(null, messageWrapper.getMessage());
        }
        else {
            MessageType mt = messageWrapper.getMessageType();
            int mt2 = JOptionPane.INFORMATION_MESSAGE;
            if (mt.equals(MessageType.INFORMATION)) {
                mt2 = JOptionPane.INFORMATION_MESSAGE;
            }
            else if (mt.equals(MessageType.WARNING)) {
                mt2 = JOptionPane.WARNING_MESSAGE;
            }
            else if (mt.equals(MessageType.ERROR)) {
                mt2 = JOptionPane.ERROR_MESSAGE;
            }

            JOptionPane.showMessageDialog((Component)messageWrapper.getParent(), messageWrapper.getMessage(),
                    messageWrapper.getTitle(), mt2);
        }
    }

}