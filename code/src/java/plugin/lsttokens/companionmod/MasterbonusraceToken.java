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
package plugin.lsttokens.companionmod;

import java.util.Collection;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Race;
import pcgen.core.character.CompanionMod;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with MASTERBONUSRACE Token
 */
public class MasterbonusraceToken extends AbstractTokenWithSeparator<CompanionMod>
        implements CDOMPrimaryToken<CompanionMod>
{
    public static final Class<Race> RACE_CLASS = Race.class;

    @Override
    public String getTokenName()
    {
        return "MASTERBONUSRACE";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, CompanionMod cMod, String value)
    {
        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

        while (tok.hasMoreTokens())
        {
            String token = tok.nextToken();

            CDOMSingleRef<Race> ref = context.getReferenceContext().getCDOMReference(RACE_CLASS, token);
            context.getObjectContext().addToList(cMod, ListKey.APPLIED_RACE, ref);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CompanionMod cMod)
    {
        Changes<CDOMSingleRef<Race>> changes = context.getObjectContext().getListChanges(cMod, ListKey.APPLIED_RACE);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        SortedSet<String> set = new TreeSet<>();
        Collection<CDOMSingleRef<Race>> added = changes.getAdded();

        for (CDOMSingleRef<Race> ref : added)
        {
            set.add(ref.getLSTformat(false));
        }
        return new String[]{StringUtil.join(set, Constants.PIPE)};
    }

    @Override
    public Class<CompanionMod> getTokenClass()
    {
        return CompanionMod.class;
    }

}
