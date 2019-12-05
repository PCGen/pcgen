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
package pcgen.cdom.facet.fact;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractItemFacet;

/**
 * XP Facet is a facet that tracks the Experience Points of a Player Character.
 * <p>
 * Earned Experience Points are Experience Points that the Player Character has
 * earned through play.
 * <p>
 * Level-Adjusted Experience Points are Experience Points that the Player
 * Character has received through level adjustments, and are independent of
 * earned Experience Points.
 * <p>
 * Total Experience Points are a combination of Earned Experience Points and
 * Level-Adjusted Experience Points.
 */
public class XPFacet extends AbstractItemFacet<CharID, Integer>
{
}
