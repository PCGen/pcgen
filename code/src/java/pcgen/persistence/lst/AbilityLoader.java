/*
 * AbilityLoader.java
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
 * this code was moved and changed from FeatLoader.java
 *
 * Current Ver: $Revision: 1.12 $
 * Last Editor: $Author: soulcatcher $
 * Last Edited: $Date: 2006/02/14 21:00:18 $
 *
 */
package pcgen.persistence.lst;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.12 $
 */
public class AbilityLoader extends LstObjectFileLoader
{
	/** Creates a new instance of AbilityLoader */
	public AbilityLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	public PObject parseLine(PObject target, String lstLine, CampaignSourceEntry source)
		throws PersistenceLayerException
	{

		Ability anAbility = (Ability) target;

		if (anAbility == null)
		{
			anAbility = new Ability();
		}
		else if (anAbility.getCategory() == null || anAbility.getCategory().length() == 0)
		{
			anAbility.setCategory("BROKENABILTYNOCATEGORYSET");
		}

		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		int col = 0;

		Map tokenMap = TokenStore.inst().getTokenMap(AbilityLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();

			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch(StringIndexOutOfBoundsException e) {
				// TODO Handle Exception
			}
			AbilityLstToken token = (AbilityLstToken) tokenMap.get(key);
			if (col == 0)
			{
				anAbility.setName(colString);
				anAbility.setSourceCampaign(source.getCampaign());
				anAbility.setSourceFile(source.getFile());
			}
			else if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, anAbility, value);
				if (!token.parse(anAbility, value))
				{
					Logging.errorPrint("Error parsing ability " + anAbility.getName() + ':' + source.getFile() + ':' + colString + "\"");
				}
			}
			//
			// moved this after name assignment so abilities named
			// PRExxx don't parse the name as a prerequisite
			//
			else if (PObjectLoader.parseTag(anAbility, colString))
			{
				continue;
			}
			/****************
			 * TODO: The ADD: tag is parsed in PObjectLoader. This code never gets processed.
			 ****************
			 */
			else if (colString.startsWith("ADD:"))
			{
				anAbility.setAddString(colString.substring(4));
			}
			else
			{
				Logging.errorPrint("Unknown tag '" + colString + "' in " + source.getFile());
			}

			++col;
		}

		finishObject(anAbility);

		//setChanged();
		//notifyObservers(anAbility);
		return null;
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectNamed(java.lang.String)
	 */
	protected PObject getObjectNamed(String baseName)
	{
		return Globals.getAbilityNamed("ALL", baseName);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#finishObject(pcgen.core.PObject)
	 */
	protected void finishObject(PObject target)
	{
		if (includeObject(target))
		{
			Ability searchFor = (Ability) target;
			final Ability anAbility = Globals.getAbilityKeyed(searchFor.getCategory(), searchFor.getKeyName());

			if (anAbility == null)
			{
				Globals.addAbility(searchFor);
			}
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	protected void performForget(PObject objToForget)
	{
		String aCat = ((Ability) objToForget).getCategory();
		String aKey = ((Ability) objToForget).getKeyName();
		Globals.removeAbilityKeyed(aCat, aKey);
	}
}
