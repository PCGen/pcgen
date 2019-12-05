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
package plugin.lsttokens.race;

import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.MapKey;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.MapChanges;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with CR Token
 */
public class CrModToken extends AbstractNonEmptyToken<Race> implements CDOMPrimaryToken<Race>
{

    /**
     * Get the token name
     */
    @Override
    public String getTokenName()
    {
        return "CRMOD";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, Race race, String value)
    {
        int pipeLoc = value.indexOf(Constants.PIPE);
        if (pipeLoc == -1)
        {
            return new ParseResult.Fail(
                    getTokenName() + " expecting '|', format is: " + "ClassTypes|CRMod was: " + value);
        }
        if (pipeLoc != value.lastIndexOf(Constants.PIPE))
        {
            return new ParseResult.Fail(
                    getTokenName() + " expecting only one '|', " + "format is: ClassTypes|CRMod was: " + value);
        }
        String keys = value.substring(0, pipeLoc);
        if (keys.isEmpty())
        {
            return new ParseResult.Fail(
                    getTokenName() + " expecting non-empty class type, " + "format is: ClassTypes|CRMod was: " + value);
        }
        String val = value.substring(pipeLoc + 1);
        if (val.isEmpty())
        {
            return new ParseResult.Fail(
                    getTokenName() + " expecting non-empty CR mod, " + "format is: ClassTypes|CRMod was: " + value);
        }
        try
        {
            StringTokenizer aTok = new StringTokenizer(keys, Constants.DOT, false);
            while (aTok.hasMoreTokens())
            {
                context.getObjectContext().put(race, MapKey.CRMOD, aTok.nextToken(), Integer.valueOf(val));
            }
        } catch (NumberFormatException e)
        {
            return new ParseResult.Fail(
                    getTokenName() + " expecting number CR mod, " + "format is: ClassTypes|CRMod was: " + value);
        }

        return ParseResult.SUCCESS;
    }

    /**
     * Unparse the CR token
     *
     * @param context
     * @param race
     * @return String array representing the CR token
     */
    @Override
    public String[] unparse(LoadContext context, Race race)
    {
        MapChanges<String, Integer> changes = context.getObjectContext().getMapChanges(race, MapKey.CRMOD);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        Set<String> set = new TreeSet<>();
        Map<String, Integer> added = changes.getAdded();
        for (Map.Entry<String, Integer> me : added.entrySet())
        {
            set.add(me.getKey() + Constants.PIPE + me.getValue());
        }
        return set.toArray(new String[0]);
    }

    /**
     * Get the token class
     *
     * @return Token class of type Race
     */
    @Override
    public Class<Race> getTokenClass()
    {
        return Race.class;
    }
}
