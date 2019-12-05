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
package pcgen.cdom.base;

import java.util.Collection;
import java.util.Collections;

import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.PlayerCharacter;

/**
 * A PrimitiveChoiceSet contains references to Objects. Often these are
 * CDOMObjects, but that is not strictly required.
 * <p>
 * The intent is for a PrimitiveChoiceSet to be created and populated with a set
 * of CDOMObjects. It is possible that the contents of the PrimitiveChoiceSet
 * will be dynamic based on the PlayerCharacter used to resolve the contents of
 * the PrimitiveChoiceSet.
 *
 * @param <T> The class of object this PrimitiveChoiceSet contains.
 */
public interface PrimitiveChoiceSet<T>
{
    @SuppressWarnings("rawtypes")
    PrimitiveChoiceSet INVALID = new PrimitiveChoiceSet()
    {

        @Override
        public Class getChoiceClass()
        {
            return Object.class;
        }

        @Override
        public GroupingState getGroupingState()
        {
            return GroupingState.INVALID;
        }

        @Override
        public String getLSTformat(boolean useAny)
        {
            return "ERROR";
        }

        @Override
        public Collection getSet(PlayerCharacter pc)
        {
            return Collections.emptyList();
        }
    };

    /**
     * Returns an "Invalid" PrimitiveChoiceSet.
     *
     * @return An "Invalid" PrimitiveChoiceSet
     */
    @SuppressWarnings("unchecked")
    static <T> PrimitiveChoiceSet<T> getInvalid()
    {
        return INVALID;
    }

    /**
     * Returns a Set containing the Objects which this PrimitiveChoiceSet
     * contains.
     * <p>
     * It is intended that classes which implement PrimitiveChoiceSet will make
     * this method value-semantic, meaning that ownership of the Set returned by
     * this method will be transferred to the calling object. Modification of
     * the returned Set should not result in modifying the PrimitiveChoiceSet,
     * and modifying the PrimitiveChoiceSet after the Set is returned should not
     * modify the Set.
     *
     * @param pc The PlayerCharacter for which the choices in this
     *           PrimitiveChoiceSet should be returned.
     * @return A Set containing the Objects which this PrimitiveChoiceSet
     * contains.
     */
    Collection<? extends T> getSet(PlayerCharacter pc);

    /**
     * The class of object this PrimitiveChoiceSet contains.
     *
     * @return The class of object this PrimitiveChoiceSet contains.
     */
    Class<? super T> getChoiceClass();

    /**
     * Returns a representation of this PrimitiveChoiceSet, suitable for storing
     * in an LST file.
     *
     * @param useAny use "ANY" for the global "ALL" reference when creating the LST
     *               format
     * @return A representation of this PrimitiveChoiceSet, suitable for storing
     * in an LST file.
     */
    String getLSTformat(boolean useAny);

    /**
     * Returns the GroupingState for this PrimitiveChoiceSet. The GroupingState
     * indicates how this PrimitiveChoiceSet can be combined with other
     * PrimitiveChoiceSets.
     *
     * @return The GroupingState for this PrimitiveChoiceSet.
     */
    GroupingState getGroupingState();
}
