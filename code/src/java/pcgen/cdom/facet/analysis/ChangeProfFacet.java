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
package pcgen.cdom.facet.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.ChangeProf;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.CDOMObjectConsolidationFacet;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.core.Globals;
import pcgen.core.WeaponProf;
import pcgen.rules.context.AbstractReferenceContext;

/**
 * ChangeProfFacet is a Facet that tracks the ChangeProf objects that are
 * contained in a Player Character.
 * 
 */
public class ChangeProfFacet extends AbstractSourcedListFacet<CharID, ChangeProf>
		implements DataFacetChangeListener<CharID, CDOMObject>
{

	private CDOMObjectConsolidationFacet consolidationFacet;

	/**
	 * Determines ChangeProf objects granted by CDOMObjects which are added to a
	 * Player Character.
	 * 
	 * Triggered when one of the Facets to which ChangeProfFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		CDOMObject cdo = dfce.getCDOMObject();
		List<ChangeProf> list = cdo.getListFor(ListKey.CHANGEPROF);
		if (list != null)
		{
			addAll(dfce.getCharID(), list, cdo);
		}
	}

	/**
	 * Determines ChangeProf objects granted by CDOMObjects which are removed
	 * from a Player Character.
	 * 
	 * Triggered when one of the Facets to which ChangeProfFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		removeAll(dfce.getCharID(), dfce.getCDOMObject());
	}

	/**
	 * For a given type of WeaponProf, returns a List of WeaponProf objects
	 * active for a Player Character after they are modified by the ChangeProf
	 * objects active on the Player Character.
	 * 
	 * This method is value-semantic in that ownership of the returned List is
	 * transferred to the class calling this method. Modification of the
	 * returned List will not modify this ChangeProfFacet and modification of
	 * this ChangeProfFacet will not modify the returned List. Modifications to
	 * the returned List will also not modify any future or previous objects
	 * returned by this (or other) methods on ChangeProfFacet. If you wish to
	 * modify the information stored in this ChangeProfFacet, you must use the
	 * add*() and remove*() methods of ChangeProfFacet.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the List
	 *            of WeaponProf objects will be returned
	 * @param master
	 *            The original group of WeaponProf objects to be modified by any
	 *            ChangeProf objects active on the Player Character.
	 * @return A List of WeaponProf objects active for a Player Character, after
	 *         they are modified by the ChangeProf objects active on the Player
	 *         Character
	 */
	public List<WeaponProf> getWeaponProfsInTarget(CharID id, CDOMGroupRef<WeaponProf> master)
	{
		String type = master.getLSTformat(false);
		if (!type.startsWith("TYPE="))
		{
			throw new IllegalArgumentException("Cannot get targets for: " + type);
		}
		AbstractReferenceContext ref = Globals.getContext().getReferenceContext();
		List<WeaponProf> aList = new ArrayList<>();
		// Can't use master because late called references may not have been
		// initialized, see 2001287
		Collection<WeaponProf> weaponProfsOfType =
				Globals.getPObjectsOfType(ref.getConstructedCDOMObjects(WeaponProf.class), type);
		for (ChangeProf cp : getSet(id))
		{
			if (cp.getResult().equals(master))
			{
				aList.addAll(cp.getSource().getContainedObjects());
			}
			else
            {
                weaponProfsOfType.removeAll(cp.getSource().getContainedObjects());
            }
		}
		aList.addAll(weaponProfsOfType);
		return aList;
	}

	public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
	{
		this.consolidationFacet = consolidationFacet;
	}

	/**
	 * Initializes the connections for ChangeProfFacet to other facets.
	 * 
	 * This method is automatically called by the Spring framework during
	 * initialization of the ChangeProfFacet.
	 */
	public void init()
	{
		consolidationFacet.addDataFacetChangeListener(this);
	}
}
