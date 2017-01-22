/*
 * SpellCastMult.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 *
 */
package plugin.bonustokens;

import pcgen.cdom.base.Constants;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.util.SpellCastInfo;
import pcgen.rules.context.LoadContext;

/**
 */
public final class SpellCastMult extends BonusObj
{
	/*
	 * CLASS.<classname OR Any>;LEVEL.<level>
	 * TYPE.<type>;LEVEL.<level>
	 * @param token
	 * @return
	 */

	/**
	 * Parse the bonus token.
	 * @see pcgen.core.bonus.BonusObj#parseToken(LoadContext, java.lang.String)
	 * @return True if successfully parsed.
	 */
	@Override
	protected boolean parseToken(LoadContext context, final String token)
	{
		int idx = token.indexOf(Constants.LST_SEMI_LEVEL_EQUAL);

		if (idx < 0)
		{
			idx = token.indexOf(Constants.LST_SEMI_LEVEL_DOT);
		}

		if (idx < 0)
		{
			return false;
		}

		final String level = token.substring(idx + Constants.SUBSTRING_LENGTH_SEVEN);

		addBonusInfo(new SpellCastInfo(token.substring(0, idx), level));

		return true;
	}

	/**
	 * Unparse the bonus token.
	 * @see pcgen.core.bonus.BonusObj#unparseToken(java.lang.Object)
	 * @param obj The object to unparse
	 * @return The unparsed string.
	 */
	@Override
	protected String unparseToken(final Object obj)
	{
		final StringBuilder sb = new StringBuilder(30);
		final SpellCastInfo sci = (SpellCastInfo) obj;

		if (sci.getType() != null)
		{
			sb.append(Constants.LST_TYPE_DOT).append(((SpellCastInfo) obj).getType());
		}
		else if (sci.getPcClassName() != null)
		{
			sb.append(Constants.LST_CLASS_DOT).append(((SpellCastInfo) obj).getPcClassName());
		}

		sb.append(Constants.LST_SEMI_LEVEL_DOT).append(((SpellCastInfo) obj).getLevel());

		return sb.toString();
	}

	/**
	 * Return the bonus tag handled by this class.
	 * @return The bonus handled by this class.
	 */
	@Override
	public String getBonusHandled()
	{
		return "SPELLCASTMULT";
	}
}
