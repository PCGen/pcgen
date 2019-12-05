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
import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ConcreteTransitionChoice;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.SpellReferenceChoiceSet;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.PCClass;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with SPELLLIST Token
 */
public class SpelllistToken extends AbstractTokenWithSeparator<PCClass> implements CDOMPrimaryToken<PCClass>
{

    private static final Class<ClassSpellList> SPELLLIST_CLASS = ClassSpellList.class;
    private static final Class<DomainSpellList> DOMAINSPELLLIST_CLASS = DomainSpellList.class;

    @Override
    public String getTokenName()
    {
        return "SPELLLIST";
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
        List<CDOMReference<? extends CDOMListObject<Spell>>> refs = new ArrayList<>();

        while (tok.hasMoreTokens())
        {
            String token = tok.nextToken();
            CDOMReference<? extends CDOMListObject<Spell>> ref;
            if (Constants.LST_ALL.equals(token))
            {
                ref = context.getReferenceContext().getCDOMAllReference(SPELLLIST_CLASS);
            } else if (token.startsWith("DOMAIN."))
            {
                ref = context.getReferenceContext().getCDOMReference(DOMAINSPELLLIST_CLASS, token.substring(7));
            } else
            {
                ref = context.getReferenceContext().getCDOMReference(SPELLLIST_CLASS, token);
            }
            refs.add(ref);
        }

        PrimitiveChoiceSet<CDOMListObject<Spell>> rcs = new SpellReferenceChoiceSet(refs);
        if (!rcs.getGroupingState().isValid())
        {
            return new ParseResult.Fail(
                    "Non-sensical " + getTokenName() + ": Contains ANY and a specific reference: " + value);
        }

        ChoiceSet<? extends CDOMListObject<Spell>> cs = new ChoiceSet<>(getTokenName(), rcs);
        cs.setTitle("Select class whose list of spells this class will use");
        TransitionChoice<CDOMListObject<Spell>> tc = new ConcreteTransitionChoice<>(cs, count);
        context.getObjectContext().put(pcc, ObjectKey.SPELLLIST_CHOICE, tc);
        tc.setRequired(false);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCClass pcc)
    {
        TransitionChoice<CDOMListObject<Spell>> grantChanges =
                context.getObjectContext().getObject(pcc, ObjectKey.SPELLLIST_CHOICE);
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
