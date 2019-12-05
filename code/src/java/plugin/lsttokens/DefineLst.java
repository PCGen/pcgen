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

import java.util.Set;
import java.util.TreeSet;

import pcgen.base.formula.Formula;
import pcgen.base.text.ParsingSeparator;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class DefineLst implements CDOMPrimaryToken<CDOMObject>
{

    public static final Class<PCStat> PCSTAT_CLASS = PCStat.class;

    @Override
    public String getTokenName()
    {
        return "DEFINE";
    }

    @Override
    public ParseResult parseToken(LoadContext context, CDOMObject obj, String value)
    {
        if (obj instanceof Ungranted)
        {
            return new ParseResult.Fail(
                    "Cannot use " + getTokenName() + " on an Ungranted object type: " + obj.getClass().getSimpleName());
        }
        ParsingSeparator sep = new ParsingSeparator(value, '|');
        sep.addGroupingPair('[', ']');
        sep.addGroupingPair('(', ')');

        if (!sep.hasNext())
        {
            return new ParseResult.Fail(getTokenName() + " may not be empty");
        }
        String firstItem = sep.next();

        if (firstItem.startsWith("UNLOCK."))
        {
            return new ParseResult.Fail(
                    "DEFINE:UNLOCK. has been deprecated, " + "please use DEFINESTAT:STAT| or DEFINESTAT:UNLOCK|");
        }
        if (!sep.hasNext())
        {
            return new ParseResult.Fail(
                    getTokenName() + " varName|varFormula" + "or LOCK.<stat>|value syntax requires an argument");
        }
        String var = firstItem;
        if (var.isEmpty())
        {
            return new ParseResult.Fail("Empty Variable Name found in " + getTokenName() + ": " + value);
        }
        try
        {
            Formula f = FormulaFactory.getFormulaFor(sep.next());
            if (!f.isValid())
            {
                return new ParseResult.Fail("Formula in " + getTokenName() + " was not valid: " + f.toString());
            }
            if ((!f.isStatic() || f.resolveStatic().intValue() != 0) && !(var.startsWith("MAXLEVELSTAT=")))
            {
                Logging.deprecationPrint(
                        "DEFINE with a non zero value has been deprecated, "
                                + "please use a DEFINE of 0 and an appropriate bonus. Tag was DEFINE:" + value + " in " + obj,
                        context);
            }
            if (sep.hasNext())
            {
                return new ParseResult.Fail(
                        getTokenName() + ' ' + firstItem + " syntax requires only one argument: " + value);
            }
            if (value.startsWith("LOCK."))
            {
                return new ParseResult.Fail(
                        "DEFINE:LOCK. has been deprecated, " + "please use DEFINESTAT:LOCL| or DEFINESTAT:NONSTAT|");
            } else
            {
                context.getObjectContext().put(obj, VariableKey.getConstant(var), f);
            }
            return ParseResult.SUCCESS;
        } catch (IllegalArgumentException e)
        {
            return new ParseResult.Fail(
                    "Illegal Formula found in " + getTokenName() + ": " + value + ' ' + e.getLocalizedMessage());
        }
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
    {
        Set<VariableKey> keys = context.getObjectContext().getVariableKeys(obj);
        TreeSet<String> set = new TreeSet<>();
        if (keys != null && !keys.isEmpty())
        {
            for (VariableKey key : keys)
            {
                set.add(key.toString() + Constants.PIPE + context.getObjectContext().getVariable(obj, key));
            }
        }
        if (set.isEmpty())
        {
            return null;
        }
        return set.toArray(new String[0]);
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }
}
