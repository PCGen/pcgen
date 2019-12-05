/*
 * Copyright 2014 (C) Thomas Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.datacontrol;

import pcgen.cdom.content.UserContent;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class ExplanationToken extends AbstractNonEmptyToken<UserContent> implements CDOMPrimaryToken<UserContent>
{

    @Override
    public String getTokenName()
    {
        return "EXPLANATION";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, UserContent factDef, String value)
    {
        factDef.setExplanation(value);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, UserContent factDef)
    {
        String name = factDef.getExplanation();
        if (name == null)
        {
            return null;
        }
        return new String[]{name};
    }

    @Override
    public Class<UserContent> getTokenClass()
    {
        return UserContent.class;
    }

}
