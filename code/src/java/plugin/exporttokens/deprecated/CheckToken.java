/*
 * CheckToken.java
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.exporttokens.deprecated;

import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.PCCheck;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.util.Delta;

/**
 * Deal with token:
 *
 * CHECK.x.y.y.z
 * x = FORTITUDE|WILL|REFLEX|0|1|2
 * y = TOTAL|BASE|MISC|EPIC|MAGIC|RACE|FEATS|STATMOD|NOEPIC|NOMAGIC|NORACE|NOFEATS|NOSTAT|NOSTATMOD
 */
public class CheckToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "CHECK";

	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	@Override
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		// If there is a .NOSIGN then replace that with an empty String
		boolean isNosign = (tokenSource.lastIndexOf(".NOSIGN") >= 0);
		tokenSource = tokenSource.replaceAll(".NOSIGN", "");

		StringTokenizer aTok = new StringTokenizer(tokenSource, ".", false);
		aTok.nextToken();

		// Get the Save type (x)
		String saveType = aTok.nextToken();

		// Gather up the modifications (y, y, z) 
		StringBuilder saveModsBuf = new StringBuilder();
		while (aTok.hasMoreTokens())
		{
			if (saveModsBuf.length() > 0)
			{
				saveModsBuf.append('.');
			}
			saveModsBuf.append(aTok.nextToken());
		}
		String saveMods = saveModsBuf.toString();

		// If its just the name then return that
		if ("NAME".equals(saveMods))
		{
			return getNameToken(saveType).toString();
		}
		if (isNosign)
		{
			return String.valueOf(getCheckToken(pc, saveType, saveMods));
		}
		return Delta.toString(getCheckToken(pc, saveType, saveMods));
	}

	/**
	 * Get the token.  If no Save Mods (y.y.z) are supplied then a TOTAL is calculated.
	 * 
	 * @param pc
	 * @param saveType
	 * @param saveMods
	 * @return int
	 */
	public static int getCheckToken(PlayerCharacter pc, String saveType, String saveMods)
	{
		PCCheck check = getNameToken(saveType);
		return pc.calculateSaveBonus(check, "".equals(saveMods) ? "TOTAL" : saveMods);
	}

	/**
	 * Get the token name
	 * @param saveType
	 * @return token name
	 */
	public static PCCheck getNameToken(String saveType)
	{
		try
		{
			int i = Integer.parseInt(saveType);

			List<PCCheck> checkList =
					Globals.getContext().getReferenceContext().getSortkeySortedCDOMObjects(PCCheck.class);
			if ((i >= 0) && (i < checkList.size()))
			{
				return checkList.get(i);
			}
		}
		catch (NumberFormatException e)
		{
			// just means it's a name, not a number
			return Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCCheck.class, saveType);
		}
		return null;
	}
}
