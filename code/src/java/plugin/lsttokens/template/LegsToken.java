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
package plugin.lsttokens.template;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.PCTemplate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractIntToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with LEGS Token
 */
public class LegsToken extends AbstractIntToken<PCTemplate> implements
		CDOMPrimaryToken<PCTemplate>
{

	public String getTokenName()
	{
		return "LEGS";
	}

	@Override
	protected IntegerKey integerKey()
	{
		return IntegerKey.LEGS;
	}
	
	@Override
	protected int minValue()
	{
		return 0;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Integer legs =
				context.getObjectContext().getInteger(pct, IntegerKey.LEGS);
		if (legs == null)
		{
			return null;
		}
		if (legs.intValue() < 0)
		{
			context
				.addWriteMessage(getTokenName() + " must be an integer >= 0");
			return null;
		}
		return new String[]{legs.toString()};
	}

	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}
}
