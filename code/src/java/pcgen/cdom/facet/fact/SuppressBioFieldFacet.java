/*
 * Copyright James Dempsey, 2012
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
package pcgen.cdom.facet.fact;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.enumeration.BiographyField;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractStorageFacet;

/**
 * The Class {@code SuppressBioFieldFacet} tracks the biography fields that
 * should be hidden from output.
 */

public class SuppressBioFieldFacet extends AbstractStorageFacet<CharID>
{

    /*
     * TODO Can this use AbstractListFacet? (seems like it can)
     */

    /**
     * Set whether the field should be hidden from output.
     *
     * @param id       The CharID representing the target Player Character
     * @param field    The BiographyField to set export suppression rules for.
     * @param suppress Should the field be hidden from output.
     * @return true if the field was set; false otherwise
     */
    public boolean setSuppressField(CharID id, BiographyField field, boolean suppress)
    {
        @SuppressWarnings("unchecked")
        Set<BiographyField> suppressedFields = (Set<BiographyField>) getCache(id);
        if (suppressedFields == null)
        {
            suppressedFields = Collections.synchronizedSet(new HashSet<>());
            setCache(id, suppressedFields);
        }

        if (suppress)
        {
            return suppressedFields.add(field);
        } else
        {
            return suppressedFields.remove(field);
        }
    }

    /**
     * Check whether the field should be hidden from output for the character.
     *
     * @param id    The CharID of the Player Character being queried.
     * @param field The BiographyField to set export suppression rules for.
     * @return true if the field should not be output, false if it may be.
     */
    public boolean getSuppressField(CharID id, BiographyField field)
    {
        @SuppressWarnings("unchecked")
        Set<BiographyField> suppressedFields = (Set<BiographyField>) getCache(id);
        return suppressedFields != null && suppressedFields.contains(field);
    }

    /**
     * Copies the contents of the SuppressBioFieldFacet from one Player
     * Character to another Player Character, based on the given CharIDs
     * representing those Player Characters.
     * <p>
     * This is a method in SuppressBioFieldFacet in order to avoid exposing the
     * mutable Map object to other classes. This should not be inlined, as the
     * Set is internal information to SuppressBioFieldFacet and should not be
     * exposed to other classes.
     * <p>
     * Note also the copy is a one-time event and no references are maintained
     * between the Player Characters represented by the given CharIDs (meaning
     * once this copy takes place, any change to the SuppressBioFieldFacet will
     * only impact the Player Character where the SuppressBioFieldFacet was
     * changed).
     *
     * @param source The CharID representing the Player Character from which the
     *               information should be copied
     * @param copy   The CharID representing the Player Character to which the
     *               information should be copied
     */
    @Override
    public void copyContents(CharID source, CharID copy)
    {
        @SuppressWarnings("unchecked")
        Set<BiographyField> set = (Set<BiographyField>) getCache(source);
        if (set != null)
        {
            Set<BiographyField> copyset = Collections.synchronizedSet(new HashSet<>());
            copyset.addAll(set);
            setCache(copy, copyset);
        }
    }

}
