/*
 * Copyright 2015 (C) Thomas Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens;

import java.text.MessageFormat;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.lang.CaseInsensitiveString;
import pcgen.base.text.MessageFormatUtilities;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.MapKey;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.MapChanges;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class InfoLst extends AbstractNonEmptyToken<CDOMObject>
        implements CDOMPrimaryToken<CDOMObject>, DeferredToken<CDOMObject>
{
    @Override
    public String getTokenName()
    {
        return "INFO";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, CDOMObject cdo, String value)
    {
        if (value.charAt(0) == '|')
        {
            return new ParseResult.Fail(getTokenName() + " arguments may not start with PIPE : " + value);
        }
        if (value.charAt(value.length() - 1) == '|')
        {
            return new ParseResult.Fail(getTokenName() + " arguments may not end with PIPE : " + value);
        }

        int pipeLoc = value.indexOf(Constants.PIPE);
        if (pipeLoc == -1)
        {
            return new ParseResult.Fail(
                    getTokenName() + " expecting '|', format is: InfoName|Info value was: " + value);
        }
        String key = value.substring(0, pipeLoc);
        //key length 0 caught by charAt(0) test above
        String val = value.substring(pipeLoc + 1);
        if (val.isEmpty())
        {
            return new ParseResult.Fail(
                    getTokenName() + " expecting non-empty value, " + "format is: InfoName|Info value was: " + value);
        }
        if (val.startsWith(Constants.PIPE))
        {
            return new ParseResult.Fail(
                    getTokenName() + " expecting non-empty value, " + "format is: InfoName|Info value was: " + value);
        }
        try
        {
            MessageFormat mf = new MessageFormat(val);
            CaseInsensitiveString cis = new CaseInsensitiveString(key);
            context.getObjectContext().put(cdo, MapKey.INFO, cis, mf);
        } catch (IllegalArgumentException e)
        {
            return new ParseResult.Fail(getTokenName() + " expected a valid MessageFormat, but received error: "
                    + e.getMessage() + " when parsing: " + value);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject cdo)
    {
        MapChanges<CaseInsensitiveString, MessageFormat> changes =
                context.getObjectContext().getMapChanges(cdo, MapKey.INFO);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        Set<String> set = new TreeSet<>();
        for (CaseInsensitiveString key : changes.getAdded().keySet())
        {
            MessageFormat value = changes.getAdded().get(key);
            set.add(key + Constants.PIPE + value.toPattern());
        }
        return set.toArray(new String[0]);
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }

    @Override
    public boolean process(LoadContext context, CDOMObject obj)
    {
        boolean returnValue = true;
        MapKey<CaseInsensitiveString, MessageFormat> infoKey = MapKey.INFO;
        MapKey<CaseInsensitiveString, String[]> infoVarKey = MapKey.INFOVARS;
        Set<CaseInsensitiveString> infoKeys = obj.getKeysFor(infoKey);
        Set<CaseInsensitiveString> infoVarKeys = obj.getKeysFor(infoVarKey);
        //Check if INFO needed INFOVARS
        for (CaseInsensitiveString s : infoKeys)
        {
            MessageFormat mf = obj.get(infoKey, s);
            int required = MessageFormatUtilities.getRequriedArgumentCount(mf);
            if (required > 0)
            {
                String[] vars = obj.get(infoVarKey, s);
                if (vars == null)
                {
                    Logging.errorPrint(obj.getClass().getSimpleName() + ' ' + obj.getKeyName()
                            + " was loaded with INFO: " + s + " that requires " + required + " arguments, but no arguments"
                            + " in INFOVARS were provided", context);
                    returnValue = false;
                } else if (vars.length != required)
                {
                    Logging.errorPrint(obj.getClass().getSimpleName() + ' ' + obj.getKeyName()
                            + " was loaded with INFO: " + s + " that requires " + required + " arguments, but "
                            + vars.length + " arguments in INFOVARS were provided", context);
                    returnValue = false;
                }
            }
        }
        //Check if "Extra" INFOVARS were provided
        for (CaseInsensitiveString s : infoVarKeys)
        {
            if (!infoKeys.contains(s))
            {
                Logging.errorPrint(obj.getClass().getSimpleName() + ' ' + obj.getKeyName()
                        + " was loaded with INFOVARS: " + s + " but no matching INFO was provided", context);
                returnValue = false;
            }
        }
        return returnValue;
    }

    @Override
    public Class<CDOMObject> getDeferredTokenClass()
    {
        return CDOMObject.class;
    }
}
