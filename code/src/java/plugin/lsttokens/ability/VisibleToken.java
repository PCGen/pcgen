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
package plugin.lsttokens.ability;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.enumeration.Visibility;

/**
 * {@code VisibleToken} handles the processing of the VISIBLE tag in the
 * definition of an Ability.
 * <p>
 * (Sat, 10 Feb 2007) $
 */
public class VisibleToken extends AbstractNonEmptyToken<Ability> implements CDOMPrimaryToken<Ability>
{

    @Override
    public String getTokenName()
    {
        return "VISIBLE";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, Ability ability, String value)
    {
        Visibility vis;
        switch (value)
        {
            case "YES":
                vis = Visibility.DEFAULT;
                break;
            case "DISPLAY":
                vis = Visibility.DISPLAY_ONLY;
                break;
            case "EXPORT":
                vis = Visibility.OUTPUT_ONLY;
                break;
            case "NO":
                vis = Visibility.HIDDEN;
                break;
            default:
                return new ParseResult.Fail("Unable to understand " + getTokenName() + " tag: " + value);
        }
        context.getObjectContext().put(ability, ObjectKey.VISIBILITY, vis);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Ability ability)
    {
        Visibility vis = context.getObjectContext().getObject(ability, ObjectKey.VISIBILITY);
        if (vis == null)
        {
            return null;
        }
        String visString;
        if (vis.equals(Visibility.DEFAULT))
        {
            visString = "YES";
        } else if (vis.equals(Visibility.DISPLAY_ONLY))
        {
            visString = "DISPLAY";
        } else if (vis.equals(Visibility.OUTPUT_ONLY))
        {
            visString = "EXPORT";
        } else if (vis.equals(Visibility.HIDDEN))
        {
            visString = "NO";
        } else
        {
            context.addWriteMessage("Visibility " + vis + " is not a valid Visibility for an Ability");
            return null;
        }
        return new String[]{visString};
    }

    @Override
    public Class<Ability> getTokenClass()
    {
        return Ability.class;
    }
}
