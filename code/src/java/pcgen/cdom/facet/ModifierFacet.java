/*
 * Copyright (c) Thomas Parker, 2014
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

import java.util.List;

import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VarScoped;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.VarModifier;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;

/**
 * ModifierFacet checks each item added to a PlayerCharacter to see if it has
 * MODIFY: entries on the object, and if so, adds them to the Solver system.
 */
public class ModifierFacet implements
		DataFacetChangeListener<CharID, CDOMObject>
{
	private ScopeFacet scopeFacet;

	private CDOMObjectConsolidationFacet consolidationFacet;

	private SolverManagerFacet solverManagerFacet;

	/**
	 * Triggered when one of the Facets to which ModifierFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.event.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.event.DataFacetChangeEvent)
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		CharID id = dfce.getCharID();
		CDOMObject obj = dfce.getCDOMObject();
		List<VarModifier<?>> modifiers = obj.getListFor(ListKey.MODIFY);
		if (modifiers != null)
		{
			ScopeInstance inst = scopeFacet.get(id, obj);
			for (VarModifier<?> vm : modifiers)
			{
				processAddition(id, obj, vm, inst);
			}
		}
		if (obj instanceof Equipment)
		{
			Equipment equip = (Equipment) obj;
			for (EquipmentHead head : equip.getEquipmentHeads())
			{
				ScopeInstance inst = scopeFacet.get(id, head);
				modifiers = head.getListFor(ListKey.MODIFY);
				if (modifiers != null)
				{
					for (VarModifier<?> vm : modifiers)
					{
						processAddition(id, head, vm, inst);
					}
				}
			}
		}
	}

	private <T> void processAddition(CharID id, VarScoped obj, VarModifier<T> vm,
		ScopeInstance inst)
	{
		solverManagerFacet.addModifier(id, vm, obj, inst);
	}

	/**
	 * Triggered when one of the Facets to which ModifierFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
	 * Character.
	 * 
	 * Long term this method needs to be symmetric with dataAdded.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.event.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.event.DataFacetChangeEvent)
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		CharID id = dfce.getCharID();
		CDOMObject obj = dfce.getCDOMObject();
		List<VarModifier<?>> modifiers = obj.getListFor(ListKey.MODIFY);
		if (modifiers != null)
		{
			ScopeInstance inst = scopeFacet.get(id, obj);
			for (VarModifier<?> vm : modifiers)
			{
				processRemoval(id, obj, vm, inst);
			}
		}
		if (obj instanceof Equipment)
		{
			Equipment equip = (Equipment) obj;
			for (EquipmentHead head : equip.getEquipmentHeads())
			{
				ScopeInstance inst = scopeFacet.get(id, head);
				modifiers = head.getListFor(ListKey.MODIFY);
				if (modifiers != null)
				{
					for (VarModifier<?> vm : modifiers)
					{
						processRemoval(id, equip, vm, inst);
					}
				}
			}
		}
	}

	private <T> void processRemoval(CharID id, VarScoped obj, VarModifier<T> vm,
		ScopeInstance inst)
	{
		solverManagerFacet.removeModifier(id, vm, obj, inst);
	}

	public void setScopeFacet(ScopeFacet scopeFacet)
	{
		this.scopeFacet = scopeFacet;
	}

	public void setSolverManagerFacet(SolverManagerFacet solverManagerFacet)
	{
		this.solverManagerFacet = solverManagerFacet;
	}

	public void setConsolidationFacet(
		CDOMObjectConsolidationFacet consolidationFacet)
	{
		this.consolidationFacet = consolidationFacet;
	}

	/**
	 * Initializes the connections for ModifierFacet to other facets.
	 * 
	 * This method is automatically called by the Spring framework during
	 * initialization of the ModifierFacet.
	 */
	public void init()
	{
		consolidationFacet.addDataFacetChangeListener(this);
	}
}
