/*
 * TableToken.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on March 3, 2006
 *
 * Current Ver: $Revision: $
 * Last Editor: $Author: $
 * Last Edited: $Date: $
 */
package plugin.lsttokens.kit;

import java.util.StringTokenizer;

import pcgen.core.Kit;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.persistence.lst.KitLstToken;

/**
 * This class parses a TABLE line from a Kit file.  It handles the TABLE
 * tag as well as the VALUES tag.  Common tags are not handled by this tag.<br>
 * The format of the line is:<br>
 * <code>TABLE:Special Ability (B)&nbsp; &nbsp; VALUES:EQMOD:ARW_CAT|1,20|LOOKUP:Special Ability
 */
public class TableToken extends KitLstToken
{
	/**
	 * Gets the name of the tag this class will parse.
	 *
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "TABLE";
	}

	/**
	 * Parses the TABLE line.  Handles the TABLE and VALUES tags.
	 *
	 * @param aKit the Kit object to add this information to
	 * @param value the token string
	 * @return true if parse OK
	 * @throws PersistenceLayerException
	 */
	public boolean parse(Kit aKit, String value)
		throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(value, SystemLoader.TAB_DELIM);
		String tableName = colToken.nextToken();
		aKit.addLookupTable(tableName);
		while (colToken.hasMoreTokens())
		{
			String colString = colToken.nextToken();
			if (colString.startsWith("VALUES:"))
			{
				colString = colString.substring(7);
				final StringTokenizer fieldToken = new StringTokenizer(colString, "|");
				while (fieldToken.hasMoreTokens())
				{
					String val = fieldToken.nextToken();
					String range = fieldToken.nextToken();
					int ind = -1;
					String lowVal;
					String highVal;
					if ((ind = range.indexOf(",")) != -1)
					{
						lowVal = range.substring(0, ind);
						highVal = range.substring(ind + 1);
					}
					else
					{
						lowVal = highVal = range;
					}
					aKit.addLookupValue(tableName, val, lowVal, highVal);
				}
			}
		}
		return true;
	}
}
