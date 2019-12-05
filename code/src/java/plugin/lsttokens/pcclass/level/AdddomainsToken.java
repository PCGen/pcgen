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
package plugin.lsttokens.pcclass.level;

import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with ADDDOMAINS Token
 */
public class AdddomainsToken extends AbstractTokenWithSeparator<PCClassLevel> implements CDOMPrimaryToken<PCClassLevel>
{

    private static final Class<Domain> DOMAIN_CLASS = Domain.class;

    @Override
    public String getTokenName()
    {
        return "ADDDOMAINS";
    }

    @Override
    protected char separator()
    {
        return '.';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, PCClassLevel level, String value)
    {
        StringTokenizer tok = new StringTokenizer(value, Constants.DOT);
        while (tok.hasMoreTokens())
        {
            String tokString = tok.nextToken();
            Prerequisite prereq = null; // Do not initialize, null is
            // significant!
            String domainKey;

            // Note: May contain PRExxx
            int openBracketLoc = tokString.indexOf('[');
            if (openBracketLoc == -1)
            {
                if (tokString.indexOf(']') != -1)
                {
                    return new ParseResult.Fail(
                            "Invalid " + getTokenName() + " must have '[' if it contains a PREREQ tag");
                }
                domainKey = tokString;
            } else
            {
                if (tokString.indexOf(']') != tokString.length() - 1)
                {
                    return new ParseResult.Fail(
                            "Invalid " + getTokenName() + " must end with ']' if it contains a PREREQ tag");
                }
                domainKey = tokString.substring(0, openBracketLoc);
                String prereqString = tokString.substring(openBracketLoc + 1, tokString.length() - 1);
                if (prereqString.isEmpty())
                {
                    return new ParseResult.Fail(getTokenName() + " cannot have empty prerequisite : " + value);
                }
                prereq = getPrerequisite(prereqString);
                if (prereq == null)
                {
                    return new ParseResult.Fail(getTokenName() + " had invalid prerequisite : " + prereqString);
                }
            }
            AssociatedPrereqObject apo = context.getListContext().addToList(getTokenName(), level,
                    PCClass.ALLOWED_DOMAINS, context.getReferenceContext().getCDOMReference(DOMAIN_CLASS, domainKey));
            if (prereq != null)
            {
                apo.addPrerequisite(prereq);
            }
        }

        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCClassLevel level)
    {
        AssociatedChanges<CDOMReference<Domain>> changes =
                context.getListContext().getChangesInList(getTokenName(), level, PCClass.ALLOWED_DOMAINS);
        Collection<CDOMReference<Domain>> removedItems = changes.getRemoved();
        if (removedItems != null && !removedItems.isEmpty() || changes.includesGlobalClear())
        {
            context.addWriteMessage(getTokenName() + " does not support .CLEAR");
            return null;
        }
        MapToList<CDOMReference<Domain>, AssociatedPrereqObject> mtl = changes.getAddedAssociations();
        if (mtl == null || mtl.isEmpty())
        {
            return null;
        }
        PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
        Set<String> set = new TreeSet<>();
        for (CDOMReference<Domain> domain : mtl.getKeySet())
        {
            for (AssociatedPrereqObject assoc : mtl.getListFor(domain))
            {
                StringBuilder sb = new StringBuilder(domain.getLSTformat(false));
                List<Prerequisite> prereqs = assoc.getPrerequisiteList();
                Prerequisite prereq;
                if (prereqs == null || prereqs.isEmpty())
                {
                    prereq = null;
                } else if (prereqs.size() == 1)
                {
                    prereq = prereqs.get(0);
                } else
                {
                    context.addWriteMessage("Added Domain from " + getTokenName() + " had more than one "
                            + "Prerequisite: " + prereqs.size());
                    return null;
                }
                if (prereq != null)
                {
                    sb.append('[');
                    StringWriter swriter = new StringWriter();
                    try
                    {
                        prereqWriter.write(swriter, prereq);
                    } catch (PersistenceLayerException e)
                    {
                        context.addWriteMessage("Error writing Prerequisite: " + e);
                        return null;
                    }
                    sb.append(swriter.toString());
                    sb.append(']');
                }
                set.add(sb.toString());
            }
        }
        return new String[]{StringUtil.join(set, Constants.DOT)};
    }

    @Override
    public Class<PCClassLevel> getTokenClass()
    {
        return PCClassLevel.class;
    }
}
