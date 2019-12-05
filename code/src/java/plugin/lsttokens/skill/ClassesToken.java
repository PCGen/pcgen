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
package plugin.lsttokens.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.list.ClassSkillList;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.FilteredReference;
import pcgen.core.Skill;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with CLASSES Token
 */
public class ClassesToken extends AbstractTokenWithSeparator<Skill> implements CDOMPrimaryToken<Skill>
{

    private static final Class<ClassSkillList> SKILLLIST_CLASS = ClassSkillList.class;

    @Override
    public String getTokenName()
    {
        return "CLASSES";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, Skill skill, String value)
    {
        StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);
        boolean added = false;

        List<CDOMReference<ClassSkillList>> allow = new ArrayList<>();
        while (pipeTok.hasMoreTokens())
        {
            String className = pipeTok.nextToken();
            if (Constants.LST_ALL.equals(className))
            {
                if (added)
                {
                    return new ParseResult.Fail(
                            "Non-sensical Skill " + getTokenName() + ": Contains ALL after a specific reference: " + value);
                }
                break;
            }
            if (className.startsWith("!"))
            {
                return new ParseResult.Fail(
                        "Non-sensical Skill " + getTokenName() + ": Contains ! without (or before) ALL: " + value);
            }
            allow.add(context.getReferenceContext().getCDOMReference(SKILLLIST_CLASS, className));
            added = true;
        }
        if (pipeTok.hasMoreTokens())
        {
            // allow is not used (empty or an error)
            FilteredReference<ClassSkillList> filtered =
                    new FilteredReference<>(context.getReferenceContext().getCDOMAllReference(SKILLLIST_CLASS));
            while (pipeTok.hasMoreTokens())
            {
                String className = pipeTok.nextToken();
                if (className.startsWith("!"))
                {
                    String clString = className.substring(1);
                    if (Constants.LST_ALL.equals(clString) || Constants.LST_ANY.equals(clString))
                    {
                        return new ParseResult.Fail("Invalid " + getTokenName() + " cannot use !ALL");
                    }
                    CDOMSingleRef<ClassSkillList> ref =
                            context.getReferenceContext().getCDOMReference(SKILLLIST_CLASS, clString);
                    filtered.addProhibitedItem(ref);
                } else
                {
                    return new ParseResult.Fail(
                            "Non-sensical Skill " + getTokenName() + ": Contains ALL and a specific reference: " + value);
                }
            }
            context.getListContext().addToMasterList(getTokenName(), skill, filtered, skill);
        } else if (allow.isEmpty())
        {
            // unqualified ALL
            context.getListContext().addToMasterList(getTokenName(), skill,
                    context.getReferenceContext().getCDOMAllReference(SKILLLIST_CLASS), skill);
        } else
        {
            // use allow
            for (CDOMReference<ClassSkillList> ref : allow)
            {
                context.getListContext().addToMasterList(getTokenName(), skill, ref, skill);
            }
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Skill skill)
    {
        Changes<CDOMReference<ClassSkillList>> masterChanges =
                context.getListContext().getMasterListChanges(getTokenName(), skill, SKILLLIST_CLASS);
        if (masterChanges.includesGlobalClear())
        {
            context.addWriteMessage(getTokenName() + " does not support .CLEAR");
            return null;
        }
        if (masterChanges.hasRemovedItems())
        {
            context.addWriteMessage(getTokenName() + " does not support .CLEAR.");
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (!masterChanges.hasAddedItems())
        {
            // That's fine - nothing to do
            return null;
        }
        boolean needBar = false;
        for (CDOMReference<ClassSkillList> ref : masterChanges.getAdded())
        {
            if (needBar)
            {
                sb.append(Constants.PIPE);
            }
            sb.append(ref.getLSTformat(false));
            needBar = true;
        }
        return new String[]{sb.toString()};
    }

    @Override
    public Class<Skill> getTokenClass()
    {
        return Skill.class;
    }
}
