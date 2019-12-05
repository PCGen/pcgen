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
package pcgen.cdom.facet;

import java.util.Collection;

import pcgen.cdom.base.CDOMList;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.helper.AvailableSpell;
import pcgen.core.spell.Spell;

public class SpellListToAvailableSpellFacet implements DataFacetChangeListener<CharID, CDOMList<Spell>>
{
    private MasterAvailableSpellFacet masterAvailableSpellFacet;
    private SpellListFacet spellListFacet;
    private ConditionallyAvailableSpellFacet conditionallyAvailableSpellFacet;
    private AvailableSpellFacet availableSpellFacet;

    private void add(CharID id, AvailableSpell as, Object source)
    {
        if (as.hasPrerequisites())
        {
            conditionallyAvailableSpellFacet.add(id, as, source);
        } else
        {
            availableSpellFacet.add(id, as.getSpelllist(), as.getLevel(), as.getSpell(), source);
        }
    }

    private void remove(CharID id, AvailableSpell as, Object source)
    {
        if (as.hasPrerequisites())
        {
            conditionallyAvailableSpellFacet.remove(id, as, source);
        } else
        {
            availableSpellFacet.remove(id, as.getSpelllist(), as.getLevel(), as.getSpell(), source);
        }
    }

    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMList<Spell>> dfce)
    {
        CharID id = dfce.getCharID();
        CDOMList<Spell> list = dfce.getCDOMObject();
        Collection<AvailableSpell> spells = masterAvailableSpellFacet.getSet(id.getDatasetID());
        for (AvailableSpell as : spells)
        {
            if (as.getSpelllist().equals(list))
            {
                add(id, as, this);
            }
        }
    }

    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMList<Spell>> dfce)
    {
        CharID id = dfce.getCharID();
        CDOMList<Spell> list = dfce.getCDOMObject();
        Collection<AvailableSpell> spells = masterAvailableSpellFacet.getSet(id.getDatasetID());
        for (AvailableSpell as : spells)
        {
            if (as.getSpelllist().equals(list))
            {
                remove(id, as, this);
            }
        }
    }

    public void init()
    {
        spellListFacet.addDataFacetChangeListener(this);
    }

    public void setSpellListFacet(SpellListFacet spellListFacet)
    {
        this.spellListFacet = spellListFacet;
    }

    public void setMasterAvailableSpellFacet(MasterAvailableSpellFacet masterAvailableSpellFacet)
    {
        this.masterAvailableSpellFacet = masterAvailableSpellFacet;
    }

    public void setConditionallyAvailableSpellFacet(ConditionallyAvailableSpellFacet conditionallyAvailableSpellFacet)
    {
        this.conditionallyAvailableSpellFacet = conditionallyAvailableSpellFacet;
    }

    public void setAvailableSpellFacet(AvailableSpellFacet availableSpellFacet)
    {
        this.availableSpellFacet = availableSpellFacet;
    }
}
