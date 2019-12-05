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
package pcgen.cdom.facet;

import pcgen.base.formula.Formula;
import pcgen.cdom.enumeration.CharID;

/**
 * This is a transition class, designed to allow things to be taken out of
 * PlayerCharacter while a transition is made to a system where variables are
 * captured when items are entered into the PlayerCharacter and is different
 * than today's (5.x) core.
 */
public class FormulaResolvingFacet
{
    private final PlayerCharacterTrackingFacet trackingFacet =
            FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

    public Number resolve(CharID id, Formula f, String source)
    {
        return f.resolve(trackingFacet.getPC(id), source);
    }

}
