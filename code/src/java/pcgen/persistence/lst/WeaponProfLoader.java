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
 * $Id$
 */
package pcgen.persistence.lst;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.WeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class WeaponProfLoader extends LstObjectFileLoader
{
	/** Creates a new instance of WeaponProfLoader */
	public WeaponProfLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	public PObject parseLine(PObject target, String lstLine, CampaignSourceEntry source)
		throws PersistenceLayerException
	{
		// Make sure we have a weapon prof to load
		WeaponProf prof = (WeaponProf) target;

		if (target == null)
		{
			prof = new WeaponProf();
		}

		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		int col = 0;

		Map tokenMap = TokenStore.inst().getTokenMap(WeaponProfLstToken.class);
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
			WeaponProfLstToken token = (WeaponProfLstToken) tokenMap.get(key);

			if (col == 0) // First column is name, without a tag
			{
				prof.setName(colString);
				prof.setKeyName(colString);
				prof.setSourceCampaign(source.getCampaign());
				prof.setSourceFile(source.getFile());
			}
			else if (token != null)
			{
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, prof, value);
				if (!token.parse(prof, value))
				{
					Logging.errorPrint("Error parsing skill " + prof.getDisplayName() + ':' + source.getFile() + ':' + colString + "\"");
				}
			}
			else if (PObjectLoader.parseTag(prof, colString))
			{
				continue;
			}
			else
			{
				Logging.errorPrint("Illegal weapon proficiency info '" + lstLine + "' in " + source.toString());
			}

			++col;
		}

		// WeaponProfs are one line each;
		// finish the object and return null
		finishObject(prof);

		return null;
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectNamed(java.lang.String)
	 */
	protected PObject getObjectKeyed(String aKey)
	{
		return Globals.getWeaponProfKeyed(aKey);
	}

	protected void finishObject(PObject target)
	{
		if (includeObject(target) && (target.getKeyName().length() > 0))
		{
			final WeaponProf wpFromFile = (WeaponProf) target;
			final WeaponProf wp = Globals.getWeaponProfKeyed(wpFromFile.getKeyName());

			if (wp == null)
			{
				Globals.addWeaponProf(wpFromFile);
				target.setNewItem(false);
			}
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	protected void performForget(PObject objToForget)
	{
		Globals.removeWeaponProfKeyed(objToForget.getKeyName());
	}
}
