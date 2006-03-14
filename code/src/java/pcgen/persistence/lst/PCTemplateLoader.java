/*
 * PCTemplateLoader.java
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
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst;

import java.net.URL;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
final class PCTemplateLoader
{
	/** Creates a new instance of PCTemplateLoader */
	private PCTemplateLoader()
	{
		// Empty Constructor
	}

	public static void parseLine(PCTemplate template, String inputLine, URL sourceURL, int lineNum)
		throws PersistenceLayerException
	{
		if (template == null)
		{
			return;
		}
		template.setName("None");

		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);
		int col = 0;

		if (!template.isNewItem())
		{
			col = 1; // .MOD skip required fields (name in this case)
			colToken.nextToken();
		}

		Map tokenMap = TokenStore.inst().getTokenMap(PCTemplateLstToken.class);
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
			PCTemplateLstToken token = (PCTemplateLstToken) tokenMap.get(key);

			if (col == 0)
			{
				template.setName(colString);
			}
			else if (colString.startsWith("CHOOSE:LANGAUTO:"))
			{
				template.setChooseLanguageAutos(colString.substring(16));
			}
			else if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, template, value);
				if (!token.parse(template, value))
				{
					Logging.errorPrint("Error parsing template " + template.getName() + ':' + sourceURL.toString() + ':' + colString + "\"");
				}
			}
			else if (PObjectLoader.parseTag(template, colString))
			{
				continue;
			}
			else
			{
				Logging.errorPrint("Unknown tag '" + colString + "' in " + sourceURL.toString());
			}

			++col;
		}
	}
}
