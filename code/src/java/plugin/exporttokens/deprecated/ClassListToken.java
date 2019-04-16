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
 */
package plugin.exporttokens.deprecated;

import pcgen.cdom.base.Constants;
import pcgen.core.PCClass;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;

/**
 * Deal with CLASSLIST token
 */
public class ClassListToken extends AbstractExportToken
{
	@Override
	public String getTokenName()
	{
		return "CLASSLIST";
	}

	@Override
	public String getToken(String tokenSource, CharacterDisplay display, ExportHandler eh)
	{
		return getClassListToken(display);
	}

	/**
	 * Get the class list token value for the PC
	 * @param display
	 * @return token value
	 */
	public static String getClassListToken(CharacterDisplay display)
	{
		StringBuilder returnString = new StringBuilder();
		boolean firstLine = true;

		for (PCClass pcClass : display.getClassSet())
		{
			if (!firstLine)
			{
				returnString.append(' ');
			}

			firstLine = false;

			String subClassKey = display.getSubClassName(pcClass);
			if (subClassKey == null || Constants.NONE.equals(subClassKey) || "".equals(subClassKey))
			{
				returnString.append(OutputNameFormatting.getOutputName(pcClass));
			}
			else
			{
				returnString.append(pcClass.getSubClassKeyed(subClassKey).getDisplayName());
			}

			returnString.append(display.getLevel(pcClass));
		}

		return returnString.toString();
	}
}
