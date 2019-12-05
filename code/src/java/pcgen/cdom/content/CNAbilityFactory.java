/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.content;

import java.util.HashMap;
import java.util.Map;

import pcgen.cdom.base.Category;
import pcgen.cdom.enumeration.Nature;
import pcgen.core.Ability;

public final class CNAbilityFactory
{

    private CNAbilityFactory()
    {
        //Do not instantiate
    }

    private static final Map<CNAbility, CNAbility> MAP = new HashMap<>();

    public static CNAbility getCNAbility(Category<Ability> cat, Nature n, Ability a)
    {
        CNAbility toMatch = new CNAbility(cat, a, n);
        CNAbility result = MAP.get(toMatch);
        if (result == null)
        {
            MAP.put(toMatch, toMatch);
            return toMatch;
        }
        return result;
    }

    public static void reset()
    {
        MAP.clear();
    }
}
