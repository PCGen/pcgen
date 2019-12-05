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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.base.text.ParsingSeparator;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;

/**
 * New Token to support Adding Levels to say a Lycanthorpe template
 */
public class AddLevelToken extends AbstractNonEmptyToken<PCTemplate> implements CDOMPrimaryToken<PCTemplate>
{

    @Override
    public String getTokenName()
    {
        return "ADDLEVEL";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, PCTemplate template, String value)
    {
        ParsingSeparator sep = new ParsingSeparator(value, '|');
        sep.addGroupingPair('[', ']');
        sep.addGroupingPair('(', ')');

        String classString = sep.next();
        if (classString.isEmpty())
        {
            ComplexParseResult cpr = new ComplexParseResult();
            cpr.addErrorMessage("Empty Class found in " + getTokenName());
            cpr.addErrorMessage("  " + getTokenName() + " requires at format: Class|LevelCount");
            return cpr;
        }
        if (!sep.hasNext())
        {
            ComplexParseResult cpr = new ComplexParseResult();
            cpr.addErrorMessage("No | found in " + getTokenName());
            cpr.addErrorMessage("  " + getTokenName() + " requires at format: Class|LevelCount");
            return cpr;
        }
        String numLevels = sep.next();
        if (numLevels.isEmpty())
        {
            ComplexParseResult cpr = new ComplexParseResult();
            cpr.addErrorMessage("Empty Level Count found in " + getTokenName());
            cpr.addErrorMessage("  " + getTokenName() + " requires at format: Class|LevelCount");
            return cpr;
        }
        if (sep.hasNext())
        {
            ComplexParseResult cpr = new ComplexParseResult();
            cpr.addErrorMessage("Two | found in " + getTokenName());
            cpr.addErrorMessage("  " + getTokenName() + " requires at format: Class|LevelCount");
            return cpr;
        }
        CDOMSingleRef<PCClass> cl = context.getReferenceContext().getCDOMReference(PCClass.class, classString);
        Formula f;
        try
        {
            int lvls = Integer.parseInt(numLevels);
            if (lvls <= 0)
            {
                return new ParseResult.Fail(
                        "Number of Levels granted in " + getTokenName() + " must be greater than zero");
            }
            f = FormulaFactory.getFormulaFor(lvls);
        } catch (NumberFormatException nfe)
        {
            f = FormulaFactory.getFormulaFor(numLevels);
        }
        if (!f.isValid())
        {
            return new ParseResult.Fail("Formula in " + getTokenName() + " was not valid: " + f.toString());
        }
        LevelCommandFactory cf = new LevelCommandFactory(cl, f);
        context.getObjectContext().addToList(template, ListKey.ADD_LEVEL, cf);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCTemplate pct)
    {
        Changes<LevelCommandFactory> changes = context.getObjectContext().getListChanges(pct, ListKey.ADD_LEVEL);
        Collection<LevelCommandFactory> added = changes.getAdded();
        if (added == null || added.isEmpty())
        {
            return null;
        }
        List<String> list = new ArrayList<>();
        for (LevelCommandFactory lcf : added)
        {
            list.add(lcf.getLSTformat() + Constants.PIPE + lcf.getLevelCount().toString());
        }

        return list.toArray(new String[0]);
    }

    @Override
    public Class<PCTemplate> getTokenClass()
    {
        return PCTemplate.class;
    }
}
