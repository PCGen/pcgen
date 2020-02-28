/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.pcclass;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCClass;
import pcgen.core.SettingsHandler;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with CR Token
 */
public class RoleToken extends AbstractNonEmptyToken<PCClass> implements CDOMPrimaryToken<PCClass>
{

	/**
	 * Get the token name
	 */
	@Override
	public String getTokenName()
	{
		return "ROLE";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, PCClass pcc, String value)
	{
		ParseResult pr = checkForIllegalSeparator('.', value);
		if (!pr.passed())
		{
			return pr;
		}

		StringTokenizer aTok = new StringTokenizer(value, Constants.DOT, false);
		while (aTok.hasMoreTokens())
		{
			String role = aTok.nextToken();
			if (SettingsHandler.getGameAsProperty().get().getMonsterRoleList().contains(role))
			{
				context.getObjectContext().addToList(pcc, ListKey.MONSTER_ROLES, role);
			}
			else
			{
				return new ParseResult.Fail(
					getTokenName() + " '" + role + "' is not a known monster role for this game mode.");
			}
		}

		return ParseResult.SUCCESS;
	}

	/**
	 * Unparse the ROLE token
	 * 
	 * @param context
	 * @return String array representing the ROLE token
	 */
	@Override
	public String[] unparse(LoadContext context, PCClass pcc)
	{
		Changes<String> changes = context.getObjectContext().getListChanges(pcc, ListKey.MONSTER_ROLES);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		Collection<?> added = changes.getAdded();
		boolean globalClear = changes.includesGlobalClear();
		if (globalClear)
		{
			sb.append(Constants.LST_DOT_CLEAR);
		}
		if (added != null && !added.isEmpty())
		{
			if (globalClear)
			{
				sb.append(Constants.DOT);
			}
			sb.append(StringUtil.join(added, Constants.DOT));
		}
		Collection<String> removed = changes.getRemoved();
		if (removed != null && !removed.isEmpty())
		{
			if (sb.length() > 0)
			{
				sb.append(Constants.DOT);
			}
			sb.append("REMOVE.");
			sb.append(StringUtil.join(removed, Constants.DOT));
		}
		if (sb.length() == 0)
		{
			context.addWriteMessage(
				getTokenName() + " was expecting non-empty changes to include " + "added items or global clear");
			return null;
		}
		return new String[]{sb.toString()};
	}

	/**
	 * Get the token class
	 * @return Token class of type Race
	 */
	@Override
	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
