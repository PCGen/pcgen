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

class CollectionChanges<T> implements Changes<T>
{
    private final Collection<T> positive;
    private final Collection<T> negative;
    private final boolean clear;

    CollectionChanges(Collection<T> added, Collection<T> removed, boolean globallyCleared)
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

    @Override
    public boolean isEmpty()
    {
        return !clear && !hasAddedItems() && !hasRemovedItems();
    }

    @Override
    public Collection<T> getAdded()
    {
        return positive;
    }

    @Override
    public boolean hasAddedItems()
    {
        return positive != null && !positive.isEmpty();
    }

    @Override
    public Collection<T> getRemoved()
    {
        return negative;
    }

    @Override
    public boolean hasRemovedItems()
    {
        return negative != null && !negative.isEmpty();
    }
}
