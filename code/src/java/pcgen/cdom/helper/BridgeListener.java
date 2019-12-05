/*
 * Copyright (c) Thomas Parker, 2017-18.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.helper;

import java.util.List;
import java.util.Objects;

import pcgen.base.util.ArrayUtilities;
import pcgen.base.util.Tuple;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.formula.PCGenScoped;
import pcgen.cdom.formula.VariableChangeEvent;
import pcgen.cdom.formula.VariableListener;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;

/**
 * A BridgeListener converts from a VariableListener to then inject items into an
 * AbstractSourcedListFacet acting in the traditional Facet events.
 */
public class BridgeListener
        implements VariableListener<Object>, ReferenceListener<Object>
{
    /**
     * The AbstractSourcedListFacet to which objects will be forwarded
     */
    private final AbstractSourcedListFacet<CharID, PCGenScoped> variableBridgeFacet;

    /**
     * The CharID that this BridgeListener is serving
     */
    private final CharID id;

    /**
     * Constructs a new BridgeListener for the given CharID which will use the given
     * AbstractSourcedListFacet
     *
     * @param id                  The CharID that this BridgeListener is serving
     * @param variableBridgeFacet The AbstractSourcedListFacet to be used as a bridge to the traditional
     *                            Facet events
     */
    public BridgeListener(CharID id,
            AbstractSourcedListFacet<CharID, PCGenScoped> variableBridgeFacet)
    {
        this.variableBridgeFacet = Objects.requireNonNull(variableBridgeFacet);
        this.id = Objects.requireNonNull(id);
    }

    @Override
    public void variableChanged(VariableChangeEvent<Object> vcEvent)
    {
        processChange(vcEvent.getOldValue(), vcEvent.getNewValue(),
                vcEvent.getSource());
    }

    @Override
    public void referenceChanged(ReferenceEvent<Object> e)
    {
        processChange(e.getOldReference(), e.getNewReference(), e.getSource());
    }

    private void processChange(Object oldValue, Object newValue, Object source)
    {
        /*
         * CONSIDER This is a hard-coding based on array - which is quirky
         */
        if (newValue.getClass().isArray())
        {
            Tuple<List<Object>, List<Object>> difference =
                    ArrayUtilities.calculateIdentityDifference(
                            (Object[]) oldValue, (Object[]) newValue);
            for (Object toRemove : difference.getFirst())
            {
                variableBridgeFacet.remove(id, (PCGenScoped) toRemove, source);
            }
            for (Object toAdd : difference.getSecond())
            {
                variableBridgeFacet.add(id, (PCGenScoped) toAdd, source);
            }
        } else
        {
            if (oldValue != null)
            {
                variableBridgeFacet.remove(id, (PCGenScoped) oldValue, source);
            }
            variableBridgeFacet.add(id, (PCGenScoped) newValue, source);
        }
    }

    @Override
    public int hashCode()
    {
        return id.hashCode() * 31 + variableBridgeFacet.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof BridgeListener)
        {
            BridgeListener other = (BridgeListener) obj;
            return id.equals(other.id)
                    && variableBridgeFacet.equals(other.variableBridgeFacet);
        }
        return false;
    }
}
