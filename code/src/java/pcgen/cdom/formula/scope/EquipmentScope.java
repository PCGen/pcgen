/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.formula.scope;

import pcgen.base.formula.base.LegalScope;

/**
 * The EquipmentScope is for variables that are local to a piece of Equipment.
 * Note that certain items may be more appropriate on a specific part of the
 * Equipment (see EquipmentPartScope).
 */
public class EquipmentScope implements LegalScope
{
	/**
	 * The parent of this scope (once loaded)
	 */
	private LegalScope parent;

	/**
	 * The String representation of the objects covered by this Scope
	 * 
	 * @see pcgen.base.formula.base.LegalScope#getName()
	 */
	@Override
	public String getName()
	{
		return "EQUIPMENT";
	}

	/**
	 * @see pcgen.base.formula.base.LegalScope#getParentScope()
	 */
	@Override
	public LegalScope getParentScope()
	{
		return parent;
	}

	/**
	 * Sets the parent LegalScope for this EquipmentScope.
	 * 
	 * @param parent
	 *            The parent LegalScope for this EquipmentScope
	 */
	public void setParent(LegalScope parent)
	{
		this.parent = parent;
	}
}
