/*
 * KitLoader.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on September 23, 2002, 1:39 PM
 *
 * $Id$
 */
package pcgen.persistence.lst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * 
 * ???
 * 
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public final class KitLoader extends LstObjectFileLoader<Kit> {

	@Override
	protected Kit getObjectKeyed(String aKey) {
		return Globals.getContext().ref.silentlyGetConstructedCDOMObject(Kit.class, aKey);
	}

	@Override
	public Kit parseLine(LoadContext context, Kit target, String inputLine, CampaignSourceEntry source)
			throws PersistenceLayerException {

		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(
				KitLstToken.class);

		// We will find the first ":" for the "controlling" line token
		final int idxColon = inputLine.indexOf(':');
		String key = "";
		try {
			key = inputLine.substring(0, idxColon);
		} catch (StringIndexOutOfBoundsException e) {
			// TODO Handle Exception
		}
		KitLstToken token = (KitLstToken) tokenMap.get(key);

		if (inputLine.startsWith("STARTPACK:")) {
			target = new Kit();
			target.setSourceCampaign(source.getCampaign());
			target.setSourceURI(source.getURI());
			if (kitPrereq != null) {
				target.addPreReq(KitLoader.kitPrereq);
			}
			if (globalTokens != null) {
				for (String tag : globalTokens) {
					PObjectLoader.parseTag(target, tag);
				}
			}
		}

		if (token != null) {
			final String value = inputLine.substring(idxColon + 1);
			LstUtils.deprecationCheck(token, target, value);
			if (!token.parse(target, value, source.getURI())) {
				Logging.errorPrint("Error parsing Kit tag "
						+ target.getDisplayName() + ':' + source.getURI()
						+ ':' + inputLine + "\"");
			}
		} else {
			Logging.errorPrint("Unknown kit info " + source.toString() + ":"
					+ " \"" + inputLine + "\"");
		}

		return target;
	}

	static List<String> globalTokens = null;

	static Prerequisite kitPrereq = null;

	public static void addGlobalToken(String string) {
		if (globalTokens == null) {
			globalTokens = new ArrayList<String>();
		}
		globalTokens.add(string);
	}

	public static void setKitPrerequisite(Prerequisite p) {
		kitPrereq = p;
	}

	public static void clearGlobalTokens() {
		globalTokens = null;
	}

	public static void clearKitPrerequisites() {
		kitPrereq = null;
	}
	
	@Override
	protected void loadLstFile(LoadContext context, CampaignSourceEntry cse) {
		clearGlobalTokens();
		clearKitPrerequisites();
		super.loadLstFile(context, cse);
	}
}
