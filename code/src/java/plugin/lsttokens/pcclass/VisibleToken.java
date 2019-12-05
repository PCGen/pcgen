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
package plugin.lsttokens.pcclass;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with VISIBLE Token
 */
public class VisibleToken extends AbstractNonEmptyToken<PCClass> implements CDOMPrimaryToken<PCClass>
{

    @Override
    public String getTokenName()
    {
        return "VISIBLE";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, PCClass pcc, String value)
    {
        Visibility vis;
        if (value.equals("NO"))
        {
            vis = Visibility.HIDDEN;
        } else if (value.equalsIgnoreCase("Y") || value.equals("YES"))
        {
            vis = Visibility.DEFAULT;
        } else
        {
            return new ParseResult.Fail("Can't understand Visibility: " + value);
        }
        context.getObjectContext().put(pcc, ObjectKey.VISIBILITY, vis);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCClass pcc)
    {
        Visibility vis = context.getObjectContext().getObject(pcc, ObjectKey.VISIBILITY);
        if (vis == null)
        {
            return null;
        }
        String visString;
        if (vis.equals(Visibility.DEFAULT))
        {
            visString = "YES";
        } else if (vis.equals(Visibility.HIDDEN))
        {
            visString = "NO";
        } else
        {
            context.addWriteMessage("Visibility " + vis + " is not a valid Visibility for a PCClass");
            return null;
        }
        return new String[]{visString};
    }

    @Override
    public Class<PCClass> getTokenClass()
    {
        return PCClass.class;
    }
}
