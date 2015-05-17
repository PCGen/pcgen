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
package plugin.format;

import pcgen.base.util.Indirect;
import pcgen.base.util.ObjectContainer;
import pcgen.core.PCAlignment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.types.FormatManager;

/**
 * A StatManager is a FormatManager for dealing with PCAlignment objects
 */
public class AlignmentManager implements FormatManager<PCAlignment>
{
	private static final Class<PCAlignment> PCALIGNMENT_CLASS =
			PCAlignment.class;

	@Override
	public PCAlignment convert(LoadContext context, String statID)
	{
		return context.getReferenceContext().silentlyGetConstructedCDOMObject(
			PCALIGNMENT_CLASS, statID);
	}

	@Override
	public Indirect<PCAlignment> convertIndirect(LoadContext context,
		String statID)
	{
		return context.getReferenceContext().getCDOMReference(
			PCALIGNMENT_CLASS, statID);
	}

	@Override
	public ObjectContainer<PCAlignment> convertObjectContainer(
		LoadContext context, String s)
	{
		return TokenUtilities.getTypeOrPrimitive(context.getReferenceContext()
			.getManufacturer(PCAlignment.class), s);
	}

	@Override
	public String unconvert(PCAlignment stat)
	{
		return stat.getKeyName();
	}

	@Override
	public Class<PCAlignment> getType()
	{
		return PCALIGNMENT_CLASS;
	}

	@Override
	public String getIdentifierType()
	{
		return "ALIGNMENT";
	}

	@Override
	public int hashCode()
	{
		return -231112;
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof BooleanManager;
	}
}
