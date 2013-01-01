/*
 * Copyright (c) 2012 Tom Parker <thpr@users.sourceforge.net> This program is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version.
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
 * ChooseDriverFacet is a Facet that drives the selection of a CHOOSE on a
 * CDOMObject.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class OldChooseCleanupFacet implements
		DataFacetChangeListener<CDOMObject>
{
	private final PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
		.getFacet(PlayerCharacterTrackingFacet.class);

	@Override
	public void dataAdded(DataFacetChangeEvent<CDOMObject> dfce)
	{
		//ignore
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<CDOMObject> dfce)
	{
		/*
		 * TODO Consider whether this is the appropriate symmetric action to add
		 * (remove associations)
		 */
		CharID id = dfce.getCharID();
		PlayerCharacter pc = trackingFacet.getPC(id);
		CDOMObject cdo = dfce.getCDOMObject();
		pc.removeAllAssociations(cdo);
	}
}
