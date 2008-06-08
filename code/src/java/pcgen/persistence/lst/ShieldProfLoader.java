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

import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.ShieldProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public final class ShieldProfLoader extends LstObjectFileLoader<ShieldProf>
{
	/** Creates a new instance of ShieldProfLoader */
	public ShieldProfLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(LoadContext, pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	@Override
	public ShieldProf parseLine(LoadContext context, ShieldProf aWP,
		String lstLine, CampaignSourceEntry source) throws PersistenceLayerException
	{
		ShieldProf prof = aWP;

		// Make sure we have a weapon prof to load
		if (prof == null)
		{
			prof = new ShieldProf();
		}

		final StringTokenizer colToken = new StringTokenizer(lstLine,
				SystemLoader.TAB_DELIM);

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
				context.commit();
			}
			else if (!PObjectLoader.parseTag(prof, token))
			{
				Logging.replayParsedMessages();
			}
			Logging.clearParseMessages();
		}

		// ShieldProfs are one line each;
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
	protected ShieldProf getObjectKeyed(String aKey)
	{
		return Globals.getContext().ref.silentlyGetConstructedCDOMObject(ShieldProf.class, aKey);
	}
}
