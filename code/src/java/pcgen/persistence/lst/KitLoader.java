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
 * $Id$
 */
package pcgen.persistence.lst;

import java.net.URL;
import java.util.Map;

import pcgen.core.Kit;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

/**
 *
 * ???
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
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
		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(KitLstToken.class);

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
				Logging.errorPrint("Error parsing Kit tag " + obj.getDisplayName() + ':' + sourceURL.getFile() + ':' + inputLine + "\"");
			}
		}
		else
		{
			Logging.errorPrint("Unknown kit info " + sourceURL.toString() + ":" + Integer.toString(lineNum) + " \""
				+ inputLine + "\"");
		}
	}
}
