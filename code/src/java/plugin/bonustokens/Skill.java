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
import pcgen.core.bonus.BonusObj;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;

/**
 * Handles the BONUS:SKILL token.
 */
public final class Skill extends BonusObj
{
	private static final String[] BONUS_TAGS = {"LIST", "ALL"};

	private static final Class<pcgen.core.Skill> SKILL_CLASS = pcgen.core.Skill.class;

	@Override
	protected boolean parseToken(LoadContext context, final String token)
	{
		for (int i = 0; i < BONUS_TAGS.length; ++i)
		{
			if (BONUS_TAGS[i].equals(token))
			{
				addBonusInfo(i);

				return true;
			}
		}

		if (token.startsWith("STAT=") || token.startsWith(Constants.LST_TYPE_EQUAL))
		{
			addBonusInfo(token.replace('=', '.'));
		}
		else
		{
			addBonusInfo(token);
		}

		if (!token.startsWith("STAT.") && !token.equals("%CHOICE") && !token.startsWith("STAT=") && !token.equals("%LIST") && !token.equals("%VAR") && !token.equals("TYPE=%LIST"))
		{
			//This is done entirely for the side effects
			context.forgetMeNot(TokenUtilities.getReference(context, SKILL_CLASS, token));
		}

		return true;
	}

	@Override
	protected String unparseToken(final Object obj)
	{
		if (obj instanceof Integer)
		{
			return BONUS_TAGS[(Integer) obj];
		}

		return (String) obj;
	}

	/**
	 * Return the bonus tag handled by this class.
	 * @return The bonus handled by this class.
	 */
	@Override
	public String getBonusHandled()
	{
		return "SKILL";
	}

	@Override
	protected boolean requiresRealCaseTarget()
	{
		return true;
	}
}
