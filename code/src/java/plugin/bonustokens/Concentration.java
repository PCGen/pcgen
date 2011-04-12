/*
 * CasterLevel.java
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
 * Current Ver: $Revision: 12683 $
 * Last Editor: $Author: thpr $
 * Last Edited: $Date: 2010-07-25 00:47:06 +0200 (So, 25 Jul 2010) $
 *
 */
package plugin.bonustokens;

import pcgen.core.bonus.BonusObj;
import pcgen.rules.context.LoadContext;

/**
 * <code>CasterLevel</code>
 *
 * @author  Stefan Radermacher <zaister@users.sourceforge.net>
 */
public final class Concentration extends BonusObj
{
	@Override
	protected boolean parseToken(LoadContext context, final String token)
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

	@Override
	protected String unparseToken(final Object obj)
	{
		return (String) obj;
	}

	@Override
	public String getBonusHandled()
	{
		return "CONCENTRATION";
	}
}
