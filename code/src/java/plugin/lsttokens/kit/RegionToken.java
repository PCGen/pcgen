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

import pcgen.core.Constants;
import pcgen.core.Kit;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.KitLoader;
import pcgen.persistence.lst.KitLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * Handles the (persistent) REGION tag for Kits.
 */
public class RegionToken extends KitLstToken
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "REGION";
	}

	/**
	 * Handles parsing the REGION tag for this Kit line.
	 * 
	 * @param aKit
	 *            ignored
	 * @param value
	 *            the token string
	 * @return true if parse OK
	 */
	@Override
	public boolean parse(Kit aKit, String value)
	{
		KitLoader.clearKitPrerequisites();
		KitLoader.clearGlobalTokens();
		
		if (value == null || value.length() == 0)
		{
			return true;
		}
		
		final StringTokenizer aTok = new StringTokenizer(value, "\t", false);
		
		String region = aTok.nextToken();

		if (!region.equalsIgnoreCase(Constants.s_NONE))
		{
			// Add a real prereq for the REGION: tag
			try
			{
				PreParserFactory factory = PreParserFactory.getInstance();
				Prerequisite p = factory.parse("PREREGION:" + region);
				KitLoader.setKitPrerequisite(p);
			}
			catch (PersistenceLayerException ple)
			{
				// TODO Deal with this Exception?
			}
		}

		if (aTok.hasMoreTokens())
		{
			KitLoader.addGlobalToken(aTok.nextToken());
		}
		return true;
	}
}
