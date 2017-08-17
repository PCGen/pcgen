/*
 * SpellListDcToken.java
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
 */
package plugin.exporttokens;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.SpellListToken;

/**
 * {@code SpellListDcToken} outputs the DC casting that level
 * of spell for the indicated class.
 */

public class SpellListDcToken extends SpellListToken
{
	/** token name */
	public static final String TOKENNAME = "SPELLLISTDC";

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
		StringBuilder retValue = new StringBuilder();

		SpellListTokenParams params =
				new SpellListTokenParams(tokenSource,
					SpellListToken.SPELLTAG_DC);

		final CDOMObject aObject = pc.getSpellClassAtIndex(params.getClassNum());

		if (aObject != null)
		{
			PCClass aClass = null;

			if (aObject instanceof PCClass)
			{
				aClass = (PCClass) aObject;
			}

			int DC = pc.getDC(new Spell(), aClass, params.getLevel(), 0, aClass);

			retValue.append(Integer.toString(DC));
		}

		return retValue.toString();
	}
}
