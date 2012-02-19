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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.WeaponProfProvider;
import pcgen.core.WeaponProf;

/**
 * AutoWeaponProfFacet is a Facet that tracks the WeaponProfs that have been
 * granted to a Player Character.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class AutoWeaponProfFacet extends
		AbstractQualifiedListFacet<WeaponProfProvider> implements
		DataFacetChangeListener<CDOMObject>
{

	private CDOMObjectConsolidationFacet consolidationFacet;

	/**
	 * Processes an added CDOMObject to extract WeaponProf objects which are
	 * granted by AUTO:WEAPONPROF. These extracted WeaponProf objects are added
	 * to the Player Character.
	 * 
	 * Triggered when one of the Facets to which AutoWeaponProfFacet listens
	 * fires a DataFacetChangeEvent to indicate a CDOMObject was added to a
	 * Player Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CDOMObject> dfce)
	{
		CDOMObject cdo = dfce.getCDOMObject();
		List<WeaponProfProvider> weaponProfs = cdo.getListFor(ListKey.WEAPONPROF);
		if (weaponProfs != null)
		{
			addAll(dfce.getCharID(), weaponProfs, cdo);
		}
	}

	/**
	 * Processes a removed CDOMObject to extract WeaponProf objects which are
	 * granted by AUTO:WEAPONPROF. These extracted WeaponProf objects are
	 * removed from the Player Character.
	 * 
	 * Triggered when one of the Facets to which AutoWeaponProfFacet listens
	 * fires a DataFacetChangeEvent to indicate a CDOMObject was removed from a
	 * Player Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataRemoved(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<CDOMObject> dfce)
	{
		removeAll(dfce.getCharID(), dfce.getCDOMObject());
	}

	/**
	 * Returns a non-null Collection of WeaponProf objects that have been
	 * granted to a Player Character.
	 * 
	 * This method is value-semantic in that ownership of the returned
	 * Collection is transferred to the class calling this method. Modification
	 * of the returned Collection will not modify this AutoWeaponProfFacet and
	 * modification of this AutoWeaponProfFacet will not modify the returned
	 * Collection. If you wish to modify the information stored in this
	 * AutoWeaponProfFacet, you must use the add*() and remove*() methods of
	 * AutoWeaponProfFacet.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            Collection of granted WeaponProf objects should be returned
	 * 
	 * @return A non-null Collection of WeaponProf objects that have been
	 *         granted to a Player Character.
	 */
	public Collection<WeaponProf> getWeaponProfs(CharID id)
	{
		Collection<WeaponProf> profs = new ArrayList<WeaponProf>();
		for (WeaponProfProvider wpp : getQualifiedSet(id))
		{
			profs.addAll(wpp.getContainedProficiencies(id));
		}
		return profs;
	}

	public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
	{
		this.consolidationFacet = consolidationFacet;
	}

	/**
	 * Initializes the connections for AutoWeaponProfFacet to other facets.
	 * 
	 * This method is automatically called by the Spring framework during
	 * initialization of the AutoWeaponProfFacet.
	 */
	public void init()
	{
		consolidationFacet.addDataFacetChangeListener(this);
	}
}
