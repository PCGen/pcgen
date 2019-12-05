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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.MapToList;
import pcgen.base.util.TreeMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.MasterListInterface;
import pcgen.cdom.base.SimpleAssociatedObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.reference.ReferenceUtilities;

public class ConsolidatedListCommitStrategy implements ListCommitStrategy, MasterListInterface
{
    private URI sourceURI;

    private URI extractURI;

    private final DoubleKeyMapToList<CDOMReference<? extends CDOMList<?>>,
            CDOMObject, AssociatedPrereqObject> masterList = new DoubleKeyMapToList<>();

    public URI getExtractURI()
    {
        return extractURI;
    }

    @Override
    public void setExtractURI(URI extractURI)
    {
        this.extractURI = extractURI;
    }

    public URI getSourceURI()
    {
        return sourceURI;
    }

    @Override
    public void setSourceURI(URI sourceURI)
    {
        this.sourceURI = sourceURI;
    }

    @Override
    public <T extends CDOMObject> AssociatedPrereqObject addToMasterList(String tokenName, CDOMObject owner,
            CDOMReference<? extends CDOMList<T>> list, T allowed)
    {
        SimpleAssociatedObject a = new SimpleAssociatedObject();
        a.setAssociation(AssociationKey.OWNER, owner);
        a.setAssociation(AssociationKey.TOKEN, tokenName);
        masterList.addToListFor(list, allowed, a);
        return a;
    }

    @Override
    public <T extends CDOMObject> void removeFromMasterList(String tokenName, CDOMObject owner,
            CDOMReference<? extends CDOMList<T>> list, T allowed)
    {
        masterList.removeListFor(list, allowed);
    }

    @Override
    public <T extends CDOMList<?>> Changes<CDOMReference<T>> getMasterListChanges(String tokenName, CDOMObject owner,
            Class<T> cl)
    {
        TreeSet<CDOMReference<T>> set = new TreeSet<>(ReferenceUtilities.REFERENCE_SORTER);
        LIST:
        for (CDOMReference<? extends CDOMList<?>> ref : masterList.getKeySet())
        {
            if (!cl.equals(ref.getReferenceClass()))
            {
                continue;
            }
            @SuppressWarnings("unchecked")
            CDOMReference<T> tr = (CDOMReference<T>) ref;
            for (CDOMObject allowed : masterList.getSecondaryKeySet(tr))
            {
                for (AssociatedPrereqObject assoc : masterList.getListFor(tr, allowed))
                {
                    if (owner.equals(assoc.getAssociation(AssociationKey.OWNER))
                            && tokenName.equals(assoc.getAssociation(AssociationKey.TOKEN)))
                    {
                        set.add(tr);
                        continue LIST;
                    }
                }
            }
        }
        return new CollectionChanges<>(set, null, false);
    }

    @Override
    public void clearAllMasterLists(String tokenName, CDOMObject owner)
    {
        for (CDOMReference<? extends CDOMList<?>> ref : masterList.getKeySet())
        {
            for (CDOMObject allowed : masterList.getSecondaryKeySet(ref))
            {
                for (AssociatedPrereqObject assoc : masterList.getListFor(ref, allowed))
                {
                    if (owner.equals(assoc.getAssociation(AssociationKey.OWNER))
                            && tokenName.equals(assoc.getAssociation(AssociationKey.TOKEN)))
                    {
                        masterList.removeFromListFor(ref, allowed, assoc);
                    }
                }
            }
        }
    }

    @Override
    public <T extends CDOMObject> AssociatedChanges<T> getChangesInMasterList(String tokenName, CDOMObject owner,
            CDOMReference<? extends CDOMList<T>> swl)
    {
        Set<CDOMObject> added = masterList.getSecondaryKeySet(swl);
        MapToList<T, AssociatedPrereqObject> owned = new TreeMapToList<>(CDOMObjectUtilities::compareKeys);
        for (CDOMObject lw : added)
        {
            List<AssociatedPrereqObject> list = masterList.getListFor(swl, lw);
            list.stream().filter(assoc -> owner.equals(assoc.getAssociation(AssociationKey.OWNER))).findFirst()
                    .ifPresent(assoc -> owned.addToListFor((T) lw, assoc));
        }
        return new AssociatedCollectionChanges<>(owned, null, false);
    }

    @Override
    public boolean hasMasterLists()
    {
        return !masterList.isEmpty();
    }

    @Override
    public <T extends CDOMObject> AssociatedPrereqObject addToList(String tokenName, CDOMObject owner,
            CDOMReference<? extends CDOMList<? super T>> list, CDOMReference<T> allowed)
    {
        SimpleAssociatedObject a = new SimpleAssociatedObject();
        a.setAssociation(AssociationKey.TOKEN, tokenName);
        owner.putToList(list, allowed, a);
        return a;
    }

    @Override
    public <T extends CDOMObject> AssociatedPrereqObject removeFromList(String tokenName, CDOMObject owner,
            CDOMReference<? extends CDOMList<? super T>> swl, CDOMReference<T> ref)
    {
        owner.removeFromList(swl, ref);
        return new SimpleAssociatedObject();
    }

    @Override
    public void removeAllFromList(String tokenName, CDOMObject owner, CDOMReference<? extends CDOMList<?>> swl)
    {
        owner.removeAllFromList(swl);
    }

    @Override
    public Collection<CDOMReference<? extends CDOMList<?>>> getChangedLists(CDOMObject owner,
            Class<? extends CDOMList<?>> cl)
    {
        ArrayList<CDOMReference<? extends CDOMList<?>>> list = new ArrayList<>();
        for (CDOMReference<? extends CDOMList<?>> ref : owner.getModifiedLists())
        {
            if (cl.equals(ref.getReferenceClass()))
            {
                list.add(ref);
            }
        }
        return list;
    }

    @Override
    public <T extends CDOMObject> AssociatedChanges<CDOMReference<T>> getChangesInList(String tokenName,
            CDOMObject owner, CDOMReference<? extends CDOMList<T>> swl)
    {
        return new ListChanges<>(tokenName, owner, null, swl, false);
    }

    @Override
    public Set<CDOMReference<? extends CDOMList<?>>> getActiveLists()
    {
        return masterList.getKeySet();
    }

    @Override
    public <T extends CDOMObject> Collection<AssociatedPrereqObject> getAssociations(
            CDOMReference<? extends CDOMList<T>> key1, T key2)
    {
        return masterList.getListFor(key1, key2);
    }

    @Override
    public <T extends CDOMObject> Collection<AssociatedPrereqObject> getAssociations(CDOMList<T> key1, T key2)
    {
        List<AssociatedPrereqObject> list = new ArrayList<>();
        for (CDOMReference ref : masterList.getKeySet())
        {
            if (ref.contains(key1))
            {
                List<AssociatedPrereqObject> tempList = masterList.getListFor(ref, key2);
                if (tempList != null)
                {
                    list.addAll(tempList);
                }
            }
        }
        return list;
    }

    @Override
    public boolean equalsTracking(ListCommitStrategy commit)
    {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CDOMObject> Collection<T> getObjects(CDOMReference<CDOMList<T>> ref)
    {
        return (Collection<T>) masterList.getSecondaryKeySet(ref);
    }

}
