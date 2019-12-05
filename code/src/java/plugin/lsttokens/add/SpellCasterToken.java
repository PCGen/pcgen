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
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ConcretePersistentTransitionChoice;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.SelectableSet;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.choiceset.SpellCasterChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.BonusAddition;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class SpellCasterToken extends AbstractNonEmptyToken<CDOMObject>
        implements CDOMSecondaryToken<CDOMObject>, PersistentChoiceActor<PCClass>
{

    private static final Class<PCClass> PCCLASS_CLASS = PCClass.class;

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
    public String getTokenName()
    {
        return "SPELLCASTER";
    }

    @Override
    public ParseResult parseNonEmptyToken(LoadContext context, CDOMObject obj, String value)
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
        StringTokenizer tok = new StringTokenizer(activeValue, Constants.COMMA);

        boolean foundAny = false;
        boolean foundOther = false;

        List<CDOMReference<PCClass>> groups = new ArrayList<>();
        List<CDOMReference<PCClass>> prims = new ArrayList<>();
        List<String> spelltypes = new ArrayList<>();
        CDOMGroupRef<PCClass> allRef = context.getReferenceContext().getCDOMAllReference(PCCLASS_CLASS);
        while (tok.hasMoreTokens())
        {
            String token = tok.nextToken();
            if (Constants.LST_ANY.equalsIgnoreCase(token))
            {
                foundAny = true;
                groups.add(allRef);
            } else
            {
                foundOther = true;
                if (token.equals("Arcane") || token.equals("Divine") || token.equals("Psionic"))
                {
                    spelltypes.add(token);
                } else if (token.startsWith(Constants.LST_TYPE_DOT) || token.startsWith(Constants.LST_TYPE_EQUAL))
                {
                    CDOMReference<PCClass> ref =
                            TokenUtilities.getTypeReference(context, PCCLASS_CLASS, token.substring(5));
                    if (ref == null)
                    {
                        return new ParseResult.Fail("  Error was encountered while parsing " + getFullName() + ": "
                                + token + " is not a valid reference: " + value);
                    }
                    groups.add(ref);
                } else
                {
                    prims.add(context.getReferenceContext().getCDOMReference(PCCLASS_CLASS, token));
                }
            }
        }

        if (foundAny && foundOther)
        {
            return new ParseResult.Fail(
                    "Non-sensical " + getFullName() + ": Contains ANY and a specific reference: " + value);
        }

        ReferenceChoiceSet<PCClass> grcs = groups.isEmpty() ? null : new ReferenceChoiceSet<>(groups);
        ReferenceChoiceSet<PCClass> prcs = prims.isEmpty() ? null : new ReferenceChoiceSet<>(prims);
        SelectableSet<PCClass> cs = new SpellCasterChoiceSet(allRef, spelltypes, grcs, prcs);
        cs.setTitle("Spell Caster Class Choice");
        PersistentTransitionChoice<PCClass> tc = new ConcretePersistentTransitionChoice<>(cs, count);
        context.getObjectContext().addToList(obj, ListKey.ADD, tc);
        tc.setChoiceActor(this);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
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
            if (PCCLASS_CLASS.equals(cs.getChoiceClass()))
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
                addStrings.add(sb.toString());

                // assoc.getAssociation(AssociationKey.CHOICE_MAXCOUNT);
            }
        }
        return addStrings.toArray(new String[0]);
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }

    @Override
    public void applyChoice(CDOMObject owner, PCClass choice, PlayerCharacter pc)
    {
        PCClass theClass = pc.getClassKeyed(choice.getKeyName());
        if (theClass == null)
        {
            pc.incrementClassLevel(0, choice);
            theClass = pc.getClassKeyed(choice.getKeyName());
            pc.setSpellLists(theClass);
        }

        BonusAddition.applyBonus("PCLEVEL|" + theClass.getKeyName() + "|1", "", pc, owner);

        pc.calcActiveBonuses();
        for (PCClass pcClass : pc.getClassSet())
        {
            pc.calculateKnownSpellsForClassLevel(pcClass);
        }
    }

    @Override
    public boolean allow(PCClass choice, PlayerCharacter pc, boolean allowStack)
    {
        return true;
    }

    @Override
    public PCClass decodeChoice(LoadContext context, String s)
    {
        return context.getReferenceContext().silentlyGetConstructedCDOMObject(PCCLASS_CLASS, s);
    }

    @Override
    public String encodeChoice(PCClass choice)
    {
        return choice.getKeyName();
    }

    @Override
    public void restoreChoice(PlayerCharacter pc, CDOMObject owner, PCClass choice)
    {
        if (owner instanceof PCClassLevel)
        {
            // Bonuses for ADD:SPELLCASTER are restored on load
            // for a PCClassLevel so no need to restore here.
            return;
        }
        applyChoice(owner, choice, pc);
    }

    @Override
    public void removeChoice(PlayerCharacter pc, CDOMObject owner, PCClass choice)
    {
        PCClass theClass = pc.getClassKeyed(choice.getKeyName());

        if (theClass != null)
        {
            BonusAddition.removeBonus("PCLEVEL|" + theClass.getKeyName() + "|1", pc, owner);
            pc.calcActiveBonuses();
            pc.getClassSet().forEach(pc::calculateKnownSpellsForClassLevel);
        }
    }
}
