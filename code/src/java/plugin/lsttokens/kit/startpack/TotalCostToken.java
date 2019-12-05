/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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

package plugin.lsttokens.kit.startpack;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.base.text.ParsingSeparator;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Kit;
import pcgen.core.QualifiedObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * TOTALCOST Token for Kit Startpack line. This specifies the total
 * purchase cost of the kit.
 */
public class TotalCostToken extends AbstractNonEmptyToken<Kit> implements CDOMPrimaryToken<Kit>
{
    /**
     * Gets the name of the tag this class will parse.
     *
     * @return Name of the tag this class handles
     */
    @Override
    public String getTokenName()
    {
        return "TOTALCOST";
    }

    @Override
    public Class<Kit> getTokenClass()
    {
        return Kit.class;
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, Kit kit, String value)
    {
        ParsingSeparator sep = new ParsingSeparator(value, '|');
        sep.addGroupingPair('[', ']');
        sep.addGroupingPair('(', ')');

        String activeValue = sep.next();
        if (looksLikeAPrerequisite(activeValue))
        {
            return new ParseResult.Fail("Cannot have only PRExxx subtoken in " + getTokenName());
        }
        Formula f = FormulaFactory.getFormulaFor(activeValue);
        if (!f.isValid())
        {
            return new ParseResult.Fail("Formula in " + getTokenName() + " was not valid: " + f.toString());
        }
        List<Prerequisite> prereqs = new ArrayList<>();

        while (sep.hasNext())
        {
            activeValue = sep.next();
            Prerequisite prereq = getPrerequisite(activeValue);
            if (prereq == null)
            {
                return new ParseResult.Fail(
                        "   (Did you put total costs after the " + "PRExxx tags in " + getTokenName() + ":?)");
            }
            prereqs.add(prereq);
        }
        kit.put(ObjectKey.KIT_TOTAL_COST, new QualifiedObject<>(f, prereqs));
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Kit kit)
    {
        QualifiedObject<Formula> qo = kit.get(ObjectKey.KIT_TOTAL_COST);
        if (qo == null)
        {
            return null;
        }
        Formula f = qo.getRawObject();
        List<Prerequisite> prereqs = qo.getPrerequisiteList();
        String ab = f.toString();
        if (prereqs != null && !prereqs.isEmpty())
        {
            ab = ab + Constants.PIPE + getPrerequisiteString(context, prereqs);
        }
        return new String[]{ab};
    }

}
