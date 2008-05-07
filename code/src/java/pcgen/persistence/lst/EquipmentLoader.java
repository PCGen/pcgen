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
import pcgen.core.EquipmentList;
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
	protected void addGlobalObject(PObject pObj) {
		//getEquipmentKeyedNoCustom??
		final Equipment aTemplate = EquipmentList
				.getEquipmentNamed(pObj.getKeyName());
		if (aTemplate == null) {
			EquipmentList.addEquipment((Equipment) pObj);
		}

	}

	@Override
	protected Equipment getObjectKeyed(String aKey) {
		return EquipmentList.getEquipmentNamed(aKey);
	}

	@Override
	public Equipment parseLine(LoadContext context, Equipment equipment,
			String inputLine, CampaignSourceEntry source) throws PersistenceLayerException {
		if (equipment == null) {
			equipment = new Equipment();
		}
		
		final StringTokenizer colToken = new StringTokenizer(inputLine,
				SystemLoader.TAB_DELIM);
		
		String name = colToken.nextToken();
		equipment.setName(name);
		equipment.setSourceCampaign(source.getCampaign());
		equipment.setSourceURI(source.getURI());

		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(
				EquipmentLstToken.class);
		while (colToken.hasMoreTokens()) {
			final String colString = colToken.nextToken().trim();

			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch (Exception e)
			{
				// TODO Handle Exception
			}
			EquipmentLstToken token = (EquipmentLstToken) tokenMap.get(key);
			if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, equipment, value);
				if (!token.parse(equipment, value))
				{
					Logging.errorPrint("Error parsing Equipment "
						+ equipment.getName() + ':' + source.getURI() + ':'
						+ colString + "\"");
				}
			}
			else if (colString.startsWith("Cost:"))
			{
				Logging.errorPrint("Cost deprecated, use COST "
					+ equipment.getName() + ':' + source.getURI() + ':'
					+ colString + "\"");
				token = (EquipmentLstToken) tokenMap.get("COST");
				final String value = colString.substring(idxColon + 1);
				if (!token.parse(equipment, value))
				{
					Logging.errorPrint("Error parsing Equipment "
						+ equipment.getName() + ':' + source.getURI() + ':'
						+ colString + "\"");
				}
			}
			else if (PObjectLoader.parseTag(equipment, colString))
			{
				continue;
			}
			else
			{
				Logging.errorPrint("Illegal Equipment info "
					+ source.toString() + ":" + " \"" + colString + "\"");
			}
		}
		
		completeObject(source, equipment);
		return null;
	}

	@Override
	protected void performForget(Equipment objToForget) {
		EquipmentList.remove(objToForget);
	}
}
