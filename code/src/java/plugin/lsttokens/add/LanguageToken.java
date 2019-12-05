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
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ConcretePersistentTransitionChoice;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.SelectableSet;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Language;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class LanguageToken extends AbstractNonEmptyToken<CDOMObject>
        implements CDOMSecondaryToken<CDOMObject>, PersistentChoiceActor<Language>
{

    private static final Class<Language> LANGUAGE_CLASS = Language.class;

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
        return "LANGUAGE";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, CDOMObject obj, String value)
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

        List<CDOMReference<Language>> refs = new ArrayList<>();
        StringTokenizer tok = new StringTokenizer(activeValue, Constants.COMMA);
        while (tok.hasMoreTokens())
        {
            String tokText = tok.nextToken();
            CDOMReference<Language> lang = TokenUtilities.getReference(context, LANGUAGE_CLASS, tokText);
            if (lang == null)
            {
                return new ParseResult.Fail("  Error was encountered while parsing " + getFullName() + ": " + value
                        + " had an invalid reference: " + tokText);
            }
            refs.add(lang);
        }

        ReferenceChoiceSet<Language> rcs = new ReferenceChoiceSet<>(refs);
        if (!rcs.getGroupingState().isValid())
        {
            return new ParseResult.Fail(
                    "Non-sensical " + getFullName() + ": Contains ANY and a specific reference: " + value);
        }

        ChoiceSet<Language> cs = new ChoiceSet<>(getTokenName(), rcs);
        cs.setTitle("Language Choice");
        PersistentTransitionChoice<Language> tc = new ConcretePersistentTransitionChoice<>(cs, count);
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
            if (cs.getName().equals(getTokenName()) && LANGUAGE_CLASS.equals(cs.getChoiceClass()))
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
    public void applyChoice(CDOMObject owner, Language choice, PlayerCharacter pc)
    {
        pc.addAddLanguage(choice, owner);
    }

    @Override
    public boolean allow(Language choice, PlayerCharacter pc, boolean allowStack)
    {
        return !pc.hasLanguage(choice);
    }

    @Override
    public Language decodeChoice(LoadContext context, String s)
    {
        return context.getReferenceContext().silentlyGetConstructedCDOMObject(LANGUAGE_CLASS, s);
    }

    @Override
    public String encodeChoice(Language choice)
    {
        return choice.getKeyName();
    }

    @Override
    public void restoreChoice(PlayerCharacter pc, CDOMObject owner, Language choice)
    {
        pc.addAddLanguage(choice, owner);
    }

    @Override
    public void removeChoice(PlayerCharacter pc, CDOMObject owner, Language choice)
    {
        pc.removeAddLanguage(choice, owner);
    }
}
