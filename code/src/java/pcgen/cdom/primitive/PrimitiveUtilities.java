/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.primitive;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

import pcgen.cdom.base.PrimitiveCollection;

import org.jetbrains.annotations.NotNull;

public final class PrimitiveUtilities
{
    /**
     * A COLLATOR used to sort Strings in a locale-aware method.
     */
    private static final Collator COLLATOR = Collator.getInstance();

    /**
     * A Comparator used to sort PrimitiveCollection objects
     */
    public static final Comparator<PrimitiveCollection<?>> COLLECTION_SORTER =
            (lstw1, lstw2) -> COLLATOR.compare(lstw1.getLSTformat(false), lstw2.getLSTformat(false));

    private PrimitiveUtilities()
    {
        //Prohibit use of constructor in final/utility class
    }

    /**
     * Joins the LST format of a Collection of PrimitiveCollection objects.
     *
     * @param pcCollection The Collection of PrimitiveCollection objects
     * @param separator    The separator used to separate the LST format of the PrimitiveCollection
     *                     objects
     * @param useAny       true if "ANY" should be used for all items; false if "ALL" should be
     *                     used
     * @return A String of the joined LST formats of the given Collection of
     * PrimitiveCollection objects
     */
    @NotNull
    public static String joinLstFormat(@NotNull Collection<? extends PrimitiveCollection<?>> pcCollection,
            @NotNull CharSequence separator, boolean useAny)
    {
        return pcCollection.stream().map(pcf -> pcf.getLSTformat(useAny)).collect(Collectors.joining(separator));
    }

}
