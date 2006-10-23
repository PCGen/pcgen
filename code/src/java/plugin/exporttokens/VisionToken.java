/*
 * VisionToken.java
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
 *
 * Created on December 15, 2003, 12:21 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.exporttokens;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

import java.util.SortedSet;
import java.util.StringTokenizer;

/**
 * <code>VisionToken</code> produces the output for the output token 
 * VISION.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author Devon Jones <soulcatcher@evilsoft.org>
 * @version $Revision$
 */
public class VisionToken extends Token
{
	/** Token Name */
	public static final String TOKENNAME = "VISION";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken();

		int visionIndex = 0;
		int startIndex = 0;

		if (aTok.hasMoreTokens())
		{
			try
			{
				visionIndex = Integer.parseInt(aTok.nextToken());
				startIndex = visionIndex;
			}
			catch (NumberFormatException e)
			{
				//TODO: Should this really be ignored?
			}
		}
		else
		{
			visionIndex = pc.getVisiontypeList().size();
		}

		StringBuffer visionBuf = new StringBuffer();

		for (int i = startIndex; i <= visionIndex; i++)
		{
			if ((i > startIndex) && (i < visionIndex))
			{
				visionBuf.append(", ");
			}

			visionBuf.append(getVisionToken(pc, i));
		}

		return visionBuf.toString();
	}

	/**
	 * Produce the output for a vision type.
	 * 
	 * @param pc The character being procesed.
	 * @param visionIndex The index of the vision type to be output.
	 * @return A description of the vision type suitable for output.
	 */
	public static String getVisionToken(PlayerCharacter pc, int visionIndex)
	{
		SortedSet<String> visionSet = pc.getVisiontypeList();

		if ((visionIndex >= 0) && (visionIndex < visionSet.size()))
		{
			return visionSet.toArray()[visionIndex].toString();
		}

		return "";
	}
}
