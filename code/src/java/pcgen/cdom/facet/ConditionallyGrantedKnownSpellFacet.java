/*
 * Copyright (c) Thomas Parker, 2012.
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

import java.util.Collection;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.helper.AvailableSpell;

public class ConditionallyGrantedKnownSpellFacet
{
    private ConditionallyKnownSpellFacet conditionallyKnownSpellFacet;

    private KnownSpellFacet knownSpellFacet;

    public void update(CharID id)
    {
        Collection<AvailableSpell> set = conditionallyKnownSpellFacet.getQualifiedSet(id);
        for (AvailableSpell as : set)
        {
            Collection<Object> sources = conditionallyKnownSpellFacet.getSources(id, as);
            for (Object source : sources)
            {
                knownSpellFacet.add(id, as.getSpelllist(), as.getLevel(), as.getSpell(), source);
            }
        }
    }

    public void setConditionallyKnownSpellFacet(ConditionallyKnownSpellFacet conditionallyKnownSpellFacet)
    {
        this.conditionallyKnownSpellFacet = conditionallyKnownSpellFacet;
    }

    public void setKnownSpellFacet(KnownSpellFacet knownSpellFacet)
    {
        this.knownSpellFacet = knownSpellFacet;
    }

}
