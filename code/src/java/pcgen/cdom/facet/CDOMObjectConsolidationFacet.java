/*
 * Copyright (c) Thomas Parker, 2010.
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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;

/**
 * This is a transition class, designed to allow things to be taken out of
 * PlayerCharacter while a transition is made to a sytem where abilities are
 * added in a forward manner, rather than a loop.
 */
public class CDOMObjectConsolidationFacet implements
		DataFacetChangeListener<CDOMObject>
{
	private CDOMObjectBridge bridgeFacet;

	public void setBridgeFacet(CDOMObjectBridge bridge)
	{
		bridgeFacet = bridge;
	}
	public void add(CharID id, CDOMObject obj, Object source)
	{
		bridgeFacet.add(id, obj, source);
	}

	public void remove(CharID id, CDOMObject obj, Object source)
	{
		bridgeFacet.remove(id, obj, source);
	}

	public void addDataFacetChangeListener(
			DataFacetChangeListener<? super CDOMObject> listener)
	{
		bridgeFacet.addDataFacetChangeListener(listener);
	}

	@Override
	public void dataAdded(DataFacetChangeEvent<CDOMObject> dfce)
	{
		CDOMObject cdo = dfce.getCDOMObject();
		add(dfce.getCharID(), cdo, dfce.getSource());
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<CDOMObject> dfce)
	{
		CDOMObject cdo = dfce.getCDOMObject();
		remove(dfce.getCharID(), cdo, dfce.getSource());
	}
}