/*
 * Copyright 2007, 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.formula;

import java.util.Objects;

import pcgen.base.formula.Formula;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;
import pcgen.core.SizeAdjustment;

/**
 * A FixedSizeFormula is a Formula that returns a deterministic value, used to
 * uniquely identify a SizeAdjustment. The SizeAdjustment for which this Formula
 * will return the value must be defined during construction of the
 * FixedSizeFormula.
 */
public class FixedSizeFormula implements Formula
{

    /**
     * The underlying SizeAdjustment for which this Formula will return the
     * identifying value.
     */
    private final CDOMSingleRef<SizeAdjustment> size;

    /**
     * Creates a new FixedSizeFormula for the given SizeAdjustment.
     *
     * @param sAdj The SizeAdjustment for which this Formula will return the
     *             identifying value.
     * @throws IllegalArgumentException if the given SizeAdjustment is null
     */
    public FixedSizeFormula(CDOMSingleRef<SizeAdjustment> sAdj)
    {
        Objects.requireNonNull(sAdj, "Size Adjustment for FixedSizeFormula cannot be null");
        size = sAdj;
    }

    /**
     * Returns a String representation of this FixedSizeFormula, primarily for
     * purposes of debugging. It is strongly advised that no dependency on this
     * method be created, as the return value may be changed without warning.
     */
    @Override
    public String toString()
    {
        return size.getLSTformat(false);
    }

    @Override
    public int hashCode()
    {
        return size.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof FixedSizeFormula && size.equals(((FixedSizeFormula) obj).size);
    }

    /**
     * Resolves to the identifying value of the SizeAdjustment provided during
     * construction of the FixedSizeFormula. The given PlayerCharacter and
     * source are ignored.
     *
     * @param pc     The PlayerCharacter relative to which the FixedSizeFormula
     *               should be resolved (ignored)
     * @param source The source object of the FixedSizeFormula, for purposes of
     *               resolution (ignored)
     * @return The identifying value of the SizeAdjustment this FixedSizeFormula
     * represents.
     */
    @Override
    public Integer resolve(PlayerCharacter pc, String source)
    {
        return resolveStatic();
    }

    /**
     * Resolves to the identifying value of the SizeAdjustment provided during
     * construction of the FixedSizeFormula. The given arguments are ignored as
     * no source or context is requried to resolve a FixedSizeFormula.
     *
     * @param equipment The Equipment relative to which the FixedSizeFormula should be
     *                  resolved (ignored)
     * @param primary   True if the primary head of the given Equipment should be used
     *                  for resolution, false if the secondary head should be used for
     *                  resolution (ignored)
     * @param apc       The PlayerCharacter relative to which the FixedSizeFormula
     *                  should be resolved (ignored)
     * @param source    The source object of the FixedSizeFormula, for purposes of
     *                  resolution (ignored)
     * @return The identifying value of the SizeAdjustment this FixedSizeFormula
     * represents.
     */
    @Override
    public Number resolve(Equipment equipment, boolean primary, PlayerCharacter apc, String source)
    {
        return resolveStatic();
    }

    /**
     * Returns true if this Formula is static (will always return the same
     * value). As a FixedSizeFormula will always return the same value except in
     * circumvention of a key assumption (a consistent, ordered set of
     * SizeAdjustment objects in a given GameMode), this will return true.
     *
     * @return true
     */
    @Override
    public boolean isStatic()
    {
        return true;
    }

    @Override
    public boolean isValid()
    {
        return true;
    }

    /**
     * Resolves to the identifying value of the SizeAdjustment provided during
     * construction of the FixedSizeFormula.
     *
     * @return The identifying value of the SizeAdjustment this FixedSizeFormula
     * represents.
     */
    @Override
    public Integer resolveStatic()
    {
        return size.get().get(IntegerKey.SIZEORDER);
    }
}
