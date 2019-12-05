/*
 * Copyright 2006 (C) Tom Parker <thpr@users.sourceforge.net>
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
 *
 *
 */
package pcgen.cdom.choiceset;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import pcgen.base.util.WeightedCollection;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.PlayerCharacter;

/**
 * A ReferenceChoiceSet contains references to Objects. Often these are
 * CDOMObjects, but that is not strictly required.
 * <p>
 * The contents of a ReferenceChoiceSet is defined at construction of the
 * ReferenceChoiceSet. The contents of a ReferenceChoiceSet is fixed, and will
 * not vary by the PlayerCharacter used to resolve the ReferenceChoiceSet.
 *
 * @param <T> The class of object this ReferenceChoiceSet contains.
 */
public class ReferenceChoiceSet<T> implements PrimitiveChoiceSet<T>
{

    /**
     * The underlying Set of CDOMReferences that contain the objects in this
     * ReferenceChoiceSet
     */
    private final Collection<CDOMReference<T>> refCollection;

    /**
     * Constructs a new ReferenceChoiceSet which contains the Set of objects
     * contained within the given CDOMReferences. The CDOMReferences do not need
     * to be resolved at the time of construction of the ReferenceChoiceSet.
     * <p>
     * This constructor is reference-semantic and value-semantic. Ownership of
     * the Collection provided to this constructor is not transferred.
     * Modification of the Collection (after this constructor completes) does
     * not result in modifying the ReferenceChoiceSet, and the
     * ReferenceChoiceSet will not modify the given Collection. However, this
     * ReferenceChoiceSet will maintain strong references to the CDOMReference
     * objects contained within the given Collection.
     *
     * @param col A Collection of CDOMReferences which define the Set of objects
     *            contained within the ReferenceChoiceSet
     * @throws IllegalArgumentException if the given Collection is null or empty.
     */
    public ReferenceChoiceSet(Collection<? extends CDOMReference<T>> col)
    {
        Objects.requireNonNull(col, "Choice Collection cannot be null");
        if (col.isEmpty())
        {
            throw new IllegalArgumentException("Choice Collection cannot be empty");
        }
        refCollection = new WeightedCollection<>(col);
    }

    /**
     * Returns a representation of this ReferenceChoiceSet, suitable for storing
     * in an LST file.
     *
     * @param useAny use "ANY" for the global "ALL" reference when creating the LST
     *               format
     * @return A representation of this ReferenceChoiceSet, suitable for storing
     * in an LST file.
     */
    @Override
    public String getLSTformat(boolean useAny)
    {
        WeightedCollection<CDOMReference<?>> sortedSet = new WeightedCollection<>(ReferenceUtilities.REFERENCE_SORTER);
        sortedSet.addAll(refCollection);
        return ReferenceUtilities.joinLstFormat(sortedSet, Constants.COMMA, useAny);
    }

    /**
     * The class of object this ReferenceChoiceSet contains.
     * <p>
     * The behavior of this method is undefined if the CDOMReference objects
     * provided during the construction of this ReferenceChoiceSet are not yet
     * resolved.
     *
     * @return The class of object this ReferenceChoiceSet contains.
     */
    @Override
    public Class<T> getChoiceClass()
    {
        return refCollection == null ? null : refCollection.iterator().next().getReferenceClass();
    }

    /**
     * Returns a Set containing the Objects which this ReferenceChoiceSet
     * contains. The contents of a ReferenceChoiceSet is fixed, and will not
     * vary by the PlayerCharacter used to resolve the ReferenceChoiceSet.
     * <p>
     * The behavior of this method is undefined if the CDOMReference objects
     * provided during the construction of this ReferenceChoiceSet are not yet
     * resolved.
     * <p>
     * Ownership of the Set returned by this method will be transferred to the
     * calling object. Modification of the returned Set should not result in
     * modifying the ReferenceChoiceSet, and modifying the ReferenceChoiceSet
     * after the Set is returned should not modify the Set. However,
     * modification of the underlying objects contained within the Set will
     * result in modification of the object contained in this
     * ReferenceChoiceSet.
     *
     * @param pc The PlayerCharacter for which the choices in this
     *           ReferenceChoiceSet should be returned.
     * @return A Set containing the Objects which this ReferenceChoiceSet
     * contains.
     */
    @Override
    public Set<T> getSet(PlayerCharacter pc)
    {
        Set<T> returnSet = new HashSet<>();
        for (CDOMReference<T> ref : refCollection)
        {
            returnSet.addAll(ref.getContainedObjects());
        }
        return returnSet;
    }

    @Override
    public int hashCode()
    {
        return refCollection.size();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof ReferenceChoiceSet)
        {
            ReferenceChoiceSet<?> other = (ReferenceChoiceSet<?>) obj;
            return refCollection.equals(other.refCollection);
        }
        return false;
    }

    /**
     * Returns the GroupingState for this ReferenceChoiceSet. The GroupingState
     * indicates how this ReferenceChoiceSet can be combined with other
     * PrimitiveChoiceSets.
     *
     * @return The GroupingState for this ReferenceChoiceSet.
     */
    @Override
    public GroupingState getGroupingState()
    {
        GroupingState state = GroupingState.EMPTY;
        for (CDOMReference<T> ref : refCollection)
        {
            state = ref.getGroupingState().add(state);
        }
        return state.compound(GroupingState.ALLOWS_UNION);
    }
}
