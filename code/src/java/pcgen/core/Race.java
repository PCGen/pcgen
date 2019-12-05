/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
package pcgen.core;

import java.util.List;
import java.util.Optional;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.LimitedVarHolder;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;

/**
 * {@code Race}.
 */
public final class Race extends PObject implements ChooseDriver, LimitedVarHolder
{

    /**
     * Checks if this race's advancement is limited.
     *
     * @return <tt>true</tt> if this race advances unlimitedly.
     */
    public boolean isAdvancementUnlimited()
    {
        List<Integer> hda = getListFor(ListKey.HITDICE_ADVANCEMENT);
        return hda == null || hda.get(hda.size() - 1) == Integer.MAX_VALUE;
    }

    /**
     * Overridden to only consider the race's name.
     *
     * @return hash code
     */
    @Override
    public int hashCode()
    {
        return getKeyName().hashCode();
    }

    public int maxHitDiceAdvancement()
    {
        List<Integer> hda = getListFor(ListKey.HITDICE_ADVANCEMENT);
        return hda == null ? 0 : hda.get(hda.size() - 1);
    }

    @Override
    public ChooseInformation<?> getChooseInfo()
    {
        return get(ObjectKey.CHOOSE_INFO);
    }

    @Override
    public Formula getSelectFormula()
    {
        return getSafe(FormulaKey.SELECT);
    }

    @Override
    public List<ChooseSelectionActor<?>> getActors()
    {
        return getListFor(ListKey.NEW_CHOOSE_ACTOR);
    }

    @Override
    public String getFormulaSource()
    {
        return getKeyName();
    }

    @Override
    public Formula getNumChoices()
    {
        return getSafe(FormulaKey.NUMCHOICES);
    }

    @Override
    public String getIdentifier()
    {
        return "RACE";
    }

    @Override
    public Optional<String> getLocalScopeName()
    {
        return Optional.of("PC.RACE");
    }

}
