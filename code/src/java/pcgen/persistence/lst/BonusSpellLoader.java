/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Created on August 12, 2002, 10:29 PM
 *
 * $Id$
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.util.StringTokenizer;

import pcgen.cdom.content.BonusSpellInfo;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public final class BonusSpellLoader extends LstLineFileLoader
{
	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.net.URL, java.lang.String)
	 */
	@Override
	public void parseLine(LoadContext context, String lstLine, URI sourceURI)
			throws PersistenceLayerException
	{
		final StringTokenizer colToken =
				new StringTokenizer(lstLine, SystemLoader.TAB_DELIM, false);
		BonusSpellInfo bsi = new BonusSpellInfo();
		if (colToken.hasMoreTokens())
		{
			bsi.setName(colToken.nextToken());
		}
		bsi.setSourceURI(sourceURI);

		while (colToken.hasMoreTokens())
		{
			final String token = colToken.nextToken().trim();
			final int colonLoc = token.indexOf(':');
			if (colonLoc == -1)
			{
				Logging
						.errorPrint("Invalid Token - does not contain a colon: '"
								+ token
								+ "' in "
								+ bsi.getClass().getSimpleName()
								+ " "
								+ bsi.getDisplayName() + " of " + sourceURI);
				continue;
			}
			else if (colonLoc == 0)
			{
				Logging.errorPrint("Invalid Token - starts with a colon: '"
						+ token + "' in " + bsi.getClass().getSimpleName() + " "
						+ bsi.getDisplayName() + " of " + sourceURI);
				continue;
			}

			String key = token.substring(0, colonLoc);
			String value = (colonLoc == token.length() - 1) ? null : token
					.substring(colonLoc + 1);
			if (context.processToken(bsi, key, value))
			{
				context.commit();
			}
			else
			{
				context.rollback();
				Logging.replayParsedMessages();
			}
			Logging.clearParseMessages();
		}
		context.ref.importObject(bsi);
	}
}
