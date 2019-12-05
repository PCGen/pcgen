/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
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
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.core.QualifiedObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with DOMAIN Token
 */
public class DomainToken extends AbstractTokenWithSeparator<PCClass> implements CDOMPrimaryToken<PCClass>
{

    private static final Class<Domain> DOMAIN_CLASS = Domain.class;

    @Override
    public String getTokenName()
    {
        return "DOMAIN";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, PCClass pcc, String value)
    {
        StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);

        boolean first = true;
        String tok = pipeTok.nextToken();

        if (looksLikeAPrerequisite(tok))
        {
            return new ParseResult.Fail("Cannot have only PRExxx subtoken in " + getTokenName() + ": " + value);
        }

        List<QualifiedObject<CDOMSingleRef<Domain>>> toAdd = new ArrayList<>();
        boolean foundClear = false;
        while (true)
        {
            if (Constants.LST_DOT_CLEAR.equals(tok))
            {
                if (!first)
                {
                    return new ParseResult.Fail(
                            "  Non-sensical " + getTokenName() + ": .CLEAR was not the first list item");
                }
                context.getObjectContext().removeList(pcc, ListKey.DOMAIN);
                foundClear = true;
            } else
            {
                CDOMSingleRef<Domain> domain = context.getReferenceContext().getCDOMReference(DOMAIN_CLASS, tok);
                QualifiedObject<CDOMSingleRef<Domain>> qo = new QualifiedObject<>(domain);
                toAdd.add(qo);
                context.getObjectContext().addToList(pcc, ListKey.DOMAIN, qo);
            }
            first = false;
            if (!pipeTok.hasMoreTokens())
            {
                // No prereqs, so we're done
                return ParseResult.SUCCESS;
            }
            tok = pipeTok.nextToken();
            if (looksLikeAPrerequisite(tok))
            {
                break;
            }
        }
        if (foundClear)
        {
            return new ParseResult.Fail("Cannot use PREREQs when using .CLEAR in " + getTokenName());
        }

        while (true)
        {
            Prerequisite prereq = getPrerequisite(tok);
            if (prereq == null)
            {
                return new ParseResult.Fail(
                        "   (Did you put feats after the " + "PRExxx tags in " + getTokenName() + ":?)");
            }
            for (PrereqObject pro : toAdd)
            {
                pro.addPrerequisite(prereq);
            }
            if (!pipeTok.hasMoreTokens())
            {
                break;
            }
            tok = pipeTok.nextToken();
        }

        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCClass pcc)
    {
        Changes<QualifiedObject<CDOMSingleRef<Domain>>> changes =
                context.getObjectContext().getListChanges(pcc, ListKey.DOMAIN);
        List<String> returnList = new ArrayList<>();
        if (changes.includesGlobalClear())
        {
            returnList.add(Constants.LST_DOT_CLEAR);
        }
        Collection<QualifiedObject<CDOMSingleRef<Domain>>> removedItems = changes.getRemoved();
        if (removedItems != null && !removedItems.isEmpty())
        {
            context.addWriteMessage(getTokenName() + " does not support .CLEAR.");
            return null;
        }
        Collection<QualifiedObject<CDOMSingleRef<Domain>>> added = changes.getAdded();
        if (added != null && !added.isEmpty())
        {
            HashMapToList<List<Prerequisite>, CDOMSingleRef<Domain>> m = new HashMapToList<>();
            for (QualifiedObject<CDOMSingleRef<Domain>> qo : added)
            {
                m.addToListFor(qo.getPrerequisiteList(), qo.getRawObject());
            }

            Set<String> returnSet = new TreeSet<>();
            for (List<Prerequisite> prereqs : m.getKeySet())
            {
                StringBuilder sb = new StringBuilder();
                sb.append(ReferenceUtilities.joinLstFormat(m.getListFor(prereqs), Constants.PIPE));
                if (prereqs != null && !prereqs.isEmpty())
                {
                    sb.append(Constants.PIPE);
                    sb.append(getPrerequisiteString(context, prereqs));
                }
                returnSet.add(sb.toString());
            }
            returnList.addAll(returnSet);
        }
        if (returnList.isEmpty())
        {
            return null;
        }
        return returnList.toArray(new String[0]);
    }

    @Override
    public Class<PCClass> getTokenClass()
    {
        return PCClass.class;
    }
}
