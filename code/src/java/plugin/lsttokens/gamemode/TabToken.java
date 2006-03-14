/*
 * TabToken.java
 * Copyright 2005 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on September 17, 2005, 2:31 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;
import pcgen.persistence.lst.TabLoader;


/**
 * <code>TabToken</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */
public class TabToken implements GameModeLstToken {

	public String getTokenName() {
		return "TAB";
	}

	public boolean parse(GameMode gameMode, String value) {
		try {
			TabLoader tabLoader = new TabLoader();
			tabLoader.parseLine(gameMode, "TAB:" + value);
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
}
