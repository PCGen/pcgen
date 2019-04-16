/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.gamemode.unitset;

import java.math.BigDecimal;

import pcgen.core.UnitSet;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class HeightfactorToken extends AbstractNonEmptyToken<UnitSet> implements CDOMPrimaryToken<UnitSet>
{

	@Override
	public String getTokenName()
	{
		return "HEIGHTFACTOR";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, UnitSet us, String value)
	{
		try
		{
			us.setHeightFactor(new BigDecimal(value));
			return ParseResult.SUCCESS;
		}
		catch (NumberFormatException nfe)
		{
			return new ParseResult.Fail("Misunderstood Double in Tag: " + value);
		}
	}

	@Override
	public String[] unparse(LoadContext context, UnitSet us)
	{
		return new String[]{us.getHeightFactor().toString()};
	}

	@Override
	public Class<UnitSet> getTokenClass()
	{
		return UnitSet.class;
	}
}
