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

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
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
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(LoadContext,
	 *      pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	@Override
	public Ability parseLine(LoadContext context, Ability ability,
		String lstLine, CampaignSourceEntry source) throws PersistenceLayerException
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

		final StringTokenizer colToken = new StringTokenizer(lstLine,
				SystemLoader.TAB_DELIM);
		
		if (colToken.hasMoreTokens())
		{
			anAbility.setName(colToken.nextToken());
			anAbility.setSourceCampaign(source.getCampaign());
			anAbility.setSourceURI(source.getURI());
		}

		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(
				AbilityLstToken.class);
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
			if (context.processToken(anAbility, key, value))
			{
				context.commit();
			}
			else if (tokenMap.containsKey(key))
			{
				AbilityLstToken tok = (AbilityLstToken) tokenMap.get(key);
				LstUtils.deprecationCheck(tok, anAbility, value);
				if (!tok.parse(anAbility, value)) {
					Logging.errorPrint("Error parsing ability "
							+ anAbility.getDisplayName() + ':'
							+ source.toString() + ':' + token + "\"");
				}
			}
			else if (!PObjectLoader.parseTag(anAbility, token))
			{
				Logging.replayParsedMessages();
 			}
			Logging.clearParseMessages();
		}

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
			String message = "Attempt to Modify/Copy/Forget an Ability ("
				+ aKey + ") without a CATEGORY=\n"
				+ "  Proper format is CATEGORY=cat|abilityKey";
			Logging.log(Logging.LST_ERROR, message);
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
	
	@Override
	protected Ability getMatchingObject(PObject aKey)
	{
		return Globals.getAbilityKeyed(((Ability) aKey).getCategory(), aKey
			.getKeyName());
	}

	/**
	 * This method should be called by finishObject implementations in
	 * order to check if the parsed object is affected by an INCLUDE or
	 * EXCLUDE request.
	 *
	 * @param parsedObject PObject to determine whether to include in
	 *         Globals etc.
	 * @return boolean true if the object should be included, else false
	 *         to exclude it
	 */
	protected final boolean includeObject(CampaignSourceEntry source, PObject parsedObject)
	{
		// Null check; never add nulls or objects without a name/key name
		if ((parsedObject == null) || (parsedObject.getDisplayName() == null)
			|| (parsedObject.getDisplayName().trim().length() == 0)
			|| (parsedObject.getKeyName() == null)
			|| (parsedObject.getKeyName().trim().length() == 0))
		{
			return false;
		}

		Ability ability = (Ability) parsedObject;
		// If includes were present, check includes for given object
		List<String> includeItems = source.getIncludeItems();

		if (!includeItems.isEmpty())
		{
			if (includeItems.contains(ability.getCategory() + "," + ability.getKeyName()))
			{
				return true;
			}
			if (includeItems.contains(ability.getKeyName()))
			{
				Logging.deprecationPrint("Deprecated INCLUDE value when loading "
					+ source.getURI()
					+ " . Abilities (including feats) must always have "
					+ "categories (e.g. "
					+ "INCLUDE:CATEGORY=cat1,key1,key2|CATEGORY=cat2,key3 ).");
				
				return true;
			}
			return false;
		}
		// If excludes were present, check excludes for given object
		List<String> excludeItems = source.getExcludeItems();

		if (!excludeItems.isEmpty())
		{
			if (excludeItems.contains(ability.getCategory() + "," + ability.getKeyName()))
			{
				return false;
			}
			if (excludeItems.contains(ability.getKeyName()))
			{
				Logging.deprecationPrint("Deprecated EXCLUDE value when loading "
					+ source.getURI()
					+ " . Abilities (including feats) must always have "
					+ "categories (e.g. "
					+ "EXCLUDE:CATEGORY=cat1,key1,key2|CATEGORY=cat2,key3 ).");
				return false;
			}
			return true;
		}

		return true;
	}
}
