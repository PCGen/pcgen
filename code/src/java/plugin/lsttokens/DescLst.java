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

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Description;
import pcgen.core.prereq.Prerequisite;
import pcgen.io.EntityEncoder;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.PatternChanges;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Handles DESC token processing
 */
public class DescLst extends AbstractTokenWithSeparator<CDOMObject> implements CDOMPrimaryToken<CDOMObject>
{
    @Override
    public String getTokenName()
    {
        return "DESC"; //$NON-NLS-1$
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String aDesc)
    {
        if (Constants.LST_DOT_CLEAR.equals(aDesc))
        {
            context.getObjectContext().removeList(obj, ListKey.DESCRIPTION);
            return ParseResult.SUCCESS;
        }
        if (aDesc.startsWith(Constants.LST_DOT_CLEAR_DOT))
        {
            context.getObjectContext().removePatternFromList(obj, ListKey.DESCRIPTION, aDesc.substring(7));
            return ParseResult.SUCCESS;
        }

        StringTokenizer tok = new StringTokenizer(aDesc, Constants.PIPE);

        String descString = tok.nextToken();

        if (looksLikeAPrerequisite(descString))
        {
            return new ParseResult.Fail(getTokenName() + " encountered only a PRExxx: " + aDesc);
        }
        String ds = EntityEncoder.decode(descString);
        if (!StringUtil.hasBalancedParens(ds))
        {
            return new ParseResult.Fail(getTokenName() + " encountered imbalanced Parenthesis: " + aDesc);
        }
        ParseResult pr = checkForInvalidXMLChars(ds);
        if (!pr.passed())
        {
            return pr;
        }
        Description desc = new Description(ds);

        if (tok.hasMoreTokens())
        {
            String token = tok.nextToken();
            while (true)
            {
                if (Constants.LST_DOT_CLEAR.equals(token))
                {
                    return new ParseResult.Fail(
                            getTokenName() + " tag confused by '.CLEAR' as a " + "middle token: " + aDesc);
                } else if (looksLikeAPrerequisite(token))
                {
                    break;
                } else
                {
                    desc.addVariable(token);
                }

                if (!tok.hasMoreTokens())
                {
                    // No prereqs, so we're done
                    context.getObjectContext().addToList(obj, ListKey.DESCRIPTION, desc);
                    return ParseResult.SUCCESS;
                }
                token = tok.nextToken();
            }

            while (true)
            {
                Prerequisite prereq = getPrerequisite(token);
                if (prereq == null)
                {
                    return new ParseResult.Fail(
                            "   (Did you put Abilities after the " + "PRExxx tags in " + getTokenName() + ":?)");
                }
                desc.addPrerequisite(prereq);
                if (!tok.hasMoreTokens())
                {
                    break;
                }
                token = tok.nextToken();
            }
        }
        context.getObjectContext().addToList(obj, ListKey.DESCRIPTION, desc);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
    {
        PatternChanges<Description> changes =
                context.getObjectContext().getListPatternChanges(obj, ListKey.DESCRIPTION);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        List<String> list = new ArrayList<>();
        Collection<String> removedItems = changes.getRemoved();
        if (changes.includesGlobalClear())
        {
            if (removedItems != null && !removedItems.isEmpty())
            {
                context.addWriteMessage(
                        "Non-sensical relationship in " + getTokenName() + ": global .CLEAR and local .CLEAR. performed");
                return null;
            }
            list.add(Constants.LST_DOT_CLEAR);
        }
        if (removedItems != null && !removedItems.isEmpty())
        {
            for (String d : removedItems)
            {
                list.add(Constants.LST_DOT_CLEAR_DOT + d);
            }
        }
        Collection<Description> addedItems = changes.getAdded();
        if (addedItems != null)
        {
            for (Description d : addedItems)
            {
                list.add(d.getPCCText());
            }
        }
        if (list.isEmpty())
        {
            return null;
        }
        return list.toArray(new String[0]);
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }
}
