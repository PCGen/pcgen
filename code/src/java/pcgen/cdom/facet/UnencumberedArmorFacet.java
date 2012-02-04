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

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.util.enumeration.Load;

/**
 * UnencumberedArmorFacet is a Facet that tracks the Load objects for
 * Unencumbered Armor that have been locked on a Player Character.
 */
public class UnencumberedArmorFacet extends AbstractSourcedListFacet<Load>
		implements DataFacetChangeListener<CDOMObject>
{
	private CDOMObjectSourceFacet cdomSourceFacet;

	/**
	 * Triggered when one of the Facets to which UnencumberedArmorFacet listens
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
		Load load = cdo.get(ObjectKey.UNENCUMBERED_ARMOR);
		if (load != null)
		{
			add(dfce.getCharID(), load, cdo);
		}
	}

	/**
	 * Triggered when one of the Facets to which UnencumberedArmorFacet listens
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

	@Override
	protected Map<Load, Set<Object>> getComponentMap()
	{
		return new TreeMap<Load, Set<Object>>();
	}

	public Load getBestLoad(CharID id)
	{
		TreeMap<Load, Set<Object>> map = (TreeMap<Load, Set<Object>>) getCachedMap(id);
		if (map == null)
		{
			return Load.LIGHT;
		}
		return map.lastKey();
	}

	public boolean ignoreLoad(CharID id, Load load)
	{
		return getBestLoad(id).compareTo(load) >= 0;
	}

	public void setCdomSourceFacet(CDOMObjectSourceFacet cdomSourceFacet)
	{
		this.cdomSourceFacet = cdomSourceFacet;
	}

	public void init()
	{
		cdomSourceFacet.addDataFacetChangeListener(this);
	}
}
