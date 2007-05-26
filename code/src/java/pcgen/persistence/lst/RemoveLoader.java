/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
 *   may be derived from a Loader file
 *    Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 */
package pcgen.persistence.lst;

import java.util.Map;

import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

public final class RemoveLoader
{

	private RemoveLoader()
	{
		super();
	}

	/**
	 * This method is static so it can be used by the REMOVE Token.
	 * 
	 * @param target
	 * @param lstLine
	 * @param source
	 * @throws PersistenceLayerException
	 */
	public static boolean parseLine(PObject target, String key, String value,
		int level)
	{
		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(RemoveLstToken.class);
		RemoveLstToken token = (RemoveLstToken) tokenMap.get(key);
		if (token != null)
		{
			LstUtils.deprecationCheck(token, target, value);
			if (!token.parse(target, value, level))
			{
				Logging
					.errorPrint("Error parsing REMOVE: " + key + ":" + value);
				return false;
			}
			return true;
		}
		else
		{
			Logging
				.errorPrint("Error parsing REMOVE, invalid SubToken: " + key);
			return false;
		}
	}
}
