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
package plugin.lsttokens.pcclass;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.content.LevelExchange;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with EXCHANGELEVEL Token
 */
public class ExchangelevelToken extends AbstractTokenWithSeparator<PCClass> implements CDOMPrimaryToken<PCClass>
{

    @Override
    public String getTokenName()
    {
        return "EXCHANGELEVEL";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, PCClass pcc, String value)
    {
        final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
        if (tok.countTokens() != 4)
        {
            return new ParseResult.Fail(getTokenName() + " must have 4 | delimited arguments : " + value);
        }

        String classString = tok.nextToken();
        CDOMSingleRef<PCClass> cl = context.getReferenceContext().getCDOMReference(PCClass.class, classString);
        String mindlString = tok.nextToken();
        int mindl;
        try
        {
            mindl = Integer.parseInt(mindlString);
        } catch (NumberFormatException nfe)
        {
            return new ParseResult.Fail(getTokenName() + " expected an integer: " + mindlString);
        }
        String maxdlString = tok.nextToken();
        int maxdl;
        try
        {
            maxdl = Integer.parseInt(maxdlString);
        } catch (NumberFormatException nfe)
        {
            return new ParseResult.Fail(getTokenName() + " expected an integer: " + maxdlString);
        }
        String minremString = tok.nextToken();
        int minrem;
        try
        {
            minrem = Integer.parseInt(minremString);
        } catch (NumberFormatException nfe)
        {
            return new ParseResult.Fail(getTokenName() + " expected an integer: " + minremString);
        }
        try
        {
            LevelExchange le = new LevelExchange(cl, mindl, maxdl, minrem);
            context.getObjectContext().put(pcc, ObjectKey.EXCHANGE_LEVEL, le);
            return ParseResult.SUCCESS;
        } catch (IllegalArgumentException e)
        {
            ComplexParseResult pr = new ComplexParseResult();
            pr.addErrorMessage("Error in " + getTokenName() + ' ' + e.getMessage());
            pr.addErrorMessage("  Token contents: " + value);
            return pr;
        }
    }

    @Override
    public String[] unparse(LoadContext context, PCClass pcc)
    {
        LevelExchange le = context.getObjectContext().getObject(pcc, ObjectKey.EXCHANGE_LEVEL);
        if (le == null)
        {
            return null;
        }
        String sb = le.getExchangeClass().getLSTformat(false) + Constants.PIPE
                + le.getMinDonatingLevel() + Constants.PIPE
                + le.getMaxDonatedLevels() + Constants.PIPE
                + le.getDonatingLowerLevelBound();
        return new String[]{sb};
    }

    @Override
    public Class<PCClass> getTokenClass()
    {
        return PCClass.class;
    }
}
