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

import java.util.Collection;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCClass;
import pcgen.core.SpellProhibitor;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.enumeration.ProhibitedSpellType;

/**
 * Class deals with PROHIBITED Token
 */
public class ProhibitedToken extends AbstractTokenWithSeparator<PCClass> implements CDOMPrimaryToken<PCClass>
{

    @Override
    public String getTokenName()
    {
        return "PROHIBITED";
    }

    @Override
    protected char separator()
    {
        return ',';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, PCClass pcc, String value)
    {
        StringTokenizer elements = new StringTokenizer(value, Constants.COMMA);
        while (elements.hasMoreTokens())
        {
            String aValue = elements.nextToken();
            if (!aValue.equalsIgnoreCase("None"))
            {
                SpellProhibitor prohibSchool = new SpellProhibitor();
                prohibSchool.setType(ProhibitedSpellType.SCHOOL);
                prohibSchool.addValue(aValue);
                context.getObjectContext().addToList(pcc, ListKey.PROHIBITED_SPELLS, prohibSchool);
                SpellProhibitor prohibSubSchool = new SpellProhibitor();
                prohibSubSchool.setType(ProhibitedSpellType.SUBSCHOOL);
                prohibSubSchool.addValue(aValue);
                context.getObjectContext().addToList(pcc, ListKey.PROHIBITED_SPELLS, prohibSubSchool);
            }
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCClass pcc)
    {
        Changes<SpellProhibitor> changes = context.getObjectContext().getListChanges(pcc, ListKey.PROHIBITED_SPELLS);
        Collection<SpellProhibitor> added = changes.getAdded();
        if (added == null || added.isEmpty())
        {
            // Zero indicates no Token present
            return null;
        }
        Set<String> set = new TreeSet<>();
        for (SpellProhibitor sp : added)
        {
            set.addAll(sp.getValueList());
        }
        return new String[]{StringUtil.join(set, Constants.COMMA)};
    }

    @Override
    public Class<PCClass> getTokenClass()
    {
        return PCClass.class;
    }

}
