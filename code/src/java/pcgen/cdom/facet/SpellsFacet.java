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

import pcgen.base.formula.Formula;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.content.SpellLikeAbility;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractQualifiedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.core.spell.Spell;

/**
 * SpellsFacet is a Facet that tracks the SpellLikeAbility objects that have
 * been granted to a Player Character through SPELLS
 */
public class SpellsFacet extends AbstractQualifiedListFacet<SpellLikeAbility>
        implements DataFacetChangeListener<CharID, CDOMObject>
{

    private CDOMObjectSourceFacet cdomSourceFacet;

    /**
     * Adds a SpellLikeAbility to this facet if the CDOMObject added to a Player
     * Character contains a SPELLS entry.
     * <p>
     * Triggered when one of the Facets to which SpellsFacet listens fires a
     * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CharID id = dfce.getCharID();
        CDOMObject cdo = dfce.getCDOMObject();

        Collection<CDOMReference<Spell>> mods = cdo.getListMods(Spell.SPELLS);
        if (mods == null)
        {
            return;
        }

        for (CDOMReference<Spell> ref : mods)
        {
            Collection<AssociatedPrereqObject> assocs = cdo.getListAssociations(Spell.SPELLS, ref);
            Collection<Spell> spells = ref.getContainedObjects();
            for (AssociatedPrereqObject apo : assocs)
            {
                Formula times = apo.getAssociation(AssociationKey.TIMES_PER_UNIT);
                String timeunit = apo.getAssociation(AssociationKey.TIME_UNIT);
                // The timeunit needs to default to day as per the docs
                if (timeunit == null)
                {
                    timeunit = "Day";
                }
                String casterlevel = apo.getAssociation(AssociationKey.CASTER_LEVEL);
                String dcformula = apo.getAssociation(AssociationKey.DC_FORMULA);
                String book = apo.getAssociation(AssociationKey.SPELLBOOK);
                String ident = cdo.getQualifiedKey();
                for (Spell sp : spells)
                {
                    SpellLikeAbility sla =
                            new SpellLikeAbility(sp, times, timeunit, book, casterlevel, dcformula, ident);
                    sla.addAllPrerequisites(apo.getPrerequisiteList());
                    add(id, sla, cdo);
                }
            }
        }
    }

    /**
     * Removes all SpellLikeAbility objects granted by a CDOMObject when that
     * CDOMObject is removed from a Player Character.
     * <p>
     * Triggered when one of the Facets to which SpellsFacet listens fires a
     * DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        removeAll(dfce.getCharID(), dfce.getCDOMObject());
    }

    public void setCdomSourceFacet(CDOMObjectSourceFacet cdomSourceFacet)
    {
        this.cdomSourceFacet = cdomSourceFacet;
    }

    /**
     * Initializes the connections for SpellsFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the SpellsFacet.
     */
    public void init()
    {
        cdomSourceFacet.addDataFacetChangeListener(this);
    }
}
