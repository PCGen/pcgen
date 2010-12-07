/*
 * PointBuyLoader.java
 * Copyright 2003 (C) David Hibbs <sage_sam@users.sourceforge.net>
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
 * Created on October 08, 2003, 12:00 PM
 *
 * Current Ver: $Revision$ <br>
 * Last Editor: $Author$ <br>
 * Last Edited: $Date$
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.util.Map;

import pcgen.core.GameMode;
import pcgen.core.SystemCollections;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * This class is a LstFileLoader used to load point-buy methods.
 *
 * <p>
 * Current Ver: $Revision$ <br>
 * Last Editor: $Author$ <br>
 * Last Edited: $Date$
 *
 * @author ad9c15
 */
public class PointBuyLoader extends LstLineFileLoader
{
	/**
	 * Constructor for PointBuyLoader.
	 */
	public PointBuyLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.net.URL, java.lang.String)
	 */
	@Override
	public void parseLine(LoadContext context, String lstLine, URI sourceURI)
	{
		GameMode thisGameMode =
				SystemCollections.getGameModeNamed(getGameMode());
		final int idxColon = lstLine.indexOf(':');
		if (idxColon < 0)
		{
			return;
		}

		final String key = lstLine.substring(0, idxColon);
		final String value = lstLine.substring(idxColon + 1);
		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(PointBuyLstToken.class);
		PointBuyLstToken token = (PointBuyLstToken) tokenMap.get(key);
		if (token != null)
		{
			LstUtils.deprecationCheck(token, thisGameMode.getName(), sourceURI,
					value);
			if (!token.parse(thisGameMode, value, sourceURI))
			{
				Logging.errorPrint("Error parsing point buy method "
					+ thisGameMode.getName() + '/' + sourceURI.toString() + ':'
					+ " \"" + lstLine + "\"");
			}
		}
		else
		{
			Logging.errorPrint("Illegal point buy method info "
				+ thisGameMode.getName() + '/' + sourceURI.toString() + ':'
				+ " \"" + lstLine + "\"");
		}
	}
}
