/*
 * DomainLoader.java
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

import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public class DomainLoader extends LstObjectFileLoader<Domain>
{
	/** Creates a new instance of DomainLoader */
	public DomainLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(LoadContext, pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	@Override
	public Domain parseLine(LoadContext context, Domain aDomain,
		String lstLine, CampaignSourceEntry source) throws PersistenceLayerException
	{
		Domain domain = aDomain;

		if (domain == null)
		{
			domain = new Domain();
		}

		final StringTokenizer colToken =
			new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		if (colToken.hasMoreTokens())
		{
			domain.setName(colToken.nextToken());
			domain.setSourceCampaign(source.getCampaign());
			domain.setSourceURI(source.getURI());
		}

		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(
				DomainLstToken.class);
		while (colToken.hasMoreTokens()) {
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
			if (context.processToken(domain, key, value))
			{
				context.commit();
			}
			else if (tokenMap.containsKey(key))
			{
				DomainLstToken tok = (DomainLstToken) tokenMap.get(key);
				LstUtils.deprecationCheck(tok, domain, value);
				if (!tok.parse(domain, value)) {
					Logging.errorPrint("Error parsing domain "
							+ domain.getDisplayName() + ':'
							+ source.toString() + ':' + token + "\"");
				}
			}
			else if (!PObjectLoader.parseTag(domain, token))
			{
				Logging.replayParsedMessages();
 			}
			Logging.clearParseMessages();
		}
		
		completeObject(source, domain);
		return null;
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectNamed(java.lang.String)
	 */
	@Override
	protected Domain getObjectKeyed(String aKey)
	{
		return Globals.getContext().ref.silentlyGetConstructedCDOMObject(Domain.class, aKey);
	}
}
