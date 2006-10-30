/*
 * LanguageLoader.java
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
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
final class LanguageLoader extends LstObjectFileLoader<Language>
{
	/** Creates a new instance of LanguageLoader */
	public LanguageLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	@Override
	public Language parseLine(Language lang, String lstLine, CampaignSourceEntry source)
		throws PersistenceLayerException
	{
		if (lang == null)
		{
			lang = new Language();
		}

		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		int col = 0;

		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(LanguageLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			final int idxColon = colString.indexOf(':');
			String key = Constants.EMPTY_STRING;
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch(Exception e)
			{
				// TODO Handle Exception
			}
			LanguageLstToken token = (LanguageLstToken) tokenMap.get(key);

			if (col == 0)
			{
				lang.setName(colString);
				lang.setSourceCampaign(source.getCampaign());
				lang.setSourceFile(source.getFile());
			}
			else if (token != null)
			{
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, lang, value);
				if (!token.parse(lang, value))
				{
					Logging.errorPrint("Error parsing language " + lang.getDisplayName() + ':' + source.getFile() + ':' + colString + "\"");
				}
			}
			else
			{
				if (PObjectLoader.parseTag(lang, colString))
				{
					continue;
				}
				Logging.errorPrint("Unknown tag '" + colString + "' in " + source.getFile());
			}

			++col;
		}

		completeObject( lang );
		return null;
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectKeyed(java.lang.String)
	 */
	protected Language getObjectKeyed(String aKey)
	{
		return Globals.getLanguageKeyed(aKey);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	@Override
	protected void performForget( final PObject objToForget )
	{
		Globals.getLanguageList().remove(objToForget);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#addGlobalObject(pcgen.core.PObject)
	 */
	@Override
	protected void addGlobalObject( final PObject pObj )
	{
		// TODO - Create Globals.addLanguage( final Language aLang )
		Globals.getLanguageList().add( (Language)pObj );
	}
}
