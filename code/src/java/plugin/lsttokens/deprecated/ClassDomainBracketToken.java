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
package plugin.lsttokens.deprecated;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.core.QualifiedObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * Class deals with DOMAIN Token
 */
public class ClassDomainBracketToken extends AbstractTokenWithSeparator<PCClass>
        implements CDOMCompatibilityToken<PCClass>, DeprecatedToken
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
        Logging.deprecationPrint(getMessage(pcc, value));
        StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);

        boolean first = true;
        while (pipeTok.hasMoreTokens())
        {
            String tok = pipeTok.nextToken();
            if (Constants.LST_DOT_CLEAR.equals(tok))
            {
                if (!first)
                {
                    return new ParseResult.Fail(
                            "  Non-sensical " + getTokenName() + ": .CLEAR was not the first list item");
                }
                context.getObjectContext().removeList(pcc, ListKey.DOMAIN);
                continue;
            }
            // Note: May contain PRExxx
            String domainKey;
            Prerequisite prereq = null;

            int openBracketLoc = tok.indexOf('[');
            if (openBracketLoc == -1)
            {
                if (tok.indexOf(']') != -1)
                {
                    return new ParseResult.Fail(
                            "Invalid " + getTokenName() + " must have '[' if it contains a PREREQ tag");
                }
                domainKey = tok;
            } else
            {
                if (tok.lastIndexOf(']') != tok.length() - 1)
                {
                    return new ParseResult.Fail(
                            "Invalid " + getTokenName() + " must end with ']' if it contains a PREREQ tag");
                }
                domainKey = tok.substring(0, openBracketLoc);
                String prereqString = tok.substring(openBracketLoc + 1, tok.length() - 1);
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
            CDOMSingleRef<Domain> domain = context.getReferenceContext().getCDOMReference(DOMAIN_CLASS, domainKey);

            QualifiedObject<CDOMSingleRef<Domain>> qo = new QualifiedObject<>(domain);
            if (prereq != null)
            {
                qo.addPrerequisite(prereq);
            }
            context.getObjectContext().addToList(pcc, ListKey.DOMAIN, qo);
            first = false;
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public Class<PCClass> getTokenClass()
    {
        return PCClass.class;
    }

    @Override
    public int compatibilityLevel()
    {
        return 6;
    }

    @Override
    public int compatibilitySubLevel()
    {
        return 2;
    }

    @Override
    public int compatibilityPriority()
    {
        return 0;
    }

    @Override
    public String getMessage(CDOMObject obj, String value)
    {
        return "Found deprecated DOMAIN: token on " + obj.getClass().getSimpleName() + ": " + obj.getKeyName() + " -- "
                + value;
    }
}
