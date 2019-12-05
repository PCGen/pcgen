/*
 * Copyright 2018 Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.gui2;

import pcgen.facade.core.SourceSelectionFacade;
import pcgen.facade.util.DefaultReferenceFacade;

/**
 * A UIContext stores the source selection information that is currently identified as
 * being loaded by the UI.
 */
public class UIContext
{
    /**
     * The currently selected sources
     */
    private final DefaultReferenceFacade<SourceSelectionFacade> currentSourceSelection;

    /**
     * Constructs a new UIContext with an empty source list.
     */
    UIContext()
    {
        this.currentSourceSelection = new DefaultReferenceFacade<>();
    }

    /**
     * Returns the reference to the currently loaded sources.
     *
     * @return The reference to the currently loaded sources.
     */
    public DefaultReferenceFacade<SourceSelectionFacade> getCurrentSourceSelectionRef()
    {
        return currentSourceSelection;
    }

}
