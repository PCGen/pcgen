/*
 * SubClassLoader.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on November 19, 2002, 10:29 PM
 *
 * $Id$
 */
package pcgen.persistence.lst;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.PObject;
import pcgen.core.SubClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class SubClassLoader
{
	private static PCClassLoader classLoader = new PCClassLoader();

	/** Creates a new instance of PCClassLoader */
	private SubClassLoader()
	{
		// TODO: Exception needs to be handled
	}

	/**
	 * This method is static so it can be used by PCClassLoader.
	 * @param target
	 * @param lstLine
	 * @param source
	 * @return PObject - subclass
	 * @throws PersistenceLayerException
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	public static PObject parseLine(PObject target, String lstLine, CampaignSourceEntry source)
		throws PersistenceLayerException
	{
		SubClass subclass = (SubClass) target;

		if (subclass == null)
		{
			return subclass;
		}

		if (!lstLine.startsWith("SUBCLASS:"))
		{
			return subclass;
		}

		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		Map tokenMap = TokenStore.inst().getTokenMap(SubClassLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch(Exception e) {
				// TODO Handle Exception
			}
			SubClassLstToken token = (SubClassLstToken) tokenMap.get(key);

			if (token != null)
			{
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, subclass, value);
				if (!token.parse(subclass, value))
				{
					Logging.errorPrint("Error parsing ability " + subclass.getDisplayName() + ':' + source.getFile() + ':' + colString + "\"");
				}
			}
			else
			{
				classLoader.parseLine(subclass, colString, source);
			}
		}

		return subclass;
	}
}
