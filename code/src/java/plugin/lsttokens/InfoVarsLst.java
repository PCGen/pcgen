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

import java.util.Set;
import java.util.TreeSet;

import pcgen.base.lang.CaseInsensitiveString;
import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.formula.scope.PCGenScope;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.MapChanges;
import pcgen.rules.context.VariableContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class InfoVarsLst extends AbstractTokenWithSeparator<CDOMObject> implements CDOMPrimaryToken<CDOMObject>
{
    @Override
    public String getTokenName()
    {
        return "INFOVARS";
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
                    getTokenName() + " expecting '|', format is: InfoName|Info value was: " + value);
        }
        String key = value.substring(0, pipeLoc);
        //key length 0 caught by charAt(0) test above
        String[] val = value.substring(pipeLoc + 1).split("\\|");
        VariableContext varContext = context.getVariableContext();
        for (String name : val)
        {
            PCGenScope scope = context.getActiveScope();
            if (!varContext.isLegalVariableID(scope, name))
            {
                return new ParseResult.Fail(
                        getTokenName() + " found an error. " + name + " is not a legal variable name in scope "
                                + scope.getName() + " in " + cdo.getClass().getSimpleName() + ' ' + cdo.getKeyName());
            }
        }
        CaseInsensitiveString cis = new CaseInsensitiveString(key);
        context.getObjectContext().put(cdo, MapKey.INFOVARS, cis, val);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject cdo)
    {
        MapChanges<CaseInsensitiveString, String[]> changes =
                context.getObjectContext().getMapChanges(cdo, MapKey.INFOVARS);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        Set<String> set = new TreeSet<>();
        for (CaseInsensitiveString key : changes.getAdded().keySet())
        {
            String[] value = changes.getAdded().get(key);
            set.add(key + Constants.PIPE
                    + StringUtil.join(value, Constants.PIPE));
        }
        return set.toArray(new String[0]);
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }

}
