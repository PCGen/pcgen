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
import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.types.FormatManager;

/**
 * A StatManager is a FormatManager for dealing with PCStat objects
 */
public class StatManager implements FormatManager<PCStat>
{
	private static final Class<PCStat> PCSTAT_CLASS = PCStat.class;

	@Override
	public PCStat convert(LoadContext context, String statID)
	{
		return context.getReferenceContext().silentlyGetConstructedCDOMObject(
			PCSTAT_CLASS, statID);
	}

	@Override
	public Indirect<PCStat> convertIndirect(LoadContext context, String statID)
	{
		return context.getReferenceContext().getCDOMReference(PCSTAT_CLASS,
			statID);
	}

	@Override
	public ObjectContainer<PCStat> convertObjectContainer(LoadContext context,
		String s)
	{
		return TokenUtilities.getTypeOrPrimitive(context.getReferenceContext()
			.getManufacturer(PCStat.class), s);
	}

	@Override
	public String unconvert(PCStat stat)
	{
		return stat.getKeyName();
	}

	@Override
	public Class<PCStat> getType()
	{
		return PCSTAT_CLASS;
	}

	@Override
	public String getIdentifierType()
	{
		return "STAT";
	}


	@Override
	public int hashCode()
	{
		return -7342;
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof StatManager;
	}
}
