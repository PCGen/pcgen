/*
 * EquipmentModifierLoader.java
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.Constants;
import pcgen.core.Campaign;
import pcgen.core.EquipmentList;
import pcgen.core.EquipmentModifier;
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
public final class EquipmentModifierLoader extends
		LstObjectFileLoader<EquipmentModifier> {
	@Override
	protected void addGlobalObject(PObject pObj) {
		// getEquipmentKeyedNoCustom??
		final EquipmentModifier aTemplate = EquipmentList.getModifierKeyed(pObj
				.getKeyName());
		if (aTemplate == null) {
			EquipmentList.addEquipmentModifier((EquipmentModifier) pObj);
		}

	}

	@Override
	protected EquipmentModifier getObjectKeyed(String aKey) {
		return EquipmentList.getModifierKeyed(aKey);
	}

	@Override
	public EquipmentModifier parseLine(LoadContext context,
			EquipmentModifier eqMod, String inputLine, CampaignSourceEntry source)
			throws PersistenceLayerException {
		if (eqMod == null) {
			eqMod = new EquipmentModifier();
		}

		final StringTokenizer colToken = new StringTokenizer(inputLine,
				SystemLoader.TAB_DELIM);

		String name = colToken.nextToken();
		eqMod.setName(name.replace('|', ' '));
		eqMod.setSourceCampaign(source.getCampaign());
		eqMod.setSourceURI(source.getURI());

		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(
				EquipmentModifierLstToken.class);
		while (colToken.hasMoreTokens()) {
			while (colToken.hasMoreTokens()) {
				final String colString = colToken.nextToken().trim();

				final int idxColon = colString.indexOf(':');
				String key = "";
				try {
					key = colString.substring(0, idxColon);
				} catch (Exception e) {
					// TODO Handle Exception
				}

				EquipmentModifierLstToken token = (EquipmentModifierLstToken) tokenMap
						.get(key);
				if (token != null) {
					final String value = colString.substring(idxColon + 1);
					LstUtils.deprecationCheck(token, eqMod, value);
					if (!token.parse(eqMod, value)) {
						Logging.errorPrint("Error parsing EqMod "
								+ eqMod.getDisplayName() + ':'
								+ source.getURI() + ':' + colString + "\"");
					}
				} else if (PObjectLoader.parseTag(eqMod, colString)) {
					continue;
				} else {
					Logging.errorPrint("Illegal equipment modifier info "
							+ source + ":" + " \"" + colString + "\"");
				}
			}
		}

		completeObject(source, eqMod);
		return null;
	}

	@Override
	protected void performForget(EquipmentModifier objToForget) {
		throw new java.lang.UnsupportedOperationException(
				"Cannot FORGET an EquipmentModifier");
	}

	/**
	 * This method adds the default available equipment modifiers to the
	 * Globals.
	 * @throws PersistenceLayerException 
	 * 
	 * @throws PersistenceLayerException
	 *             if some bizarre error occurs, likely due to a change in
	 *             EquipmentModifierLoader
	 */
	public void addDefaultEquipmentMods() throws PersistenceLayerException {
		CampaignSourceEntry source;
		try {
			source = new CampaignSourceEntry(new Campaign(),
					new URI("file:/" + getClass().getName() + ".java"));
		} catch (URISyntaxException e) {
			throw new UnreachableError(e);
		}
		String aLine;
		EquipmentModifier anObj = new EquipmentModifier();
		aLine = "Add Type\tKEY:ADDTYPE\tTYPE:ALL\tCOST:0\tNAMEOPT:NONAME\tSOURCELONG:PCGen Internal\tCHOOSE:EQBUILDER.EQTYPE|COUNT=ALL|TITLE=desired TYPE(s)";
		parseLine(null, anObj, aLine, source);

		//
		// Add internal equipment modifier for adding weapon/armor types to
		// equipment
		//
		anObj = new EquipmentModifier();
		aLine = Constants.s_INTERNAL_EQMOD_WEAPON
				+ "\tTYPE:Weapon\tVISIBLE:No\tCHOOSE:NOCHOICE\tNAMEOPT:NONAME";
		parseLine(null, anObj, aLine, source);

		anObj = new EquipmentModifier();
		aLine = Constants.s_INTERNAL_EQMOD_ARMOR
				+ "\tTYPE:Armor\tVISIBLE:No\tCHOOSE:NOCHOICE\tNAMEOPT:NONAME";
		parseLine(null, anObj, aLine, source);
	}
}
