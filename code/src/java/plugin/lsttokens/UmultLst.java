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
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractIntToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class UmultLst extends AbstractIntToken<CDOMObject> implements CDOMPrimaryToken<CDOMObject>
{

    @Override
    public String getTokenName()
    {
        return "UMULT";
    }

    @Override
    protected IntegerKey integerKey()
    {
        return IntegerKey.UMULT;
    }

    @Override
    protected int minValue()
    {
        return 1;
    }

    @Override
    public ParseResult parseToken(LoadContext context, CDOMObject obj, String value)
    {
        if (obj instanceof Ungranted)
        {
            return new ParseResult.Fail(
                    "Cannot use " + getTokenName() + " on an Ungranted object type: " + obj.getClass().getSimpleName());
        }
        if (Constants.LST_DOT_CLEAR.equals(value))
        {
            context.getObjectContext().remove(obj, IntegerKey.UMULT);
            return ParseResult.SUCCESS;
        } else
        {
            return super.parseToken(context, obj, value);
        }
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
    {
        Integer mult = context.getObjectContext().getInteger(obj, IntegerKey.UMULT);
        boolean b = context.getObjectContext().wasRemoved(obj, IntegerKey.UMULT);
        List<String> list = new ArrayList<>();
        if (b)
        {
            list.add(Constants.LST_DOT_CLEAR);
        }
        if (mult != null)
        {
            if (mult <= 0)
            {
                context.addWriteMessage(getTokenName() + " must be an integer > 0");
                return null;
            }
            list.add(mult.toString());
        }
        if (list.isEmpty())
        {
            return null;
        }
        return list.toArray(new String[0]);
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }
}
