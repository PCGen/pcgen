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
import pcgen.core.PlayerCharacter;

/**
 * This is a transition class, designed to allow things to be taken out of
 * PlayerCharacter while a transition is made to a system where variables are
 * captured when items are entered into the PlayerCharacter and is different
 * than today's (5.x) core.
 */
public class ObjectAdditionFacet implements DataFacetChangeListener<CDOMObject>
{
	private final Class<?> thisClass = getClass();

	private CDOMObjectConsolidationFacet consolidationFacet;

	public void associatePlayerCharacter(CharID id, PlayerCharacter pc)
	{
		FacetCache.set(id, thisClass, pc);
	}

	@Override
	public void dataAdded(DataFacetChangeEvent<CDOMObject> dfce)
	{
		CDOMObject cdo = dfce.getCDOMObject();
		CharID id = dfce.getCharID();
		PlayerCharacter pc = (PlayerCharacter) FacetCache.get(id, thisClass);
		pc.processAddition(cdo);
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<CDOMObject> dfce)
	{
		CDOMObject cdo = dfce.getCDOMObject();
		CharID id = dfce.getCharID();
		PlayerCharacter pc = (PlayerCharacter) FacetCache.get(id, thisClass);
		pc.processRemoval(cdo);
	}

	public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
	{
		this.consolidationFacet = consolidationFacet;
	}
	
	public void init()
	{
		consolidationFacet.addDataFacetChangeListener(this);
	}
}
