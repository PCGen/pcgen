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

import java.util.LinkedList;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.content.SpellResistance;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class SrLst implements CDOMPrimaryToken<CDOMObject>
{

    @Override
    public String getTokenName()
    {
        return "SR";
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
            context.getObjectContext().remove(obj, ObjectKey.SR);
        } else
        {
            Formula formula = FormulaFactory.getFormulaFor(value);
            if (!formula.isValid())
            {
                return new ParseResult.Fail("Formula in " + getTokenName() + " was not valid: " + formula.toString());
            }
            context.getObjectContext().put(obj, ObjectKey.SR, new SpellResistance(formula));
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
    {
        SpellResistance sr = context.getObjectContext().getObject(obj, ObjectKey.SR);
        boolean b = context.getObjectContext().wasRemoved(obj, ObjectKey.SR);
        List<String> list = new LinkedList<>();
        if (b)
        {
            list.add(Constants.LST_DOT_CLEAR);
        }
        if (sr != null)
        {
            list.add(sr.getLSTformat());
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
