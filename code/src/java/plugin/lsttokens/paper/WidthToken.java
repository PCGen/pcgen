/*
 * WidthToken.java
 * Copyright 2006 (C) Devon Jones <soulcatcher@evilsoft.org>
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
 * Created on September 2, 2002, 8:02 AM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.lsttokens.paper;

import pcgen.cdom.base.Constants;
import pcgen.core.PaperInfo;
import pcgen.persistence.lst.PaperInfoLstToken;

/**
 * <code>WidthToken</code>
 *
 * @author  Devon Jones <soulcatcher@evilsoft.org>
 */
public class WidthToken implements PaperInfoLstToken
{

	/**
	 * Get token name
	 * @return token name
	 */
	public String getTokenName()
	{
		return "WIDTH";
	}

	/**
	 * Parse WIDTH token
	 * 
	 * @param paperInfo 
	 * @param value 
	 * @return true 
	 */
	public boolean parse(PaperInfo paperInfo, String value)
	{
		paperInfo.setPaperInfo(Constants.PAPERINFO_WIDTH, value);
		return true;
	}
}
