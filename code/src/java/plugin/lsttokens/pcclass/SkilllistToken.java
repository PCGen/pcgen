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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ConcreteTransitionChoice;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with SKILLLIST Token
 */
public class SkilllistToken extends AbstractTokenWithSeparator<PCClass> implements CDOMPrimaryToken<PCClass>
{
    private static final Class<ClassSkillList> SKILLLIST_CLASS = ClassSkillList.class;

    @Override
    public String getTokenName()
    {
        return "SKILLLIST";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, PCClass pcc, String value)
    {
        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
        Formula count = FormulaFactory.getFormulaFor(tok.nextToken());
        if (!count.isValid())
        {
            return new ParseResult.Fail("Count in " + getTokenName() + " was not valid: " + count.toString());
        }
        if (!count.isStatic() || count.resolveStatic().intValue() <= 0)
        {
            return new ParseResult.Fail("Count in " + getTokenName() + " must be > 0");
        }
        if (!tok.hasMoreTokens())
        {
            return new ParseResult.Fail(
                    getTokenName() + " must have a | separating " + "count from the list of possible values: " + value);
        }
        List<CDOMReference<ClassSkillList>> refs = new ArrayList<>();

        while (tok.hasMoreTokens())
        {
            String token = tok.nextToken();
            CDOMReference<ClassSkillList> ref;
            if (Constants.LST_ALL.equals(token))
            {
                ref = context.getReferenceContext().getCDOMAllReference(SKILLLIST_CLASS);
            } else
            {
                ref = context.getReferenceContext().getCDOMReference(SKILLLIST_CLASS, token);
            }
            refs.add(ref);
        }

        ReferenceChoiceSet<ClassSkillList> rcs = new ReferenceChoiceSet<>(refs);
        if (!rcs.getGroupingState().isValid())
        {
            return new ParseResult.Fail(
                    "Non-sensical " + getTokenName() + ": Contains ANY and a specific reference: " + value);
        }
        ChoiceSet<ClassSkillList> cs = new ChoiceSet<>(getTokenName(), rcs);
        cs.setTitle("Select class whose class-skills this class will inherit");
        TransitionChoice<ClassSkillList> tc = new ConcreteTransitionChoice<>(cs, count);
        context.getObjectContext().put(pcc, ObjectKey.SKILLLIST_CHOICE, tc);
        tc.setRequired(false);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCClass pcc)
    {
        TransitionChoice<ClassSkillList> grantChanges =
                context.getObjectContext().getObject(pcc, ObjectKey.SKILLLIST_CHOICE);
        if (grantChanges == null)
        {
            // Zero indicates no Token
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Formula count = grantChanges.getCount();
        if (count == null)
        {
            context.addWriteMessage("Unable to find " + getTokenName() + " Count");
            return null;
        }
        sb.append(count);
        sb.append(Constants.PIPE);
        sb.append(grantChanges.getChoices().getLSTformat().replaceAll(Constants.COMMA, Constants.PIPE));
        return new String[]{sb.toString()};
    }

    @Override
    public Class<PCClass> getTokenClass()
    {
        return PCClass.class;
    }
}
