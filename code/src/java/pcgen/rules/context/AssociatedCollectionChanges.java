/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
 */
package pcgen.rules.context;

import java.util.Collection;

import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;

public class AssociatedCollectionChanges<T> implements AssociatedChanges<T>
{
    private final MapToList<T, AssociatedPrereqObject> positive;
    private final MapToList<T, AssociatedPrereqObject> negative;
    private final boolean clear;

    public AssociatedCollectionChanges(MapToList<T, AssociatedPrereqObject> added,
            MapToList<T, AssociatedPrereqObject> removed, boolean globallyCleared)
    {
        positive = added;
        negative = removed;
        clear = globallyCleared;
    }

    @Override
    public boolean includesGlobalClear()
    {
        return clear;
    }

    public boolean isEmpty()
    {
        return !clear && !hasAddedItems() && !hasRemovedItems();
    }

    @Override
    public Collection<T> getAdded()
    {
        return positive.getKeySet();
    }

    public boolean hasAddedItems()
    {
        return positive != null && !positive.isEmpty();
    }

    @Override
    public Collection<T> getRemoved()
    {
        return negative == null ? null : negative.getKeySet();
    }

    public boolean hasRemovedItems()
    {
        return negative != null && !negative.isEmpty();
    }

    @Override
    public MapToList<T, AssociatedPrereqObject> getAddedAssociations()
    {
        return positive;
    }

    @Override
    public MapToList<T, AssociatedPrereqObject> getRemovedAssociations()
    {
        return negative;
    }
}
