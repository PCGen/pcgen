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
package plugin.lsttokens.deity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.list.DomainList;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with DOMAINS Token
 */
public class DomainsToken extends AbstractTokenWithSeparator<Deity> implements CDOMPrimaryToken<Deity>
{

    private static final Class<Domain> DOMAIN_CLASS = Domain.class;

    @Override
    public String getTokenName()
    {
        return "DOMAINS";
    }

    @Override
    protected char separator()
    {
        return ',';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, Deity deity, String value)
    {
        StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);
        StringTokenizer commaTok = new StringTokenizer(pipeTok.nextToken(), Constants.COMMA);
        CDOMReference<DomainList> dl = Deity.DOMAINLIST;
        ArrayList<AssociatedPrereqObject> proList = new ArrayList<>();

        boolean first = true;
        boolean foundAll = false;
        boolean foundOther = false;
        boolean foundClear = false;

        while (commaTok.hasMoreTokens())
        {
            String tokString = commaTok.nextToken();
            if (looksLikeAPrerequisite(tokString))
            {
                return new ParseResult.Fail("Invalid " + getTokenName() + ": PRExxx was comma delimited : " + value);
            }
            if (Constants.LST_DOT_CLEAR.equals(tokString))
            {
                if (!first)
                {
                    return new ParseResult.Fail(
                            "  Non-sensical " + getTokenName() + ": .CLEAR was not the first list item: " + value);
                }
                context.getListContext().removeAllFromList(getTokenName(), deity, dl);
                foundClear = true;
            } else if (tokString.startsWith(Constants.LST_DOT_CLEAR_DOT))
            {
                CDOMReference<Domain> ref;
                String clearText = tokString.substring(7);
                if (Constants.LST_ALL.equals(clearText) || Constants.LST_ANY.equals(clearText))
                {
                    ref = context.getReferenceContext().getCDOMAllReference(DOMAIN_CLASS);
                } else
                {
                    ref = context.getReferenceContext().getCDOMReference(DOMAIN_CLASS, clearText);
                }
                context.getListContext().removeFromList(getTokenName(), deity, dl, ref);
                foundClear = true;
            } else if (Constants.LST_ALL.equals(tokString) || Constants.LST_ANY.equals(tokString))
            {
                CDOMGroupRef<Domain> ref = context.getReferenceContext().getCDOMAllReference(DOMAIN_CLASS);
                proList.add(context.getListContext().addToList(getTokenName(), deity, dl, ref));
                foundAll = true;
            } else
            {
                CDOMSingleRef<Domain> ref = context.getReferenceContext().getCDOMReference(DOMAIN_CLASS, tokString);
                proList.add(context.getListContext().addToList(getTokenName(), deity, dl, ref));
                foundOther = true;
            }
            first = false;
        }

        if (foundAll && foundOther)
        {
            return new ParseResult.Fail(
                    "Non-sensical " + getTokenName() + ": Contains ALL and a specific reference: " + value);
        }

        while (pipeTok.hasMoreTokens())
        {
            if (foundClear)
            {
                return new ParseResult.Fail("Cannot use PREREQs when using .CLEAR or .CLEAR. in " + getTokenName());
            }
            String tokString = pipeTok.nextToken();
            Prerequisite prereq = getPrerequisite(tokString);
            if (prereq == null)
            {
                return new ParseResult.Fail(
                        "   (Did you put items after the " + "PRExxx tags in " + getTokenName() + ":?)");
            }
            for (AssociatedPrereqObject ao : proList)
            {
                ao.addPrerequisite(prereq);
            }
        }

        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Deity deity)
    {
        CDOMReference<DomainList> dl = Deity.DOMAINLIST;
        AssociatedChanges<CDOMReference<Domain>> changes =
                context.getListContext().getChangesInList(getTokenName(), deity, dl);
        List<String> list = new ArrayList<>();
        Collection<CDOMReference<Domain>> removedItems = changes.getRemoved();
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
            list.add(Constants.LST_DOT_CLEAR_DOT + ReferenceUtilities.joinLstFormat(removedItems, ",.CLEAR.", true));
        }
        MapToList<CDOMReference<Domain>, AssociatedPrereqObject> mtl = changes.getAddedAssociations();
        if (mtl != null && !mtl.isEmpty())
        {
            MapToList<Set<Prerequisite>, CDOMReference<Domain>> m = new HashMapToList<>();
            for (CDOMReference<Domain> ab : mtl.getKeySet())
            {
                for (AssociatedPrereqObject assoc : mtl.getListFor(ab))
                {
                    m.addToListFor(new HashSet<>(assoc.getPrerequisiteList()), ab);
                }
            }
            Set<String> set = new TreeSet<>();
            for (Set<Prerequisite> prereqs : m.getKeySet())
            {
                Set<CDOMReference<Domain>> domainSet = new TreeSet<>(ReferenceUtilities.REFERENCE_SORTER);
                domainSet.addAll(m.getListFor(prereqs));
                StringBuilder sb =
                        new StringBuilder(ReferenceUtilities.joinLstFormat(domainSet, Constants.COMMA, true));
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
    public Class<Deity> getTokenClass()
    {
        return Deity.class;
    }
}
