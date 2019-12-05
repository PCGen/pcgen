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
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with XTRASKILLPTSPERLVL Token
 */
public class XtraskillptsperlvlToken extends AbstractNonEmptyToken<Race> implements CDOMPrimaryToken<Race>
{

    /**
     * Get token name
     *
     * @return token name
     */
    @Override
    public String getTokenName()
    {
        return "XTRASKILLPTSPERLVL";
    }

    @Override
    public Class<Race> getTokenClass()
    {
        return Race.class;
    }

    @Override
    public ParseResult parseNonEmptyToken(LoadContext context, Race race, String value)
    {
        Formula formula = FormulaFactory.getFormulaFor(value);
        if (!formula.isValid())
        {
            return new ParseResult.Fail("Formula in " + getTokenName() + " was not valid: " + formula.toString());
        }
        context.getObjectContext().put(race, FormulaKey.SKILL_POINTS_PER_LEVEL, formula);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Race race)
    {
        Formula f = context.getObjectContext().getFormula(race, FormulaKey.SKILL_POINTS_PER_LEVEL);
        if (f == null)
        {
            return null;
        }
        return new String[]{f.toString()};
    }
}
