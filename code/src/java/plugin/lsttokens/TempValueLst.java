/*
 * Copyright 2015 (C) Thomas Parker <thpr@users.sourceforge.net>
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

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.StringKey;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * TempValueLst handles the TEMPVALUE token, which is used to select the value
 * for a TEMPBONUS when it is using %LIST
 */
public class TempValueLst extends AbstractTokenWithSeparator<CDOMObject> implements CDOMPrimaryToken<CDOMObject>
{

    @Override
    public String getTokenName()
    {
        return "TEMPVALUE";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
    {
        int pipeLoc = value.indexOf(Constants.PIPE);
        if (pipeLoc == -1)
        {
            return new ParseResult.Fail(getTokenName() + " must have three | delimited arguments : " + value);
        }
        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
        if (tok.countTokens() != 3)
        {
            return new ParseResult.Fail(getTokenName() + " requires three arguments, MIN=, MAX= and TITLE= : " + value);
        }
        if (!tok.nextToken().startsWith("MIN="))
        {
            return new ParseResult.Fail(getTokenName() + " first argument was not MIN=");
        }
        if (!tok.nextToken().startsWith("MAX="))
        {
            return new ParseResult.Fail(getTokenName() + " second argument was not MAX=");
        }
        if (!tok.nextToken().startsWith("TITLE="))
        {
            return new ParseResult.Fail(getTokenName() + " third argument was not TITLE=");
        }
        context.getObjectContext().put(obj, StringKey.TEMPVALUE, value);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject cdo)
    {
        String tv = context.getObjectContext().getString(cdo, StringKey.TEMPVALUE);
        if (tv == null)
        {
            return null;
        }
        return new String[]{tv};
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }

}
