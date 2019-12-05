/*
 * Copyright 2007, 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.choiceset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.list.DomainSpellList;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;
import pcgen.util.Logging;

/**
 * A SpellReferenceChoiceSet contains references to CDOMListObjects. This is a
 * specialized PrimitiveChoiceSet designed for use with {@code CDOMListObject<Spell>}
 * lists.
 * <p>
 * The contents of a SpellReferenceChoiceSet is defined at construction of the
 * SpellReferenceChoiceSet. The contents of a SpellReferenceChoiceSet is fixed,
 * and will not vary by the PlayerCharacter used to resolve the
 * SpellReferenceChoiceSet.
 * <p>
 * This exists as a special case (from a generic PrimitiveChoiceSet) for two
 * reasons: (1) getChoiceClass() is hardcoded to return CDOMListObject (vs. the
 * risk of returning an inconsistent answer like ClassSpellList or
 * DomainSpellList [this may contain either or both]) (2) getLSTformat needs to
 * correct for DomainSpellList references having a "DOMAIN." prefix in order to
 * distinguish them from ClassSpellList references/names
 */
public class SpellReferenceChoiceSet implements PrimitiveChoiceSet<CDOMListObject<Spell>>
{
    /**
     * The underlying Set of CDOMReferences that contain the CDOMListObjects in
     * this SpellReferenceChoiceSet
     */
    private final Set<CDOMReference<? extends CDOMListObject<Spell>>> set;

    /**
     * Constructs a new SpellReferenceChoiceSet which contains the Set of
     * CDOMListObjects contained within the given CDOMReferences. The
     * CDOMReferences do not need to be resolved at the time of construction of
     * the SpellReferenceChoiceSet.
     * <p>
     * This constructor is both reference-semantic and value-semantic. Ownership
     * of the Collection provided to this constructor is not transferred.
     * Modification of the Collection (after this constructor completes) does
     * not result in modifying the ReferenceChoiceSet, and the
     * SpellReferenceChoiceSet will not modify the given Collection. However,
     * this SpellReferenceChoiceSet will maintain hard references to the
     * CDOMReference objects contained within the given Collection.
     *
     * @param listRefCollection A Collection of CDOMReferences which define the Set of
     *                          CDOMListObjects contained within the SpellReferenceChoiceSet
     * @throws IllegalArgumentException if the given Collection is null or empty.
     */
    public SpellReferenceChoiceSet(Collection<CDOMReference<? extends CDOMListObject<Spell>>> listRefCollection)
    {
        Objects.requireNonNull(listRefCollection, "Choice Collection cannot be null");
        if (listRefCollection.isEmpty())
        {
            throw new IllegalArgumentException("Choice Collection cannot be empty");
        }
        set = new HashSet<>(listRefCollection);
        if (set.size() != listRefCollection.size())
        {
            if (Logging.isLoggable(Level.WARNING))
            {
                Logging.log(Level.WARNING, "Found duplicate item in " + listRefCollection);
            }
            //TODO need to trigger a bad GroupingState...
        }
    }

    /**
     * Returns a representation of this SpellReferenceChoiceSet, suitable for
     * storing in an LST file.
     *
     * @param useAny use "ANY" for the global "ALL" reference when creating the LST
     *               format
     * @return A representation of this SpellReferenceChoiceSet, suitable for
     * storing in an LST file.
     */
    @Override
    public String getLSTformat(boolean useAny)
    {
        Set<CDOMReference<?>> sortedSet = new TreeSet<>(ReferenceUtilities.REFERENCE_SORTER);
        sortedSet.addAll(set);
        StringBuilder sb = new StringBuilder();
        List<CDOMReference<?>> domainList = new ArrayList<>();
        boolean needComma = false;
        for (CDOMReference<?> ref : sortedSet)
        {
            if (DomainSpellList.class.equals(ref.getReferenceClass()))
            {
                domainList.add(ref);
            } else
            {
                if (needComma)
                {
                    sb.append(Constants.COMMA);
                }
                sb.append(ref.getLSTformat(false));
                needComma = true;
            }
        }
        for (CDOMReference<?> ref : domainList)
        {
            if (needComma)
            {
                sb.append(Constants.COMMA);
            }
            sb.append("DOMAIN.");
            sb.append(ref.getLSTformat(false));
            needComma = true;
        }
        return sb.toString();
    }

    /**
     * The class of object this SpellReferenceChoiceSet contains.
     *
     * @return The class of object this SpellReferenceChoiceSet contains.
     */
    @Override
    public Class<CDOMListObject> getChoiceClass()
    {
        return CDOMListObject.class;
    }

    /**
     * Returns a Set containing the CDOMListObjects which this
     * SpellReferenceChoiceSet contains. The contents of a
     * SpellReferenceChoiceSet is fixed, and will not vary by the
     * PlayerCharacter used to resolve the SpellReferenceChoiceSet.
     * <p>
     * The behavior of this method is undefined if the CDOMReference objects
     * provided during the construction of this SpellReferenceChoiceSet are not
     * yet resolved.
     * <p>
     * This method is value-semantic, meaning that ownership of the Set returned
     * by this method will be transferred to the calling object. Modification of
     * the returned Set should not result in modifying the
     * SpellReferenceChoiceSet, and modifying the SpellReferenceChoiceSet after
     * the Set is returned should not modify the Set.
     *
     * @param pc The PlayerCharacter for which the choices in this
     *           SpellReferenceChoiceSet should be returned.
     * @return A Set containing the CDOMListObjects which this
     * SpellReferenceChoiceSet contains.
     */
    @Override
    public Set<CDOMListObject<Spell>> getSet(PlayerCharacter pc)
    {
        Set<CDOMListObject<Spell>> returnSet = new HashSet<>();
        for (CDOMReference<? extends CDOMListObject<Spell>> ref : set)
        {
            returnSet.addAll(ref.getContainedObjects());
        }
        return returnSet;
    }

    @Override
    public int hashCode()
    {
        return set.size();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof SpellReferenceChoiceSet)
        {
            SpellReferenceChoiceSet other = (SpellReferenceChoiceSet) obj;
            return set.equals(other.set);
        }
        return false;
    }

    /**
     * Returns the GroupingState for this SpellReferenceChoiceSet. The
     * GroupingState indicates how this SpellReferenceChoiceSet can be combined
     * with other PrimitiveChoiceSets.
     *
     * @return The GroupingState for this SpellReferenceChoiceSet.
     */
    @Override
    public GroupingState getGroupingState()
    {
        GroupingState state = GroupingState.EMPTY;
        for (CDOMReference<? extends CDOMListObject<Spell>> listref : set)
        {
            state = state.add(listref.getGroupingState());
        }
        //TODO I think this needs state.compound(GroupingState.ALLOWS_UNION)??
        return state;
    }

}
