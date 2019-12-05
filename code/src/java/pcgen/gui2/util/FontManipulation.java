/*
 * Copyright 2013 (C) Vincent Lhote
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
package pcgen.gui2.util;

import java.awt.Container;
import java.awt.Font;

/**
 * This class regroups font manipulation methods, like having bold on a label.
 * Font manipulation should be done in very few cases because it usually breaks
 * the Look-and-feel. Using fixed point value for font shouldn't be done because
 * it might make text unreadable on system where default size is quite big (or
 * small).
 * <p>
 * The method name refer to the CSS relative font size:
 * <ul>
 * <li>xx-large
 * <li>x-large
 * <li>large
 * <li>medium
 * <li>small
 * <li>x-small
 * <li>xx-small
 * </ul>
 */
public final class FontManipulation
{

    private FontManipulation()
    {
    }

    public static Font title(Font f)
    {
        // XXX In Japanese, bold is not used and hardly readeable if small, just
        // use gothic/non gothic instead.
        return f.deriveFont(Font.BOLD);
    }

    /**
     * For title font
     *
     * @param container element to change the font of
     */
    public static void title(Container container)
    {
        container.setFont(title(container.getFont()));
    }

    /**
     * For extra large font.
     *
     * @param f base font
     */
    public static Font xxlarge(Font f)
    {
        return f.deriveFont(f.getSize() * 1.5f);
    }

    /**
     * For extra large font.
     *
     * @param container element to change the font of
     */
    public static void xxlarge(Container container)
    {
        Font font = container.getFont();
        container.setFont(xxlarge(font));
    }

    /**
     * For large font.
     *
     * @param f base font
     */
    public static Font large(Font f)
    {
        return f.deriveFont(f.getSize() * 1.167f);
    }

    /**
     * For large font.
     *
     * @param container element to change the font of
     */
    public static void large(Container container)
    {
        Font font = container.getFont();
        container.setFont(large(font));
    }

    /**
     * For a bit smaller font.
     *
     * @param f base font
     */
    public static Font small(Font f)
    {
        return f.deriveFont(f.getSize() * 0.917f);
    }

    /**
     * Change font of container for a bit smaller font.
     *
     * @param container element to change font size of
     */
    public static void small(Container container)
    {
        Font font = container.getFont();
        container.setFont(small(font));
    }

    /**
     * For extra smaller font.
     *
     * @param f base font
     */
    private static Font xsmall(Font f)
    {
        return f.deriveFont(f.getSize() * 0.833f);
    }

    /**
     * For extra smaller font.
     *
     * @param container element to change the font of
     */
    public static void xsmall(Container container)
    {
        Font font = container.getFont();
        container.setFont(xsmall(font));
    }

    /**
     * For less important text, like grayed out italic.
     *
     * @param f base font
     */
    public static Font less(Font f)
    {
        return f.deriveFont(Font.ITALIC);
    }

    /**
     * For plain font.
     *
     * @param f base font
     */
    public static Font plain(Font f)
    {
        return f.deriveFont(Font.PLAIN);
    }

    /**
     * For bold font.
     *
     * @param f base font
     */
    public static Font bold(Font f)
    {
        return f.deriveFont(Font.BOLD);
    }

    /**
     * For bold italic font.
     *
     * @param f base font
     */
    public static Font bold_italic(Font f)
    {
        return f.deriveFont(Font.BOLD | Font.ITALIC);
    }
}
