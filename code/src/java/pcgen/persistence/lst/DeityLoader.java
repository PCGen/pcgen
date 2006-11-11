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

import pcgen.core.Constants;
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
public class DeityLoader extends LstObjectFileLoader<Deity>
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
	@Override
	public Deity parseLine(Deity aDeity, String lstLine, CampaignSourceEntry source)
		throws PersistenceLayerException
	{
		Deity deity = aDeity;
		
		if (deity == null)
		{
			deity = new Deity();
		}

		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		boolean firstCol = true;

		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(DeityLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			final int idxColon = colString.indexOf(':');
			String key = Constants.EMPTY_STRING;
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
					Logging.errorPrint("Error parsing deity "
						+ deity.getDisplayName() + ':' + source.getFile() + ':'
						+ colString + "\"");
				}
			}
			else if (PObjectLoader.parseTag(deity, colString))
			{
				continue;
			}
			else if (firstCol)
			{
				if ((!colString.equals(deity.getKeyName()))
					&& (colString.indexOf(".MOD") < 0))
				{
					completeObject(deity);
					deity = new Deity();
					deity.setName(colString);
					deity.setSourceCampaign(source.getCampaign());
					deity.setSourceFile(source.getFile());
				}
				firstCol = false;
			}
			else
			{
				Logging.errorPrint("Illegal deity info '" + colString
					+ "' for " + deity.getDisplayName() + " in "
					+ source.getFile() + " of " + source.getCampaign() + ".");
			}
		}

		completeObject( deity );
		return null;
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectKeyed(java.lang.String)
	 */
	@Override
	protected Deity getObjectKeyed( final String aKey )
	{
		return Globals.getDeityKeyed(aKey);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	@Override
	protected void performForget(PObject objToForget)
	{
		Globals.getDeityList().remove(objToForget);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#addGlobalObject(pcgen.core.PObject)
	 */
	@Override
	protected void addGlobalObject( final PObject pObj )
	{
		// TODO - Create Globals.addDeity( final Deity aDeity );
		Globals.getDeityList().add( (Deity)pObj );
	}
}
