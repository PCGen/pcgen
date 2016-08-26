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
import pcgen.cdom.facet.model.VarScopedFacet;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;

/**
 * RemoteModifierFacet is a Facet that tracks remove Modifiers that have been
 * granted to a Player Character by looking for MODIFYOTHER: entries on
 * CDOMObjects added to/removed from the Player Character.
 */
public class RemoteModifierFacet extends
		AbstractAssociationFacet<CharID, RemoteModifier<?>, Object> implements
		DataFacetChangeListener<CharID, VarScoped>
{

	private VarScopedFacet varScopedFacet;

	private SolverManagerFacet solverManagerFacet;

	@Override
	public void dataAdded(DataFacetChangeEvent<CharID, VarScoped> dfce)
	{
		CharID id = dfce.getCharID();
		VarScoped vs = dfce.getCDOMObject();
		/*
		 * If this can have local variables, find what may have been modified by
		 * previous objects
		 */
		getSet(id).forEach(rm ->
		{
			Object src = get(id, rm);
			processAdd(id, rm, vs, src);
			if (vs instanceof Equipment)
			{
				Equipment e = (Equipment) vs;
				e.getEquipmentHeads().forEach(head ->
				{
					processAdd(id, rm, head, src);
				});
			}
		});
		/*
		 * Look at what newly added object can modify on others
		 */
		if (vs instanceof CDOMObject)
		{
			List<RemoteModifier<?>> list =
					((CDOMObject) vs).getListFor(ListKey.REMOTE_MODIFIER);
			if (list != null)
			{
				Set<? extends VarScoped> targets = varScopedFacet.getSet(id);
				//Apply to existing as necessary
				list.forEach(rm ->
				{
					set(id, rm, vs);
					//Apply to existing as necessary
					targets.forEach(obj ->
					{
						processAdd(id, rm, obj, vs);
						if (obj instanceof Equipment)
						{
							Equipment e = (Equipment) obj;
							e.getEquipmentHeads().forEach(head ->
							{
								processAdd(id, rm, head, vs);
							});
						}
					});
				});
			}
		}
	}

	private <MT> void processAdd(CharID id, RemoteModifier<MT> rm,
		VarScoped vs, Object src)
	{
		if (rm.getGrouping().contains(vs))
		{
			VarModifier<MT> vm = rm.getVarModifier();
			solverManagerFacet.addModifier(id, vm, vs, src);
		}
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<CharID, VarScoped> dfce)
	{
		CharID id = dfce.getCharID();
		VarScoped vs = dfce.getCDOMObject();
		/*
		 * If this can have local variables, find what had been modified by
		 * previous objects
		 */
		getSet(id).forEach(rm ->
		{
			Object src = get(id, rm);
			processRemove(id, rm, vs, src);
			if (vs instanceof Equipment)
			{
				Equipment e = (Equipment) vs;
				e.getEquipmentHeads().forEach(head ->
				{
					processRemove(id, rm, head, src);
				});
			}
		});
		/*
		 * Look at what newly added object can modify on others
		 */
		if (vs instanceof CDOMObject)
		{
			List<RemoteModifier<?>> list =
					((CDOMObject) vs).getListFor(ListKey.REMOTE_MODIFIER);
			if (list != null)
			{
				Set<? extends VarScoped> targets = varScopedFacet.getSet(id);
				//RemoveFrom existing as necessary
				list.forEach(rm ->
				{
					remove(id, rm);
					//RemoveFrom existing as necessary
					targets.forEach(obj ->
					{
						processRemove(id, rm, obj, vs);
						if (obj instanceof Equipment)
						{
							Equipment e = (Equipment) obj;
							e.getEquipmentHeads().forEach(head ->
							{
								processRemove(id, rm, head, vs);
							});
						}
					});
				});
			}
		}
	}

	private <MT> void processRemove(CharID id, RemoteModifier<MT> rm,
		VarScoped vs, Object src)
	{
		if (rm.getGrouping().contains(vs))
		{
			VarModifier<MT> vm = rm.getVarModifier();
			solverManagerFacet.removeModifier(id, vm, vs, src);
		}
	}

	public void setVarScopedFacet(VarScopedFacet varScopedFacet)
	{
		this.varScopedFacet = varScopedFacet;
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
		varScopedFacet.addDataFacetChangeListener(this);
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
