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

import pcgen.cdom.base.CDOMList;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.ClassFacet;
import pcgen.cdom.facet.model.DomainFacet;
import pcgen.cdom.helper.ClassSource;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.DomainApplication;
import pcgen.core.spell.Spell;

/**
 * DomainSpellsFacet tracks the Domain Spells allowed / granted to the Player
 * Character due to the Domain selections of the Player Character.
 */
public class DomainSpellsFacet extends AbstractSourcedListFacet<CharID, CDOMList<Spell>>
        implements DataFacetChangeListener<CharID, Domain>
{

    private final PlayerCharacterTrackingFacet trackingFacet =
            FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

    private DomainFacet domainFacet;

    private ClassFacet classFacet;

    /**
     * Adds Domain Spells allowed / granted to the Player Character due to the
     * Domain selections of the Player Character.
     * <p>
     * Triggered when one of the Facets to which DomainSpellsFacet listens fires
     * a DataFacetChangeEvent to indicate a Domain was added to a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, Domain> dfce)
    {
        Domain domain = dfce.getCDOMObject();
        CharID id = dfce.getCharID();
        ClassSource source = domainFacet.getSource(id, domain);
        if (source != null)
        {
            String classKey = source.getPcclass().getKeyName();
            PCClass domainClass = getClassKeyed(id, classKey);
            if (domainClass != null)
            {
                PlayerCharacter pc = trackingFacet.getPC(id);
                final int maxLevel = pc.getSpellSupport(domainClass).getMaxCastLevel();
                DomainApplication.addSpellsToClassForLevels(pc, domain, domainClass, 0, maxLevel);
            }
        }
    }

    //FUTURE Won't need this if classes aren't cloned...
    private PCClass getClassKeyed(CharID id, String classKey)
    {
        for (PCClass aClass : classFacet.getSet(id))
        {
            if (aClass.getKeyName().equalsIgnoreCase(classKey))
            {
                return aClass;
            }
        }

        return null;
    }

    /**
     * Removes Domain Spells allowed / granted to the Player Character due to
     * the Domain selections of the Player Character (a Domain being removed).
     * <p>
     * Triggered when one of the Facets to which DomainSpellsFacet listens fires
     * a DataFacetChangeEvent to indicate a Domain was added to a Player
     * Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, Domain> dfce)
    {
        /*
         * Nothing right now
         *
         * TODO This should require some form of symmetry with the dataAdded
         * method of DomainSpellsFacet
         */
    }

    public void setDomainFacet(DomainFacet domainFacet)
    {
        this.domainFacet = domainFacet;
    }

    public void setClassFacet(ClassFacet classFacet)
    {
        this.classFacet = classFacet;
    }

    /**
     * Initializes the connections for DomainSpellsFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the DomainSpellsFacet.
     */
    public void init()
    {
        domainFacet.addDataFacetChangeListener(this);
    }
}
