/*
 * Copyright 2009 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.primitive.deity;

import java.util.Collection;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Converter;
import pcgen.cdom.base.PrimitiveFilter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Deity;
import pcgen.core.PCAlignment;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

/**
 * AlignToken is a Primitive that represents the Alignment of a Deity.
 */
public class AlignToken implements PrimitiveToken<Deity>, PrimitiveFilter<Deity>
{

    private static final Class<PCAlignment> ALIGNMENT_CLASS = PCAlignment.class;
    private static final Class<Deity> DEITY_CLASS = Deity.class;
    private CDOMSingleRef<PCAlignment> alignment;
    private CDOMReference<Deity> allDeities;

    @Override
    public boolean initialize(LoadContext context, Class<Deity> cl, String value, String args)
    {
        if (args != null)
        {
            return false;
        }
        alignment = context.getReferenceContext().getCDOMReference(ALIGNMENT_CLASS, value);
        allDeities = context.getReferenceContext().getCDOMAllReference(DEITY_CLASS);
        return alignment != null;
    }

    @Override
    public String getTokenName()
    {
        return "ALIGN";
    }

    @Override
    public Class<Deity> getReferenceClass()
    {
        return DEITY_CLASS;
    }

    @Override
    public String getLSTformat(boolean useAny)
    {
        return getTokenName() + '=' + alignment.getLSTformat(false);
    }

    @Override
    public boolean allow(PlayerCharacter pc, Deity deity)
    {
        CDOMSingleRef<PCAlignment> alignRef = deity.get(ObjectKey.ALIGNMENT);
        return (alignRef != null) && alignment.get().equals(alignRef.get());
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
        if (obj instanceof AlignToken)
        {
            AlignToken other = (AlignToken) obj;
            return alignment.equals(other.alignment);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return alignment == null ? -5 : alignment.hashCode();
    }

    @Override
    public <R> Collection<? extends R> getCollection(PlayerCharacter pc, Converter<Deity, R> c)
    {
        return c.convert(allDeities, this);
    }

}
