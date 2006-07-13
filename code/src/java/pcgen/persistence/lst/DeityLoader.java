/*
 * DeityLoader.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on February 22, 2002, 10:29 PM
 *
 * $Id$
 */
package pcgen.persistence.lst;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Deity;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * This class is an LstObjectLoader that loads deity information.
 *
 * @author David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public class DeityLoader extends LstObjectFileLoader
{
	/**
	 * Creates a new instance of DeityLoader
	 */
	public DeityLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	public PObject parseLine(PObject target, String lstLine, CampaignSourceEntry source)
		throws PersistenceLayerException
	{
		Deity deity = (Deity) target;

		if (deity == null)
		{
			deity = new Deity();
		}

		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		int col = 0;

		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(DeityLstToken.class);
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
			DeityLstToken token = (DeityLstToken) tokenMap.get(key);
			if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, deity, value);
				if (!token.parse(deity, value))
				{
					Logging.errorPrint("Error parsing deity " + deity.getDisplayName() + ':' + source.getFile() + ':' + colString + "\"");
				}
			}
			else if (PObjectLoader.parseTag(deity, colString))
			{
				continue;
			}
			else if ((col >= 0) && (col < 6))
			{
				switch (col)
				{
					case 0:

						if ((!colString.equals(deity.getKeyName())) && (colString.indexOf(".MOD") < 0))
						{
							finishObject(deity);
							deity = new Deity();
							deity.setName(colString);
							deity.setSourceCampaign(source.getCampaign());
							deity.setSourceFile(source.getFile());
						}

						break;

					default:
						Logging.errorPrint("In DeityLoader.parseLine the column " + col + " is not possible.");

						break;
				}

				col++;
			}
			else
			{
				Logging.errorPrint("Illegal deity info '" + colString + "' in " + source.getFile());
			}
		}

		return deity;
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectNamed(java.lang.String)
	 */
	protected PObject getObjectKeyed(String aKey)
	{
		return Globals.getDeityKeyed(aKey);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#finishObject(pcgen.core.PObject)
	 */
	protected void finishObject(PObject target)
	{
		if (target == null)
		{
			return;
		}
		if (includeObject(target))
		{
			if (Globals.getDeityKeyed(target.getKeyName()) == null)
			{
				Globals.getDeityList().add((Deity)target);
			}
		}
		else
		{
			excludedObjects.add(target.getKeyName());
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	protected void performForget(PObject objToForget)
	{
		Globals.getDeityList().remove(objToForget);
	}
}
