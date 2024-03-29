/*
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
 */
package plugin.bonustokens;

import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.bonus.BonusObj;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * Handles the BONUS:UDam token.
 */
public final class UDam extends BonusObj
{
	@Override
	protected boolean parseToken(LoadContext context, final String token)
	{
		if (token.startsWith(Constants.LST_CLASS_EQUAL) || token.startsWith(Constants.LST_CLASS_DOT))
		{
			addBonusInfo(token.substring(Constants.SUBSTRING_LENGTH_SIX));
			return true;
		}

		Logging.errorPrint("BONUS:UDAM syntax must have Info (2nd arg to BONUS) start with CLASS= or CLASS. ");
		return false;
	}

	@Override
	protected String unparseToken(final Object obj)
	{
		if (obj instanceof String sObj)
		{
			final AbstractReferenceContext ref = Globals.getContext().getReferenceContext();
			final PCClass aClass = ref.silentlyGetConstructedCDOMObject(PCClass.class, sObj);

			if (aClass != null)
			{
				replaceBonusInfo(obj, aClass);
			}
			return Constants.LST_CLASS_DOT + obj;
		}
		return Constants.LST_CLASS_DOT + ((PCClass) obj).getKeyName();
	}

	/**
	 * Return the bonus tag handled by this class.
	 * @return The bonus handled by this class.
	 */
	@Override
	public String getBonusHandled()
	{
		return "UDAM";
	}
}
