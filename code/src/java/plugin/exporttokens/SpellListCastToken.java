/*
 * SpellListCastToken.java
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
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.SpellListToken;

/**
 * {@code SpellListCastToken} outputs the number of spells
 * that can cast for the specified class and level.
 */
///SPELLLISTCAST0.0 KNOWN0.0 BOOK0.0 TYPE0
public class SpellListCastToken extends SpellListToken
{

	/** Token name */
	public static final String TOKENNAME = "SPELLLISTCAST";

	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	@Override
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		StringBuilder retValue = new StringBuilder();

		SpellListTokenParams params = new SpellListTokenParams(tokenSource, SpellListToken.SPELLTAG_CAST);

		final CDOMObject aObject = pc.getSpellClassAtIndex(params.getClassNum());

		if (aObject != null)
		{
			retValue.append(getCastNum(aObject, params.getLevel(), pc));
		}

		return retValue.toString();
	}

	/**
	 * Retrieve the number of spells the pc may cast of the specified
	 * level of the supplied class, including bonus spells. eg 4+1
	 *
	 * @param aObject The class
	 * @param level The spell level
	 * @param pc The character being queried
	 * @return The number of spells allowed to be cast.
	 */
	private String getCastNum(CDOMObject aObject, int level, PlayerCharacter pc)
	{
		String castNum = "--";

		if (aObject instanceof PCClass aClass)
		{
			//			castNum = String.valueOf(aClass.getCastForLevel(level, Globals.getDefaultSpellBook(), pc))
			castNum = String.valueOf(
				pc.getSpellSupport(aClass).getCastForLevel(level, Globals.getDefaultSpellBook(), true, false, pc))
				+ pc.getSpellSupport(aClass).getBonusCastForLevelString(level, Globals.getDefaultSpellBook(), pc);
		}

		return castNum;
	}

}
