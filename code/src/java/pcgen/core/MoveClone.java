/*
 * Copyright 2019 (C) Tom Parker <thpr@sourceforge.net>
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
package pcgen.core;

import java.util.Objects;
import java.util.function.Function;

import pcgen.cdom.enumeration.MovementType;

/**
 * MoveClone represents a MOVECLONE: token from the data.
 */
public class MoveClone
{

    /**
     * Contains the base movement type for this MoveClone (e.g. "Walk").
     */
    private final MovementType baseMovementType;

    /**
     * Contains the target MovementType of the clone of movement for this MoveClone (e.g.
     * "Fly").
     */
    private final MovementType cloneMovementType;

    /**
     * The Function that performs the conversion from the base MovementType to the cloned
     * MovementType.
     */
    private final Function<Double, Double> conversion;

    /**
     * A String representation of the Function, so that it can be restored to LST.
     */
    private final String formulaString;

    /**
     * Creates a MoveClone with the given parameters.
     *
     * @param baseMovementType  The base MovementType for this MoveClone (e.g. "Walk").
     * @param cloneMovementType The target MovementType of the clone of movement for this MoveClone
     *                          (e.g. "Fly").
     * @param conversion        The Function that performs the conversion from the base MovementType to
     *                          the cloned MovementType.
     * @param formulaString     A String representation of the Function, so that it can be restored to
     *                          LST.
     */
    public MoveClone(MovementType baseMovementType,
            MovementType cloneMovementType, Function<Double, Double> conversion,
            String formulaString)
    {
        this.baseMovementType = Objects.requireNonNull(baseMovementType);
        this.cloneMovementType = Objects.requireNonNull(cloneMovementType);
        this.conversion = Objects.requireNonNull(conversion);
        this.formulaString = Objects.requireNonNull(formulaString);
    }

    /**
     * Returns the base movement type for this MoveClone (e.g. "Walk").
     *
     * @return The base movement type for this MoveClone (e.g. "Walk")
     */
    public MovementType getBaseType()
    {
        return baseMovementType;
    }

    /**
     * Returns the target MovementType of the clone of movement for this MoveClone (e.g.
     * "Fly").
     *
     * @return The target MovementType of the clone of movement for this MoveClone (e.g.
     * "Fly")
     */
    public MovementType getCloneType()
    {
        return cloneMovementType;
    }

    /**
     * Returns a String representation of the Function, so that it can be restored to LST.
     *
     * @return A String representation of the Function, so that it can be restored to LST
     */
    public String getFormulaString()
    {
        return formulaString;
    }

    /**
     * Applies the conversion Function in this MoveClone to the given baseMove value.
     *
     * @param baseMove The baseMove value that should be converted by the conversion function
     *                 in this MoveClone
     * @return The converted value of movement to be used as the movement of the clone
     * MovementType in this MoveClone
     */
    public double apply(Double baseMove)
    {
        return conversion.apply(baseMove);
    }
}
