/*
 * SizeAdjustmentLoader.java
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

import java.net.URI;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.SizeAdjustment;
import pcgen.core.SystemCollections;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
final class SizeAdjustmentLoader extends LstLineFileLoader
{
	/** Prevent creation of a new instance of SizeAdjustmentLoader */
	public SizeAdjustmentLoader()
	{
		// TODO: Exception needs to be handled
	}

	@Override
	public void loadLstFile(LoadContext context, URI fileName, String gameModeIn)
		throws PersistenceLayerException
	{
		SystemCollections.getGameModeNamed(gameModeIn)
			.clearSizeAdjustmentList();
		super.loadLstFile(context, fileName, gameModeIn);
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.net.URL, java.lang.String)
	 */
	@Override
	public void parseLine(LoadContext context, String lstLine, URI sourceURI)
		throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(lstLine,
				SystemLoader.TAB_DELIM);

		SizeAdjustment sa = new SizeAdjustment();
		if (colToken.hasMoreTokens())
		{
			String nameToken = colToken.nextToken();
			final int colonLoc = nameToken.indexOf(':');
			if (colonLoc == -1)
			{
				Logging.errorPrint("Invalid Token - does not contain a colon: "
						+ nameToken);
				return;
			}
			else if (colonLoc == 0)
			{
				Logging.errorPrint("Invalid Token - starts with a colon: "
						+ nameToken);
				return;
			}
			String key = nameToken.substring(0, colonLoc);
			if (!"SIZENAME".equals(key))
			{
				Logging
						.errorPrint("Expected first token in SizeAdjustment to be SIZENAME");
				return;
			}
			String value = (colonLoc == nameToken.length() - 1) ? null
					: nameToken.substring(colonLoc + 1);
			sa = SystemCollections.getGameModeNamed(getGameMode())
					.getSizeAdjustmentNamed(value);
			if (sa == null)
			{
				sa = new SizeAdjustment();
				sa.setName(value);
				SystemCollections.getGameModeNamed(getGameMode())
					.addToSizeAdjustmentList(sa);
			}
		}

		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(
				SizeAdjustmentLstToken.class);
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
			if (context.processToken(sa, key, value))
			{
				context.commit();
			}
			else if (tokenMap.containsKey(key))
			{
				SizeAdjustmentLstToken tok = (SizeAdjustmentLstToken) tokenMap
						.get(key);
				LstUtils.deprecationCheck(tok, sa, value);
				if (!tok.parse(sa, value))
				{
					Logging.errorPrint("Error parsing SizeAdjustment "
							+ sa.getDisplayName() + ':' + sourceURI.toString()
							+ ':' + token + "\"");
				}
			}
			else if (!PObjectLoader.parseTag(sa, token))
			{
				Logging.replayParsedMessages();
			}
			Logging.clearParseMessages();
		}
	}
}
