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
package plugin.primitive.weaponprof;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Converter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Deity;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

/**
 * DeityWeaponToken is a Primitive that allows pulling the WeaponProf objects provided by
 * a Deity.
 */
public class DeityWeaponToken implements PrimitiveToken<WeaponProf>
{

    private static final Class<WeaponProf> WEAPONPROF_CLASS = WeaponProf.class;

    @Override
    public boolean initialize(LoadContext context, Class<WeaponProf> cl, String value, String args)
    {
        return (value == null) && (args == null);
    }

    @Override
    public String getTokenName()
    {
        return "DEITYWEAPON";
    }

    @Override
    public Class<WeaponProf> getReferenceClass()
    {
        return WEAPONPROF_CLASS;
    }

    @Override
    public String getLSTformat(boolean useAny)
    {
        return "DEITYWEAPON";
    }

    @Override
    public GroupingState getGroupingState()
    {
        return GroupingState.ANY;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof DeityWeaponToken;
    }

    @Override
    public int hashCode()
    {
        return 5783;
    }

    @Override
    public <R> Collection<R> getCollection(PlayerCharacter pc, Converter<WeaponProf, R> c)
    {
        Deity deity = pc.getDisplay().getDeity();
        if (deity == null)
        {
            return Collections.emptySet();
        }
        HashSet<R> set = new HashSet<>();
        List<CDOMReference<WeaponProf>> dwp = deity.getSafeListFor(ListKey.DEITYWEAPON);
        for (CDOMReference<WeaponProf> ref : dwp)
        {
            set.addAll(c.convert(ref));
        }
        return set;
    }
}
