/*
 * PCAlignment.java
 * Copyright 2002 (C) Greg Bingleman (byngl@hotmail.com)
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
 * Created on October 14, 2002 10:01PM
 */
package pcgen.core;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.facade.AlignmentFacade;

/**
 * <code>PCAlignment</code>.
 * 
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public final class PCAlignment extends PObject implements AlignmentFacade
{
	public String getAbb()
	{
		return get(StringKey.ABB);
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (obj instanceof PCAlignment)
		{
			return getAbb().equals(((PCAlignment) obj).getAbb());
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		String abb = getAbb();
		return abb == null ? 0 : getAbb().hashCode();
	}

	public String getName()
	{
		return getDisplayName();
	}

	public String getAbbreviation()
	{
		return getKeyName();
	}
}
