/*
 * Copyright 2012 Vincent Lhote
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
package pcgen.cdom.facet.fact;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.Handed;
import pcgen.cdom.facet.base.AbstractItemFacet;
import pcgen.util.Logging;

/**
 * HandedFacet is a Facet that tracks the Handed of a Player Character.
 * 
 */
public class HandedFacet extends AbstractItemFacet<CharID, Handed>
{

	/**
	 * Sets the Handed of the Player Character represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character on which the
	 *            Handed should be set
	 * @param obj
	 *            The Handed to set on the Player Character represented by the
	 *            given CharID
	 * @return
	 * 			  true if the Handedness was set; false otherwise
	 */
	public boolean setHanded(CharID id, Handed obj)
	{
		return set(id, obj);
	}

	/**
	 * Removes the Handed of the Player Character represented by the given
	 * CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            Handed should be removed
	 */
	public void removeHanded(CharID id)
	{
		/*
		 * TODO Need to consider if this makes any sense - should this be like
		 * Race that doesn't allow a null value? - if so, that needs to be
		 * documented and this method removed.
		 */
		remove(id);
	}

	/**
	 * Returns the Handed for the Player Character represented by the given
	 * CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            Handed should be returned
	 * @return The Handed for the Player Character represented by the given
	 *         CharID
	 */
	public Handed getHanded(CharID id)
	{
		Handed g = get(id);
		if (Logging.isDebugMode())
		{
			Logging.debugPrint("HandedFacet handed value "+g);
		}
		return g == null ? Handed.getDefaultValue() : g;
	}
}
