/*
 * Copyright 2015 (C) Thomas Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.datacontrol;

import pcgen.cdom.content.UserFunction;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class ValueToken extends AbstractNonEmptyToken<UserFunction> implements
		CDOMPrimaryToken<UserFunction>
{

	@Override
	public String getTokenName()
	{
		return "VALUE";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context,
		UserFunction ftn, String value)
	{
		String existing = ftn.getOriginalExpression();
		if (existing != null)
		{
			if (!existing.equalsIgnoreCase(value))
			{
				return new ParseResult.Fail("Parse of Function: "
					+ ftn.getKeyName() + " with value: " + value
					+ " attempted to reassign from: " + existing, context);
			}
			return ParseResult.SUCCESS;
		}
		try
		{
			ftn.setFunction(value);
		}
		catch (IllegalArgumentException e)
		{
			return new ParseResult.Fail("Parse of Function: "
				+ ftn.getKeyName() + " with value: " + value
				+ " failed due to: " + e.getMessage(), context);
		}
		context.getVariableContext().addFunction(ftn.getFunction());
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, UserFunction ftn)
	{
		return new String[]{ftn.getOriginalExpression()};
	}

	@Override
	public Class<UserFunction> getTokenClass()
	{
		return UserFunction.class;
	}
}
