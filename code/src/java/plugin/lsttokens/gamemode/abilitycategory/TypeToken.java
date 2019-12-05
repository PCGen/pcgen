/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.gamemode.abilitycategory;

import java.util.Set;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.Type;
import pcgen.core.AbilityCategory;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class TypeToken extends AbstractNonEmptyToken<AbilityCategory>
        implements CDOMPrimaryToken<AbilityCategory>, DeferredToken<AbilityCategory>
{

    @Override
    public String getTokenName()
    {
        return "TYPE";
    }

    @Override
    public ParseResult parseNonEmptyToken(LoadContext context, AbilityCategory ac, String value)
    {
        if ("*".equals(value))
        {
            ac.setAllAbilityTypes(true);
            return ParseResult.SUCCESS;
        }

        ParseResult pr = checkForIllegalSeparator('.', value);
        if (!pr.passed())
        {
            return pr;
        }

        StringTokenizer st = new StringTokenizer(value, Constants.DOT);
        while (st.hasMoreTokens())
        {
            String typeString = st.nextToken();
            if ("*".equals(typeString))
            {
                return new ParseResult.Fail("Use of named types along with TYPE:* in category " + ac.getDisplayName()
                        + " is invalid.  Found: " + value);
            } else
            {
                ac.addAbilityType(Type.getConstant(typeString));
            }
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, AbilityCategory ac)
    {
        if (ac.isAllAbilityTypes())
        {
            return new String[]{"*"};
        }
        Set<Type> types = ac.getTypes();
        if (types.isEmpty())
        {
            return null;
        }
        return new String[]{StringUtil.join(types, Constants.DOT)};
    }

    @Override
    public Class<AbilityCategory> getTokenClass()
    {
        return AbilityCategory.class;
    }

    @Override
    public Class<AbilityCategory> getDeferredTokenClass()
    {
        return AbilityCategory.class;
    }

    @Override
    public boolean process(LoadContext context, AbilityCategory ac)
    {
        if (ac.isAllAbilityTypes())
        {
            if (!ac.getTypes().isEmpty())
            {
                Logging.log(Logging.LST_ERROR,
                        "Use of named types along with TYPE:* in category " + ac.getDisplayName() + " is invalid.");
                return false;
            }
            if (ac.hasDirectReferences())
            {
                Logging.log(Logging.LST_ERROR,
                        "Use of ABILITYLIST along with TYPE:* in category " + ac.getDisplayName() + " is invalid.");
                return false;
            }
        }
        return true;
    }
}
