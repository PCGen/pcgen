/*
 * AbilityCategoryLoader.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.AbilityCategory;
import pcgen.core.GameMode;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * This class handles parsing the whole ABILITYCATEGORY line and passing each
 * token to the correct parser.
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public class AbilityCategoryLoader
{
	/**
	 * Default Constructor
	 */
	public AbilityCategoryLoader()
	{
		// Nothing to do.
	}

	/**
	 * Parse the ABILITYCATEGORY line.
	 * 
	 * @param aGameMode The <tt>GameMode</tt> this object belongs to.
	 * @param aLine The line to parse
	 * 
	 * @throws PersistenceLayerException
	 */
	public void parseLine(final GameMode aGameMode, final String aLine, URI source)
		throws PersistenceLayerException
	{
		final StringTokenizer colToken =
				new StringTokenizer(aLine, SystemLoader.TAB_DELIM);

		// Get all the tokens for this tag (all classes implementing the 
		// AbilityCategoryLstToken interface.
		final Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(AbilityCategoryLstToken.class);

		AbilityCategory cat = null;
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			final int idxColon = colString.indexOf(':');
			final String key;
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				throw new PersistenceLayerException(PropertyFactory
					.getFormattedString("Errors.LstTokens.InvalidTokenFormat", //$NON-NLS-1$
						getClass().toString(), colString));
			}

			final AbilityCategoryLstToken token =
					(AbilityCategoryLstToken) tokenMap.get(key);

			if (key.equals("ABILITYCATEGORY")) //$NON-NLS-1$
			{
				final String value = colString.substring(idxColon + 1).trim();
				cat = aGameMode.getAbilityCategory(value);

				if (cat == null)
				{
					cat = new AbilityCategory(value);
					aGameMode.addAbilityCategory(cat);
				}
			}
			else if (token != null)
			{
				final String value = colString.substring(idxColon + 1).trim();
				// TODO - i18n
				LstUtils.deprecationCheck(token, "Ability Category", source,
						value);
				if (!token.parse(cat, value))
				{
					// TODO - i18n
					Logging.errorPrint("Error parsing ability category:"
						+ "miscinfo.lst from the " + aGameMode.getName()
						+ " Game Mode" + ':' + colString + "\"");
				}
			}
			else
			{
				// TODO - i18n
				Logging.errorPrint("Invalid sub tag " + key
					+ " on ABILITYCATEGORY line");
				throw new PersistenceLayerException("Invalid sub tag " + key
					+ " on ABILITYCATEGORY line");
			}
		}
	}
}
