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
import java.util.Collection;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;

public interface ListCommitStrategy
{

    /**
     * Create a new AssociatedPrereqObject for the owner and link it to the
     * list. The type of link is characterised by the token, which is normally
     * the LSTToken that would define the link (e.g. CLASSES to define a
     * spell's membership in a ClassSpellList).
     *
     * @param <T>       The type of CDOMObject being held in the list (e.g. Spell in a classSpellList)
     * @param tokenName The name of the LST token defining the link between the owner and the list. (e.g. CLASSES)
     * @param owner     The object being linked from (e.g. spell)
     * @param list      The list being linked to. (e.g. ClassSpellList)
     * @param allowed   TODO: often the same as the owner.
     * @return The new AssociatedPrereqObject, now part of the list.
     */
    <T extends CDOMObject> AssociatedPrereqObject addToMasterList(String tokenName, CDOMObject owner,
            CDOMReference<? extends CDOMList<T>> list, T allowed);

    <T extends CDOMObject> void removeFromMasterList(String tokenName, CDOMObject owner,
            CDOMReference<? extends CDOMList<T>> list, T allowed);

    <T extends CDOMList<?>> Changes<CDOMReference<T>> getMasterListChanges(String tokenName, CDOMObject owner,
            Class<T> cl);

    boolean hasMasterLists();

    <T extends CDOMObject> AssociatedChanges<T> getChangesInMasterList(String tokenName, CDOMObject owner,
            CDOMReference<? extends CDOMList<T>> swl);

    <T extends CDOMObject> AssociatedPrereqObject addToList(String tokenName, CDOMObject owner,
            CDOMReference<? extends CDOMList<? super T>> list, CDOMReference<T> allowed);

    Collection<CDOMReference<? extends CDOMList<?>>> getChangedLists(CDOMObject owner,
            Class<? extends CDOMList<?>> cl);

    void removeAllFromList(String tokenName, CDOMObject owner, CDOMReference<? extends CDOMList<?>> swl);

    <T extends CDOMObject> AssociatedPrereqObject removeFromList(String tokenName, CDOMObject owner,
            CDOMReference<? extends CDOMList<? super T>> swl, CDOMReference<T> ref);

    <T extends CDOMObject> AssociatedChanges<CDOMReference<T>> getChangesInList(String tokenName,
            CDOMObject owner, CDOMReference<? extends CDOMList<T>> swl);

    void setSourceURI(URI sourceURI);

    void setExtractURI(URI sourceURI);

    void clearAllMasterLists(String tokenName, CDOMObject owner);

    boolean equalsTracking(ListCommitStrategy commit);
}
