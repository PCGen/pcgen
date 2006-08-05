/*
 * SkillToken.java
 * Copyright 2004 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on Aug 5, 2004
 *
 * $Id$
 *
 */
package plugin.exporttokens;

import pcgen.core.PlayerCharacter;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.SkillToken;
import pcgen.util.Logging;

/**
 * <code>SkillLevelToken</code>  outputs the number of skills
 * the character obtained at the specified level. The format 
 * for this tag is SKILLLEVEL.x.TOTAL 
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */

// SKILLLEVEL
public class SkillLevelToken extends SkillToken
{
	/** token name */
	public static final String TOKEN_NAME = "SKILLLEVEL";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return TOKEN_NAME;
	}
	
	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		SkillDetails details = buildSkillDetails(tokenSource);

		if (details.getProperties().length > 0
			&& "TOTAL".equals(details.getProperties()[0]))
		{
			final int aLevelOffset;

			try
			{
				aLevelOffset = Integer.parseInt(details.getSkillId()) - 1;

				if ((aLevelOffset >= pc.getLevelInfoSize())
					|| (aLevelOffset < 0))
				{
					return "0";
				}

				final PCLevelInfo wLevelInfo = pc.getLevelInfo()
					.get(aLevelOffset);
				final int wOutput = wLevelInfo.getSkillPointsGained();
				return Integer.toString(wOutput);
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Error replacing SKILLLEVEL." + tokenSource,
					nfe);

				return "";
			}
		}

		return "";
	}

}