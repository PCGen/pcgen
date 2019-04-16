/*
 * PaperInfoToken.java
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

import pcgen.core.SettingsHandler;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;
import pcgen.util.Logging;

//PAPERINFO
public class InvalidTextToken extends AbstractExportToken
{
	@Override
	public String getTokenName()
	{
		return "INVALIDTEXT";
	}

	@Override
	public String getToken(String tokenSource, CharacterDisplay display, ExportHandler eh)
	{
		String sourceText = tokenSource.substring(12);

		switch (sourceText)
		{
			case "TOHIT":
				return SettingsHandler.getInvalidToHitText();
			case "DAMAGE":
				return SettingsHandler.getInvalidDmgText();
			default:
				Logging.errorPrint("Invalid INVALIDTEXT token:" + tokenSource);
				return "";
		}
	}
}
