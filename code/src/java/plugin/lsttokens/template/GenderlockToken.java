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
package plugin.lsttokens.template;

import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with GENDERLOCK Token
 */
public class GenderlockToken extends AbstractNonEmptyToken<PCTemplate> implements CDOMPrimaryToken<PCTemplate>
{

    @Override
    public String getTokenName()
    {
        return "GENDERLOCK";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, PCTemplate template, String value)
    {
        try
        {
            context.getObjectContext().put(template, ObjectKey.GENDER_LOCK, Gender.valueOf(value));
            return ParseResult.SUCCESS;
        } catch (IllegalArgumentException iae)
        {
            return new ParseResult.Fail("Invalid Gender provided in " + getTokenName() + ": " + value);
        }
    }

    @Override
    public String[] unparse(LoadContext context, PCTemplate pct)
    {
        Gender g = context.getObjectContext().getObject(pct, ObjectKey.GENDER_LOCK);
        if (g == null)
        {
            return null;
        }
        return new String[]{g.name()};
    }

    @Override
    public Class<PCTemplate> getTokenClass()
    {
        return PCTemplate.class;
    }
}
