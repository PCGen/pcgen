/*
 * Copyright (c) James Dempsey, 2011.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet.fact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractListFacet;
import pcgen.core.ChronicleEntry;
import pcgen.util.Logging;

/**
 * ChronicleEntryFacet is a Facet that tracks the chronicle entries that have
 * been entered for a Player Character.
 */
public class ChronicleEntryFacet extends AbstractListFacet<CharID, ChronicleEntry>
{

    @Override
    protected Collection<ChronicleEntry> getCopyForNewOwner(Collection<ChronicleEntry> componentSet)
    {
        List<ChronicleEntry> newCopies = new ArrayList<>();
        for (ChronicleEntry entry : componentSet)
        {
            try
            {
                newCopies.add(entry.clone());
            } catch (CloneNotSupportedException e)
            {
                Logging.errorPrint("ChronicleEntryFacet.getCopyForNewOwner failed for " + entry, e);
            }
        }
        return newCopies;
    }

    /**
     * Overrides the default behavior of AbstractListFacet, since we need to
     * ensure we are storing all chronicle entries (otherwise duplicate blanks
     * are skipped, see CODE-2861)
     */
    @Override
    protected List<ChronicleEntry> getComponentSet()
    {
        return new ArrayList<>();
    }

}
