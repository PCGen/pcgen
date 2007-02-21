/*
 * SubClassLoader.java
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 * Created on February 17, 2007
 *
 * $Id: SubClassLoader.java 2077 2007-01-27 16:45:58Z thpr $
 */
package pcgen.persistence.lst;

import java.util.Map;

import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

public final class AutoLoader
{
	private AutoLoader()
	{
		//Utility Class, no construction needed
	}

	/**
	 * This method is static so it can be used by the AUTO Token.
	 * @param target
	 * @param lstLine
	 * @param source
	 * @throws PersistenceLayerException
	 */
	public static boolean parseLine(PObject target, String key, String value)
	{
		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(AutoLstToken.class);
		AutoLstToken token = (AutoLstToken) tokenMap.get(key);
		if (token != null)
		{
			LstUtils.deprecationCheck(token, target, value);
			if (!token.parse(target, value))
			{
				Logging.errorPrint("Error parsing AUTO: " + key + ":" + value);
				return false;
			}
			return true;
		}
		else
		{
			Logging.errorPrint("Error parsing AUTO, invalid SubToken: " + key);
			return false;
		}
	}
}
