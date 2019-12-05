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

package plugin.lsttokens.kit.basekit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.base.text.ParsingSeparator;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.helper.OptionBound;
import pcgen.core.kit.BaseKit;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class OptionToken extends AbstractNonEmptyToken<BaseKit> implements CDOMPrimaryToken<BaseKit>
{
    /**
     * Gets the name of the tag this class will parse.
     *
     * @return Name of the tag this class handles
     */
    @Override
    public String getTokenName()
    {
        return "OPTION";
    }

    @Override
    public Class<BaseKit> getTokenClass()
    {
        return BaseKit.class;
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, BaseKit kit, String value)
    {
        ParsingSeparator pipeSep = new ParsingSeparator(value, '|');
        pipeSep.addGroupingPair('[', ']');
        pipeSep.addGroupingPair('(', ')');

        while (pipeSep.hasNext())
        {
            String subTok = pipeSep.next();
            if (subTok.isEmpty())
            {
                return new ParseResult.Fail(getTokenName() + " arguments has invalid pipe separator: " + value);
            }
            ParseResult pr = checkForIllegalSeparator(',', subTok);
            if (!pr.passed())
            {
                return pr;
            }
            ParsingSeparator commaSep = new ParsingSeparator(subTok, ',');
            commaSep.addGroupingPair('[', ']');
            commaSep.addGroupingPair('(', ')');
            String minString = commaSep.next();
            String maxString;
            if (commaSep.hasNext())
            {
                maxString = commaSep.next();
            } else
            {
                maxString = subTok;
            }
            if (commaSep.hasNext())
            {
                return new ParseResult.Fail("Token cannot have more than one separator ','");
            }
            Formula min = FormulaFactory.getFormulaFor(minString);
            if (!min.isValid())
            {
                return new ParseResult.Fail("Min Formula in " + getTokenName() + " was not valid: " + min.toString());
            }
            Formula max = FormulaFactory.getFormulaFor(maxString);
            if (!max.isValid())
            {
                return new ParseResult.Fail("Max Formula in " + getTokenName() + " was not valid: " + max.toString());
            }
            kit.setOptionBounds(min, max);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, BaseKit kit)
    {
        Collection<OptionBound> bounds = kit.getBounds();
        if (bounds == null)
        {
            return null;
        }
        List<String> list = new ArrayList<>();
        for (OptionBound bound : bounds)
        {
            Formula min = bound.getOptionMin();
            Formula max = bound.getOptionMax();
            if (min == null || max == null)
            {
                // Error if only one is null
                return null;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(min);
            if (!min.equals(max))
            {
                sb.append(',').append(max);
            }
            list.add(sb.toString());
        }
        return new String[]{StringUtil.join(list, Constants.PIPE)};
    }
}
