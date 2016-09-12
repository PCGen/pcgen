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

public final class StatAdapter extends AbstractAdapter<Integer> implements
		WriteableReferenceFacade<Integer>,
		ScopeFacetChangeListener<CharID, PCStat, Integer>
{
	private StatValueFacet statValueFacet = FacetLibrary
		.getFacet(StatValueFacet.class);

	private final CharID id;
	private final PCStat stat;
	private int lastKnown;

	private StatAdapter(CharID id, PCStat stat)
	{
		this.id = id;
		this.stat = stat;
		lastKnown = 0;
	}

	@Override
	public Integer get()
	{
		return statValueFacet.get(id, stat);
	}

	@Override
	public void set(Integer value)
	{
		statValueFacet.set(id, stat, value);
	}

	public static StatAdapter generate(CharID id, PCStat stat)
	{
		StatAdapter sa = new StatAdapter(id, stat);
		sa.statValueFacet.addScopeFacetChangeListener(sa);
		return sa;
	}

	@Override
	public void dataAdded(ScopeFacetChangeEvent<CharID, PCStat, Integer> dfce)
	{
		if (dfce.getCharID().equals(id) && dfce.getScope().equals(stat))
		{
			fireReferenceChangedEvent(this, lastKnown, dfce.getCDOMObject());
		}
	}

	@Override
	public void dataRemoved(ScopeFacetChangeEvent<CharID, PCStat, Integer> dfce)
	{
		if (dfce.getCharID().equals(id) && dfce.getScope().equals(stat))
		{
			lastKnown = dfce.getCDOMObject();
		}
	}

}
