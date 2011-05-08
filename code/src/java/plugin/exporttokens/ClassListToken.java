/*
 * ClassListToken.java
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

import pcgen.cdom.base.Constants;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/**
 * Deal with CLASSLIST token
 */
public class ClassListToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "CLASSLIST"; //$NON-NLS-1$

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		return getClassListToken(pc);
	}

	/**
	 * Get the class list token value for the PC
	 * @param pc
	 * @return token value
	 */
	public static String getClassListToken(PlayerCharacter pc)
	{
		StringBuffer returnString = new StringBuffer();
		boolean firstLine = true;

		for (PCClass pcClass : pc.getClassSet())
		{
			if (!firstLine)
			{
				returnString.append(" ");
			}

			firstLine = false;

			String subClassKey = pc.getSubClassName(pcClass);
			if (subClassKey == null || Constants.NONE.equals(subClassKey)
					|| "".equals(subClassKey))
			{
				returnString.append(OutputNameFormatting.getOutputName(pcClass));
			}
			else
			{
				returnString.append(pcClass.getSubClassKeyed(
						subClassKey).getDisplayName());
			}

			returnString.append(pc.getLevel(pcClass));
		}

		return returnString.toString();
	}
}
