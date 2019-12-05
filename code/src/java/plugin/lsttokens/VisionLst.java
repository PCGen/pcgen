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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Vision;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;

/**
 * {@code VisionLst} handles the processing of the VISION tag in LST
 * code.
 * <p>
 * (Sun, 15 Jun 2008) $
 */
public class VisionLst extends AbstractTokenWithSeparator<CDOMObject> implements CDOMPrimaryToken<CDOMObject>
{

    @Override
    public String getTokenName()
    {
        return "VISION";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
    {
        if (obj instanceof Ungranted)
        {
            return new ParseResult.Fail(
                    "Cannot use " + getTokenName() + " on an Ungranted object type: " + obj.getClass().getSimpleName());
        }
        StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);
        String visionString = aTok.nextToken();
        if (looksLikeAPrerequisite(visionString))
        {
            return new ParseResult.Fail("Cannot have only PRExxx subtoken in " + getTokenName() + ": " + value);
        }

        ArrayList<AssociatedPrereqObject> edgeList = new ArrayList<>();

        boolean foundClear = false;

        while (true)
        {
            if (Constants.LST_DOT_CLEAR.equals(visionString))
            {
                context.getListContext().removeAllFromList(getTokenName(), obj, Vision.VISIONLIST);
                foundClear = true;
            } else if (visionString.startsWith(Constants.LST_DOT_CLEAR_DOT))
            {
                try
                {
                    Vision vis = Vision.getVision(visionString.substring(7));
                    context.getListContext().removeFromList(getTokenName(), obj, Vision.VISIONLIST,
                            context.getReferenceContext().getCDOMDirectReference(vis));
                } catch (IllegalArgumentException e)
                {
                    ComplexParseResult cpr = new ComplexParseResult();
                    cpr.addErrorMessage("Bad Syntax for Cleared Vision in " + getTokenName());
                    cpr.addErrorMessage(e.getMessage());
                    return cpr;
                }
                foundClear = true;
            } else if (looksLikeAPrerequisite(visionString))
            {
                break;
            } else
            {
                try
                {
                    Vision vision = Vision.getVision(visionString);
                    AssociatedPrereqObject edge = context.getListContext().addToList(getTokenName(), obj,
                            Vision.VISIONLIST, context.getReferenceContext().getCDOMDirectReference(vision));
                    edgeList.add(edge);
                } catch (IllegalArgumentException e)
                {
                    ComplexParseResult cpr = new ComplexParseResult();
                    cpr.addErrorMessage("Bad Syntax for Vision in " + getTokenName());
                    cpr.addErrorMessage(e.getMessage());
                    return cpr;
                }
            }
            if (!aTok.hasMoreTokens())
            {
                return ParseResult.SUCCESS;
            }
            visionString = aTok.nextToken();
        }

        if (foundClear)
        {
            return new ParseResult.Fail("Cannot use PREREQs when using .CLEAR or .CLEAR. in " + getTokenName());
        }

        while (true)
        {
            Prerequisite prereq = getPrerequisite(visionString);
            if (prereq == null)
            {
                return new ParseResult.Fail(
                        "   (Did you put vision after the " + "PRExxx tags in " + getTokenName() + ":?)");
            }
            for (AssociatedPrereqObject edge : edgeList)
            {
                edge.addPrerequisite(prereq);
            }
            if (!aTok.hasMoreTokens())
            {
                break;
            }
            visionString = aTok.nextToken();
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
    {
        AssociatedChanges<CDOMReference<Vision>> changes =
                context.getListContext().getChangesInList(getTokenName(), obj, Vision.VISIONLIST);
        List<String> list = new ArrayList<>();
        Collection<CDOMReference<Vision>> removedItems = changes.getRemoved();
        if (changes.includesGlobalClear())
        {
            if (removedItems != null && !removedItems.isEmpty())
            {
                context.addWriteMessage(
                        "Non-sensical relationship in " + getTokenName() + ": global .CLEAR and local .CLEAR. performed");
                return null;
            }
            list.add(Constants.LST_DOT_CLEAR);
        } else if (removedItems != null && !removedItems.isEmpty())
        {
            list.add(Constants.LST_DOT_CLEAR_DOT + ReferenceUtilities.joinLstFormat(removedItems, "|.CLEAR."));
        }
        MapToList<CDOMReference<Vision>, AssociatedPrereqObject> mtl = changes.getAddedAssociations();
        if (mtl != null && !mtl.isEmpty())
        {
            MapToList<Set<Prerequisite>, Vision> m = new HashMapToList<>();
            for (CDOMReference<Vision> ab : mtl.getKeySet())
            {
                for (AssociatedPrereqObject assoc : mtl.getListFor(ab))
                {
                    m.addAllToListFor(new HashSet<>(assoc.getPrerequisiteList()), ab.getContainedObjects());
                }
            }
            Set<String> set = new TreeSet<>();
            for (Set<Prerequisite> prereqs : m.getKeySet())
            {
                StringBuilder sb = new StringBuilder(StringUtil.join(m.getListFor(prereqs), Constants.PIPE));
                if (prereqs != null && !prereqs.isEmpty())
                {
                    sb.append(Constants.PIPE);
                    sb.append(getPrerequisiteString(context, prereqs));
                }
                set.add(sb.toString());
            }
            list.addAll(set);
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
