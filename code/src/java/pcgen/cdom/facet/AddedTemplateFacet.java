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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;

/**
 * AddedTemplateFacet is a Facet that tracks the Templates that have been added
 * to a Player Character.
 */
public class AddedTemplateFacet extends AbstractSourcedListFacet<CharID, PCTemplate>
        implements DataFacetChangeListener<CharID, CDOMObject>
{

    private PrerequisiteFacet prerequisiteFacet;

    private final PlayerCharacterTrackingFacet trackingFacet =
            FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

    private CDOMObjectConsolidationFacet consolidationFacet;

    /**
     * Establishes the list of PCTemplates to be added to the PlayerCharacter
     * identified by the given CharID. Does not actually perform the addition of
     * the PCTemplates to the Player Character. The given CDOMObject is the
     * source of the PCTemplates to be added to the Player Character.
     *
     * @param id The CharID identifying the Player Character to which the
     *           PCTemplates will be added.
     * @param po The CDOMObject which grants the PCTemplates that will be added
     *           to the Player Character.
     * @return The list of PCTemplates to be added to the PlayerCharacter
     * identified by the given CharID.
     */
    public Collection<PCTemplate> select(CharID id, CDOMObject po)
    {
        List<PCTemplate> list = new ArrayList<>();
        removeAll(id, po);
        PlayerCharacter pc = trackingFacet.getPC(id);
        if (!pc.isImporting())
        {
            for (CDOMReference<PCTemplate> ref : po.getSafeListFor(ListKey.TEMPLATE))
            {
                for (PCTemplate pct : ref.getContainedObjects())
                {
                    add(id, pct, po);
                    list.add(pct);
                }
            }
            List<PCTemplate> added = new ArrayList<>();
            for (CDOMReference<PCTemplate> ref : po.getSafeListFor(ListKey.TEMPLATE_ADDCHOICE))
            {
                added.addAll(ref.getContainedObjects());
            }
            for (CDOMReference<PCTemplate> ref : po.getSafeListFor(ListKey.TEMPLATE_CHOOSE))
            {
                List<PCTemplate> chooseList = new ArrayList<>(added);
                chooseList.addAll(ref.getContainedObjects());
                PCTemplate selected = chooseTemplate(po, chooseList, true, id);
                if (selected != null)
                {
                    add(id, selected, po);
                    list.add(selected);
                }
            }
        }
        return list;
    }

    /**
     * Returns a list of Templates to be removed from the Player Character as
     * defined by TEMPLATE:REMOVE
     *
     * @param id The CharID identifying the PlayerCharacter being processed.
     * @param po The owning CDOMObject being processed to determine if there is
     *           any content defined by TEMPLATE:REMOVE:
     * @return The Collection of objects defined in TEMPLATE:REMOVE: of the
     * given CDOMObject
     */
    public Collection<PCTemplate> remove(CharID id, CDOMObject po)
    {
        List<PCTemplate> list = new ArrayList<>();
        PlayerCharacter pc = trackingFacet.getPC(id);
        if (!pc.isImporting())
        {
            for (CDOMReference<PCTemplate> ref : po.getSafeListFor(ListKey.REMOVE_TEMPLATES))
            {
                list.addAll(ref.getContainedObjects());
            }
        }
        return list;
    }

    /**
     * Drives selection of a PCTemplate from the given list of choices.
     *
     * @param anOwner     The owning CDOMObject that is driving the Template selection
     * @param list        The list of PCTemplates available to be selected
     * @param forceChoice true if the user is forced to make a choice of a PCTemplate;
     *                    false otherwise
     * @param id          The CharID for which the PCTempalte selection is being made.
     * @return The PCTemplate selected
     */
    public PCTemplate chooseTemplate(CDOMObject anOwner, List<PCTemplate> list, boolean forceChoice, CharID id)
    {
        final List<PCTemplate> availableList = new ArrayList<>();
        for (PCTemplate pct : list)
        {
            if (prerequisiteFacet.qualifies(id, pct, anOwner))
            {
                availableList.add(pct);
            }
        }
        if (availableList.size() == 1)
        {
            return availableList.get(0);
        }
        // If we are left without a choice, don't show the chooser.
        if (availableList.isEmpty())
        {
            return null;
        }
        List<PCTemplate> selectedList = new ArrayList<>(1);
        String title = "Template Choice";
        if (anOwner != null)
        {
            title += " (" + anOwner.getDisplayName() + ")";
        }
        selectedList = Globals.getChoiceFromList(title, availableList, selectedList, 1, forceChoice, false,
                trackingFacet.getPC(id));
        if (selectedList.size() == 1)
        {
            return selectedList.get(0);
        }

        return null;
    }

    /**
     * Returns a non-null copy of the Collection of PCTemplates added from the
     * given CDOMObject source to the Player Character identified by the given
     * CharID
     *
     * @param id  The CharID identifying the Player Character for which the
     *            added PCTemplates will be returned
     * @param cdo The source CDOMObject which granted the PCTemplates to be
     *            returned
     * @return A non-null copy of the Collection of PCTemplates added from the
     * given CDOMObject source to the Player Character identified by the
     * given CharID
     */
    public Collection<PCTemplate> getFromSource(CharID id, CDOMObject cdo)
    {
        List<PCTemplate> list = new ArrayList<>();
        Map<PCTemplate, Set<Object>> map = getCachedMap(id);
        if (map != null)
        {
            for (Map.Entry<PCTemplate, Set<Object>> me : map.entrySet())
            {
                Set<Object> sourceSet = me.getValue();
                if (sourceSet.contains(cdo))
                {
                    list.add(me.getKey());
                }
            }
        }
        return list;
    }

    /**
     * Adds and removes (as appropriate) the PCTemplates associated with a
     * CDOMObject which is granted to a Player Character.
     * <p>
     * Triggered when one of the Facets to which AddedTemplateFacet listens
     * fires a DataFacetChangeEvent to indicate a CDOMObject was added to a
     * Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CharID id = dfce.getCharID();
        CDOMObject cdo = dfce.getCDOMObject();
        PlayerCharacter pc = trackingFacet.getPC(id);
        Collection<PCTemplate> list = getFromSource(id, cdo);
        /*
         * If someone pre-set the list, then we use the preset list. If not, we
         * need to do selections
         */
        if (list.isEmpty())
        {
            for (PCTemplate pct : select(id, cdo))
            {
                pc.addTemplate(pct);
            }
            for (PCTemplate pct : remove(id, cdo))
            {
                pc.removeTemplate(pct);
            }
        } else
        {
            for (PCTemplate pct : list)
            {
                pc.addTemplate(pct);
            }
        }
    }

    /**
     * Adds and removes (as appropriate - opposite of the action defined in the
     * LST files) the PCTemplates associated with a CDOMObject which is removed
     * from a Player Character.
     * <p>
     * Triggered when one of the Facets to which AddedTemplateFacet listens
     * fires a DataFacetChangeEvent to indicate a CDOMObject was removed from a
     * Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
    {
        CDOMObject cdo = dfce.getCDOMObject();
        CharID id = dfce.getCharID();
        PlayerCharacter pc = trackingFacet.getPC(id);
        Collection<PCTemplate> list = getFromSource(id, cdo);
        if (list != null)
        {
            for (PCTemplate pct : list)
            {
                pc.removeTemplate(pct);
            }
        }
        removeAll(id, cdo);

        Collection<CDOMReference<PCTemplate>> refList = cdo.getListFor(ListKey.TEMPLATE);
        if (refList != null)
        {
            for (CDOMReference<PCTemplate> pctr : refList)
            {
                for (PCTemplate pct : pctr.getContainedObjects())
                {
                    pc.removeTemplate(pct);
                }
            }
        }
    }

    public void setPrerequisiteFacet(PrerequisiteFacet prerequisiteFacet)
    {
        this.prerequisiteFacet = prerequisiteFacet;
    }

    public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
    {
        this.consolidationFacet = consolidationFacet;
    }

    /**
     * Initializes the connections for AddedTemplateFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the AddedTemplateFacet.
     */
    public void init()
    {
        consolidationFacet.addDataFacetChangeListener(this);
    }
}
