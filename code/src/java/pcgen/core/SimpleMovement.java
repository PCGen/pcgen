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

import pcgen.cdom.enumeration.MovementType;

/**
 * A SimpleMovement represents info from one movement of the MOVE: token in the data.
 */
public class SimpleMovement
{

    /**
     * Contains the MovementType for this SimpleMovement (e.g. "Walk", "Fly")
     */
    private final MovementType movementType;

    /**
     * Contains the associated movement rate (in feet) for the movement type. A movement
     * rate must be greater than or equal to zero.
     */
    private final int movement;

    /**
     * Creates a SimpleMovement object with the given parameters.
     *
     * @param movementType The MovementType for this MoveClone (e.g. "Walk").
     * @param movement     The movement (in feet) for the given MovementType.
     */
    public SimpleMovement(MovementType movementType, int movement)
    {
        this.movementType = Objects.requireNonNull(movementType);
        this.movement = Objects.requireNonNull(movement);
        if (movement < 0)
        {
            throw new IllegalArgumentException("Movement for type "
                    + movementType + " must be >=0: " + movement);
        }
    }

    /**
     * Return the creature's movement type in this SimpleMovement.
     *
     * @return The creature's movement type in this SimpleMovement
     */
    public MovementType getMovementType()
    {
        return movementType;
    }

    /**
     * Return the creature's movement rate (in feet) for the movement type in this
     * SimpleMovement.
     *
     * @return The creature's movement rate (in feet) for the movement type in this
     * SimpleMovement
     */
    public int getMovement()
    {
        return movement;
    }

}
