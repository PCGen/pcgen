/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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

package plugin.lsttokens.kit;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Race;
import pcgen.core.kit.KitRace;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Handles the RACE tag as well as Common tags on the RACE line.
 */
public class RaceToken extends AbstractNonEmptyToken<KitRace> implements
		CDOMPrimaryToken<KitRace>
{
	private static final Class<Race> RACE_CLASS = Race.class;

	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "RACE";
	}

	@Override
	public Class<KitRace> getTokenClass()
	{
		return KitRace.class;
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, KitRace kitRace, String value)
	{
		if (Constants.NONESELECTED.equals(value))
		{
			ComplexParseResult pr = new ComplexParseResult();
			pr.addWarningMessage("NONESELECTED is not necessary in KIT RACE: "
					+ "Token is not processed");
			return pr;
		}
		CDOMSingleRef<Race> ref =
				context.getReferenceContext().getCDOMReference(RACE_CLASS, value);
		kitRace.setRace(ref);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, KitRace kitRace)
	{
		CDOMReference<Race> race = kitRace.getRace();
		if (race == null)
		{
			return null;
		}
		return new String[]{race.getLSTformat(false)};
	}

}
