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
import pcgen.core.PObject;
import pcgen.io.EntityEncoder;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public class DomainLoader extends LstObjectFileLoader
{
	/** Creates a new instance of DomainLoader */
	public DomainLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	public PObject parseLine(PObject target, String lstLine, CampaignSourceEntry source)
		throws PersistenceLayerException
	{
		Domain domain = (Domain) target;

		if (domain == null)
		{
			domain = new Domain();
		}

		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		int col = 0;

		Map tokenMap = TokenStore.inst().getTokenMap(DomainLstToken.class);
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
			DomainLstToken token = (DomainLstToken) tokenMap.get(key);
			if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, domain, value);
				if (!token.parse(domain, value))
				{
					Logging.errorPrint("Error parsing domain " + domain.getDisplayName() + ':' + source.getFile() + ':' + colString + "\"");
				}
			}
			else if (PObjectLoader.parseTag(domain, colString))
			{
				continue;
			}
			else if (col == 0)
			{
				if ((!colString.equals(domain.getKeyName())) && (colString.indexOf(".MOD") < 0))
				{
					finishObject(domain);
					domain = new Domain();
					domain.setName(colString);
					domain.setSourceCampaign(source.getCampaign());
					domain.setSourceFile(source.getFile());
				}
			}
			else if (col == 1) //TODO (DJ)[tok]: This is crap, make this into a tag before in 6.0
			{
				domain.setDescription(EntityEncoder.decode(colString));
			}
			else
			{
				Logging.errorPrint("Illegal obj info '" + colString + "' in " + source.getFile());
			}

			++col;
		}

		return domain;
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectNamed(java.lang.String)
	 */
	protected PObject getObjectKeyed(String aKey)
	{
		return Globals.getDomainKeyed(aKey);
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
			if (Globals.getDomainKeyed(target.getKeyName()) == null)
			{
				Globals.addDomain((Domain) target);
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
		Globals.getDomainList().remove(objToForget);
	}
}
