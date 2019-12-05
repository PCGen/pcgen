/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.base;

import pcgen.base.formula.Formula;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;

/**
 * JEPFormula is a variable-value Formula designed to be run through the JEP
 * formula evaluation system.
 */
public class JEPFormula implements Formula
{

    /**
     * The value of this JEPFormula
     */
    private final String formula;

    /**
     * Creates a new JEPFormula from the given String.
     *
     * @param formulaString The String value of this JEPFormula.
     */
    JEPFormula(String formulaString)
    {
        formula = formulaString;
    }

    /**
     * Returns a String representation of this JEPFormula.
     */
    @Override
    public String toString()
    {
        return formula;
    }

    @Override
    public int hashCode()
    {
        return formula.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof JEPFormula && ((JEPFormula) obj).formula.equals(formula);
    }

    /**
     * Resolves this JEPFormula, returning the value of this JEPFormula in the
     * context of the given PlayerCharacter and source.
     *
     * @param character The PlayerCharacter relative to which the JEPFormula should be
     *                  resolved.
     * @param source    The source object of the JEPFormula, for purposes of
     *                  resolution.
     * @return The value of this JEPFormula in the context of the given
     * PlayerCharacter and source.
     * @throws NullPointerException if the given PlayerCharacter is null
     */
    @Override
    public Float resolve(PlayerCharacter character, String source)
    {
        return character.getVariableValue(formula, source);
    }

    /**
     * Returns true if this Formula is static (will always return the same
     * value). As a JEPFormula will likely return different values except in
     * rare cases, this will return false.
     *
     * @return false
     */
    @Override
    public boolean isStatic()
    {
        return false;
    }

    /**
     * Resolves this JEPFormula, returning the value of this JEPFormula in
     * context to the given Equipment, PlayerCharacter, and Source identifier.
     *
     * @param equipment The Equipment relative to which the JEPFormula should be
     *                  resolved.
     * @param primary   True if the primary head of the given Equipment should be used
     *                  for resolution, false if the secondary head should be used for
     *                  resolution.
     * @param pc        The PlayerCharacter relative to which the JEPFormula should be
     *                  resolved.
     * @param source    The source object of the JEPFormula, for purposes of
     *                  resolution.
     * @return The value of this JEPFormula in the context of the given
     * Equipment, PlayerCharacter, and Source identifier.
     */
    @Override
    public Number resolve(Equipment equipment, boolean primary, PlayerCharacter pc, String source)
    {
        return equipment.getVariableValue(formula, source, primary, pc);
    }

    @Override
    public boolean isValid()
    {
        return true;
    }

    @Override
    public Number resolveStatic()
    {
        throw new UnsupportedOperationException("Formula is not static");
    }
}
