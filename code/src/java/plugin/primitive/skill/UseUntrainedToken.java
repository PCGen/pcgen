/*
 * Copyright 2014 (C) Thomas Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.primitive.skill;

import java.util.Collection;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Converter;
import pcgen.cdom.base.PrimitiveFilter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

/**
 * UseUntrainedToken is a Primitive that represents Whether a Skill can be used untrained.
 */
public class UseUntrainedToken implements PrimitiveToken<Skill>, PrimitiveFilter<Skill>
{
    private static final Class<Skill> SKILL_CLASS = Skill.class;
    private CDOMReference<Skill> allSkills;

    @Override
    public boolean initialize(LoadContext context, Class<Skill> cl, String value, String args)
    {
        if (args != null)
        {
            return false;
        }
        if (value != null)
        {
            return false;
        }
        allSkills = context.getReferenceContext().getCDOMAllReference(SKILL_CLASS);
        return true;
    }

    @Override
    public String getTokenName()
    {
        return "USEUNTRAINED";
    }

    @Override
    public Class<Skill> getReferenceClass()
    {
        return SKILL_CLASS;
    }

    @Override
    public String getLSTformat(boolean useAny)
    {
        return getTokenName();
    }

    @Override
    public boolean allow(PlayerCharacter pc, Skill skill)
    {
        return skill.getSafe(ObjectKey.USE_UNTRAINED);
    }

    @Override
    public GroupingState getGroupingState()
    {
        return GroupingState.ANY;
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj == this) || (obj instanceof UseUntrainedToken);
    }

    @Override
    public int hashCode()
    {
        return 2378542;
    }

    @Override
    public <R> Collection<? extends R> getCollection(PlayerCharacter pc, Converter<Skill, R> c)
    {
        return c.convert(allSkills, this);
    }
}
