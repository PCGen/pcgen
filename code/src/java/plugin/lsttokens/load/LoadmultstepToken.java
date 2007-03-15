/*
 * LoadmultstepToken.java
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
 * Created on March 14, 2007
 *
 * Current Ver: $Revision: 1777 $
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006-12-16 23:36:01 -0500 (Sat, 16 Dec 2006) $
 *
 */
package plugin.lsttokens.load;

import pcgen.core.system.LoadInfo;
import pcgen.persistence.lst.LoadInfoLstToken;

public class LoadmultstepToken implements LoadInfoLstToken
{

	public String getTokenName()
	{
		return "LOADMULTSTEP";
	}

	public boolean parse(LoadInfo loadInfo, String value)
	{
		try
		{
			loadInfo.setLoadMultStep(Integer.parseInt(value));
		}
		catch (Exception e)
		{
			return false;
		}
		return true;
	}
}
