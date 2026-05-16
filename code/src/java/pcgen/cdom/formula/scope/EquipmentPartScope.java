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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import pcgen.base.formula.base.ImplementedScope;
import pcgen.base.util.FormatManager;
import pcgen.rules.context.LoadContext;

/**
 * EquipmentPartScope represents the variable scope that is on each Equipment
 * Part (currently called a Head in the code). This is necessary because certain
 * items (such as tohit value) are local to a head, not the equipment.
 */
public class EquipmentPartScope implements PCGenScope
{
	/**
	 * Full scope name as used in PCGen.
	 */
	public static final String PC_EQUIPMENT_PART = "PC.EQUIPMENT.PART";

	/**
	 * The scopes this scope draws from (once loaded)
	 */
	private List<ImplementedScope> drawsFrom = Collections.emptyList();

	@Override
	public String getName()
	{
		return "PART";
	}

	@Override
	public boolean isGlobal()
	{
		return false;
	}

	@Override
	public List<ImplementedScope> drawsFrom()
	{
		return drawsFrom;
	}

	/**
	 * Sets the parent PCGenScope for this EquipmentPartScope.
	 *
	 * @param parent
	 *            The parent PCGenScope for this EquipmentPartScope
	 */
	public void setParent(PCGenScope parent)
	{
		this.drawsFrom = List.of(parent);
	}

	@Override
	public Optional<FormatManager<?>> getFormatManager(LoadContext context)
	{
		return Optional.empty();
	}
}
