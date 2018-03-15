/*
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.    See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core;

import pcgen.base.formula.base.VarScoped;
import pcgen.cdom.base.NonInteractive;
import pcgen.cdom.base.SortKeyRequired;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.facade.core.StatFacade;

public final class PCStat extends PObject implements StatFacade,
		NonInteractive, SortKeyRequired, VarScoped
{
	@Override
	public int getMinValue()
	{
		return getSafe(IntegerKey.MIN_VALUE);		
	}

	/*
	 * This is what the UI displays for the CHOOSE:PCSTAT.
	 */
	@Override
	public String toString()
	{
		return getKeyName();
	}

    @Override
	public String getName()
	{
		return getDisplayName();
	}

	@Override
	public String getLocalScopeName()
	{
		return "PC.STAT";
	}

	@Override
	public String getSortKey()
	{
		return get(StringKey.SORT_KEY);
	}
}
