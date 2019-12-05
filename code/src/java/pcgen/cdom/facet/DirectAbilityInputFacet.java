/*
 * Copyright (c) Thomas Parker, 2009-14.
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

import java.util.Objects;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSingleSourceListFacet;
import pcgen.cdom.helper.CNAbilitySelection;

/**
 * DirectAbilityInputFacet is a Facet that tracks the Abilities that are added
 * with indirect grants via %LIST that have been granted to a Player Character.
 */
public class DirectAbilityInputFacet extends AbstractSingleSourceListFacet<CNAbilitySelection, CDOMObject>
{
    public void add(CharID id, CDOMObject owner, CNAbilitySelection as)
    {
        Objects.requireNonNull(owner, "Owner Object may not be null");
        Objects.requireNonNull(as, "CNAbilitySelection to add may not be null");
        add(id, as, owner);
    }

    public void remove(CharID id, CDOMObject owner, CNAbilitySelection as)
    {
        Objects.requireNonNull(owner, "Owner Object may not be null");
        Objects.requireNonNull(as, "CNAbilitySelection to add may not be null");
        remove(id, as, owner);
    }
}
