/*
 * Copyright 2010 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 */
package plugin.primitive.equipment;

import java.util.Collection;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Converter;
import pcgen.cdom.base.PrimitiveFilter;
import pcgen.cdom.enumeration.EqWield;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;
import pcgen.util.Logging;

/**
 * WieldCategoryToken is a Primitive that filters based on the Wield Category of a piece
 * of Equipment (weapon).
 */
public class WieldCategoryToken implements PrimitiveToken<Equipment>, PrimitiveFilter<Equipment>
{
	private static final Class<Equipment> EQUIPMENT_CLASS = Equipment.class;
	private EqWield category;
	private CDOMReference<Equipment> allEquipment;

	@Override
	public boolean initialize(LoadContext context, Class<Equipment> cl, String value, String args)
	{
		if (args != null)
		{
			return false;
		}
		if ("Light".equalsIgnoreCase(value))
		{
			category = EqWield.Light;
		}
		else if ("1 handed".equalsIgnoreCase(value))
		{
			category = EqWield.OneHanded;
		}
		else if ("2 handed".equalsIgnoreCase(value))
		{
			category = EqWield.TwoHanded;
		}
		else if ("onehanded".equalsIgnoreCase(value))
		{
			category = EqWield.OneHanded;
		}
		else if ("twohanded".equalsIgnoreCase(value))
		{
			category = EqWield.TwoHanded;
		}
		else
		{
			Logging.errorPrint("Unable to understand Wield Category: " + value);
			return false;
		}
		allEquipment = context.getReferenceContext().getCDOMAllReference(EQUIPMENT_CLASS);
		return true;
	}

	@Override
	public String getTokenName()
	{
		return "WIELD";
	}

	@Override
	public Class<Equipment> getReferenceClass()
	{
		return EQUIPMENT_CLASS;
	}

	@Override
	public String getLSTformat(boolean useAny)
	{
		return "WIELD=" + category;
	}

	@Override
	public boolean allow(PlayerCharacter pc, Equipment eq)
	{
		return category.checkWield(pc, eq);
	}

	@Override
	public GroupingState getGroupingState()
	{
		return GroupingState.ANY;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (obj instanceof WieldCategoryToken other)
		{
			return category.equals(other.category);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return category == null ? -13 : category.hashCode();
	}

	@Override
	public <R> Collection<? extends R> getCollection(PlayerCharacter pc, Converter<Equipment, R> c)
	{
		return c.convert(allEquipment, this);
	}
}
