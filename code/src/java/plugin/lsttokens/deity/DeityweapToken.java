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
package plugin.lsttokens.deity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Deity;
import pcgen.core.WeaponProf;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with DEITYWEAP Token
 */
public class DeityweapToken extends AbstractTokenWithSeparator<Deity> implements CDOMPrimaryToken<Deity>
{

    private static final Class<WeaponProf> WEAPONPROF_CLASS = WeaponProf.class;

    @Override
    public String getTokenName()
    {
        return "DEITYWEAP";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, Deity deity, String value)
    {
        boolean first = true;
        boolean foundAny = false;
        boolean foundOther = false;

        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

        while (tok.hasMoreTokens())
        {
            String token = tok.nextToken();
            CDOMReference<WeaponProf> ref;
            if (Constants.LST_DOT_CLEAR.equals(token))
            {
                if (!first)
                {
                    return new ParseResult.Fail(
                            "  Non-sensical " + getTokenName() + ": .CLEAR was not the first list item");
                }
                context.getObjectContext().removeList(deity, ListKey.DEITYWEAPON);
            } else
            {
                if (Constants.LST_ALL.equalsIgnoreCase(token) || Constants.LST_ANY.equalsIgnoreCase(token))
                {
                    foundAny = true;
                    ref = context.getReferenceContext().getCDOMAllReference(WEAPONPROF_CLASS);
                } else
                {
                    foundOther = true;
                    ref = context.getReferenceContext().getCDOMReference(WEAPONPROF_CLASS, token);
                }
                context.getObjectContext().addToList(deity, ListKey.DEITYWEAPON, ref);
            }
            first = false;
        }
        if (foundAny && foundOther)
        {
            return new ParseResult.Fail(
                    "Non-sensical " + getTokenName() + ": Contains ANY and a specific reference: " + value);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Deity deity)
    {
        Changes<CDOMReference<WeaponProf>> changes =
                context.getObjectContext().getListChanges(deity, ListKey.DEITYWEAPON);
        Collection<CDOMReference<WeaponProf>> removedItems = changes.getRemoved();
        List<String> list = new ArrayList<>();
        if (removedItems != null && !removedItems.isEmpty())
        {
            context.addWriteMessage(Constants.LST_DOT_CLEAR_DOT + " not supported in " + getTokenName());
            return null;
        }
        if (changes.includesGlobalClear())
        {
            list.add(Constants.LST_DOT_CLEAR);
        }
        Collection<CDOMReference<WeaponProf>> added = changes.getAdded();
        if (added != null && !added.isEmpty())
        {
            list.add(ReferenceUtilities.joinLstFormat(added, Constants.PIPE, true));
        }
        if (list.isEmpty())
        {
            return null;
        }
        return list.toArray(new String[0]);
    }

    @Override
    public Class<Deity> getTokenClass()
    {
        return Deity.class;
    }
}
