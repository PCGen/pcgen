/*
 * PPCost.java
 * Copyright 2005 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on January 27, 2005, 11:26 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.bonustokens;

import pcgen.core.bonus.BonusObj;


/**
 * <code>PPCost</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */
public final class PPCost extends BonusObj
{
	private static final String[] bonusHandled =
		{
			"PPCOST"
		};

	protected boolean parseToken(final String token)
	{
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

	protected String unparseToken(final Object obj)
	{
		return (String) obj;
	}

	protected String[] getBonusesHandled()
	{
		return bonusHandled;
	}
}
