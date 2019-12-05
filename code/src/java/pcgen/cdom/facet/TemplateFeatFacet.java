/*
 * Copyright (c) Thomas Parker, 2014.
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

import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;

/**
 * TemplateFeatFacet is a Facet that tracks the selections from the FEAT tokens
 * on Templates that have been added to a Player Character.
 */
public class TemplateFeatFacet extends AbstractSourcedListFacet<CharID, CNAbilitySelection>
        implements DataFacetChangeListener<CharID, PCTemplate>
{
    private TemplateFacet templateFacet;

    private final PlayerCharacterTrackingFacet trackingFacet =
            FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

    /**
     * Adds all of the feats to the Player Character triggered by the FEAT token'
     * on the given PCTemplate
     * <p>
     * Triggered when one of the Facets to which ConditionalTemplateFacet
     * listens fires a DataFacetChangeEvent to indicate a PCTemplate was added
     * to a Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, PCTemplate> dfce)
    {
        CharID id = dfce.getCharID();
        PCTemplate source = dfce.getCDOMObject();
        if (!containsFrom(id, source))
        {
            PersistentTransitionChoice<CNAbilitySelection> choice = source.get(ObjectKey.TEMPLATE_FEAT);
            if (choice != null)
            {
                PlayerCharacter pc = trackingFacet.getPC(id);
                Collection<? extends CNAbilitySelection> result = choice.driveChoice(pc);
                choice.act(result, source, pc);
                for (CNAbilitySelection cas : result)
                {
                    add(id, cas, source);
                }
            }
        }
    }

    /**
     * Removes all of the feats granted by FEAT: on the PCTemplate removed
     * <p>
     * Triggered when one of the Facets to which ConditionalTemplateFacet
     * listens fires a DataFacetChangeEvent to indicate a PCTemplate was removed
     * from a Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, PCTemplate> dfce)
    {
        CharID id = dfce.getCharID();
        PCTemplate source = dfce.getCDOMObject();
        PersistentTransitionChoice<CNAbilitySelection> choice = source.get(ObjectKey.TEMPLATE_FEAT);
        if (choice != null)
        {
            PlayerCharacter pc = trackingFacet.getPC(id);
            choice.remove(source, pc);
        }
        removeAll(id, source);
    }

    public void setTemplateFacet(TemplateFacet templateFacet)
    {
        this.templateFacet = templateFacet;
    }

    public void init()
    {
        templateFacet.addDataFacetChangeListener(this);
    }

}
