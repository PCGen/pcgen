/*
 * Copyright 2010 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.rules;

import pcgen.core.RuleCheck;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with DEFAULT Token
 */
public class DefaultToken extends AbstractNonEmptyToken<RuleCheck> implements CDOMPrimaryToken<RuleCheck>
{

    @Override
    public String getTokenName()
    {
        return "DEFAULT";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, RuleCheck rule, String value)
    {
        Boolean set;
        char firstChar = value.charAt(0);
        if (firstChar == 'y' || firstChar == 'Y')
        {
            if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
            {
                return new ParseResult.Fail("You should use 'YES' as the " + getTokenName() + ": " + value);
            }
            set = Boolean.TRUE;
        } else
        {
            if (firstChar != 'N' && firstChar != 'n')
            {
                return new ParseResult.Fail("You should use 'YES' or 'NO' as the " + getTokenName() + ": " + value);
            }
            if (value.length() > 1 && !value.equalsIgnoreCase("NO"))
            {
                return new ParseResult.Fail("You should use 'YES' or 'NO' as the " + getTokenName() + ": " + value);
            }
            set = Boolean.FALSE;
        }
        rule.setDefault(set);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, RuleCheck rule)
    {
        boolean set = rule.getDefault();
        return new String[]{set ? "YES" : "NO"};
    }

    @Override
    public Class<RuleCheck> getTokenClass()
    {
        return RuleCheck.class;
    }
}
