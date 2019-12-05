/*
 * Copyright 2019 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.gamemode.ageset;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceActor;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ConcreteTransitionChoice;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.NonInteractive;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.choiceset.QualifiedDecorator;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.core.AgeSet;
import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Implements the KIT token for AgeSet objects.
 */
public class KitToken extends AbstractTokenWithSeparator<AgeSet>
        implements CDOMPrimaryToken<AgeSet>, ChoiceActor<Kit>
{

    private static final Class<Kit> KIT_CLASS = Kit.class;

    @Override
    public String getTokenName()
    {
        return "KIT";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, AgeSet obj, String value)
    {
        if (obj instanceof Ungranted)
        {
            return new ParseResult.Fail(
                    "Cannot use " + getTokenName() + " on an Ungranted object type: " + obj.getClass().getSimpleName());
        }
        if (obj instanceof NonInteractive)
        {
            return new ParseResult.Fail("Cannot use " + getTokenName() + " on an Non-Interactive object type: "
                    + obj.getClass().getSimpleName());
        }
        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
        Formula count = FormulaFactory.getFormulaFor(tok.nextToken());
        if (!count.isValid())
        {
            return new ParseResult.Fail("Count in " + getTokenName() + " was not valid: " + count.toString());
        }
        if (!count.isStatic())
        {
            return new ParseResult.Fail("Count in " + getTokenName() + " must be a number");
        }
        if (count.resolveStatic().intValue() <= 0)
        {
            return new ParseResult.Fail("Count in " + getTokenName() + " must be > 0");
        }
        if (!tok.hasMoreTokens())
        {
            return new ParseResult.Fail(
                    getTokenName() + " must have a | separating " + "count from the list of possible values: " + value);
        }
        List<CDOMReference<Kit>> refs = new ArrayList<>();

        while (tok.hasMoreTokens())
        {
            String token = tok.nextToken();
            CDOMReference<Kit> ref;
            if (Constants.LST_ALL.equals(token))
            {
                ref = context.getReferenceContext().getCDOMAllReference(KIT_CLASS);
            } else
            {
                ref = context.getReferenceContext().getCDOMReference(KIT_CLASS, token);
            }
            refs.add(ref);
        }

        ReferenceChoiceSet<Kit> rcs = new ReferenceChoiceSet<>(refs);
        if (!rcs.getGroupingState().isValid())
        {
            return new ParseResult.Fail(
                    "Non-sensical " + getTokenName() + ": Contains ANY and a specific reference: " + value);
        }
        ChoiceSet<Kit> cs = new ChoiceSet<>(getTokenName(), new QualifiedDecorator<>(rcs));
        cs.setTitle("Kit Selection");
        TransitionChoice<Kit> tc = new ConcreteTransitionChoice<>(cs, count);
        obj.addKit(tc);
        tc.setRequired(false);
        tc.setChoiceActor(this);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, AgeSet ageSet)
    {
        Set<String> set = new TreeSet<>();
        for (TransitionChoice<Kit> tc : ageSet.getKits())
        {
            String sb = tc.getCount()
                    + Constants.PIPE
                    + tc.getChoices().getLSTformat().replaceAll(Constants.COMMA, Constants.PIPE);
            set.add(sb);
        }
        return set.toArray(new String[0]);
    }

    @Override
    public Class<AgeSet> getTokenClass()
    {
        return AgeSet.class;
    }

    @Override
    public void applyChoice(CDOMObject owner, Kit choice, PlayerCharacter pc)
    {
        Kit.applyKit(choice, pc);
    }

    @Override
    public boolean allow(Kit choice, PlayerCharacter pc, boolean allowStack)
    {
        for (Kit k : pc.getKitInfo())
        {
            if (k.getKeyName().equalsIgnoreCase(choice.getKeyName()))
            {
                return false;
            }
        }
        return true;
    }
}
