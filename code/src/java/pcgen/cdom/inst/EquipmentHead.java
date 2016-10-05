/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.inst;

import java.util.List;

import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VarScoped;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.VarModifier;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.ScopeFacet;
import pcgen.cdom.facet.SolverManagerFacet;
import pcgen.cdom.facet.analysis.ResultFacet;
import pcgen.core.EquipmentModifier;

/**
 * An EquipmentHead is a CDOMObject that represents characteristics of a single
 * "head" of a weapon. It is possible for a weapon to have more than one "head",
 * such as a Double Axe.
 */
public final class EquipmentHead extends CDOMObject
{
	private static final SolverManagerFacet SOLVER_FACET = FacetLibrary
			.getFacet(SolverManagerFacet.class);
	private static final ScopeFacet SCOPE_FACET = FacetLibrary.getFacet(ScopeFacet.class);

	/*
	 * Note: The equality issue referenced below (and the reason for the
	 * headSource field) doesn't necessarily present itself within PCGen at SVN
	 * 6700, but it is an issue if this is ever stored in a Graph, as the
	 * EquipmentHead from two pieces of Equipment must not be shared, in case
	 * .MODs are applied.
	 */
	/**
	 * The source of this EquipmentHead; used to establish equality
	 */
	private final VarScoped headSource;

	/**
	 * The index (location) of this Head on the Equipment
	 */
	private final int index;

	/**
	 * Creates a new EquipmentHead with the given source and index.
	 * 
	 * @param source
	 *            The source object for this EquipmentHead
	 * @param idx
	 *            The index of this EquipmentHead for the given source
	 * @throws IllegalArgumentException
	 *             if the given source is null
	 */
	public EquipmentHead(VarScoped source, int idx)
	{
		if (source == null)
		{
			throw new IllegalArgumentException(
				"Source for EquipmentHead cannot be null");
		}
		index = idx;
		headSource = source;
	}

	/**
	 * Returns the index (location) of this Head on the Equipment
	 * 
	 * @return the index (location) of this Head on the Equipment
	 */
	public int getHeadIndex()
	{
		return index;
	}

	/**
	 * Returns the consistent-with-equals hashCode for this EquipmentHead
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return index ^ headSource.hashCode();
	}

	public Object getOwner()
	{
		return headSource;
	}

	/**
	 * Returns true if this EquipmentHead is equal to the given Object. Equality
	 * is defined as being another EquipmentHead object with equal CDOM
	 * characteristics
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (!(obj instanceof EquipmentHead))
		{
			return false;
		}
		EquipmentHead other = (EquipmentHead) obj;
		return other.index == index && other.headSource.equals(headSource);
	}

	/**
	 * Returns true if the EquipmentHead is of the given Type; false otherwise.
	 * 
	 * @see pcgen.cdom.base.CDOMObject#isType(java.lang.String)
	 */
	@Override
	public boolean isType(String type)
	{
		return false;
	}

	public void removeVarModifiers(CharID id, EquipmentModifier aMod)
	{
		List<VarModifier<?>> modifiers = aMod.getListFor(ListKey.MODIFY);
		if (modifiers != null)
		{
			ScopeInstance inst = SCOPE_FACET.get(id, aMod.getLocalScopeName(), aMod);
			for (VarModifier<?> vm : modifiers)
			{
				SOLVER_FACET.addModifier(id, vm, this, inst);
			}
		}
	}

	public void addVarModifiers(CharID id, EquipmentModifier aMod)
	{
		List<VarModifier<?>> modifiers = aMod.getListFor(ListKey.MODIFY);
		if (modifiers != null)
		{
			ScopeInstance inst = SCOPE_FACET.get(id, aMod.getLocalScopeName(), aMod);
			for (VarModifier<?> vm : modifiers)
			{
				SOLVER_FACET.addModifier(id, vm, this, inst);
			}
		}
	}

	@Override
	public String getLocalScopeName()
	{
		return "EQUIPMENT.PART";
	}

	@Override
	public VarScoped getVariableParent()
	{
		return headSource;
	}

	public Object getLocalVariable(CharID id, String varName)
	{
		ResultFacet resultFacet = FacetLibrary.getFacet(ResultFacet.class);
		return resultFacet.getLocalVariable(id, this, varName);
	}

}
