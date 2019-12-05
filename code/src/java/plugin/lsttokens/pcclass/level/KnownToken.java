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
package plugin.lsttokens.pcclass.level;

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.base.text.ParsingSeparator;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with KNOWN Token
 */
public class KnownToken extends AbstractTokenWithSeparator<PCClassLevel> implements CDOMPrimaryToken<PCClassLevel>
{

    @Override
    public String getTokenName()
    {
        return "KNOWN";
    }

    @Override
    protected char separator()
    {
        return ',';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, PCClassLevel level, String value)
    {
        context.getObjectContext().removeList(level, ListKey.KNOWN);

        ParsingSeparator sep = new ParsingSeparator(value, ',');
        sep.addGroupingPair('(', ')');

        while (sep.hasNext())
        {
            String tok = sep.next();
            try
            {
                if (Integer.parseInt(tok) < 0)
                {
                    return new ParseResult.Fail("Invalid Spell Count: " + tok + " is less than zero");
                }
            } catch (NumberFormatException e)
            {
                // OK, it must be a formula...
            }
            Formula formula = FormulaFactory.getFormulaFor(tok);
            if (!formula.isValid())
            {
                return new ParseResult.Fail("Formula in " + getTokenName() + " was not valid: " + formula.toString());
            }
            context.getObjectContext().addToList(level, ListKey.KNOWN, formula);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCClassLevel level)
    {
        Changes<Formula> changes = context.getObjectContext().getListChanges(level, ListKey.KNOWN);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        return new String[]{StringUtil.join(changes.getAdded(), Constants.COMMA)};
    }

    @Override
    public Class<PCClassLevel> getTokenClass()
    {
        return PCClassLevel.class;
    }
}
