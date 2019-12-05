/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with SELECT Token
 */
public class SelectLst implements CDOMPrimaryToken<CDOMObject>
{

    @Override
    public String getTokenName()
    {
        return "SELECT";
    }

    @Override
    public ParseResult parseToken(LoadContext context, CDOMObject cdo, String value)
    {
        if (cdo instanceof Ungranted)
        {
            return new ParseResult.Fail(
                    "Cannot use " + getTokenName() + " on an Ungranted object type: " + cdo.getClass().getSimpleName());
        }
        Formula formula = FormulaFactory.getFormulaFor(value);
        if (!formula.isValid())
        {
            return new ParseResult.Fail("Formula in " + getTokenName() + " was not valid: " + formula.toString());
        }
        context.getObjectContext().put(cdo, FormulaKey.SELECT, formula);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject cdo)
    {
        Formula f = context.getObjectContext().getFormula(cdo, FormulaKey.SELECT);
        if (f == null)
        {
            return null;
        }
        return new String[]{f.toString()};
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }
}
