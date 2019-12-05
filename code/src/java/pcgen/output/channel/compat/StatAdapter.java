/*
 * Copyright (c) Thomas Parker, 2016.
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
package pcgen.output.channel.compat;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.StatValueFacet;
import pcgen.cdom.facet.event.ScopeFacetChangeEvent;
import pcgen.cdom.facet.event.ScopeFacetChangeListener;
import pcgen.core.PCStat;
import pcgen.facade.util.WriteableReferenceFacade;

/**
 * A StatAdapter is the historical compatibility interface (facade) used to wrap to the
 * gui2 non-channel system for setting and retrieving PCStat values.
 */
public final class StatAdapter extends AbstractAdapter<Number>
        implements WriteableReferenceFacade<Number>, ScopeFacetChangeListener<CharID, PCStat, Number>
{
    private StatValueFacet statValueFacet = FacetLibrary.getFacet(StatValueFacet.class);

    private final CharID id;
    private final PCStat stat;
    private Number lastKnown;

    private StatAdapter(CharID id, PCStat stat)
    {
        this.id = id;
        this.stat = stat;
        lastKnown = 0;
    }

    @Override
    public Number get()
    {
        return statValueFacet.get(id, stat);
    }

    @Override
    public void set(Number value)
    {
        statValueFacet.set(id, stat, value);
    }

    /**
     * Returns a StatAdapter for the PlayerCharacter represented by the given CharID and
     * the given PCStat.
     *
     * @param id   The CharID representing the PlayerCharacter for which the given
     *             StatAdapter should be returned
     * @param stat The PCStat for which the StatAdapter will operate
     * @return A StatAdapter for the PlayerCharacter represented by the given CharID and
     * the given PCStat.
     */
    public static StatAdapter generate(CharID id, PCStat stat)
    {
        StatAdapter sa = new StatAdapter(id, stat);
        sa.statValueFacet.addScopeFacetChangeListener(sa);
        return sa;
    }

    @Override
    public void dataAdded(ScopeFacetChangeEvent<CharID, PCStat, Number> dfce)
    {
        if (dfce.getCharID().equals(id) && dfce.getScope().equals(stat))
        {
            fireReferenceChangedEvent(this, lastKnown, dfce.getCDOMObject());
        }
    }

    @Override
    public void dataRemoved(ScopeFacetChangeEvent<CharID, PCStat, Number> dfce)
    {
        if (dfce.getCharID().equals(id) && dfce.getScope().equals(stat))
        {
            lastKnown = dfce.getCDOMObject();
        }
    }

}
