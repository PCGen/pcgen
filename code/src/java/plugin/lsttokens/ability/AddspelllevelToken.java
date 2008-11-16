/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.ability;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Ability;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Delta;
import pcgen.util.Logging;

/**
 * Class deals with ADDSPELLLEVEL Token
 */
public class AddspelllevelToken extends AbstractToken implements
		CDOMPrimaryToken<Ability>
{

	@Override
	public String getTokenName()
	{
		return "ADDSPELLLEVEL";
	}

	public boolean parse(LoadContext context, Ability ability, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		try
		{
			Integer i = Delta.parseInt(value);
			if (i.intValue() < 0)
			{
				Logging.log(Logging.LST_ERROR, getTokenName() + " must be an integer >= 0");
				return false;
			}
			context.getObjectContext().put(ability, IntegerKey.ADD_SPELL_LEVEL,
					i);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.log(Logging.LST_ERROR, getTokenName()
					+ " expected an integer.  Tag must be of the form: "
					+ getTokenName() + ":<int>");
			return false;
		}
	}

	public String[] unparse(LoadContext context, Ability ability)
	{
		Integer lvl = context.getObjectContext().getInteger(ability,
				IntegerKey.ADD_SPELL_LEVEL);
		if (lvl == null)
		{
			return null;
		}
		if (lvl.intValue() < 0)
		{
			context
					.addWriteMessage(getTokenName()
							+ " must be an integer >= 0");
			return null;
		}
		return new String[] { lvl.toString() };
	}

	public Class<Ability> getTokenClass()
	{
		return Ability.class;
	}

}
