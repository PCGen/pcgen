/*
 * KitLoader.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on September 23, 2002, 1:39 PM
 *
 * $Id: KitLoader.java,v 1.51 2006/02/17 02:50:07 boomer70 Exp $
 */
package pcgen.persistence.lst;

import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.kit.KitAbilities;
import pcgen.core.kit.KitAlignment;
import pcgen.core.kit.KitClass;
import pcgen.core.kit.KitDeity;
import pcgen.core.kit.KitFunds;
import pcgen.core.kit.KitGear;
import pcgen.core.kit.KitKit;
import pcgen.core.kit.KitProf;
import pcgen.core.kit.KitRace;
import pcgen.core.kit.KitSkill;
import pcgen.core.kit.KitSpells;
import pcgen.core.kit.KitStat;
import pcgen.core.kit.KitTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;
import pcgen.core.kit.KitSelect;
import pcgen.core.kit.BaseKit;
import pcgen.core.kit.KitLevelAbility;
import pcgen.core.kit.KitBio;
import java.util.Map;

/**
 *
 * ???
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.51 $
 */
final class KitLoader
{
	/** Creates a new instance of KitLoader */
	private KitLoader()
	{
		// Empty Constructor
	}

	/**
	 * parse the Kit in the data file
	 * @param obj
	 * @param inputLine
	 * @param sourceURL
	 * @param lineNum
	 * @throws PersistenceLayerException
	 */
	public static void parseLine(Kit obj, String inputLine, URL sourceURL, int lineNum)
		throws PersistenceLayerException
	{
		Map tokenMap = TokenStore.inst().getTokenMap(KitLstToken.class);

		// We will find the first ":" for the "controlling" line token
		final int idxColon = inputLine.indexOf(':');
		String key = "";
		try
		{
			key = inputLine.substring(0, idxColon);
		}
		catch(StringIndexOutOfBoundsException e) {
			// TODO Handle Exception
		}
		KitLstToken token = (KitLstToken) tokenMap.get(key);

		if (token != null)
		{
			final String value = inputLine.substring(idxColon + 1);
			LstUtils.deprecationCheck(token, obj, value);
			if (!token.parse(obj, value))
			{
				Logging.errorPrint("Error parsing Kit tag " + obj.getName() + ':' + sourceURL.getFile() + ':' + inputLine + "\"");
			}
		}
		else
		{
			Logging.errorPrint("Unknown kit info " + sourceURL.toString() + ":" + Integer.toString(lineNum) + " \""
				+ inputLine + "\"");
		}
	}

}
