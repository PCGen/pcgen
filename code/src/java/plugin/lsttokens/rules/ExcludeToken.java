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

import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.RuleCheck;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with EXCLUDE Token
 */
public class ExcludeToken extends AbstractNonEmptyToken<RuleCheck> implements CDOMPrimaryToken<RuleCheck>
{

    @Override
    public String getTokenName()
    {
        return "EXCLUDE";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, RuleCheck rule, String value)
    {
        CDOMSingleRef<RuleCheck> excludeRef = context.getReferenceContext().getCDOMReference(RuleCheck.class, value);
        rule.setExclude(excludeRef);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, RuleCheck rule)
    {
        CDOMSingleRef<RuleCheck> exclude = rule.getExclude();
        if (exclude == null)
        {
            return null;
        }
        return new String[]{exclude.getLSTformat(false)};
    }

    @Override
    public Class<RuleCheck> getTokenClass()
    {
        return RuleCheck.class;
    }
}
