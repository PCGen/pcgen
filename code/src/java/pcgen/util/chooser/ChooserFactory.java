/*
 * Copyright 2002 (C) Jonas Karlsson
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
package pcgen.util.chooser;

import java.util.Optional;

import pcgen.facade.core.UIDelegate;

/**
 * This factory class returns a Chooser of the appropriate type. This is intended
 * to reduce the core/gui interdependence. Much more work is needed on this...
 * Currently only a SwingChooser has been implemented.
 */
@Deprecated()
public final class ChooserFactory
{
    private static UIDelegate delegate;
    private static boolean useRandomChooser = false;

    private ChooserFactory()
    {
    }

    /**
     * Retrieve an optional handler for making choices. If no handler is
     * currently registered, it is expected that the UI class caller will
     * display an interactive dialog. If multiple handlers are currently
     * registered the most recently registered (LIFO) will be returned.
     *
     * @return The most recently registered ChoiceHandler, if any.
     */
    public static Optional<RandomChooser> getChoiceHandler()
    {
        if (useRandomChooser)
        {
            return Optional.of(new RandomChooser());
        } else
        {
            return Optional.empty();
        }
    }

    /**
     * Used by tests to use random choice.
     */
    public static void useRandomChooser()
    {
        useRandomChooser = true;
    }

    /**
     * @return the delegate
     */
    public static UIDelegate getDelegate()
    {
        return delegate;
    }

    /**
     * @param delegate the delgate to set
     */
    public static void setDelegate(UIDelegate delegate)
    {
        ChooserFactory.delegate = delegate;
    }
}
