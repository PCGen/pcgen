/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.ability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Ability;
import pcgen.core.Description;
import pcgen.core.prereq.Prerequisite;
import pcgen.io.EntityEncoder;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.PatternChanges;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * This class deals with the BENEFIT Token
 */
public class BenefitToken extends AbstractNonEmptyToken<Ability> implements CDOMPrimaryToken<Ability>
{

    @Override
    public String getTokenName()
    {
        return "BENEFIT";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, Ability ability, String value)
    {
        if (Constants.LST_DOT_CLEAR.equals(value))
        {
            context.getObjectContext().removeList(ability, ListKey.BENEFIT);
            return ParseResult.SUCCESS;
        }
        if (value.startsWith(Constants.LST_DOT_CLEAR_DOT))
        {
            context.getObjectContext().removePatternFromList(ability, ListKey.BENEFIT, value.substring(7));
            return ParseResult.SUCCESS;
        }

        ParseResult pr = checkNonEmpty(value);
        if (!pr.passed())
        {
            return pr;
        }
        pr = checkForIllegalSeparator('|', value);
        if (!pr.passed())
        {
            return pr;
        }
        final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

        String firstToken = tok.nextToken();
        if (PreParserFactory.isPreReqString(firstToken))
        {
            return new ParseResult.Fail("Invalid " + getTokenName() + "(PRExxx can not be only value): " + value);
        }
        String ds = EntityEncoder.decode(firstToken);
        if (!StringUtil.hasBalancedParens(ds))
        {
            return new ParseResult.Fail(getTokenName() + " encountered imbalanced Parenthesis: " + value);
        }

        Description ben = new Description(ds);

        boolean isPre = false;
        while (tok.hasMoreTokens())
        {
            final String token = tok.nextToken();
            if (PreParserFactory.isPreReqString(token))
            {
                Prerequisite prereq = getPrerequisite(token);
                if (prereq == null)
                {
                    return new ParseResult.Fail(getTokenName() + " had invalid prerequisite : " + token);
                }
                ben.addPrerequisite(prereq);
                isPre = true;
            } else
            {
                if (isPre)
                {
                    return new ParseResult.Fail(
                            "Invalid " + getTokenName() + "(PRExxx must be at the END of the Token): " + value);
                }
                ben.addVariable(token);
            }
        }

        context.getObjectContext().addToList(ability, ListKey.BENEFIT, ben);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Ability ability)
    {
        PatternChanges<Description> changes =
                context.getObjectContext().getListPatternChanges(ability, ListKey.BENEFIT);
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
        } else if (removedItems != null && !removedItems.isEmpty())
        {
            for (String d : removedItems)
            {
                list.add(Constants.LST_DOT_CLEAR_DOT + d);
            }
        }
        Collection<Description> added = changes.getAdded();
        if (added != null && !added.isEmpty())
        {
            for (Description d : added)
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
    public Class<Ability> getTokenClass()
    {
        return Ability.class;
    }

}
