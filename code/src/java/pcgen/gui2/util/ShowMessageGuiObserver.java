/*
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
 *
 *
 *
 *
 */
package pcgen.gui2.util;

import java.util.Observable;
import java.util.Observer;

import pcgen.cdom.base.Constants;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.MessageWrapper;
import pcgen.facade.core.UIDelegate;

public class ShowMessageGuiObserver implements Observer
{
    private final UIDelegate uiDelegate;

    public ShowMessageGuiObserver(UIDelegate uiDelegate)
    {
        this.uiDelegate = uiDelegate;
    }

    @Override
    public void update(Observable o, Object arg)
    {
        if (arg instanceof MessageWrapper)
        {
            showMessageDialog((MessageWrapper) arg);
        }
    }

    public void showMessageDialog(MessageWrapper messageWrapper)
    {
        MessageType mt = messageWrapper.getMessageType();
        String title = messageWrapper.getTitle();
        if (title == null)
        {
            title = Constants.APPLICATION_NAME;
        }

        String message = String.valueOf(messageWrapper.getMessage());
        if (mt.equals(MessageType.WARNING))
        {
            uiDelegate.showWarningMessage(title, message);
        } else if (mt.equals(MessageType.ERROR))
        {
            uiDelegate.showErrorMessage(title, message);
        } else
        {
            uiDelegate.showInfoMessage(title, message);
        }
    }

}
