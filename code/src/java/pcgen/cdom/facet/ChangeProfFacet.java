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
import pcgen.cdom.content.ChangeProf;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.core.Globals;
import pcgen.core.WeaponProf;
import pcgen.rules.context.ReferenceContext;

/**
 * ChangeProfFacet is a Facet that tracks the ChangeProf objects that are
 * contained in a Player Character.
 */
public class ChangeProfFacet extends AbstractSourcedListFacet<ChangeProf>
		implements DataFacetChangeListener<CDOMObject>
{

	private CDOMObjectConsolidationFacet consolidationFacet;

	/**
	 * Triggered when one of the Facets to which ChangeProfFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
	 * Character.
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
		List<ChangeProf> list = cdo.getListFor(ListKey.CHANGEPROF);
		if (list != null)
		{
			addAll(dfce.getCharID(), list, cdo);
		}
	}

	/**
	 * Triggered when one of the Facets to which ChangeProfFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
	 * Character.
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

	public List<WeaponProf> getWeaponProfsInTarget(String type, CharID id,
			CDOMGroupRef<WeaponProf> master)
	{
		ReferenceContext ref = Globals.getContext().ref;
		List<WeaponProf> aList = new ArrayList<WeaponProf>();
		// Can't use master because late called references may not have been
		// initialized, see 2001287
		Collection<WeaponProf> weaponProfsOfType = Globals.getPObjectsOfType(
				ref.getConstructedCDOMObjects(WeaponProf.class), type);
		for (ChangeProf cp : getSet(id))
		{
			if (cp.getResult().equals(master))
			{
				aList.addAll(cp.getSource().getContainedObjects());
			}
			else if (weaponProfsOfType != null)
			{
				weaponProfsOfType.removeAll(cp.getSource()
						.getContainedObjects());
			}
		}
		aList.addAll(weaponProfsOfType);
		return aList;
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
