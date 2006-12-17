/*
 * LevelAbilityToken.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Created on March 6, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit.levelability;

import pcgen.core.kit.KitLevelAbility;
import pcgen.persistence.lst.KitLevelAbilityLstToken;
import pcgen.util.Logging;

/**
 * Level Ability token (a component of Kits)
 */
public class LevelAbilityToken implements KitLevelAbilityLstToken
{
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "LEVELABILITY";
	}

	/**
	 * parse
	 * 
	 * @param kitLA
	 *            KitLevelAbility
	 * @param value
	 *            String
	 * @return boolean
	 */
	public boolean parse(KitLevelAbility kitLA, String value)
	{
		Logging.errorPrint("Ignoring second LEVELABILITY tag \"" + value
			+ "\" in Kit.");
		return false;
	}
}
