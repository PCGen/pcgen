/*
 * PCStat.java
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
 *
 * Created on August 10, 2002, 11:58 PM
 */
package pcgen.core;

import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.facade.StatFacade;

/**
 * <code>PCStat</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class PCStat extends PObject implements StatFacade
{
	public String getAbb()
	{
		return get(StringKey.ABB);
	}

	@Override
	public String toString()
	{
		final StringBuffer sb = new StringBuffer(30);
		sb.append("stat:").append(getAbb()).append(' ');
		sb.append("formula:").append(getSafe(FormulaKey.STAT_MOD)).append(' ');
		boolean rolled = getSafe(ObjectKey.ROLLED);
		if (!rolled)
		{
			sb.append(' ').append("rolled:").append(rolled);
		}

		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.StatFacade#getAbbreviation()
	 */
	public String getAbbreviation()
	{
		return getAbb();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.StatFacade#getName()
	 */
	public String getName()
	{
		return getDisplayName();
	}
}
