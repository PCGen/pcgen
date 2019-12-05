/*
 * Copyright 2014 (C) James Dempsey
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
package pcgen.util.enumeration;

/**
 * {@code View} is an enumeration of possible view types. It is
 * closely related to the Visibility enumeration.
 */
public enum View
{
    ALL("ALL"), // Show all visibility types
    HIDDEN_DISPLAY("HIDDEN_DISPLAY"), // Show only those not visible
    HIDDEN_EXPORT("HIDDEN_EXPORT"), // Show only those not visible
    VISIBLE_DISPLAY("VISIBLE_DISPLAY"), // Shows types visible to the GUI
    VISIBLE_EXPORT("VISIBLE_EXPORT"); // Shows types visible to the Export

    private final String text;

    /**
     * Create a new view based on a name.
     *
     * @param s
     */
    View(String s)
    {
        text = s;
    }

    @Override
    public String toString()
    {
        return text;
    }

    /**
     * Retrieve a View matching the supplied name.
     *
     * @param name The name of the view
     * @return The view, or null if not a view name.
     */
    public static View getViewFromName(String name)
    {
        for (final View view : View.values())
        {
            if (view.text.equalsIgnoreCase(name))
            {
                return view;
            }
        }
        return null;
    }
}
