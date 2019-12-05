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
import java.util.List;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.helper.AvailableSpell;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.spell.Spell;

public class KnownSpellInputFacet implements DataFacetChangeListener<CharID, CDOMObject>
{

    private ConditionallyKnownSpellFacet conditionallyKnownSpellFacet;

    private KnownSpellFacet knownSpellFacet;

    private CDOMObjectConsolidationFacet consolidationFacet;

    /**
     * Triggered when one of the Facets to which ConditionallyKnownSpellFacet
     * listens fires a DataFacetChangeEvent to indicate a CDOMObject was added
     * to a Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CDOMObject cdo = dfce.getCDOMObject();
        Collection<CDOMReference<? extends CDOMList<?>>> listrefs = cdo.getModifiedLists();
        CharID id = dfce.getCharID();
        for (CDOMReference<? extends CDOMList<?>> ref : listrefs)
        {
            processListRef(id, cdo, ref);
        }
    }

    private void processListRef(CharID id, CDOMObject cdo, CDOMReference<? extends CDOMList<?>> listref)
    {
        for (CDOMList<?> list : listref.getContainedObjects())
        {
            if (!(list instanceof ClassSpellList) && !(list instanceof DomainSpellList))
            {
                continue;
            }
            CDOMList<Spell> spelllist = (CDOMList<Spell>) list;
            processList(id, spelllist, listref, cdo);
        }
    }

    private void processList(CharID id, CDOMList<Spell> spelllist, CDOMReference<? extends CDOMList<?>> listref,
            CDOMObject cdo)
    {
        for (CDOMReference<Spell> objref : cdo.getListMods((CDOMReference<? extends CDOMList<Spell>>) listref))
        {
            for (AssociatedPrereqObject apo : cdo.getListAssociations(listref, objref))
            {
                Boolean known = apo.getAssociation(AssociationKey.KNOWN);
                if ((known == null) || !known)
                {
                    continue;
                }
                Collection<Spell> spells = objref.getContainedObjects();
                Integer lvl = apo.getAssociation(AssociationKey.SPELL_LEVEL);
                if (apo.hasPrerequisites())
                {
                    List<Prerequisite> prereqs = apo.getPrerequisiteList();
                    for (Spell spell : spells)
                    {
                        AvailableSpell as = new AvailableSpell(spelllist, spell, lvl);
                        as.addAllPrerequisites(prereqs);
                        conditionallyKnownSpellFacet.add(id, as, cdo);
                    }
                } else
                {
                    for (Spell spell : spells)
                    {
                        knownSpellFacet.add(id, spelllist, lvl, spell, cdo);
                    }
                }
            }
        }
    }

    /**
     * Triggered when one of the Facets to which ConditionallyKnownSpellFacet
     * listens fires a DataFacetChangeEvent to indicate a CDOMObject was removed
     * from a Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CharID id = dfce.getCharID();
        CDOMObject source = dfce.getCDOMObject();
        conditionallyKnownSpellFacet.removeAll(id, source);
        knownSpellFacet.removeAllFromSource(id, source);
    }

    /**
     * Initializes the connections for KnwonSpellFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the KnwonSpellFacet.
     */
    public void init()
    {
        consolidationFacet.addDataFacetChangeListener(this);
    }

    public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
    {
        this.consolidationFacet = consolidationFacet;
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
