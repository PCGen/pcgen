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

import java.math.BigDecimal;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with CR Token
 */
public class CrToken extends AbstractNonEmptyToken<PCTemplate> implements CDOMPrimaryToken<PCTemplate>
{

    @Override
    public String getTokenName()
    {
        return "CR";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, PCTemplate template, String value)
    {
        try
        {
            context.getObjectContext().put(template, ObjectKey.CR_MODIFIER, new BigDecimal(value));
            return ParseResult.SUCCESS;
        } catch (NumberFormatException nfe)
        {
            return new ParseResult.Fail("Misunderstood Double in Tag: " + value);
        }
    }

    @Override
    public String[] unparse(LoadContext context, PCTemplate pct)
    {
        BigDecimal mod = context.getObjectContext().getObject(pct, ObjectKey.CR_MODIFIER);
        if (mod == null)
        {
            return null;
        }
        return new String[]{mod.toString()};
    }

    @Override
    public Class<PCTemplate> getTokenClass()
    {
        return PCTemplate.class;
    }
}
