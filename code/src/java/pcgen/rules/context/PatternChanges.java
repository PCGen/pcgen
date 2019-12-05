/*
 * Copyright 2008 (C) Thomas Parker
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
package pcgen.rules.context;

import java.util.Collection;

/**
 * The Class {@code PatternChanges} is responsible for tracking changes
 * to a map so that the changes can be committed or rolled back at a later
 * stage. Items can be added to the map, removed from the map or the map can be
 * cleared.
 */
public class PatternChanges<T>
{
    private final Collection<T> positive;
    private final Collection<String> negative;
    private final boolean clear;

    public PatternChanges(Collection<T> added, Collection<String> removed, boolean globallyCleared)
    {
        positive = added;
        negative = removed;
        clear = globallyCleared;
    }

    public boolean includesGlobalClear()
    {
        return clear;
    }

    public boolean isEmpty()
    {
        return !clear && !hasAddedItems() && !hasRemovedItems();
    }

    public Collection<T> getAdded()
    {
        return positive;
    }

    public boolean hasAddedItems()
    {
        return positive != null && !positive.isEmpty();
    }

    public Collection<String> getRemoved()
    {
        return negative;
    }

    public boolean hasRemovedItems()
    {
        return negative != null && !negative.isEmpty();
    }
}
