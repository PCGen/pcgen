/*
 * ToHit.java
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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.lsttokens.deprecated;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.rules.context.LoadContext;

/**
 * <code>ToHit</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */
public final class ToHit extends BonusObj implements DeprecatedToken
{
	private static final String[] bonusTags = {"TOHIT"};

	@Override
	protected boolean parseToken(LoadContext context, final String token)
	{
		for (int i = 0; i < bonusTags.length; ++i)
		{
			if (bonusTags[i].equals(token))
			{
				addBonusInfo(Integer.valueOf(i));

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
			return bonusTags[((Integer) obj).intValue()];
		}

		return (String) obj;
	}

	@Override
	public String getBonusHandled()
	{
		return "TOHIT";
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.DeprecatedToken#getMessage(pcgen.core.PObject, java.lang.String)
	 */
	public String getMessage(CDOMObject obj, String value)
	{
		return "You should use BONUS:COMBAT|TOHIT|y instead.";
	}
}
