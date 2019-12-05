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

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.ChallengeRating;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with CR Token
 */
public class CrToken extends AbstractNonEmptyToken<Race> implements CDOMPrimaryToken<Race>, DeferredToken<Race>
{

    /**
     * Get the token name
     */
    @Override
    public String getTokenName()
    {
        return "CR";
    }

    @Override
    public ParseResult parseNonEmptyToken(LoadContext context, Race race, String value)
    {
        try
        {
            int intRating = Integer.parseInt(value.startsWith("1/") ? value.substring(2) : value);
            if (intRating < 0)
            {
                return new ParseResult.Fail(getTokenName() + " Challenge Rating cannot be negative");
            }
        } catch (NumberFormatException e)
        {
            return new ParseResult.Fail(getTokenName() + "Challenge Rating must be a positive integer i or 1/i");
        }
        Formula formula = FormulaFactory.getFormulaFor(value);
        if (!formula.isValid())
        {
            return new ParseResult.Fail("Formula in " + getTokenName() + " was not valid: " + formula.toString());
        }
        ChallengeRating cr = new ChallengeRating(formula);
        context.getObjectContext().put(race, ObjectKey.CHALLENGE_RATING, cr);
        return ParseResult.SUCCESS;
    }

    /**
     * Unparse the CR token
     *
     * @param context
     * @param race
     * @return String array representing the CR token
     */
    @Override
    public String[] unparse(LoadContext context, Race race)
    {
        ChallengeRating cr = context.getObjectContext().getObject(race, ObjectKey.CHALLENGE_RATING);
        if (cr == null)
        {
            // indicates no Token present
            return null;
        }
        return new String[]{cr.getLSTformat()};
    }

    /**
     * Get the token class
     *
     * @return Token class of type Race
     */
    @Override
    public Class<Race> getTokenClass()
    {
        return Race.class;
    }

    @Override
    public boolean process(LoadContext context, Race race)
    {
        Formula levelAdjFormula = race.getSafe(FormulaKey.LEVEL_ADJUSTMENT);
        if (levelAdjFormula.isStatic())
        {
            Number la = levelAdjFormula.resolveStatic();
            ChallengeRating cr = race.get(ObjectKey.CHALLENGE_RATING);
            if ((la.floatValue() != 0) && cr == null)
            {
                race.put(ObjectKey.CHALLENGE_RATING, new ChallengeRating(FormulaFactory.getFormulaFor(la.toString())));
            }
        }
        //else Nothing to do here, matches 5.14 behavior
        //TODO Should there at LEAST be a message in an else??
        return true;
    }

    @Override
    public Class<Race> getDeferredTokenClass()
    {
        return Race.class;
    }
}
