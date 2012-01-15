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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.core.BioSet;
import pcgen.core.PlayerCharacter;

/**
 * BioSetFacet is a Facet that tracks the BioSet active in a Game Mode and thus
 * active for a Player Character.
 */
public class BioSetFacet extends AbstractItemFacet<BioSet> implements
		DataFacetChangeListener<CDOMObject>
{
	private PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
		.getFacet(PlayerCharacterTrackingFacet.class);

	@Override
	public void dataAdded(DataFacetChangeEvent<CDOMObject> dfce)
	{
		CharID id = dfce.getCharID();
		PlayerCharacter pc = trackingFacet.getPC(id);

		if (!pc.isImporting())
		{
			get(id).randomize("AGE.HT.WT", pc);
		}

	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<CDOMObject> dfce)
	{
		//Nothing to do
	}

}
