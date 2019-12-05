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
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.formula.FixedSizeFormula;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with SIZE Token
 */
public class SizeToken extends AbstractNonEmptyToken<Race> implements CDOMPrimaryToken<Race>
{

    @Override
    public String getTokenName()
    {
        return "SIZE";
    }

    @Override
    public ParseResult parseNonEmptyToken(LoadContext context, Race race, String value)
    {
        CDOMSingleRef<SizeAdjustment> size =
                context.getReferenceContext().getCDOMReference(SizeAdjustment.class, value);
        Formula sizeFormula = new FixedSizeFormula(size);
        context.getObjectContext().put(race, FormulaKey.SIZE, sizeFormula);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Race race)
    {
        Formula res = context.getObjectContext().getFormula(race, FormulaKey.SIZE);
        if (res == null)
        {
            return null;
        }
        return new String[]{res.toString()};
    }

    @Override
    public Class<Race> getTokenClass()
    {
        return Race.class;
    }
}
