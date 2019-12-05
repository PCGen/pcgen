/*
 * Copyright 2007 (C) James Dempsey <jdemspey@users.sourceforge.net>
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

import pcgen.cdom.enumeration.DisplayLocation;
import pcgen.core.AbilityCategory;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.system.LanguageBundle;

public class DisplayLocationToken extends AbstractNonEmptyToken<AbilityCategory>
        implements CDOMPrimaryToken<AbilityCategory>
{

    @Override
    public String getTokenName()
    {
        return "DISPLAYLOCATION";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, AbilityCategory ac, String value)
    {
        /*
         * TODO This i18n is a bit challenging - in that it can create
         * incompatible datasets between those that are i18n enabled and those
         * that are not. Not sure whether in_Feat should be equivalent to Feat
         * (logic can argue either way). Need some consideration if data sets
         * should be i18n aware (AbilityCategory is currently the only object
         * that is, and I'm not sure we want the data to be i18n aware) - TP
         *
         * See also DiaplayNameToken and PluralToken (and how they are processed
         * in AbilityCategory)
         */
        String loc;
        if (value.startsWith("in_"))
        {
            loc = LanguageBundle.getString(value);
        } else
        {
            loc = value;
        }
        ac.setDisplayLocation(DisplayLocation.getConstant(loc));
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, AbilityCategory ac)
    {
        DisplayLocation location = ac.getDisplayLocation();
        if (location == null)
        {
            return null;
        }
        return new String[]{location.toString()};
    }

    @Override
    public Class<AbilityCategory> getTokenClass()
    {
        return AbilityCategory.class;
    }
}
