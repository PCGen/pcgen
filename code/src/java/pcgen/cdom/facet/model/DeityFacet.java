/*
 * Copyright (c) Thomas Parker, 2009.
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
package pcgen.cdom.facet.model;

import pcgen.cdom.base.ItemFacet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractItemFacet;
import pcgen.core.Deity;
import pcgen.output.publish.OutputDB;

/**
 * DeityFacet is a Facet that tracks the Deity worshipped by a Player Character.
 * <p>
 * null is a legal value for this Facet
 */
public class DeityFacet extends AbstractItemFacet<CharID, Deity> implements ItemFacet<CharID, Deity>
{
    public void init()
    {
        OutputDB.register("deity", this);
    }
}
