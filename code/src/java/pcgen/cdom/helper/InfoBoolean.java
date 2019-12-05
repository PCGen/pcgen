/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.helper;

import java.util.Objects;

import pcgen.base.formula.inst.NEPFormula;

/**
 * An InfoBoolean is a class designed to hold both a name and a Boolean formula. The name
 * is intended to match the name of an INFO token.
 */
public class InfoBoolean
{
    /**
     * The INFO name associated with this InfoBoolean.
     */
    private final String infoName;

    /**
     * The Boolean Formula for this InfoBoolean.
     */
    private final NEPFormula<Boolean> formula;

    /**
     * Constructs a new InfoBoolean with the given name and formula.
     *
     * @param infoName The INFO name associated with this InfoBoolean
     * @param formula  The Boolean Formula for this InfoBoolean
     */
    public InfoBoolean(String infoName, NEPFormula<Boolean> formula)
    {
        this.infoName = Objects.requireNonNull(infoName);
        this.formula = Objects.requireNonNull(formula);
    }

    /**
     * Returns the INFO name associated with this InfoBoolean.
     *
     * @return The INFO name associated with this InfoBoolean
     */
    public String getInfoName()
    {
        return infoName;
    }

    /**
     * Returns the Boolean Formula for this InfoBoolean.
     *
     * @return The Boolean Formula for this InfoBoolean
     */
    public NEPFormula<Boolean> getFormula()
    {
        return formula;
    }
}
