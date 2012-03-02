/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.rules.persistence.token;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.PCClass;
import pcgen.core.SubClass;
import pcgen.core.SubstitutionClass;
import pcgen.rules.context.LoadContext;

public class ClassWrappedToken implements CDOMCompatibilityToken<PCClassLevel>
{

	private static int wrapIndex = Integer.MIN_VALUE;

	private static final Integer ONE = Integer.valueOf(1);

	private final CDOMToken<PCClass> wrappedToken;

	private final int priority = wrapIndex++;

	@Override
	public Class<PCClassLevel> getTokenClass()
	{
		return PCClassLevel.class;
	}

	public ClassWrappedToken(CDOMToken<PCClass> tok)
	{
		wrappedToken = tok;
	}

	@Override
	public ParseResult parseToken(LoadContext context, PCClassLevel obj,
		String value)
	{
		if (ONE.equals(obj.get(IntegerKey.LEVEL)))
		{
			PCClass parent = (PCClass) obj.get(ObjectKey.TOKEN_PARENT);
			if (parent instanceof SubClass
					|| parent instanceof SubstitutionClass)
			{
				return new ParseResult.Fail("Data used token: " + value
						+ " which is a Class token, "
						+ "but it was used in a class level for a "
						+ parent.getClass().getSimpleName());
			}
			return wrappedToken.parseToken(context, parent, value);
		}
		return new ParseResult.Fail("Data used token: " + value
				+ " which is a Class token, "
				+ "but it was used in a class level line other than level 1");
	}

	@Override
	public String getTokenName()
	{
		return wrappedToken.getTokenName();
	}

	@Override
	public int compatibilityLevel()
	{
		return 5;
	}

	@Override
	public int compatibilityPriority()
	{
		return priority;
	}

	@Override
	public int compatibilitySubLevel()
	{
		return 14;
	}

}