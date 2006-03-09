/*
 * SizemultToken.java
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
 * Current Ver: $Revision: 1.3 $
 * Last Editor: $Author: soulcatcher $
 * Last Edited: $Date: 2006/02/16 01:03:16 $
 *
 */
package plugin.lsttokens.load;

import java.util.StringTokenizer;

import pcgen.core.system.LoadInfo;
import pcgen.persistence.lst.LoadInfoLstToken;


/**
 * <code>SizemultToken</code>
 *
 * @author  Devon Jones <soulcatcher@evilsoft.org>
 */
public class SizemultToken implements LoadInfoLstToken {

	public String getTokenName() {
		return "SIZEMULT";
	}

	public boolean parse(LoadInfo loadInfo, String value) {
		final StringTokenizer token = new StringTokenizer(value, "|");
		if(token.countTokens() != 2) {
			return false;
		}
		try {
			String size = token.nextToken().substring(0,1);
			Float num = new Float(token.nextToken());
			loadInfo.addSizeAdjustment(size, num);
		}
		catch(Exception e) {
			return false;
		}
		return true;
	}
}
