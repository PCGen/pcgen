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
package plugin.lsttokens.variable;

import pcgen.base.formula.exception.LegalVariableException;
import pcgen.base.util.FormatManager;
import pcgen.cdom.content.DatasetVariable;
import pcgen.cdom.formula.scope.GlobalPCScope;
import pcgen.cdom.formula.scope.PCGenScope;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class GlobalToken extends AbstractNonEmptyToken<DatasetVariable> implements CDOMPrimaryToken<DatasetVariable>
{

    @Override
    public String getTokenName()
    {
        return "GLOBAL";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, DatasetVariable dv, String value)
    {
        //Just a name
        if (dv.getDisplayName() != null)
        {
            return new ParseResult.Fail(getTokenName() + " must be the first token on the line");
        }
        String format;
        String varName;
        int equalLoc = value.indexOf('=');
        if (equalLoc == -1)
        {
            //Defaults to NUMBER
            format = "NUMBER";
            varName = value;
        } else
        {
            format = value.substring(0, equalLoc);
            varName = value.substring(equalLoc + 1);
        }
        FormatManager<?> formatManager;
        try
        {
            formatManager = context.getReferenceContext().getFormatManager(format);
        } catch (NullPointerException | IllegalArgumentException e)
        {
            return new ParseResult.Fail(getTokenName() + " does not support format " + format + ", found in " + value
                    + " due to " + e.getMessage());
        }
        PCGenScope scope = context.getActiveScope();

        if (!DatasetVariable.isLegalName(varName))
        {
            return new ParseResult.Fail(varName + " is not a valid variable name");
        }

        try
        {
            context.getVariableContext().assertLegalVariableID(varName, scope, formatManager);
        } catch (LegalVariableException e)
        {
            return new ParseResult.Fail(
                    getTokenName() + " encountered an exception in varible definition : " + e.getMessage());
        }
        dv.setName(varName);
        dv.setFormat(formatManager);
        dv.setScope(scope);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, DatasetVariable dv)
    {
        PCGenScope scope = dv.getScope();
        if (scope != null && !scope.getName().equals(GlobalPCScope.GLOBAL_SCOPE_NAME))
        {
            //is a local variable
            return null;
        }
        FormatManager<?> format = dv.getFormat();
        if (format == null)
        {
            //Not a valid object
            return null;
        }
        String varName = dv.getKeyName();
        if (!DatasetVariable.isLegalName(varName))
        {
            //internal variable
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String identifier = format.getIdentifierType();
        if (!"NUMBER".equals(identifier))
        {
            sb.append(format.getIdentifierType());
            sb.append('=');
        }
        sb.append(varName);
        return new String[]{sb.toString()};
    }

    @Override
    public Class<DatasetVariable> getTokenClass()
    {
        return DatasetVariable.class;
    }

}
