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
package plugin.lsttokens.pcclass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.PatternMatchingReference;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.PCClass;
import pcgen.core.Skill;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class CcskillToken extends AbstractTokenWithSeparator<PCClass> implements CDOMPrimaryToken<PCClass>
{

    private static final Class<Skill> SKILL_CLASS = Skill.class;

    @Override
    public String getTokenName()
    {
        return "CCSKILL";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, PCClass obj, String value)
    {
        boolean first = true;
        boolean foundAny = false;
        boolean foundOther = false;

        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
        while (tok.hasMoreTokens())
        {
            String tokText = tok.nextToken();
            if (Constants.LST_DOT_CLEAR.equals(tokText))
            {
                if (!first)
                {
                    return new ParseResult.Fail(
                            "  Non-sensical " + getTokenName() + ": .CLEAR was not the first list item");
                }
                context.getObjectContext().removeList(obj, ListKey.LOCALCCSKILL);
            } else if (tokText.startsWith(Constants.LST_DOT_CLEAR_DOT))
            {
                String clearText = tokText.substring(7);
                if (Constants.LST_ALL.equals(clearText))
                {
                    context.getObjectContext().removeFromList(obj, ListKey.LOCALCCSKILL,
                            context.getReferenceContext().getCDOMAllReference(SKILL_CLASS));
                } else
                {
                    CDOMReference<Skill> ref = TokenUtilities.getTypeOrPrimitive(context, SKILL_CLASS, clearText);
                    if (ref == null)
                    {
                        return new ParseResult.Fail("  Error was encountered while parsing " + getTokenName());
                    }
                    context.getObjectContext().removeFromList(obj, ListKey.LOCALCCSKILL, ref);
                }
            } else
            {
                /*
                 * Note this HAS to be done one-by-one, because the
                 * .clearChildNodeOfClass method above does NOT recognize the
                 * C/CC Skill object and therefore doesn't know how to search
                 * the sublists
                 */
                if (Constants.LST_ALL.equals(tokText))
                {
                    foundAny = true;
                    context.getObjectContext().addToList(obj, ListKey.LOCALCCSKILL,
                            context.getReferenceContext().getCDOMAllReference(SKILL_CLASS));
                } else
                {
                    foundOther = true;
                    CDOMReference<Skill> ref = getSkillReference(context, tokText);
                    if (ref == null)
                    {
                        return new ParseResult.Fail("  Error was encountered while parsing " + getTokenName());
                    }
                    context.getObjectContext().addToList(obj, ListKey.LOCALCCSKILL, ref);
                }
            }
            first = false;
        }
        if (foundAny && foundOther)
        {
            return new ParseResult.Fail(
                    "Non-sensical " + getTokenName() + ": Contains ANY and a specific reference: " + value);
        }
        return ParseResult.SUCCESS;
    }

    private CDOMReference<Skill> getSkillReference(LoadContext context, String tokText)
    {
        if (tokText.endsWith(Constants.PERCENT))
        {
            return new PatternMatchingReference<>(context.getReferenceContext().getCDOMAllReference(SKILL_CLASS),
                    tokText);
        } else
        {
            return TokenUtilities.getTypeOrPrimitive(context, SKILL_CLASS, tokText);
        }
    }

    @Override
    public String[] unparse(LoadContext context, PCClass obj)
    {
        Changes<CDOMReference<Skill>> changes = context.getObjectContext().getListChanges(obj, ListKey.LOCALCCSKILL);
        List<String> list = new ArrayList<>();
        Collection<CDOMReference<Skill>> removedItems = changes.getRemoved();
        if (removedItems != null && !removedItems.isEmpty())
        {
            if (changes.includesGlobalClear())
            {
                context.addWriteMessage(
                        "Non-sensical relationship in " + getTokenName() + ": global .CLEAR and local .CLEAR. performed");
                return null;
            }
            list.add(Constants.LST_DOT_CLEAR_DOT + ReferenceUtilities.joinLstFormat(removedItems, "|.CLEAR."));
        }
        if (changes.includesGlobalClear())
        {
            list.add(Constants.LST_DOT_CLEAR);
        }
        Collection<CDOMReference<Skill>> added = changes.getAdded();
        if (added != null && !added.isEmpty())
        {
            list.add(ReferenceUtilities.joinLstFormat(added, Constants.PIPE));
        }
        if (list.isEmpty())
        {
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
