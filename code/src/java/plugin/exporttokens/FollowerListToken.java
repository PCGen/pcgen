/*
 * FollowerOfToken.java
 * Copyright 2006 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 *
 * Created on Jun 17, 2006
 *
 * $Id: InfoKnownSpells.java 1030 2006-05-26 08:25:10Z jdempsey $
 *
 */
package plugin.exporttokens;

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.Follower;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.FileAccess;
import pcgen.io.exporttoken.AbstractExportToken;

/**
 * Deal with FOLLOWERLIST Token
 * 
 *
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006-05-26 18:25:10 +1000 (Fri, 26 May 2006) $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 1030 $
 */
public class FollowerListToken extends AbstractExportToken
{
	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "FOLLOWERLIST";
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, CharacterDisplay display,
		ExportHandler eh)
	{
		return getFollowerListToken(display);
	}

	/**
	 * Get FOLLOWERLIST Token
	 *
	 * @param pc The character to be queried
	 * @return The list of followers.
	 */
	public static String getFollowerListToken(CharacterDisplay display)
	{
		StringBuilder buf = new StringBuilder();

		boolean needComma = false;

		for (Follower aF : display.getFollowerList())
		{
			for (PlayerCharacter nPC : Globals.getPCList())
			{
				if (aF.getFileName().equals(nPC.getFileName()))
				{
					if (needComma)
					{
						buf.append(", ");
					}

					buf.append(FileAccess.filterString(nPC.getName()));
					needComma = true;
				}
			}
		}

		return buf.toString();
	}
}
