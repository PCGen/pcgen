/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.content.fact;

import pcgen.base.util.FormatManager;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.FactKey;

/**
 * FactInfo represents the key information required in order to use a Fact. This
 * includes the usable location, the name, and the FormatManager that can
 * process the objects of the type in the Fact.
 *
 * @param <T> The Type of object upon which the underlying Fact for this
 *            FactInfo can be applied
 * @param <F> The format of the objects stored in the Fact
 */
public interface FactInfo<T extends CDOMObject, F>
{

    /**
     * Returns the FormatManager for this FactInfo. This is used to convert
     * to/from the String format used to serialize content for LST files.
     *
     * @return The FormatManager for this FactInfo
     */
    FormatManager<F> getFormatManager();

    /**
     * Returns the "usable location" of this FactInfo (related to what LST files
     * this FactInfo will be usable within).
     *
     * @return The "usable location" of this FactInfo
     */
    Class<T> getUsableLocation();

    /**
     * Returns the Fact Name for this FactInfo. This effectively serves as the
     * subtoken name in the LST data.
     *
     * @return The Fact Name for this FactInfo
     */
    String getFactName();

    /**
     * Returns the FactKey for this FactInfo.
     *
     * @return The FactKey for this FactInfo
     */
    FactKey<F> getFactKey();

}
