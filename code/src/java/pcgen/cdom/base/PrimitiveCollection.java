/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
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

public interface PrimitiveCollection<T>
{

    @SuppressWarnings("rawtypes")
    PrimitiveCollection INVALID = new PrimitiveCollection()
    {

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
        public Class getReferenceClass()
        {
            return Object.class;
        }

        @Override
        public Collection getCollection(PlayerCharacter pc, Converter c)
        {
            return Collections.emptyList();
        }
    };

    <R> Collection<? extends R> getCollection(PlayerCharacter pc, Converter<T, R> c);

    /**
     * Returns the GroupingState for this PrimitiveCollection. The GroupingState
     * indicates how this PrimitiveCollection can be combined with other
     * PrimitiveCollections.
     *
     * @return The GroupingState for this PrimitiveCollection.
     */
    GroupingState getGroupingState();

    /**
     * Returns the Class object representing the Class that this
     * PrimitiveCollection evaluates.
     *
     * @return Class object representing the Class that this PrimitiveCollection
     * evaluates
     */
    Class<? super T> getReferenceClass();

    /**
     * Returns a representation of this PrimitiveCollection, suitable for
     * storing in an LST file.
     *
     * @param useAny use "ANY" for the global "ALL" reference when creating the LST
     *               format
     * @return A representation of this PrimitiveCollection, suitable for
     * storing in an LST file.
     */
    String getLSTformat(boolean useAny);

    /**
     * Implemented as a similar result as Collections.emptyList() vs.
     * Collections.EMPTY_LIST. The former allows one to avoid generic warnings
     * in "runtime" code, as FIXED.invalid() would here.
     */
    PrimLibrary FIXED = new PrimLibrary()
    {
        /**
         * Returns an "Invalid" PrimitiveCollection.
         *
         * @return An "Invalid" PrimitiveCollection
         */
        @SuppressWarnings("unchecked")
        @Override
        public <PCT> PrimitiveCollection<PCT> invalid()
        {
            return INVALID;
        }
    };

    interface PrimLibrary
    {
        <PCT> PrimitiveCollection<PCT> invalid();
    }

}
