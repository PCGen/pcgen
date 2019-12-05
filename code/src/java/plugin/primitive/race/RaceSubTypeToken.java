/*
 * Copyright 2010 (C) Thomas Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.primitive.race;

import java.util.Collection;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Converter;
import pcgen.cdom.base.PrimitiveFilter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

/**
 * RaceSubTypeToken is a Primitive that filters based on the RaceSubType of a Race.
 */
public class RaceSubTypeToken implements PrimitiveToken<Race>, PrimitiveFilter<Race>
{

    private static final Class<Race> RACE_CLASS = Race.class;
    private RaceSubType racetype;
    private CDOMReference<Race> allRaces;

    @Override
    public boolean initialize(LoadContext context, Class<Race> cl, String value, String args)
    {
        if (args != null)
        {
            return false;
        }
        racetype = RaceSubType.getConstant(value);
        allRaces = context.getReferenceContext().getCDOMAllReference(RACE_CLASS);
        return true;
    }

    @Override
    public String getTokenName()
    {
        return "RACESUBTYPE";
    }

    @Override
    public Class<Race> getReferenceClass()
    {
        return RACE_CLASS;
    }

    @Override
    public String getLSTformat(boolean useAny)
    {
        return getTokenName() + '=' + racetype.toString();
    }

    @Override
    public boolean allow(PlayerCharacter pc, Race race)
    {
        return race.containsInList(ListKey.RACESUBTYPE, racetype);
    }

    @Override
    public GroupingState getGroupingState()
    {
        return GroupingState.ANY;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof RaceSubTypeToken)
        {
            RaceSubTypeToken other = (RaceSubTypeToken) obj;
            return racetype.equals(other.racetype);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return racetype == null ? -11 : racetype.hashCode();
    }

    @Override
    public <R> Collection<? extends R> getCollection(PlayerCharacter pc, Converter<Race, R> c)
    {
        return c.convert(allRaces, this);
    }
}
