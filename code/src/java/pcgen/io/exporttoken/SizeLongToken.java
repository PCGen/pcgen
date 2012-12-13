/*
 * SizeLongToken.java
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
package pcgen.io.exporttoken;

import pcgen.core.SizeAdjustment;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;

/**
 * SIZELONG for export
 */
public class SizeLongToken extends AbstractExportToken
{
	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "SIZELONG";
	}

	//TODO: this really should be in the Size token as SIZE.LONG
	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, CharacterDisplay display,
		ExportHandler eh)
	{
		return getSizeLongToken(display);
	}

	/**
	 * Get value SIZELONG token
	 * @param pc
	 * @return value SIZELONG token
	 */
	public static String getSizeLongToken(CharacterDisplay display)
	{
		final SizeAdjustment sadj = display.getSizeAdjustment();
		if (sadj != null)
		{
			return sadj.getDisplayName();
		}
		return "";
	}
}
