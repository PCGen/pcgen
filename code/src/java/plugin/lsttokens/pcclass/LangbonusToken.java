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
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Language;
import pcgen.core.PCClass;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with LANGBONUS Token
 */
public class LangbonusToken extends AbstractTokenWithSeparator<PCClass> implements CDOMPrimaryToken<PCClass>
{

    private static final Class<Language> LANGUAGE_CLASS = Language.class;

    @Override
    public String getTokenName()
    {
        return "LANGBONUS";
    }

    @Override
    protected char separator()
    {
        return ',';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, PCClass cl, String value)
    {
        StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);
        boolean foundAny = false;
        boolean foundOther = false;
        boolean firstToken = true;

        while (tok.hasMoreTokens())
        {
            String tokText = tok.nextToken();
            if (Constants.LST_DOT_CLEAR.equals(tokText))
            {
                if (!firstToken)
                {
                    return new ParseResult.Fail("Non-sensical situation was " + "encountered while parsing "
                            + getTokenName() + ": When used, .CLEAR must be the first argument");
                }
                context.getListContext().removeAllFromList(getTokenName(), cl, Language.STARTING_LIST);
            } else if (tokText.startsWith(Constants.LST_DOT_CLEAR_DOT))
            {
                CDOMReference<Language> lang;
                String clearText = tokText.substring(7);
                if (Constants.LST_ALL.equals(clearText))
                {
                    lang = context.getReferenceContext().getCDOMAllReference(LANGUAGE_CLASS);
                } else
                {
                    lang = TokenUtilities.getTypeOrPrimitive(context, LANGUAGE_CLASS, clearText);
                }
                if (lang == null)
                {
                    return new ParseResult.Fail("  Error was encountered while parsing " + getTokenName() + ": " + value
                            + " had an invalid .CLEAR. reference: " + clearText);
                }
                context.getListContext().removeFromList(getTokenName(), cl, Language.STARTING_LIST, lang);
            } else
            {
                /*
                 * Note this is done one-by-one, because .CLEAR. token type
                 * needs to be able to perform the unlink. That could be
                 * changed, but the increase in complexity isn't worth it.
                 * (Changing it to a grouping object that didn't place links in
                 * the graph would also make it harder to trace the source of
                 * class skills, etc.)
                 */
                CDOMReference<Language> lang;
                if (Constants.LST_ALL.equals(tokText))
                {
                    foundAny = true;
                    lang = context.getReferenceContext().getCDOMAllReference(LANGUAGE_CLASS);
                } else
                {
                    foundOther = true;
                    lang = TokenUtilities.getTypeOrPrimitive(context, LANGUAGE_CLASS, tokText);
                }
                if (lang == null)
                {
                    return new ParseResult.Fail("  Error was encountered while parsing " + getTokenName() + ": " + value
                            + " had an invalid reference: " + tokText);
                }
                context.getListContext().addToList(getTokenName(), cl, Language.STARTING_LIST, lang);
            }
            firstToken = false;
        }
        if (foundAny && foundOther)
        {
            return new ParseResult.Fail(
                    "Non-sensical " + getTokenName() + ": Contains ANY and a specific reference: " + value);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCClass pcl)
    {
        AssociatedChanges<CDOMReference<Language>> changes =
                context.getListContext().getChangesInList(getTokenName(), pcl, Language.STARTING_LIST);
        List<String> list = new ArrayList<>();
        Collection<CDOMReference<Language>> removedItems = changes.getRemoved();
        if (removedItems != null && !removedItems.isEmpty())
        {
            if (changes.includesGlobalClear())
            {
                context.addWriteMessage(
                        "Non-sensical relationship in " + getTokenName() + ": global .CLEAR and local .CLEAR. performed");
                return null;
            }
            list.add(Constants.LST_DOT_CLEAR_DOT + ReferenceUtilities.joinLstFormat(removedItems, ",.CLEAR."));
        }
        if (changes.includesGlobalClear())
        {
            list.add(Constants.LST_DOT_CLEAR);
        }
        Collection<CDOMReference<Language>> addedItems = changes.getAdded();
        if (addedItems != null && !addedItems.isEmpty())
        {
            list.add(ReferenceUtilities.joinLstFormat(addedItems, Constants.COMMA));
        }
        if (list.isEmpty())
        {
            // Zero indicates no add or global clear
            return null;
        }
        return list.toArray(new String[0]);
    }

    @Override
    public Class<PCClass> getTokenClass()
    {
        return PCClass.class;
    }
}
