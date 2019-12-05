/*
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.tools;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

public class PCGenAction extends AbstractAction
{
    public PCGenAction(String prop)
    {
        this(prop, null, null, null);
    }

    public PCGenAction(String prop, Icons icon)
    {
        this(prop, null, null, icon);
    }

    public PCGenAction(String prop, String command)
    {
        this(prop, command, null, null);
    }

    public PCGenAction(String prop, String command, Icons icon)
    {
        this(prop, command, null, icon);
    }

    public PCGenAction(String prop, String command, String accelerator)
    {
        this(prop, command, accelerator, null);
    }

    public PCGenAction(String prop, String command, String accelerator, Icons icon, Object... substitutes)
    {
        CommonMenuText.name(this, prop, substitutes);

        if (command != null)
        {
            putValue(ACTION_COMMAND_KEY, command);
        }
        if (accelerator != null)
        {
            // accelerator has three possible forms:
            // 1) shortcut +
            // 2) shortcut-alt +
            // 3) F1
            // (error checking is for the weak!)
            int iShortCut = InputEvent.CTRL_DOWN_MASK;
            int menuShortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
            StringTokenizer aTok = new StringTokenizer(accelerator);

            // get the first argument
            String aString = aTok.nextToken();

            if (aString.equalsIgnoreCase("shortcut"))
            {
                iShortCut = menuShortcutKeyMask;
            } else if (aString.equalsIgnoreCase("alt"))
            {
                iShortCut = InputEvent.ALT_DOWN_MASK;
            } else if (aString.equalsIgnoreCase("shift-shortcut"))
            {
                iShortCut = menuShortcutKeyMask | InputEvent.SHIFT_DOWN_MASK;
            } else if (aString.matches("F[0-9]+"))
            {
                iShortCut = 0;
            }

            if (aTok.hasMoreTokens())
            {
                // get the second argument
                aString = aTok.nextToken();
            }

            KeyStroke aKey = KeyStroke.getKeyStroke(aString);

            if (aKey != null)
            {
                int iKeyCode = aKey.getKeyCode();
                putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(iKeyCode, iShortCut));
            }
        }
        if (icon != null)
        {
            putValue(SMALL_ICON, icon.getImageIcon());
        }
    }

    /**
     * Does nothing.
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {

    }

}
