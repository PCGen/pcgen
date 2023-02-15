/*
 * Copyright 2003 (C) Greg Bingleman <byngl@hotmail.com>
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
package pcgen.core.bonus;

import pcgen.rules.context.LoadContext;

/**
 * {@code MultiTagBonusObj}
 */
public abstract class MultiTagBonusObj extends BonusObj
{

	@Override
	protected boolean parseToken(LoadContext context, final String token)
	{
		for (int i = 0; i < getBonusTagLength(); ++i)
		{
			if (getBonusTag(i).equals(token))
			{
				addBonusInfo(i);

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

	@Override
	protected String unparseToken(final Object obj)
	{
		if (obj instanceof Integer)
		{
			return getBonusTag((Integer) obj);
		}

		return (String) obj;
	}

	protected abstract String getBonusTag(int tagNumber);

	protected abstract int getBonusTagLength();

}
