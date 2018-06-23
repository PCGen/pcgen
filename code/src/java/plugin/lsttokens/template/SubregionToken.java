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

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SubRegion;
import pcgen.core.PCTemplate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with SUBREGION Token
 */
public class SubregionToken extends AbstractNonEmptyToken<PCTemplate> implements CDOMPrimaryToken<PCTemplate>
{

	@Override
	public String getTokenName()
	{
		return "SUBREGION";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, PCTemplate template, String value)
	{
		if (value.equalsIgnoreCase("YES"))
		{
			context.getObjectContext().put(template, ObjectKey.USETEMPLATENAMEFORSUBREGION, true);
			context.getObjectContext().put(template, ObjectKey.SUBREGION, null);
		}
		else
		{
			context.getObjectContext().put(template, ObjectKey.USETEMPLATENAMEFORSUBREGION, null);
			context.getObjectContext().put(template, ObjectKey.SUBREGION, SubRegion.getConstant(value));
		}
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Boolean useName = context.getObjectContext().getObject(pct, ObjectKey.USETEMPLATENAMEFORSUBREGION);
		SubRegion subregion = context.getObjectContext().getObject(pct, ObjectKey.SUBREGION);
		if (useName != null && useName)
		{
			if (subregion != null)
			{
				context.addWriteMessage("Cannot have Template with " + getTokenName() + " YES and specific value");
			}
			return new String[]{"YES"};
		}
		if (subregion == null)
		{
			// Okay, nothing set
			return null;
		}
		return new String[]{subregion.toString()};
	}

	@Override
	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}
}
