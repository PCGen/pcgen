/*
 * EquipmentLoader.java
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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Equipment;
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
public final class EquipmentLoader extends LstObjectFileLoader<Equipment> {

	@Override
	protected Equipment getObjectKeyed(String aKey)
	{
		return Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				Equipment.class, aKey);
	}

	@Override
	public Equipment parseLine(LoadContext context, Equipment equipment,
			String inputLine, CampaignSourceEntry source) throws PersistenceLayerException {
		if (equipment == null) {
			equipment = new Equipment();
		}
		
		final StringTokenizer colToken = new StringTokenizer(inputLine,
				SystemLoader.TAB_DELIM);

		if (colToken.hasMoreTokens())
		{
			equipment.setName(colToken.nextToken());
			equipment.setSourceCampaign(source.getCampaign());
			equipment.setSourceURI(source.getURI());
		}

		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(
				EquipmentLstToken.class);
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
			if (context.processToken(equipment, key, value))
			{
				context.commit();
			}
			else if (tokenMap.containsKey(key))
			{
				EquipmentLstToken tok = (EquipmentLstToken) tokenMap.get(key);
				LstUtils.deprecationCheck(tok, equipment, value);
				if (!tok.parse(equipment, value))
				{
					Logging.errorPrint("Error parsing Equipment "
						+ equipment.getName() + ':' + source.getURI() + ':'
						+ token + "\"");
				}
			}
			else if (!PObjectLoader.parseTag(equipment, token))
			{
				Logging.replayParsedMessages();
 			}
			Logging.clearParseMessages();
		}
		
		completeObject(source, equipment);
		return null;
	}
}
