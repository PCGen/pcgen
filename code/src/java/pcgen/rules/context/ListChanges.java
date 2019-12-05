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
import java.util.TreeSet;

import pcgen.base.util.MapToList;
import pcgen.base.util.TreeMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.reference.ReferenceUtilities;

class ListChanges<T extends CDOMObject> implements AssociatedChanges<CDOMReference<T>>
{
    private final String tokenName;
    private final CDOMObject positive;
    private final CDOMObject negative;
    private final CDOMReference<? extends CDOMList<T>> list;
    private final boolean clear;

    public ListChanges(String token, CDOMObject added, CDOMObject removed, CDOMReference<? extends CDOMList<T>> listref,
            boolean globallyCleared)
    {
        tokenName = token;
        positive = added;
        negative = removed;
        list = listref;
        clear = globallyCleared;
    }

    @Override
    public boolean includesGlobalClear()
    {
        return clear;
    }

    public boolean isEmpty()
    {
        /*
         * TODO This lies because it doesn't analyze tokenName
         */
        return !clear && !hasAddedItems() && !hasRemovedItems();
    }

    @Override
    public Collection<CDOMReference<T>> getAdded()
    {
        TreeSet<CDOMReference<T>> set = new TreeSet<>(ReferenceUtilities.REFERENCE_SORTER);
        Collection<CDOMReference<T>> listMods = positive.getListMods(list);
        if (listMods != null)
        {
            for (CDOMReference<T> ref : listMods)
            {
                for (AssociatedPrereqObject assoc : positive.getListAssociations(list, ref))
                {
                    if (tokenName.equals(assoc.getAssociation(AssociationKey.TOKEN)))
                    {
                        set.add(ref);
                    }
                }
            }
        }
        return set;
    }

    public boolean hasAddedItems()
    {
        /*
         * TODO This lies because it doesn't analyze tokenName
         */
        return positive != null && positive.getListMods(list) != null && !positive.getListMods(list).isEmpty();
    }

    @Override
    public Collection<CDOMReference<T>> getRemoved()
    {
        TreeSet<CDOMReference<T>> set = new TreeSet<>(ReferenceUtilities.REFERENCE_SORTER);
        if (negative == null)
        {
            return set;
        }
        Collection<CDOMReference<T>> listMods = negative.getListMods(list);
        if (listMods != null)
        {
            for (CDOMReference<T> ref : listMods)
            {
                for (AssociatedPrereqObject assoc : negative.getListAssociations(list, ref))
                {
                    if (tokenName.equals(assoc.getAssociation(AssociationKey.TOKEN)))
                    {
                        set.add(ref);
                    }
                }
            }
        }
        return set;
    }

    public boolean hasRemovedItems()
    {
        /*
         * TODO This lies because it doesn't analyze tokenName
         */
        return negative != null && negative.getListMods(list) != null && !negative.getListMods(list).isEmpty();
    }

    @Override
    public MapToList<CDOMReference<T>, AssociatedPrereqObject> getAddedAssociations()
    {
        Collection<CDOMReference<T>> mods = positive.getListMods(list);
        if (mods == null)
        {
            return null;
        }
        MapToList<CDOMReference<T>, AssociatedPrereqObject> owned =
                new TreeMapToList<>(ReferenceUtilities.REFERENCE_SORTER);
        for (CDOMReference<T> lw : mods)
        {
            Collection<AssociatedPrereqObject> assocs = positive.getListAssociations(list, lw);
            for (AssociatedPrereqObject assoc : assocs)
            {
                if (tokenName.equals(assoc.getAssociation(AssociationKey.TOKEN)))
                {
                    owned.addToListFor(lw, assoc);
                }
            }
        }
        if (owned.isEmpty())
        {
            return null;
        }
        return owned;
    }

    @Override
    public MapToList<CDOMReference<T>, AssociatedPrereqObject> getRemovedAssociations()
    {
        MapToList<CDOMReference<T>, AssociatedPrereqObject> owned =
                new TreeMapToList<>(ReferenceUtilities.REFERENCE_SORTER);
        if (negative == null)
        {
            return owned;
        }
        Collection<CDOMReference<T>> mods = negative.getListMods(list);
        if (mods == null)
        {
            return owned;
        }
        for (CDOMReference<T> lw : mods)
        {
            Collection<AssociatedPrereqObject> assocs = negative.getListAssociations(list, lw);
            for (AssociatedPrereqObject assoc : assocs)
            {
                if (tokenName.equals(assoc.getAssociation(AssociationKey.TOKEN)))
                {
                    owned.addToListFor(lw, assoc);
                }
            }
        }
        if (owned.isEmpty())
        {
            return null;
        }
        return owned;
    }
}
