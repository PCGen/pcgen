/*
 * EndManualWhitespaceToken.java
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
 */
package plugin.exporttokens.deprecated;

import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;

/**
 * Token that represents the end of author controlled whitespace
 */
public class EndManualWhitespaceToken extends AbstractExportToken
{
	@Override
	public String getTokenName()
	{
		return "ENDMANUALWHITESPACE";
	}

	@Override
	public String getToken(String tokenSource, CharacterDisplay display, ExportHandler eh)
	{
		eh.setManualWhitespace(false);
		return "";
	}

}
