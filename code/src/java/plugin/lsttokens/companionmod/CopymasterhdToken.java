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
package plugin.lsttokens.companionmod;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.character.CompanionMod;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with COPYMASTERHP Token
 */
public class CopymasterhdToken extends AbstractToken implements
		CDOMPrimaryToken<CompanionMod>
{

	@Override
	public String getTokenName()
	{
		return "COPYMASTERHP";
	}

	public boolean parse(LoadContext context, CompanionMod cMod, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		context.getObjectContext()
			.put(cMod, StringKey.MASTER_HP_FORMULA, value);
		return true;
	}

	public String[] unparse(LoadContext context, CompanionMod cMod)
	{
		String masterHP =
				context.getObjectContext().getString(cMod,
					StringKey.MASTER_HP_FORMULA);
		if (masterHP == null)
		{
			return null;
		}
		return new String[]{masterHP};
	}

	public Class<CompanionMod> getTokenClass()
	{
		return CompanionMod.class;
	}

}
