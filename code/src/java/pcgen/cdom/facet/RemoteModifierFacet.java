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

import java.util.List;
import java.util.Set;

import pcgen.base.formula.base.VarScoped;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.RemoteModifier;
import pcgen.cdom.content.VarModifier;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.base.AbstractAssociationFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;

/**
 * RemoteModifierFacet is a Facet that tracks remove Modifiers that have been
 * granted to a Player Character by looking for MODIFYOTHER: entries on
 * CDOMObjects added to/removed from the Player Character.
 */
public class RemoteModifierFacet extends
		AbstractAssociationFacet<CharID, RemoteModifier<?, ?>, Object>
		implements DataFacetChangeListener<CharID, CDOMObject>
{

	private CDOMObjectConsolidationFacet consolidationFacet;

	private SolverManagerFacet solverManagerFacet;

	@Override
	public void dataAdded(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		CharID id = dfce.getCharID();
		CDOMObject cdo = dfce.getCDOMObject();
		/*
		 * If this can have local variables, find what may have been modified by
		 * previous objects
		 */
		for (RemoteModifier<?, ?> rm : getSet(id))
		{
			Object src = get(id, rm);
			processAdd(id, rm, cdo, src);
			if (cdo instanceof Equipment)
			{
				Equipment e = (Equipment) cdo;
				for (EquipmentHead head : e.getEquipmentHeads())
				{
					processAdd(id, rm, head, src);
				}
			}
		}
		/*
		 * Now look at what newly added object can modify on others
		 */
		List<RemoteModifier<?, ?>> list =
				cdo.getListFor(ListKey.REMOTE_MODIFIER);
		if (list != null)
		{
			Set<CDOMObject> cdomObjects = consolidationFacet.getSet(id);
			for (RemoteModifier<?, ?> rm : list)
			{
				set(id, rm, cdo);
				//Apply to existing as necessary
				for (CDOMObject obj : cdomObjects)
				{
					processAdd(id, rm, obj, cdo);
					if (obj instanceof Equipment)
					{
						Equipment e = (Equipment) obj;
						for (EquipmentHead head : e.getEquipmentHeads())
						{
							processAdd(id, rm, head, cdo);
						}
					}
				}
			}
		}
	}

	private <GT extends CDOMObject & VarScoped, MT> void processAdd(CharID id,
		RemoteModifier<GT, MT> rm, CDOMObject cdo, Object src)
	{
		if (rm.getGroupClass().isAssignableFrom(cdo.getClass()))
		{
			@SuppressWarnings("unchecked")
			GT vs = (GT) cdo;
			if (rm.grouping.contains(vs))
			{
				VarModifier<MT> vm = rm.varModifier;
				solverManagerFacet.addModifier(id, vm, vs, src);
			}
		}
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<CharID, CDOMObject> dfce)
	{
		CharID id = dfce.getCharID();
		CDOMObject cdo = dfce.getCDOMObject();
		/*
		 * If this can have local variables, find what had been modified by
		 * previous objects
		 */
		for (RemoteModifier<?, ?> rm : getSet(id))
		{
			Object src = get(id, rm);
			processRemove(id, rm, cdo, src);
			if (cdo instanceof Equipment)
			{
				Equipment e = (Equipment) cdo;
				for (EquipmentHead head : e.getEquipmentHeads())
				{
					processRemove(id, rm, head, src);
				}
			}
		}
		/*
		 * Now look at what newly removed object had modified on others
		 */
		List<RemoteModifier<?, ?>> list =
				cdo.getListFor(ListKey.REMOTE_MODIFIER);
		if (list != null)
		{
			Set<CDOMObject> cdomObjects = consolidationFacet.getSet(id);
			for (RemoteModifier<?, ?> rm : list)
			{
				remove(id, rm);
				//RemoveFrom existing as necessary
				for (CDOMObject obj : cdomObjects)
				{
					processRemove(id, rm, obj, cdo);
					if (obj instanceof Equipment)
					{
						Equipment e = (Equipment) obj;
						for (EquipmentHead head : e.getEquipmentHeads())
						{
							processRemove(id, rm, head, cdo);
						}
					}
				}
			}
		}
	}

	private <GT extends CDOMObject & VarScoped, MT> void processRemove(
		CharID id, RemoteModifier<GT, MT> rm, CDOMObject cdo, Object src)
	{
		if (rm.getGroupClass().isAssignableFrom(cdo.getClass()))
		{
			@SuppressWarnings("unchecked")
			GT vs = (GT) cdo;
			if (rm.grouping.contains(vs))
			{
				VarModifier<MT> vm = rm.varModifier;
				solverManagerFacet.removeModifier(id, vm, vs, src);
			}
		}
	}

	public void setConsolidationFacet(
		CDOMObjectConsolidationFacet consolidationFacet)
	{
		this.consolidationFacet = consolidationFacet;
	}

	public void setSolverManagerFacet(SolverManagerFacet solverManagerFacet)
	{
		this.solverManagerFacet = solverManagerFacet;
	}

	/**
	 * Initializes the connections for ArmorProfFacet to other facets.
	 * 
	 * This method is automatically called by the Spring framework during
	 * initialization of the ArmorProfFacet.
	 */
	public void init()
	{
		consolidationFacet.addDataFacetChangeListener(this);
	}

	/*
	 * In Ability: MODIFYOTHER:EQUIPMENT|GROUP=Martial|EqCritRange|ADD|1
	 * 
	 * In Global:
	 * MODIFYOTHER:EQUIPMENT.PART|ALL|CritRange|SOLVE|value()+EqCritRange
	 * |PRIORITY=10000
	 * 
	 * effectively we are solving APPLYTO:EQUIPMENT|PC[GROUP=Martial]|...
	 * 
	 * EQUIPMENT ends up in the Modifier as stDef
	 * 
	 * EqCritRange|ADD|1 also ends up in the VarModifier (as the varName and the
	 * Modifier itself)
	 * 
	 * type is getGroupClass()
	 * 
	 * 
	 * 
	 * GROUP=Martial is static
	 * 
	 * PC is dynamic and attached to a Facet
	 * 
	 * list(PC) && list(GROUP=Martial)
	 * 
	 * But what happens when something lands on the list?
	 * 
	 * Then we need to circle around & load local vars...
	 * 
	 * When New member of a certain VarID (e.g. Applied Equipment) Then take
	 * that member and get the VariableStore Look up in the database all
	 * VarModifiers to be applied merge VariableStore and VarModifiers...
	 */
}
