/*
 * SubstitutionClassLoader.java
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
 * $Id: SubstitutionClassLoader.java 197 2006-03-14 22:59:43 +0000 (Tue, 14 Mar 2006) nuance $
 */
package pcgen.persistence.lst;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.PObject;
import pcgen.core.SubstitutionClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 197 $
 */
public final class SubstitutionClassLoader
{
	private static PCClassLoader classLoader = new PCClassLoader();

	/** Creates a new instance of PCClassLoader */
	private SubstitutionClassLoader()
	{
	    // TODO: Exception needs to be handled
	}

	/**
	 * This method is static so it can be used by PCClassLoader.
	 * @param target
	 * @param lstLine
	 * @param source
	 * @return PObject - substitutionclass
	 * @throws PersistenceLayerException
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	public static PObject parseLine(PObject target, String lstLine, CampaignSourceEntry source)
		throws PersistenceLayerException
	{
		SubstitutionClass substitutionclass = (SubstitutionClass) target;

		if (substitutionclass == null)
		{
			return substitutionclass;
		}

		if (!lstLine.startsWith("SUBSTITUTIONCLASS:"))
		{
			return substitutionclass;
		}

		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(SubstitutionClassLstToken.class);
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
			SubstitutionClassLstToken token = (SubstitutionClassLstToken) tokenMap.get(key);

			if (token != null)
			{
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, substitutionclass, value);
				if (!token.parse(substitutionclass, value))
				{
					Logging.errorPrint("Error parsing ability " + substitutionclass.getDisplayName() + ':' + source.getFile() + ':' + colString + "\"");
				}
			}
			else
			{
				classLoader.parseLine(substitutionclass, colString, source);
			}
		}

		return substitutionclass;
	}
}
