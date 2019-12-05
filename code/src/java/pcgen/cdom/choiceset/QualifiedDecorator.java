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
package pcgen.cdom.choiceset;

import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.PlayerCharacter;

/**
 * A QualifiedDecorator decorates a PrimitiveChoiceSet in order to restrict the
 * contents of the underlying PrimitiveChoiceSet to a PlayerCharacter that meets
 * the Prerequisites defined by the objects in that PrimitiveChoiceSet.
 * <p>
 * Note that this does not mean that there is a separate set of Prerequisites
 * entered into the QualifiedDecorator, only that this QualifiedDecorator is a
 * PrimitiveChoiceSet that tests the prerequisites on the underlying objects,
 * whereas other classes that implement PrimitiveChoiceSet may not perform such
 * checks. If there are no Prerequisite objects in the underlying objects, then
 * this QualifiedDecorator will have no effect on the results returned by the
 * underlying PrimitiveChoiceSet.
 *
 * @param <T> The Type of object returned by this QualifiedDecorator.
 */
public class QualifiedDecorator<T extends CDOMObject> implements PrimitiveChoiceSet<T>
{

    /**
     * The underlying PrimitiveChoiceSet from which items will be returned, if
     * the PlayerCharacter is qualified for the item.
     */
    private final PrimitiveChoiceSet<T> underlyingPCS;

    /**
     * Constructs a new QualifiedDecorator which decorates the given
     * PrimitiveChoiceSet.
     *
     * @param underlyingSet The underlying PrimitiveChoiceSet from which items will be
     *                      returned, if the PlayerCharacter is qualified for the item.
     */
    public QualifiedDecorator(PrimitiveChoiceSet<T> underlyingSet)
    {
        underlyingPCS = underlyingSet;
    }

    /**
     * The class of object this QualifiedDecorator and the underlying
     * PrimitiveChoiceSet contains.
     *
     * @return The class of object this QualifiedDecorator and the underlying
     * PrimitiveChoiceSet contains.
     */
    @Override
    public Class<? super T> getChoiceClass()
    {
        return underlyingPCS.getChoiceClass();
    }

    /**
     * Returns a representation of this QualifiedDecorator, suitable for storing
     * in an LST file.
     *
     * @param useAny use "ANY" for the global "ALL" reference when creating the LST
     *               format
     * @return A representation of this QualifiedDecorator, suitable for storing
     * in an LST file.
     */
    @Override
    public String getLSTformat(boolean useAny)
    {
        return underlyingPCS.getLSTformat(useAny);
    }

    /**
     * Returns a Set containing the Objects which this QualifiedDecorator
     * contains. Objects will only be included in the returned set if the given
     * PlayerCharacter qualifies for the object.
     * <p>
     * Ownership of the Set returned by this method will be transferred to the
     * calling object. Modification of the returned Set should not result in
     * modifying the PrimitiveChoiceSet, and modifying the PrimitiveChoiceSet
     * after the Set is returned should not modify the Set. However, the objects
     * contained in the returned set are passed by reference. Modification of
     * the objects contained within the returned Set will modify the objects
     * within this QualifiedDecorator.
     *
     * @param pc The PlayerCharacter for which the choices in this
     *           QualifiedDecorator should be returned.
     * @return A Set containing the Objects which this QualifiedDecorator
     * contains.
     */
    @Override
    public Set<T> getSet(PlayerCharacter pc)
    {
        Set<T> returnSet = new HashSet<>();
        for (T item : underlyingPCS.getSet(pc))
        {
            if (item.qualifies(pc, item))
            {
                returnSet.add(item);
            }
        }
        return returnSet;
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof QualifiedDecorator) && ((QualifiedDecorator<?>) obj).underlyingPCS.equals(underlyingPCS);
    }

    @Override
    public int hashCode()
    {
        return 1 - underlyingPCS.hashCode();
    }

    /**
     * Returns the GroupingState for this QualifiedDecorator. The GroupingState
     * indicates how this QualifiedDecorator can be combined with other
     * PrimitiveChoiceSets.
     *
     * @return The GroupingState for this QualifiedDecorator.
     */
    @Override
    public GroupingState getGroupingState()
    {
        return underlyingPCS.getGroupingState().reduce();
    }
}
