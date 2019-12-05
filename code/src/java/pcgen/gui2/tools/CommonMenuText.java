/*
 * Copyright 2012 Vincent Lhote
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
package pcgen.gui2.tools;

import javax.swing.AbstractButton;
import javax.swing.Action;

import pcgen.system.LanguageBundle;

/**
 * Common menu text (name, tool tip text, mnemonic) generation.
 */
public final class CommonMenuText
{
    private static final String MNEMONIC_SUFFIX = LanguageBundle.KEY_PREFIX + "mn_"; //$NON-NLS-1$
    private static final String TIP_SUFFIX = "Tip"; //$NON-NLS-1$

    private CommonMenuText()
    {
    }

    /**
     * @param a           the action to change the text, short description and mnemonic
     * @param substitutes substitutes to use in a message format
     * @param prop        key bundle to use
     */
    public static void name(Action a, String prop, Object... substitutes)
    {
        a.putValue(Action.NAME, getName(prop, substitutes));
        String shortDesc = getShortDesc(prop, substitutes);
        if (shortDesc != null && !shortDesc.isEmpty())
        {
            a.putValue(Action.SHORT_DESCRIPTION, shortDesc);
        }
        a.putValue(Action.MNEMONIC_KEY, getMnemonic(prop));
    }

    private static int getMnemonic(String prop)
    {
        return LanguageBundle.getMnemonic(MNEMONIC_SUFFIX + prop);
    }

    private static String getShortDesc(String prop, Object... substitutes)
    {
        return LanguageBundle.getFormattedString(LanguageBundle.KEY_PREFIX + prop + TIP_SUFFIX, substitutes);
    }

    private static String getName(String prop, Object... substitutes)
    {
        return LanguageBundle.getFormattedString(LanguageBundle.KEY_PREFIX + prop, substitutes);
    }

    /**
     * @param m           the button item to change the text, short description and mnemonic
     * @param substitutes substitutes to use in a message format
     * @param prop        key bundle to use
     */
    public static void name(AbstractButton m, String prop, Object... substitutes)
    {
        m.setText(getName(prop, substitutes));
        String shortDesc = getShortDesc(prop, substitutes);
        if (shortDesc != null && !shortDesc.isEmpty())
        {
            m.setToolTipText(shortDesc);
        }
        m.setMnemonic(getMnemonic(prop));
    }
}
