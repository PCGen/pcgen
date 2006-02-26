/*
 * Move.java
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
 * Created on December 13, 2002, 9:19 AM
 *
 * Current Ver: $Revision: 1.17 $
 * Last Editor: $Author: zaister $
 * Last Edited: $Date: 2006/01/04 09:42:16 $
 *
 */
package pcgen.core.bonus;

import pcgen.core.Constants;

/**
 * <code>Move</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 **/
final class Move extends BonusObj
{
	private static final String[] bonusHandled =
		{
		    "MOVE",
			"MOVEADD",
			"MOVEMULT",
			"POSTMOVEADD"
		};

	private static final String[] bonusTags = { Constants.s_LOAD_LIGHT, Constants.s_LOAD_MEDIUM, Constants.s_LOAD_HEAVY, Constants.s_LOAD_OVERLOAD };

	boolean parseToken(final String token)
	{
		for (int i = 0; i < bonusTags.length; ++i)
		{
			if (bonusTags[i].equals(token))
			{
				addBonusInfo(new Integer(i));

				return true;
			}
		}

		if (token.startsWith("TYPE="))
		{
			addBonusInfo(token.replace('=', '.'));
		}
		else
		{
			addBonusInfo(token);
		}

		return true;
	}

	String unparseToken(final Object obj)
	{
		if (obj instanceof Integer)
		{
			return bonusTags[((Integer) obj).intValue()];
		}

		return (String) obj;
	}

	String[] getBonusesHandled()
	{
		return bonusHandled;
	}
}
