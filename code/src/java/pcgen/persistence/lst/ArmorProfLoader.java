/*
 * WeaponProfLoader.java
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
 * $Id: WeaponProfLoader.java 3827 2007-08-20 21:42:58Z thpr $
 */
package pcgen.persistence.lst;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.ArmorProf;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public final class ArmorProfLoader extends LstObjectFileLoader<ArmorProf>
{
	/** Creates a new instance of ArmorProfLoader */
	public ArmorProfLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(LoadContext, pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	@Override
	public ArmorProf parseLine(LoadContext context, ArmorProf aWP,
		String lstLine, CampaignSourceEntry source) throws PersistenceLayerException
	{
		ArmorProf prof = aWP;

		// Make sure we have a weapon prof to load
		if (prof == null)
		{
			prof = new ArmorProf();
		}

		final StringTokenizer colToken =
				new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		int col = 0;

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(ArmorProfLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch (Exception e)
			{
				// TODO Handle Exception
			}
			ArmorProfLstToken token = (ArmorProfLstToken) tokenMap.get(key);

			if (col == 0) // First column is name, without a tag
			{
				prof.setName(colString);
				prof.setSourceCampaign(source.getCampaign());
				prof.setSourceURI(source.getURI());
			}
			else if (token != null)
			{
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, prof, value);
				if (!token.parse(prof, value))
				{
					Logging.errorPrint("Error parsing skill "
						+ prof.getDisplayName() + ':' + source.getURI() + ':'
						+ colString + "\"");
				}
			}
			else if (PObjectLoader.parseTag(prof, colString))
			{
				continue;
			}
			else
			{
				Logging.errorPrint("Illegal armor proficiency info '"
					+ lstLine + "' in " + source.toString());
			}

			++col;
		}

		// WeaponProfs are one line each;
		// finish the object and return null
		completeObject(source, prof);

		return null;
	}

	/**
	 * Get the weapon prof object with key aKey
	 * 
	 * @param aKey 
	 * @return PObject
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectKeyed(java.lang.String)
	 */
	@Override
	protected ArmorProf getObjectKeyed(String aKey)
	{
		return Globals.getArmorProfKeyed(aKey);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	@Override
	protected void performForget(final ArmorProf objToForget)
	{
		Globals.removeArmorProfKeyed(objToForget.getKeyName());
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#addGlobalObject(pcgen.core.PObject)
	 */
	@Override
	protected void addGlobalObject(final PObject pObj)
	{
		Globals.addArmorProf((ArmorProf) pObj);
		// TODO - What exactly is this doing?  Why would we set that it is not
		// a new item when we just added it?
		pObj.setNewItem(false);
	}
}
