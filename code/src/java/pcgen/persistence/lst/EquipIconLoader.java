/**
 * Copyright James Dempsey, 2011
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
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.util.Map;

import pcgen.core.GameMode;
import pcgen.core.SystemCollections;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * The Class {@code EquipIconLoader} loads the equipIcon.lst game mode file.
 *
 * 
 */
public class EquipIconLoader extends LstLineFileLoader
{

	@Override
	public void parseLine(LoadContext context, String lstLine, URI sourceURI)
    {
		final int idxColon = lstLine.indexOf(':');
		if (idxColon < 0)
		{
			return;
		}
		final GameMode game = SystemCollections.getGameModeNamed(gameMode);

		final String key = lstLine.substring(0, idxColon);
		final String value = lstLine.substring(idxColon + 1);
		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(EquipIconLstToken.class);
		EquipIconLstToken token = (EquipIconLstToken) tokenMap.get(key);
		if (token != null)
		{
			LstUtils.deprecationCheck(token, key, sourceURI, lstLine);
			if (!token.parse(game, value, sourceURI))
			{
				Logging.errorPrint("Error parsing EquipIcon object: " + lstLine + " at " + sourceURI.toString());
			}
		}
		else
		{
			Logging.errorPrint("Illegal EquipIcon object: " + lstLine + " at " + sourceURI.toString());
		}
	}

}
