/*
 * AbilityToken.java
 * Copyright 2006 (C) Tom Parker <thpr at yahoo.com>
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
 * Created on January 11, 2007
 *
 * Current Ver: $Revision: 191 $
 * Last Editor: $Author: nuance $
 * Last Edited: $Date: 2006-03-14 17:16:52 -0500 (Tue, 14 Mar 2006) $
 */

package plugin.lsttokens.kit;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.Kit;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * Handles the (persistent) REGION tag for Kits.
 */
public class RegionToken extends AbstractToken implements CDOMSecondaryToken<Kit>
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "REGION";
	}

	public boolean parse(LoadContext context, Kit obj, String value)
		throws PersistenceLayerException
	{
		context.clearStatefulInformation();
		if (isEmpty(value))
		{
			//This is okay - just clears global tokens
			return true;
		}
		StringTokenizer st = new StringTokenizer(value, "\t");

		String region = st.nextToken();
		if (!region.equalsIgnoreCase(Constants.LST_NONE))
		{
			// Add a real prereq for the REGION: tag
			if (!context.addStatefulToken("PREREGION:" + region))
			{
				return false;
			}
		}

		if (st.hasMoreTokens())
		{
			String gt = st.nextToken();
			final int colonLoc = gt.indexOf(':');
			if (colonLoc == -1)
			{
				Logging.errorPrint("Invalid Token - does not contain a colon: "
					+ gt);
				return false;
			}
			else if (colonLoc == 0)
			{
				Logging
					.errorPrint("Invalid Token - starts with a colon: " + gt);
				return false;
			}

			String key = gt.substring(0, colonLoc);
			String val =
					(colonLoc == gt.length() - 1) ? null : gt
						.substring(colonLoc + 1);
			if (!context.processToken(obj, key, val))
			{
				return false;
			}
		}

		return false;
	}

	public String[] unparse(LoadContext context, Kit obj)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Class<Kit> getTokenClass()
	{
		return Kit.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

}
