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
 *
 */
package pcgen.persistence.lst;

import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.SettingsHandler;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * 
 * @author David Rice &lt;david-pcgen@jcuz.com&gt;
 */
public class AbilityLoader extends LstObjectFileLoader<Ability>
{

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(LoadContext, CDOMObject, String, SourceEntry)
	 */
	@Override
	public Ability parseLine(LoadContext context, Ability ability,
		String lstLine, SourceEntry source) throws PersistenceLayerException
	{
		Ability anAbility = ability;

		boolean isnew = false;
		if (anAbility == null)
		{
			anAbility = new Ability();
			isnew = true;
		}

		final StringTokenizer colToken = new StringTokenizer(lstLine,
				SystemLoader.TAB_DELIM);
		
		if (colToken.hasMoreTokens())
		{
			anAbility.setName(colToken.nextToken().intern());
			anAbility.put(ObjectKey.SOURCE_CAMPAIGN, source.getCampaign());
			anAbility.setSourceURI(source.getURI());
			if (isnew)
			{
				context.addStatefulInformation(anAbility);
				context.getReferenceContext().importObject(anAbility);
			}
		}

		while (colToken.hasMoreTokens()) 
		{
			LstUtils.processToken(context, anAbility, source, colToken.nextToken());
		}

		completeObject(context, source, anAbility);
		return null;
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectKeyed(LoadContext, java.lang.String)
	 */
	@Override
	protected Ability getObjectKeyed(LoadContext context, String aKey)
	{
		if (aKey == null || aKey.isEmpty())
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
		AbilityCategory ac = SettingsHandler.getGame().getAbilityCategory(
				abilityCatName);
		return context.getReferenceContext().silentlyGetConstructedCDOMObject(Ability.class, ac,
				abilityKey);
	}

	@Override
	protected Ability getMatchingObject(LoadContext context, CDOMObject key)
	{
		return context.getReferenceContext().silentlyGetConstructedCDOMObject(Ability.class,
				((Ability) key).getCDOMCategory(), key.getKeyName());
	}

	/**
	 * This method should be called by finishObject implementations in
	 * order to check if the parsed object is affected by an INCLUDE or
	 * EXCLUDE request.
	 *
	 * @param cdo PObject to determine whether to include in
	 *         Globals etc.
	 * @return boolean true if the object should be included, else false
	 *         to exclude it
	 */
    @Override
	protected final boolean includeObject(SourceEntry source, CDOMObject cdo)
	{
		// Null check; never add nulls or objects without a name/key name
		if ((cdo == null) || (cdo.getDisplayName() == null)
			|| (cdo.getDisplayName().trim().isEmpty())
			|| (cdo.getKeyName() == null)
			|| (cdo.getKeyName().trim().isEmpty()))
		{
			return false;
		}

		Ability ability = (Ability) cdo;
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
