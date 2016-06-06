/*
 * AbilityScope.java
 * Copyright 2016 Connor Petty <cpmeister@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on Jun 5, 2016, 11:25:47 PM
 */
package pcgen.cdom.formula.scope;

import pcgen.base.formula.base.LegalScope;

/**
 * Defines a Scope that covers variables local to Ability objects
 */
public class AbilityScope implements LegalScope
{
	
	/**
	 * The parent of this scope (once loaded)
	 */
	private LegalScope parent;

	/**
	 * The String representation of the objects covered by this Scope
	 * 
	 * @see pcgen.cdom.base.LoadableLegalScope#getName()
	 */
	@Override
	public String getName()
	{
		return "ABILITY";
	}

	/**
	 * @see pcgen.base.formula.variable.LegalScope#getParentScope()
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
