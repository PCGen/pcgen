/*
 * Copyright 2014 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * This processes the FACT token.
 * <p>
 * Note that there are no EXPLICIT subtokens in the code. They are all
 * IMPLICITLY created by FACTDEF tokens.
 *
 * @see plugin.lsttokens.datacontrol.FactDefToken
 */
public class FactLst extends AbstractTokenWithSeparator<CDOMObject> implements CDOMPrimaryToken<CDOMObject>
{
    @Override
    public String getTokenName()
    {
        return "FACT";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject cdo, String value)
    {
        int pipeLoc = value.indexOf(Constants.PIPE);
        if (pipeLoc == -1)
        {
            return new ParseResult.Fail(
                    getTokenName() + " expecting '|', format is: " + "Fact identification|Fact value ... was: " + value);
        } else if (pipeLoc != value.lastIndexOf(Constants.PIPE))
        {
            return new ParseResult.Fail(getTokenName() + " expecting only one '|', format is: "
                    + "Fact identification|Fact value ... was: " + value);
        }
        String factID = value.substring(0, pipeLoc);
        if (factID.isEmpty())
        {
            return new ParseResult.Fail(getTokenName() + " expecting non-empty identification, "
                    + "format is: Fact identification|Fact value ... was: " + value);
        }
        String factStr = value.substring(pipeLoc + 1);
        if (factStr.isEmpty())
        {
            return new ParseResult.Fail(getTokenName() + " expecting non-empty value, "
                    + "format is: Fact identification|Fact value ... was: " + value);
        }
        return context.processSubToken(cdo, getTokenName(), factID, factStr);
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject cdo)
    {
        return context.unparseSubtoken(cdo, getTokenName());
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }

}
