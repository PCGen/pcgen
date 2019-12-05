/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
package pcgen.cdom.enumeration;

/**
 * An enumeration of &quot;Standard&quot; spell components
 */
public enum Component
{
    /**
     * Verbal Component &quot;V&quot;
     */
    VERBAL("V", "Spell.Components.Verbal"), //$NON-NLS-1$ //$NON-NLS-2$
    /**
     * Somatic (movement) Component &quot;S&quot;
     */
    SOMATIC("S", "Spell.Components.Somatic"), //$NON-NLS-1$ //$NON-NLS-2$
    /**
     * Material Component &quot;M&quot;
     */
    MATERIAL("M", "Spell.Components.Material"), //$NON-NLS-1$ //$NON-NLS-2$
    /**
     * Divine Focus Component (usually holy symbol) &quot;DF&quot;
     */
    DIVINEFOCUS("DF", "Spell.Components.DivineFocus"), //$NON-NLS-1$ //$NON-NLS-2$
    /**
     * Non-divine Focus Component &quot;F&quot;
     */
    FOCUS("F", "Spell.Components.Focus"), //$NON-NLS-1$ //$NON-NLS-2$
    /**
     * Experience Point cost &quot;XP&quot;
     */
    EXPERIENCE("XP", "Spell.Components.Experience"), //$NON-NLS-1$ //$NON-NLS-2$
    /**
     * Anything other than the standard components
     */
    OTHER("See text", "Spell.Components.SeeText"); //$NON-NLS-1$ //$NON-NLS-2$

    private final String theKey;
    private final String theName;

    Component(final String aKey, final String aName)
    {
        theKey = aKey;
        theName = aName;
    }

    /**
     * Returns the String key of the component.
     *
     * @return The key.
     */
    public String getKey()
    {
        return theKey;
    }

    /**
     * Factory method to get a Component from a string key.
     *
     * @param aKey The component key to get a Component for (e.g. V or S)
     * @return A Component object.  If no object matches <tt>OTHER</tt> is
     * returned.
     */
    public static Component getComponentFromKey(final String aKey)
    {
        for (Component c : Component.values())
        {
            if (c.getKey().equalsIgnoreCase(aKey))
            {
                return c;
            }
        }
        return OTHER;
    }

    /**
     * Returns the string abbreviation of this component.
     *
     * @return The abbreviation
     */
    @Override
    public String toString()
    {
        return theName;
    }
}
