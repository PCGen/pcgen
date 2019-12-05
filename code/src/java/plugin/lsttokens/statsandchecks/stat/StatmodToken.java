/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.statsandchecks.stat;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with STATMOD Token
 */
public class StatmodToken implements CDOMPrimaryToken<PCStat>
{

    @Override
    public String getTokenName()
    {
        return "STATMOD";
    }

    @Override
    public ParseResult parseToken(LoadContext context, PCStat stat, String value)
    {
        if (value == null || value.isEmpty())
        {
            return new ParseResult.Fail(getTokenName() + " arguments may not be empty");
        }
        Formula formula = FormulaFactory.getFormulaFor(value);
        if (!formula.isValid())
        {
            return new ParseResult.Fail("Formula in " + getTokenName() + " was not valid: " + formula.toString());
        }
        context.getObjectContext().put(stat, FormulaKey.STAT_MOD, formula);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCStat stat)
    {
        Formula target = context.getObjectContext().getFormula(stat, FormulaKey.STAT_MOD);
        if (target == null)
        {
            return null;
        }
        return new String[]{target.toString()};
    }

    @Override
    public Class<PCStat> getTokenClass()
    {
        return PCStat.class;
    }
}
