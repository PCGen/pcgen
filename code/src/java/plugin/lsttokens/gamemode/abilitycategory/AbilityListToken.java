/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2008 (C) James Dempsey
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

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * The Class {@code AbilityListToken} is responsible for parsing the
 * ABILITYLIST token. This allows the specific named abilities to be included in
 * a 'child' ability category. The list may also specify ability subsets, e.g.
 * Weapon Focus(Sap) to be included.
 * <p>
 * Note: This tag is additive with the TYPE tag and may be used instead of or in
 * addition to the TYPE tag. The abilities included in the category will be the
 * sum of the sets defined by the two tags.
 * <p>
 */
public class AbilityListToken extends AbstractTokenWithSeparator<AbilityCategory>
        implements CDOMPrimaryToken<AbilityCategory>
{
    @Override
    public String getTokenName()
    {
        return "ABILITYLIST";
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, AbilityCategory ac, String value)
    {
        StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
        while (st.hasMoreTokens())
        {
            ac.addAbilityKey(context.getReferenceContext().getManufacturerId(ac).getReference(st.nextToken()));
        }
        return ParseResult.SUCCESS;
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    public String[] unparse(LoadContext context, AbilityCategory ac)
    {
        Collection<CDOMSingleRef<Ability>> abilities = ac.getAbilityRefs();
        if (abilities.isEmpty())
        {
            return null;
        }
        return new String[]{ReferenceUtilities.joinLstFormat(abilities, Constants.PIPE)};
    }

    @Override
    public Class<AbilityCategory> getTokenClass()
    {
        return AbilityCategory.class;
    }

}
