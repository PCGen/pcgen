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

import pcgen.cdom.content.ChallengeRating;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.util.Logging;

/**
 * Class deals with CR Token
 */
public class CrToken extends AbstractToken implements CDOMPrimaryToken<Race>,
		DeferredToken<Race>
{

	/**
	 * Get the token name
	 */
	@Override
	public String getTokenName()
	{
		return "CR";
	}

	/**
	 * Parse the CR token
	 * 
	 * @param context 
	 * @param race 
	 * @param value 
	 * @return true if the parse was successful, else false
	 */
	public boolean parse(LoadContext context, Race race, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		try
		{
			ChallengeRating cr = new ChallengeRating(value);
			context.getObjectContext()
				.put(race, ObjectKey.CHALLENGE_RATING, cr);
			return true;
		}
		catch (IllegalArgumentException e)
		{
			Logging.errorPrint(getTokenName() + " encountered error: "
				+ e.getLocalizedMessage());
			return false;
		}
	}

	/**
	 * Unparse the CR token
	 * 
	 * @param context 
	 * @param race 
	 * @return String array representing the CR token 
	 */
	public String[] unparse(LoadContext context, Race race)
	{
		ChallengeRating cr =
				context.getObjectContext().getObject(race,
					ObjectKey.CHALLENGE_RATING);
		if (cr == null)
		{
			// indicates no Token present
			return null;
		}
		return new String[]{cr.getLSTformat()};
	}

	/**
	 * Get the token class
	 * @return Token class of type Race
	 */
	public Class<Race> getTokenClass()
	{
		return Race.class;
	}

	public boolean process(LoadContext context, Race race)
	{
		try
		{
			Number la =
					race.getSafe(FormulaKey.LEVEL_ADJUSTMENT).resolve(null, "");
			ChallengeRating cr = race.get(ObjectKey.CHALLENGE_RATING);
			if ((la.floatValue() != 0) && cr == null)
			{
				race.put(ObjectKey.CHALLENGE_RATING, new ChallengeRating(la
					.toString()));
			}
		}
		catch (NullPointerException soWhat)
		{
			//Nothing to do here, matches 5.14 behavior
		}
		return true;
	}

	public Class<Race> getDeferredTokenClass()
	{
		return Race.class;
	}
}
