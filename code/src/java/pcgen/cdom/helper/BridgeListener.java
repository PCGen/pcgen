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

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Set;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.formula.PCGenScoped;
import pcgen.cdom.formula.VariableChangeEvent;
import pcgen.cdom.formula.VariableListener;

import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * A BridgeListener converts from a VariableListener to then inject items into an
 * AbstractSourcedListFacet acting in the traditional Facet events.
 */
public class BridgeListener implements VariableListener<Object>
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
	 * @param id
	 *            The CharID that this BridgeListener is serving
	 * @param variableBridgeFacet
	 *            The AbstractSourcedListFacet to be used as a bridge to the traditional
	 *            Facet events
	 */
	public BridgeListener(CharID id, AbstractSourcedListFacet<CharID, PCGenScoped> variableBridgeFacet)
	{
		this.variableBridgeFacet = Objects.requireNonNull(variableBridgeFacet);
		this.id = Objects.requireNonNull(id);
	}

	@Override
	public void variableChanged(VariableChangeEvent<Object> vcEvent)
	{
		Object oldValue = vcEvent.getOldValue();
		Object newValue = vcEvent.getNewValue();
		/*
		 * CONSIDER This is a hard-coding based on array - the format manager, which is
		 * available from the event, might want to provide more insight. Currently, this
		 * isn't possible, but it's something to think about with the FormatManager
		 * objects going forward...
		 */
		if (newValue.getClass().isArray())
		{
			ImmutablePair<Set<Object>, Set<Object>> t = processIdentityDeltas((Object[]) oldValue, (Object[]) newValue);
			for (Object o : t.getLeft())
			{
				variableBridgeFacet.remove(id, (PCGenScoped) o, vcEvent.getSource());
			}
			for (Object o : t.getRight())
			{
				variableBridgeFacet.add(id, (PCGenScoped) o, vcEvent.getSource());
			}
		}
		else
		{
			if (oldValue != null)
			{
				variableBridgeFacet.remove(id, (PCGenScoped) oldValue, vcEvent.getSource());
			}
			variableBridgeFacet.add(id, (PCGenScoped) newValue, vcEvent.getSource());
		}
	}

	private ImmutablePair<Set<Object>, Set<Object>> processIdentityDeltas(Object[] oldValue, Object[] newValue)
	{
		Set<Object> toAdd = Collections.newSetFromMap(new IdentityHashMap<>());
		Collections.addAll(toAdd, newValue);
		Set<Object> toRemove = Collections.newSetFromMap(new IdentityHashMap<>());
		Collections.addAll(toRemove, oldValue);
		if ((oldValue.length != 0) && (newValue.length != 0))
		{
			//Note order sensitivity
			toRemove.removeAll(toAdd);
			Collections.addAll(toAdd, oldValue);
		}
		return new ImmutablePair<>(toRemove, toAdd);
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
			return id.equals(other.id) && variableBridgeFacet.equals(other.variableBridgeFacet);
		}
		return false;
	}
}
