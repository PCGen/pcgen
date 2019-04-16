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
package plugin.lsttokens.race;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceType;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with RACETYPE Token
 */
public class RacetypeToken extends AbstractNonEmptyToken<Race> implements CDOMPrimaryToken<Race>
{

	@Override
	public String getTokenName()
	{
		return "RACETYPE";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, Race race, String value)
	{
		context.getObjectContext().put(race, ObjectKey.RACETYPE, RaceType.getConstant(value));
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, Race race)
	{
		RaceType raceType = context.getObjectContext().getObject(race, ObjectKey.RACETYPE);
		if (raceType == null)
		{
			return null;
		}
		return new String[]{raceType.toString()};
	}

	@Override
	public Class<Race> getTokenClass()
	{
		return Race.class;
	}

	//	if (race.get(ObjectKey.RACETYPE) == null)
	//	{
	//		/** TODO Uncomment this once the data is updated. */
	//		//			logError("Race " + race.getName() + " has no race type.");
	//	}
}
