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
package pcgen.core;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;

import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.DoubleKeyMapToList;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.AssociationListKey;

public class AssociationSupport implements Cloneable
{

    private DoubleKeyMapToList assocMTL = new DoubleKeyMapToList(IdentityHashMap.class, IdentityHashMap.class);
    private DoubleKeyMap assocMap = new DoubleKeyMap(IdentityHashMap.class, IdentityHashMap.class);

    public <T> void addAssoc(Object obj, AssociationListKey<T> ak, T o)
    {
        assocMTL.addToListFor(obj, ak, o);
    }

    public <T> void removeAssoc(Object obj, AssociationListKey<T> ak, T o)
    {
        assocMTL.removeFromListFor(obj, ak, o);
    }

    public <T> List<T> removeAllAssocs(Object obj, AssociationListKey<T> ak)
    {
        return assocMTL.removeListFor(obj, ak);
    }

    public int getAssocCount(Object obj, AssociationListKey<?> ak)
    {
        return assocMTL.sizeOfListFor(obj, ak);
    }

    public boolean hasAssocs(Object obj, AssociationListKey<?> ak)
    {
        return assocMTL.containsListFor(obj, ak);
    }

    public <T> List<T> getAssocList(Object obj, AssociationListKey<T> ak)
    {
        return assocMTL.getListFor(obj, ak);
    }

    public <T> boolean containsAssoc(Object obj, AssociationListKey<T> ak, T o)
    {
        return assocMTL.containsInList(obj, ak, o);
    }

    public <T> void setAssoc(Object obj, AssociationKey<T> ak, T o)
    {
        assocMap.put(obj, ak, o);
    }

    public <T> void removeAssoc(Object obj, AssociationKey<T> ak)
    {
        assocMap.remove(obj, ak);
    }

    public boolean hasAssocs(Object obj, AssociationKey<?> ak)
    {
        return assocMap.containsKey(obj, ak);
    }

    public <T> T getAssoc(Object obj, AssociationKey<T> ak)
    {
        return (T) assocMap.get(obj, ak);
    }

    @Override
    public AssociationSupport clone() throws CloneNotSupportedException
    {
        AssociationSupport as = (AssociationSupport) super.clone();
        as.assocMTL = assocMTL.clone();
        as.assocMap = assocMap.clone();
        return as;
    }

    public void convertAssociations(Object oldTarget, Object newTarget)
    {
        for (Object secKey : assocMap.getSecondaryKeySet(oldTarget))
        {
            assocMap.put(newTarget, secKey, assocMap.remove(oldTarget, secKey));
        }
        for (Object secKey : assocMTL.getSecondaryKeySet(oldTarget))
        {
            assocMTL.addAllToListFor(newTarget, secKey, assocMTL.removeListFor(oldTarget, secKey));
        }
    }

    public boolean containsAssocList(Object o, AssociationListKey<?> alk)
    {
        return assocMTL.containsListFor(o, alk);
    }

    public <T extends Comparable<T>> void sortAssocList(Object obj, AssociationListKey<T> ak)
    {
        List list = assocMTL.removeListFor(obj, ak);
        if (list != null)
        {
            Collections.sort(list);
            assocMTL.addAllToListFor(obj, ak, list);
        }
    }
}
