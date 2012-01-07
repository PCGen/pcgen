/*
 * CompanionModLoader.java
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
 *
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @Created on July 10th, 2002, 3:55 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Globals;
import pcgen.core.character.CompanionMod;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * Loads the level based Mount and Familiar benefits
 *
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision$
 **/
public class CompanionModLoader extends LstObjectFileLoader<CompanionMod> 
{

	@Override
	protected void addGlobalObject(CDOMObject cdo) {
		//This is commented out to avoid problems - see Tracker 
		//  - thpr 1/11/07
//		final CompanionMod cm = Globals.getCompanionMod(pObj.getKeyName());
//		if (cm == null) {
			Globals.addCompanionMod((CompanionMod) cdo);
//		}
	}

	@Override
	protected CompanionMod getObjectKeyed(LoadContext context, String aKey) {
		return null;
		//This is commented out to avoid problems - see Tracker 
		//  - thpr 1/11/07
		//return Globals.getCompanionMod(aKey);
	}

	@Override
	public CompanionMod parseLine(LoadContext context, CompanionMod cmpMod,
			String inputLine, SourceEntry source) throws PersistenceLayerException {
		if (cmpMod == null) {
			cmpMod = new CompanionMod();
		}
		
		final StringTokenizer colToken = new StringTokenizer(inputLine,
				SystemLoader.TAB_DELIM);
		
		String name = null;
		cmpMod.put(ObjectKey.SOURCE_CAMPAIGN, source.getCampaign());
		cmpMod.setSourceURI(source.getURI());

		while (colToken.hasMoreTokens())
		{
			final String token = colToken.nextToken().trim();
			final int colonLoc = token.indexOf(':');
			// Companion mods don't have a name, but instead start straight into the first token
			if (name == null)
			{
				name = token;
				cmpMod.setName(name);
			}
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
			if (context.processToken(cmpMod, key, value))
			{
				context.commit();
			}
			else
			{
				context.rollback();
				Logging.replayParsedMessages();
			}
			Logging.clearParseMessages();
		}
		
		completeObject(context, source, cmpMod);
		return null;
	}

	@Override
	protected void performForget(LoadContext context, CompanionMod objToForget) {
		super.performForget(context, objToForget);
		Globals.removeCompanionMod(objToForget);
	}
}
