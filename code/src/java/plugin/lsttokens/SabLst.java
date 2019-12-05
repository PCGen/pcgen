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
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.SpecialAbility;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class SabLst extends AbstractTokenWithSeparator<CDOMObject> implements CDOMPrimaryToken<CDOMObject>
{

    @Override
    public String getTokenName()
    {
        return "SAB";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    /**
     * This method sets the special abilities granted by this [object]. For
     * efficiency, avoid calling this method except from I/O routines.
     *
     * @param context
     * @param obj     the CDOMbject that is to receive the new SpecialAbility
     * @param aString String of special abilities delimited by pipes
     */
    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String aString)
    {
        if (obj instanceof Ungranted)
        {
            return new ParseResult.Fail(
                    "Cannot use " + getTokenName() + " on an Ungranted object type: " + obj.getClass().getSimpleName());
        }
        StringTokenizer tok = new StringTokenizer(aString, Constants.PIPE);

        String firstToken = tok.nextToken();
        if (looksLikeAPrerequisite(firstToken))
        {
            return new ParseResult.Fail("Cannot have only PRExxx subtoken in " + getTokenName());
        }

        boolean foundClear = false;

        if (Constants.LST_DOT_CLEAR.equals(firstToken))
        {
            context.getObjectContext().removeList(obj, ListKey.SAB);
            if (!tok.hasMoreTokens())
            {
                return ParseResult.SUCCESS;
            }
            firstToken = tok.nextToken();
            foundClear = true;
        }

        if (looksLikeAPrerequisite(firstToken))
        {
            return new ParseResult.Fail("Cannot use PREREQs when using .CLEAR in " + getTokenName());
        }

        if (Constants.LST_DOT_CLEAR.equals(firstToken))
        {
            return new ParseResult.Fail("SA tag confused by redundant '.CLEAR'" + aString);
        }

        SpecialAbility sa = new SpecialAbility(firstToken.intern());

        if (!tok.hasMoreTokens())
        {
            sa.setName(firstToken.intern());
            context.getObjectContext().addToList(obj, ListKey.SAB, sa);
            return ParseResult.SUCCESS;
        }

        StringBuilder saName = new StringBuilder(aString.length());
        saName.append(firstToken);

        String token = tok.nextToken();
        while (true)
        {
            if (Constants.LST_DOT_CLEAR.equals(token))
            {
                return new ParseResult.Fail("SA tag confused by '.CLEAR' as a " + "middle token: " + aString);
            } else if (looksLikeAPrerequisite(token))
            {
                break;
            } else
            {
                saName.append(Constants.PIPE).append(token);
                // sa.addVariable(FormulaFactory.getFormulaFor(token));
            }

            if (!tok.hasMoreTokens())
            {
                // No prereqs, so we're done
                // CONSIDER This is a HACK and not the long term strategy of SA:
                sa.setName(saName.toString());
                context.getObjectContext().addToList(obj, ListKey.SAB, sa);
                return ParseResult.SUCCESS;
            }
            token = tok.nextToken();
        }
        // CONSIDER This is a HACK and not the long term strategy of SA:
        sa.setName(saName.toString());

        if (foundClear)
        {
            return new ParseResult.Fail(
                    "Cannot use PREREQs when using .CLEAR and a Special Ability in " + getTokenName());
        }

        while (true)
        {
            Prerequisite prereq = getPrerequisite(token);
            if (prereq == null)
            {
                return new ParseResult.Fail(
                        "   (Did you put Abilities after the " + "PRExxx tags in " + getTokenName() + ":?)");
            }
            sa.addPrerequisite(prereq);
            if (!tok.hasMoreTokens())
            {
                break;
            }
            token = tok.nextToken();
        }
        context.getObjectContext().addToList(obj, ListKey.SAB, sa);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
    {
        Changes<SpecialAbility> changes = context.getObjectContext().getListChanges(obj, ListKey.SAB);
        Collection<SpecialAbility> added = changes.getAdded();
        List<String> list = new ArrayList<>();
        if (changes.includesGlobalClear())
        {
            list.add(Constants.LST_DOT_CLEAR);
        } else if (added == null || added.isEmpty())
        {
            // Zero indicates no Token (and no global clear, so nothing to do)
            return null;
        }
        if (added != null)
        {
            for (SpecialAbility ab : added)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(ab.getKeyName());
                if (ab.hasPrerequisites())
                {
                    sb.append(Constants.PIPE);
                    sb.append(getPrerequisiteString(context, ab.getPrerequisiteList()));
                }
                list.add(sb.toString());
            }
        }
        return list.toArray(new String[0]);
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }
}
