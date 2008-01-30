/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.auto;

import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.persistence.lst.AutoLstToken;
import pcgen.util.Logging;

public class ArmorProfToken implements AutoLstToken
{

	public String getTokenName()
	{
		return "ARMORPROF";
	}

	public boolean parse(PObject target, String value, int level)
	{
		if (level > 1)
		{
			Logging.errorPrint("AUTO:" + getTokenName()
					+ " is not supported on class level lines");
			return false;
		}
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		while (st.hasMoreTokens())
		{
			if (st.nextToken().startsWith("TYPE"))
			{
				Logging.deprecationPrint("TYPE= in AUTO:ARMORPROF is "
						+ "deprecated.  Use ARMORTYPE=");
				break;
			}
		}
		target.addAutoArray(getTokenName(), value);
		return true;
	}

}
