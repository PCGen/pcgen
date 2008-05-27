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

import java.util.Date;
import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PObject;
import pcgen.core.SettingsHandler;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
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
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(LoadContext, pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	@Override
	public Language parseLine(LoadContext context, Language aLang,
		String lstLine, CampaignSourceEntry source) throws PersistenceLayerException
	{
		Language lang = aLang;

		if (lang == null)
		{
			lang = new Language();
		}

		final StringTokenizer colToken =
				new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		if (colToken.hasMoreTokens())
		{
			lang.setName(colToken.nextToken());
			lang.setSourceCampaign(source.getCampaign());
			lang.setSourceURI(source.getURI());
		}

		while (colToken.hasMoreTokens())
		{
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
			if (context.processToken(lang, key, value))
			{
				Logging.clearParseMessages();
				context.commit();
			}
			else if (PObjectLoader.parseTag(lang, token))
 			{
				Logging.clearParseMessages();
 				continue;
 			}
 			else
 			{
				Logging.rewindParseMessages();
				Logging.replayParsedMessages();
 			}
		}

		completeObject(source, lang);
		return null;
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectKeyed(java.lang.String)
	 */
	@Override
	protected Language getObjectKeyed(String aKey)
	{
		return Globals.getLanguageKeyed(aKey);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	@Override
	protected void performForget(final Language objToForget)
	{
		Globals.getLanguageList().remove(objToForget);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#addGlobalObject(pcgen.core.PObject)
	 */
	@Override
	protected void addGlobalObject(final PObject pObj)
	{
		// TODO - Create Globals.addLanguage( final Language aLang )
		Globals.getLanguageList().add((Language) pObj);
		Globals.getContext().ref.importObject(pObj);
	}

	@Override
	protected void storeObject(PObject pObj)
	{
		final Language matching = getMatchingObject(pObj);

		if (matching == null || !pObj.getType().equals(matching.getType()))
		{
			addGlobalObject(pObj);
		}
		else
		{
			//Yes, this is instance equality, NOT .equals!!!!!
			if (matching != pObj)
			{
				if (SettingsHandler.isAllowOverride())
				{
					// If the new object is more recent than the current
					// one, use the new object
					final Date pObjDate =
							pObj.getSourceEntry().getSourceBook().getDate();
					final Date currentObjDate =
							matching.getSourceEntry().getSourceBook()
								.getDate();
					if ((pObjDate != null)
						&& ((currentObjDate == null) || ((pObjDate
							.compareTo(currentObjDate) > 0))))
					{
						performForget(matching);
						addGlobalObject(pObj);
					}
				}
				else
				{
					// Duplicate loading error
					Logging.errorPrintLocalised(
						"Warnings.LstFileLoader.DuplicateObject", //$NON-NLS-1$
						pObj.getKeyName(), matching.getSourceURI(), pObj
							.getSourceURI());
				}
			}
		}
	}
	
	
}
