/*
 * RaceLoader.java
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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class RaceLoader extends LstObjectFileLoader<Race>
{
	/** Creates a new instance of RaceLoader */
	public RaceLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(LoadContext, pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	@Override
	public Race parseLine(LoadContext context, Race aRace, String lstLine, CampaignSourceEntry source)
		throws PersistenceLayerException
	{
		Race race = aRace;

		if (race == null)
		{
			race = new Race();
		}

		final StringTokenizer colToken =
				new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		if (colToken.hasMoreTokens())
		{
			race.setName(colToken.nextToken());
			race.setSourceCampaign(source.getCampaign());
			race.setSourceURI(source.getURI());
		}

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(RaceLstToken.class);
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
			if (context.processToken(race, key, value))
			{
				Logging.clearParseMessages();
				context.commit();
			}
			else if (tokenMap.containsKey(key))
			{
				RaceLstToken tok = (RaceLstToken) tokenMap.get(key);
				LstUtils.deprecationCheck(tok, race, value);
				if (!tok.parse(race, value))
				{
					Logging.errorPrint("Error parsing race "
						+ race.getDisplayName() + ':' + source.getURI() + ':'
						+ token + "\"");
				}
				Logging.clearParseMessages();
 				continue;
			}
			else if (PObjectLoader.parseTag(race, token))
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

		if ((race.getLevelAdjustment(null) != 0) && (race.getCR() == 0))
		{
			race.setCR(race.getLevelAdjustment(null));
		}

		if (race.get(ObjectKey.RACETYPE) == null)
		{
			/** TODO Uncomment this once the data is updated. */
			//			logError("Race " + race.getName() + " has no race type.");
		}

		completeObject(source, race);
		return null;
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectKeyed(java.lang.String)
	 */
	@Override
	protected Race getObjectKeyed(String aKey)
	{
		return Globals.getRaceKeyed(aKey);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	@Override
	protected void performForget(final Race objToForget)
	{
		Globals.removeRaceKeyed(objToForget.getKeyName());
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#addGlobalObject(pcgen.core.PObject)
	 */
	@Override
	protected void addGlobalObject(final PObject pObj)
	{
		Globals.addRace((Race) pObj);
	}
}
