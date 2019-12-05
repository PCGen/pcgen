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
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.content.DamageReduction;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;

public class DrLst extends AbstractTokenWithSeparator<CDOMObject> implements CDOMPrimaryToken<CDOMObject>
{
    @Override
    public String getTokenName()
    {
        return "DR";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
    {
        if (obj instanceof Ungranted)
        {
            return new ParseResult.Fail(
                    "Cannot use " + getTokenName() + " on an Ungranted object type: " + obj.getClass().getSimpleName());
        }
        if (Constants.LST_DOT_CLEAR.equals(value))
        {
            context.getObjectContext().removeList(obj, ListKey.DAMAGE_REDUCTION);
            return ParseResult.SUCCESS;
        }

        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
        String drString = tok.nextToken();
        ParseResult pr = checkForIllegalSeparator('/', drString);
        if (!pr.passed())
        {
            return pr;
        }
        String[] values = drString.split("/");
        if (values.length != 2)
        {
            ComplexParseResult cpr = new ComplexParseResult();
            cpr.addErrorMessage(getTokenName() + " failed to build DamageReduction with value " + value);
            cpr.addErrorMessage("  ...expected a String with one / as a separator");
            return cpr;
        }
        Formula formula = FormulaFactory.getFormulaFor(values[0]);
        if (!formula.isValid())
        {
            return new ParseResult.Fail("Formula in " + getTokenName() + " was not valid: " + formula.toString());
        }
        DamageReduction dr = new DamageReduction(formula, values[1]);

        if (tok.hasMoreTokens())
        {
            String currentToken = tok.nextToken();
            Prerequisite prereq = getPrerequisite(currentToken);
            if (prereq == null)
            {
                return ParseResult.INTERNAL_ERROR;
            }
            dr.addPrerequisite(prereq);
        }
        context.getObjectContext().addToList(obj, ListKey.DAMAGE_REDUCTION, dr);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
    {
        Changes<DamageReduction> changes = context.getObjectContext().getListChanges(obj, ListKey.DAMAGE_REDUCTION);
        Collection<DamageReduction> added = changes.getAdded();
        List<String> list = new ArrayList<>();
        if (changes.includesGlobalClear())
        {
            list.add(Constants.LST_DOT_CLEAR);
        } else if (added == null || added.isEmpty())
        {
            // Zero indicates no Token (and no global clear, so nothing to do)
            return null;
        }
        Set<String> set = new TreeSet<>();
        if (added != null)
        {
            for (DamageReduction lw : added)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(lw.getLSTformat());
                if (lw.hasPrerequisites())
                {
                    sb.append(Constants.PIPE);
                    sb.append(context.getPrerequisiteString(lw.getPrerequisiteList()));
                }
                set.add(sb.toString());
            }
        }
        list.addAll(set);
        return list.toArray(new String[0]);
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }
}
