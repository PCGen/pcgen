/*
 * EncumbranceToken.java
 * Copyright 2006 (C) Devon Jones <soulcatcher@evilsoft.org>
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
 * Created on September 2, 2002, 8:02 AM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.lsttokens.load;

import java.util.StringTokenizer;

import pcgen.core.system.LoadInfo;
import pcgen.persistence.lst.LoadInfoLstToken;


/**
 * <code>EncumbranceToken</code>
 *
 * @author  Devon Jones <soulcatcher@evilsoft.org>
 */
public class EncumbranceToken implements LoadInfoLstToken {

	public String getTokenName() {
		return "ENCUMBRANCE";
	}

	public boolean parse(LoadInfo loadInfo, String value) {
		final StringTokenizer token = new StringTokenizer(value, "|");
		if(token.countTokens() < 2 || token.countTokens() > 4) {
			return false;
		}

		try {
			String moveFormula = "";
			Integer checkPenalty = Integer.valueOf(0);
			String type = token.nextToken();
			String number = token.nextToken();

			if (token.hasMoreTokens() && value.indexOf("||") == -1) {
				moveFormula = token.nextToken();
			}

			if (token.hasMoreTokens()) {
				checkPenalty = Integer.valueOf(token.nextToken());
			}

			double mult = 0;
			if (number.indexOf("/") == -1) {
				mult = Double.parseDouble(number);
			}
			else {
				final StringTokenizer numTok = new StringTokenizer(number, "/");
				mult = Double.parseDouble(numTok.nextToken()) / Double.parseDouble(numTok.nextToken());
			}
			loadInfo.addLoadMultiplier(type.toUpperCase(), new Float(mult), moveFormula, checkPenalty);
		}
		catch(Exception e) {
			return false;
		}
		return true;
	}
}
