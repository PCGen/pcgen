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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Ability;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * 
 * @author David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public class AbilityLoader extends LstObjectFileLoader<Ability>
{
	/** Creates a new instance of AbilityLoader */
	public AbilityLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject,
	 *      java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	@Override
	public Ability parseLine(Ability ability, String lstLine,
		CampaignSourceEntry source) throws PersistenceLayerException
	{
		Ability anAbility = ability;

		if (anAbility == null)
		{
			anAbility = new Ability();
		}
		else if (anAbility.getCategory() == null
			|| anAbility.getCategory().length() == 0)
		{
			// TODO - Make this into an Enum Categorisable.Category.NONE
			anAbility.setCategory("BROKENABILTYNOCATEGORYSET");
		}

		final StringTokenizer colToken =
				new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		int col = 0;

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(AbilityLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();

			final int idxColon = colString.indexOf(':');
			String key = Constants.EMPTY_STRING;
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// TODO Handle Exception
			}
			AbilityLstToken token = (AbilityLstToken) tokenMap.get(key);
			if (col == 0)
			{
				anAbility.setName(colString);
				anAbility.setSourceCampaign(source.getCampaign());
				anAbility.setSourceURI(source.getURI());
			}
			else if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, anAbility, value);
				if (!token.parse(anAbility, value))
				{
					Logging.errorPrintLocalised(
						"Errors.AbilityLoader.ParsingError", //$NON-NLS-1$
						anAbility.getDisplayName(), source.getURI(), colString);
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
			/*******************************************************************
			 * TODO: The ADD: tag is parsed in PObjectLoader. This code never
			 * gets processed. ***************
			 */
			else if (colString.startsWith("ADD:"))
			{
				anAbility.setAddString(colString.substring(4));
			}
			else
			{
				Logging.errorPrintLocalised("Errors.AbilityLoader.UnknownTag", //$NON-NLS-1$
					colString, source.getURI());
			}

			++col;
		}

		// setChanged();
		// notifyObservers(anAbility);

		completeObject(source, anAbility);
		return null;
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectKeyed(java.lang.String)
	 */
	@Override
	protected Ability getObjectKeyed(String aKey)
	{
		if (aKey == null || aKey.length() == 0)
		{
			return null;
		}

		String abilityCatName;
		String abilityKey;

		String[] parts = aKey.split("\\|");
		if (parts.length == 2 && parts[0].startsWith("CATEGORY="))
		{
			abilityCatName = parts[0].substring(9);
			abilityKey = parts[1];
		}
		else
		{
			Logging.errorPrint("Attempt to Modify/Copy/Forget an Ability ("
				+ aKey + ") without a CATEGORY=\n"
				+ "  Proper format is CATEGORY=cat|abilityKey");
			return null;
		}
		return Globals.getAbilityKeyed(abilityCatName, abilityKey);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	@Override
	protected void performForget(Ability objToForget)
	{
		String aCat = objToForget.getCategory();
		String aKey = objToForget.getKeyName();
		Globals.removeAbilityKeyed(aCat, aKey);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#addGlobalObject(pcgen.core.PObject)
	 * 
	 * @author boomer70 <boomer70@yahoo.com>
	 * 
	 * @since 5.11
	 */
	@Override
	protected void addGlobalObject(final PObject pObj)
	{
		Globals.addAbility((Ability) pObj);
	}
}
