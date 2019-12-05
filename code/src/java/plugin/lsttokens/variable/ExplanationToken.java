/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.variable;

import pcgen.cdom.content.DatasetVariable;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class ExplanationToken extends AbstractNonEmptyToken<DatasetVariable>
        implements CDOMPrimaryToken<DatasetVariable>
{

    @Override
    public String getTokenName()
    {
        return "EXPLANATION";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, DatasetVariable dv, String value)
    {
        if (dv.getDisplayName() == null)
        {
            return new ParseResult.Fail(getTokenName() + " must not be the first token on the line");
        }
        dv.setExplanation(value);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, DatasetVariable dv)
    {
        String explanation = dv.getExplanation();
        if (explanation == null)
        {
            return null;
        }
        return new String[]{explanation};
    }

    @Override
    public Class<DatasetVariable> getTokenClass()
    {
        return DatasetVariable.class;
    }

}
