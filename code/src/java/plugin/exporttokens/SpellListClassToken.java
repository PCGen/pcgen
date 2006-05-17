/*
 * SpellListClassToken.java
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
 * Created on Jul 15, 2004
 *
 * $Id$
 *
 */
package plugin.exporttokens;

import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.SpellListToken;

import java.util.StringTokenizer;

/**
 * <code>SpellListClassToken</code> outputs either the name of the
 * spellcaster classname, or the effective casting level, including
 * bonus levels from other classes. The level is output if the token
 * ends in .LEVEL
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */

public class SpellListClassToken extends SpellListToken
{

	/** Token name */
	public static final String TOKENNAME = "SPELLLISTCLASS";

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
		int i;
		StringBuffer retValue = new StringBuffer();

		// Determine the tag type
		final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken();

		i = Integer.parseInt(aTok.nextToken());


		//
		final PObject aObject = pc.getSpellClassAtIndex(i);

		if (aObject != null)
		{
			PCClass aClass = null;

			if (aObject instanceof PCClass)
			{
				aClass = (PCClass) aObject;
			}

			if ((aClass != null) && tokenSource.endsWith("LEVEL"))
			{
				retValue.append(String.valueOf(aClass.getLevel()
					+ (int) pc.getTotalBonusTo("PCLEVEL", aClass
						.getKeyName())));
			}
			else
			{
				retValue.append(aObject.getOutputName());
			}
		}

		return retValue.toString();
	}

}
