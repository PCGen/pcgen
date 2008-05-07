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

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.PCTemplate;
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
public final class PCTemplateLoader extends LstObjectFileLoader<PCTemplate> {

	@Override
	protected void addGlobalObject(PObject pObj) {
		final PCTemplate aTemplate = Globals
				.getTemplateKeyed(pObj.getKeyName());
		if (aTemplate == null) {
			Globals.getTemplateList().add((PCTemplate) pObj);
		}

	}

	@Override
	protected PCTemplate getObjectKeyed(String aKey) {
		return Globals.getTemplateKeyed(aKey);
	}

	@Override
	public PCTemplate parseLine(LoadContext context, PCTemplate template,
			String inputLine, CampaignSourceEntry source) throws PersistenceLayerException {
		if (template == null) {
			template = new PCTemplate();
		}
		
		final StringTokenizer colToken = new StringTokenizer(inputLine,
				SystemLoader.TAB_DELIM);
		
		String name = colToken.nextToken();
		template.setName(name);
		template.setSourceCampaign(source.getCampaign());
		template.setSourceURI(source.getURI());

		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(
				PCTemplateLstToken.class);
		while (colToken.hasMoreTokens()) {
			final String colString = colToken.nextToken().trim();

			final int idxColon = colString.indexOf(':');
			String key = "";
			try {
				key = colString.substring(0, idxColon);
			} catch (StringIndexOutOfBoundsException e) {
				// TODO Handle Exception
			}
			PCTemplateLstToken token = (PCTemplateLstToken) tokenMap.get(key);

			if (colString.startsWith("CHOOSE:LANGAUTO:")) {
				template.setChooseLanguageAutos(colString.substring(16));
			} else if (token != null) {
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, template, value);
				if (!token.parse(template, value)) {
					Logging.errorPrint("Error parsing template "
							+ template.getDisplayName() + ':'
							+ source.toString() + ':' + colString + "\"");
				}
			} else if (PObjectLoader.parseTag(template, colString)) {
				continue;
			} else {
				Logging.errorPrint("Unknown tag '" + colString + "' in "
						+ source.toString());
			}
		}
		
		completeObject(source, template);
		return null;
	}

	@Override
	protected void performForget(PCTemplate objToForget) {
		Globals.getTemplateList().remove(objToForget);
	}
}
