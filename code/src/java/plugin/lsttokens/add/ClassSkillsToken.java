/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.add;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.base.text.ParsingSeparator;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ConcretePersistentTransitionChoice;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.SelectableSet;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.ClassSkillChoiceActor;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.ObjectMatchingReference;
import pcgen.core.PCClass;
import pcgen.core.Skill;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class ClassSkillsToken extends AbstractNonEmptyToken<PCClass> implements CDOMSecondaryToken<PCClass>
{
    @Override
    public String getTokenName()
    {
        return "CLASSSKILLS";
    }

    private static final Class<Skill> SKILL_CLASS = Skill.class;

    @Override
    public String getParentToken()
    {
        return "ADD";
    }

    private String getFullName()
    {
        return getParentToken() + Constants.COLON + getTokenName();
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, PCClass obj, String value)
    {
        ParsingSeparator sep = new ParsingSeparator(value, '|');
        sep.addGroupingPair('[', ']');
        sep.addGroupingPair('(', ')');

        String activeValue = sep.next();
        Formula count;
        if (!sep.hasNext())
        {
            count = FormulaFactory.ONE;
        } else
        {
            count = FormulaFactory.getFormulaFor(activeValue);
            if (!count.isValid())
            {
                return new ParseResult.Fail("Count in " + getTokenName() + " was not valid: " + count.toString());
            }
            if (count.isStatic() && count.resolveStatic().doubleValue() <= 0)
            {
                return new ParseResult.Fail("Count in " + getFullName() + " must be > 0");
            }
            activeValue = sep.next();
        }
        if (sep.hasNext())
        {
            return new ParseResult.Fail(getFullName() + " had too many pipe separated items: " + value);
        }
        ParseResult pr = checkSeparatorsAndNonEmpty(',', activeValue);
        if (!pr.passed())
        {
            return pr;
        }

        List<CDOMReference<Skill>> refs = new ArrayList<>();
        StringTokenizer tok = new StringTokenizer(activeValue, Constants.COMMA);
        CDOMGroupRef<Skill> allRef = context.getReferenceContext().getCDOMAllReference(SKILL_CLASS);
        Integer autoRank = null;
        while (tok.hasMoreTokens())
        {
            String tokText = tok.nextToken();
            if (Constants.LST_ALL.equals(tokText) || Constants.LST_ANY.equals(tokText))
            {
                refs.add(allRef);
            } else
            {
                if (Constants.LST_UNTRAINED.equals(tokText))
                {
                    ObjectMatchingReference<Skill, Boolean> omr =
                            new ObjectMatchingReference<>(tokText, allRef, ObjectKey.USE_UNTRAINED, Boolean.TRUE);
                    omr.returnIncludesNulls(true);
                    refs.add(omr);
                } else if (Constants.LST_TRAINED.equals(tokText))
                {
                    refs.add(new ObjectMatchingReference<>(tokText, allRef, ObjectKey.USE_UNTRAINED, Boolean.FALSE));
                } else if (Constants.LST_EXCLUSIVE.equals(tokText))
                {
                    refs.add(new ObjectMatchingReference<>(tokText, allRef, ObjectKey.EXCLUSIVE, Boolean.TRUE));
                } else if (Constants.LST_NONEXCLUSIVE.equals(tokText) || Constants.LST_CROSS_CLASS.equals(tokText))
                {
                    ObjectMatchingReference<Skill, Boolean> omr =
                            new ObjectMatchingReference<>(tokText, allRef, ObjectKey.EXCLUSIVE, Boolean.FALSE);
                    omr.returnIncludesNulls(true);
                    refs.add(omr);
                } else if (tokText.startsWith("AUTORANK="))
                {
                    if (autoRank != null)
                    {
                        return new ParseResult.Fail(
                                "Cannot have two " + "AUTORANK= items in " + getFullName() + ": " + value);
                    }
                    String rankString = tokText.substring(9);
                    try
                    {
                        autoRank = Integer.decode(rankString);
                        if (autoRank <= 0)
                        {
                            return new ParseResult.Fail(
                                    "Expected AUTORANK= to be" + " greater than zero, found: " + autoRank);
                        }
                    } catch (NumberFormatException e)
                    {
                        return new ParseResult.Fail(
                                "Expected AUTORANK= to have" + " an integer value, found: " + rankString);
                    }
                } else
                {
                    CDOMReference<Skill> skref = TokenUtilities.getTypeOrPrimitive(context, SKILL_CLASS, tokText);
                    if (skref == null)
                    {
                        return new ParseResult.Fail("  Error was encountered while parsing " + getFullName() + ": "
                                + value + " had an invalid reference: " + tokText);
                    }
                    refs.add(skref);
                }
            }
        }

        if (refs.isEmpty())
        {
            return new ParseResult.Fail("Non-sensical " + getFullName() + ": Contains no skill reference: " + value);
        }

        ReferenceChoiceSet<Skill> rcs = new ReferenceChoiceSet<>(refs);
        if (!rcs.getGroupingState().isValid())
        {
            return new ParseResult.Fail(
                    "Non-sensical " + getFullName() + ": Contains ANY and a specific reference: " + value);
        }
        ChoiceSet<Skill> cs = new ChoiceSet<>(getTokenName(), rcs, true);
        PersistentTransitionChoice<Skill> tc = new ConcretePersistentTransitionChoice<>(cs, count);
        ClassSkillChoiceActor actor = new ClassSkillChoiceActor(obj, autoRank);
        tc.setChoiceActor(actor);
        context.getObjectContext().addToList(obj, ListKey.ADD, tc);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCClass obj)
    {
        Changes<PersistentTransitionChoice<?>> grantChanges =
                context.getObjectContext().getListChanges(obj, ListKey.ADD);
        Collection<PersistentTransitionChoice<?>> addedItems = grantChanges.getAdded();
        if (addedItems == null || addedItems.isEmpty())
        {
            // Zero indicates no Token
            return null;
        }
        List<String> addStrings = new ArrayList<>();
        for (TransitionChoice<?> container : addedItems)
        {
            SelectableSet<?> cs = container.getChoices();
            if (getTokenName().equals(cs.getName()) && SKILL_CLASS.equals(cs.getChoiceClass()))
            {
                Formula f = container.getCount();
                if (f == null)
                {
                    context.addWriteMessage("Unable to find " + getFullName() + " Count");
                    return null;
                }
                if (f.isStatic() && f.resolveStatic().doubleValue() <= 0)
                {
                    context.addWriteMessage("Count in " + getFullName() + " must be > 0");
                    return null;
                }
                if (!cs.getGroupingState().isValid())
                {
                    context.addWriteMessage("Non-sensical " + getFullName()
                            + ": Contains ANY and a specific reference: " + cs.getLSTformat());
                    return null;
                }
                StringBuilder sb = new StringBuilder();
                if (!FormulaFactory.ONE.equals(f))
                {
                    sb.append(f).append(Constants.PIPE);
                }
                sb.append(cs.getLSTformat());
                ClassSkillChoiceActor actor = (ClassSkillChoiceActor) container.getChoiceActor();
                Integer rank = actor.getApplyRank();
                if (rank != null)
                {
                    sb.append(Constants.COMMA).append("AUTORANK=").append(rank);
                }
                addStrings.add(sb.toString());
            }
        }
        return addStrings.toArray(new String[0]);
    }

    @Override
    public Class<PCClass> getTokenClass()
    {
        return PCClass.class;
    }
}
