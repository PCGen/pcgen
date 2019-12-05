/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.qualifier.equipment;

import java.util.Collection;
import java.util.logging.Level;

import pcgen.cdom.base.Converter;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.base.PrimitiveFilter;
import pcgen.cdom.converter.AddFilterConverter;
import pcgen.cdom.converter.NegateFilterConverter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.reference.SelectionCreator;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.QualifierToken;
import pcgen.util.Logging;

public class CarriedToken implements QualifierToken<Equipment>, PrimitiveFilter<Equipment>
{

    private PrimitiveCollection<Equipment> pcs = null;

    private boolean wasRestricted = false;

    private boolean negated = false;

    @Override
    public String getTokenName()
    {
        return "CARRIED";
    }

    @Override
    public Class<Equipment> getReferenceClass()
    {
        return Equipment.class;
    }

    @Override
    public String getLSTformat(boolean useAny)
    {
        StringBuilder sb = new StringBuilder();
        if (negated)
        {
            sb.append('!');
        }
        sb.append(getTokenName());
        if (wasRestricted)
        {
            sb.append('[').append(pcs.getLSTformat(useAny)).append(']');
        }
        return sb.toString();
    }

    @Override
    public boolean initialize(LoadContext context, SelectionCreator<Equipment> sc, String condition, String value,
            boolean negate)
    {
        if (condition != null)
        {
            Logging.addParseMessage(Level.SEVERE,
                    getTokenName() + " Must not be a conditional Qualifier (no equals), e.g. " + getTokenName());
            return false;
        }
        negated = negate;
        if (value == null)
        {
            pcs = sc.getAllReference();
        } else
        {
            pcs = context.getPrimitiveChoiceFilter(sc, value);
            wasRestricted = true;
        }
        return pcs != null;
    }

    @Override
    public GroupingState getGroupingState()
    {
        GroupingState gs = pcs == null ? GroupingState.ANY : pcs.getGroupingState().reduce();
        return negated ? gs.negate() : gs;
    }

    @Override
    public int hashCode()
    {
        return pcs == null ? 0 : pcs.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof CarriedToken)
        {
            CarriedToken other = (CarriedToken) o;
            if (negated == other.negated)
            {
                if (pcs == null)
                {
                    return other.pcs == null;
                }
                return pcs.equals(other.pcs);
            }
        }
        return false;
    }

    @Override
    public <R> Collection<? extends R> getCollection(PlayerCharacter pc, Converter<Equipment, R> c)
    {
        Converter<Equipment, R> conv = new AddFilterConverter<>(c, this);
        conv = negated ? new NegateFilterConverter<>(conv) : conv;
        return pcs.getCollection(pc, conv);
    }

    @Override
    public boolean allow(PlayerCharacter pc, Equipment sk)
    {
        return pc.getEquipmentMasterList().contains(sk);
    }
}
