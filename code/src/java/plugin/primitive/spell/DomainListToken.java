/*
 * Copyright 2010 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.primitive.spell;

import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.MasterAvailableSpellFacet;
import pcgen.cdom.helper.AvailableSpell;
import pcgen.cdom.list.DomainSpellList;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractRestrictedSpellPrimitive;

/**
 * The Class {@code DomainListToken} handles the restriction of a spell choice to a spell from a
 * domain spell list.
 */
public class DomainListToken extends AbstractRestrictedSpellPrimitive
{
    private CDOMSingleRef<DomainSpellList> spelllist;
    private MasterAvailableSpellFacet masterAvailableSpellFacet;

    @Override
    public boolean initialize(LoadContext context, Class<Spell> cl, String value, String args)
    {
        if (value == null)
        {
            return false;
        }
        spelllist = context.getReferenceContext().getCDOMReference(DomainSpellList.class, value);
        masterAvailableSpellFacet = FacetLibrary.getFacet(MasterAvailableSpellFacet.class);
        return initialize(context, args);
    }

    @Override
    public String getTokenName()
    {
        return "DOMAINLIST";
    }

    @Override
    public boolean allow(PlayerCharacter pc, Spell spell)
    {
        DomainSpellList list = spelllist.get();
        DataSetID datasetID = pc.getCharID().getDatasetID();

        for (AvailableSpell availSpell : masterAvailableSpellFacet.getMatchingSpellsInList(list, datasetID, spell))
        {
            int level = availSpell.getLevel();
            if (level >= 0 && allow(pc, level, "", spell, list))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public GroupingState getGroupingState()
    {
        return GroupingState.ANY;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof DomainListToken)
        {
            DomainListToken other = (DomainListToken) obj;
            if (spelllist == null)
            {
                return other.spelllist == null;
            }
            return spelllist.equals(other.spelllist) && equalsRestrictedPrimitive(other);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return spelllist == null ? -7 : spelllist.hashCode();
    }

    @Override
    public CharSequence getPrimitiveLST()
    {
        return new StringBuilder().append(getTokenName()).append('=').append(spelllist.getLSTformat(false))
                .append(getRestrictionLST());
    }

}
