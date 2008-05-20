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

import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.WeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class WeaponProfLoader extends LstObjectFileLoader<WeaponProf>
{
	/** Creates a new instance of WeaponProfLoader */
	public WeaponProfLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(LoadContext, pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	@Override
	public WeaponProf parseLine(LoadContext context, WeaponProf aWP,
		String lstLine, CampaignSourceEntry source) throws PersistenceLayerException
	{
		WeaponProf prof = aWP;

		// Make sure we have a weapon prof to load
		if (prof == null)
		{
			prof = new WeaponProf();
		}

		final StringTokenizer colToken =
				new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		if (colToken.hasMoreTokens())
		{
			prof.setName(colToken.nextToken());
			prof.setSourceCampaign(source.getCampaign());
			prof.setSourceURI(source.getURI());
		}

		while (colToken.hasMoreTokens())
		{
			final String token = colToken.nextToken().trim();
			final int colonLoc = token.indexOf(':');
			if (colonLoc == -1)
			{
				Logging.errorPrint("Invalid Token - does not contain a colon: "
						+ token);
				continue;
			}
			else if (colonLoc == 0)
 			{
				Logging.errorPrint("Invalid Token - starts with a colon: "
						+ token);
				continue;
 			}

			String key = token.substring(0, colonLoc);
			String value = (colonLoc == token.length() - 1) ? null : token
					.substring(colonLoc + 1);
			if (context.processToken(prof, key, value))
			{
				Logging.clearParseMessages();
				context.commit();
			}
			else if (PObjectLoader.parseTag(prof, token))
 			{
				Logging.clearParseMessages();
 				continue;
 			}
 			else
 			{
				Logging.rewindParseMessages();
				Logging.replayParsedMessages();
 			}
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
	protected WeaponProf getObjectKeyed(String aKey)
	{
		return Globals.getWeaponProfKeyed(aKey);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	@Override
	protected void performForget(final WeaponProf objToForget)
	{
		Globals.removeWeaponProfKeyed(objToForget.getKeyName());
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#addGlobalObject(pcgen.core.PObject)
	 */
	@Override
	protected void addGlobalObject(final PObject pObj)
	{
		Globals.addWeaponProf((WeaponProf) pObj);
		Globals.getContext().ref.importObject(pObj);
		// TODO - What exactly is this doing?  Why would we set that it is not
		// a new item when we just added it?
		pObj.setNewItem(false);
	}
}
