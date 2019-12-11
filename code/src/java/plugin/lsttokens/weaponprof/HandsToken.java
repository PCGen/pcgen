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
package plugin.lsttokens.weaponprof;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.WeaponProf;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractIntToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with HANDS Token
 */
public class HandsToken extends AbstractIntToken<WeaponProf> implements CDOMPrimaryToken<WeaponProf>
{

	@Override
	public String getTokenName()
	{
		return "HANDS";
	}

	@Override
	protected IntegerKey integerKey()
	{
		return IntegerKey.HANDS;
	}

	@Override
	protected int minValue()
	{
		return 0;
	}

	@Override
	public ParseResult parseToken(LoadContext context, WeaponProf prof, String value)
	{
		int hands;
		if ("1IFLARGERTHANWEAPON".equals(value))
		{
			hands = Constants.HANDS_SIZE_DEPENDENT;
			context.getObjectContext().put(prof, integerKey(), hands);
			return ParseResult.SUCCESS;
		}
		else
		{
			return super.parseToken(context, prof, value);
		}
	}

	@Override
	public String[] unparse(LoadContext context, WeaponProf prof)
	{
		Integer i = context.getObjectContext().getInteger(prof, IntegerKey.HANDS);
		/*
		 * Not a required Token, so it's possible it was never set. If so, don't
		 * write anything.
		 */
		if (i == null)
		{
			return null;
		}
		String hands;
		int intValue = i;
		if (intValue == Constants.HANDS_SIZE_DEPENDENT)
		{
			hands = "1IFLARGERTHANWEAPON";
		}
		else if (intValue < 0)
		{
			context.addWriteMessage(getTokenName() + " must be greater than or equal to zero or special value "
				+ Constants.HANDS_SIZE_DEPENDENT + " for 1IFLARGERTHANWEAPON");
			return null;
		}
		else
		{
			hands = i.toString();
		}
		return new String[]{hands};
	}

	@Override
	public Class<WeaponProf> getTokenClass()
	{
		return WeaponProf.class;
	}
}
