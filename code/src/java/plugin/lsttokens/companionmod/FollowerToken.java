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

import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCClass;
import pcgen.core.character.CompanionMod;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.MapChanges;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with FOLLOWER Token
 */
public class FollowerToken extends AbstractTokenWithSeparator<CompanionMod> implements CDOMPrimaryToken<CompanionMod>
{

    private static final Class<PCClass> PCCLASS_CLASS = PCClass.class;

    @Override
    public String getTokenName()
    {
        return "FOLLOWER";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, CompanionMod cMod, String value)
    {
        int equalLoc = value.indexOf('=');
        if (equalLoc == -1)
        {
            return new ParseResult.Fail("No = in token.");
        }
        if (equalLoc != value.lastIndexOf('='))
        {
            return new ParseResult.Fail("Too many = in token.");
        }
        String classString = value.substring(0, equalLoc);
        String levelString = value.substring(equalLoc + 1);
        Integer lvl = Integer.valueOf(levelString);
        context.getObjectContext().put(cMod, IntegerKey.LEVEL, lvl);

        final StringTokenizer bTok = new StringTokenizer(classString, ",");

        while (bTok.hasMoreTokens())
        {
            String classKey = bTok.nextToken();
            PCClass pcClass = context.getReferenceContext().silentlyGetConstructedCDOMObject(PCCLASS_CLASS, classKey);

            if (pcClass != null)
            {
                CDOMSingleRef<PCClass> pcc = context.getReferenceContext().getCDOMReference(PCCLASS_CLASS, classKey);
                context.getObjectContext().put(cMod, MapKey.APPLIED_CLASS, pcc, lvl);
            } else
            {
                // Now we accept VARiable names here.
                context.getObjectContext().put(cMod, MapKey.APPLIED_VARIABLE, classKey, lvl);
            }
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CompanionMod cMod)
    {
        MapChanges<CDOMSingleRef<? extends PCClass>, Integer> changes =
                context.getObjectContext().getMapChanges(cMod, MapKey.APPLIED_CLASS);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        SortedSet<String> set = new TreeSet<>();
        Map<CDOMSingleRef<? extends PCClass>, Integer> map = changes.getAdded();
        for (Map.Entry<CDOMSingleRef<? extends PCClass>, Integer> me : map.entrySet())
        {
            CDOMSingleRef<? extends PCClass> ref = me.getKey();
            String prefix = ref.getPersistentFormat();
            if (prefix.startsWith("SUBCLASS="))
            {
                set.add(prefix.substring(9) + Constants.DOT + ref.getLSTformat(false) + '=' + me.getValue());
            } else
            {
                set.add(ref.getLSTformat(false) + '=' + me.getValue());
            }
        }
        return new String[]{StringUtil.join(set, Constants.PIPE)};
    }

    @Override
    public Class<CompanionMod> getTokenClass()
    {
        return CompanionMod.class;
    }

}
