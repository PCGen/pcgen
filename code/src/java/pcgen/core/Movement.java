/*
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;
import java.util.StringTokenizer;

import pcgen.util.Logging;

public class Movement
{

	/**
	 * Contains the movement Types for this Movement (e.g. "Walk", "Fly")
	 */
	private final String[] movementTypes;

	/**
	 * Contains the associated movement rate (in feet) for the movement type of
	 * the same index. A movement rate must be greater than or equal to zero.
	 */
	private final double[] movements;

	/**
	 * The movement multiplier for the movement type of the same index. A
	 * movement Multiplier be greater than zero.
	 */
	private final double[] movementMult;

	/**
	 * The movement operation for the movement type of the same index. (e.g. "*"
	 * or "/")
	 */
	private final String[] movementMultOp;

	/*
	 * A class invariant is that the four above arrays should always have the
	 * same length.
	 */

	/**
	 * The Movement Rates flag indicating which type of Movement object this is
	 * 0 indicates a basic assignment
	 * 2 indicates this clones one movement rate into another movement rate
	 */
	private int moveRatesFlag;

	/**
	 * The index within the movements array indicating the default movement type
	 * ("Walk")
	 */
	private int movement;

	/**
	 * Creates a Movement object with arrays of the given length. It is assumed
	 * that the user of this constructor will initialize all of the arrays, as
	 * this constructor does not perform initialization.
	 *
	 * @param i
	 *            The length of the movement arrays to be assigned.
	 */
	public Movement(int i)
	{
		if (i <= 0)
		{
			throw new IllegalArgumentException(
				"Argument of array length to ConcreteMovement" + "constructor must be positive");
		}
		movementTypes = new String[i];
		movements = new double[i];
		movementMult = new double[i];
		movementMultOp = new String[i];

		// default the basic movement to the first movement type, if the creature has a
		// walk speed in some entry other than 0 this will be changed by the assign
		// movement operation.
		movement = 0;
	}

	/**
	 * Sets the Move Rates Flag on this Movement object.
	 *
	 * @param i
	 *            The move rates flag.
	 */
	public void setMoveRatesFlag(int i)
	{
		if (i != 0 && i != 2)
		{
			throw new IllegalArgumentException("Rate Flag must be 0 or 2");
		}
		moveRatesFlag = i;
	}

	/**
	 * Gets the Movement Rates Flag for this Movement object.
	 * @return move rates flag
	 */
	public int getMoveRatesFlag()
	{
		return moveRatesFlag;
	}

	/**
	 * Return the creature's basic movement, this will be set to the walk
	 * speed (if the creature has one) by the assign movement operation.
	 * If no walk speed is assigned to the creature then the first movement
	 * defined is returned.
	 * @return movement as a Double
	 */
	public Double getDoubleMovement()
	{
		return movements[movement];
	}

	/**
	 * Get a movement multiplier
	 * @param index of the specified movement multiplier
	 * @return a movement multiplier
	 */
	public double getMovementMult(int index)
	{
		return movementMult[index];
	}

	/**
	 * a movement multiplier operator
	 * @param index of the specified movement
	 * @return a movement multiplier operator
	 */
	public String getMovementMultOp(int index)
	{
		return movementMultOp[index];
	}

	/**
	 * Get all of the movement multipliers
	 * @return clone of the movement multipliers array
	 */
	public double[] getMovementMult()
	{
		return movementMult.clone();
	}

	/**
	 * Get all of the movement multiplier operators
	 * @return clone of the movement multiplier operators array
	 */
	public String[] getMovementMultOp()
	{
		return movementMultOp.clone();
	}

	/**
	 * Get the number of movement types
	 * @return the number of movement types
	 */
	public int getNumberOfMovementTypes()
	{
		return movementTypes.length;
	}

	/**
	 * Get the movement type from the array 
	 * @param i
	 * @return movement type
	 */
	public String getMovementType(int i)
	{
		return (i < movementTypes.length) ? movementTypes[i] : "";
	}

	/**
	 * Get the movement types
	 * @return the movement types
	 */
	public String[] getMovementTypes()
	{
		return movementTypes.clone();
	}

	/**
	 * Get the movement at index i
	 * @param i
	 * @return the movement at index i or 0
	 */
	public double getMovement(int i)
	{
		return (i < movements.length) ? movements[i] : 0.0d;
	}

	/**
	 * Get the number of movements
	 * @return number of movements
	 */
	public int getNumberOfMovements()
	{
		return movements.length;
	}

	/**
	 * Get movements
	 * @return movements
	 */
	public double[] getMovements()
	{
		return movements.clone();
	}

	/**
	 * Provides a String representation of this Movement object, suitable for
	 * display to a user.
	 * @return String
	 */
	@Override
	public String toString()
	{
		final StringBuilder movelabel = new StringBuilder();
		if (movementTypes.length > 0)
		{
			movelabel.append(movementTypes[0]);
			NumberFormat numFmt = NumberFormat.getNumberInstance();
			movelabel.append(' ')
				.append(numFmt.format(Globals.getGameModeUnitSet().convertDistanceToUnitSet(movements[0])));
			movelabel.append(Globals.getGameModeUnitSet().getDistanceUnit());
			if (movementMult[0] != 0)
			{
				movelabel.append('(').append(movementMultOp[0]).append(numFmt.format(movementMult[0])).append(')');
			}

			for (int i = 1; i < movementTypes.length; ++i)
			{
				movelabel.append(", ");
				movelabel.append(movementTypes[i]);
				movelabel.append(' ')
					.append(numFmt.format(Globals.getGameModeUnitSet().convertDistanceToUnitSet(movements[i])));
				movelabel.append(Globals.getGameModeUnitSet().getDistanceUnit());
				if (movementMult[i] != 0)
				{
					movelabel.append('(').append(movementMultOp[i]).append(numFmt.format(movementMult[i])).append(')');
				}
			}
		}
		return movelabel.toString();
	}

	public void addTokenContents(StringBuilder txt)
	{
		if (moveRatesFlag == 2)
		{
			txt.append(movementTypes[0]);
			txt.append(',');
			txt.append(movementTypes[1]);
			txt.append(',');
			if (!movementMultOp[1].isEmpty())
			{
				String multValue = NumberFormat.getNumberInstance().format(movementMult[1]);
				txt.append(movementMultOp[1]).append(multValue);
			}
			else
			{
				txt.append(new DecimalFormat("###0").format(movements[1]));
			}
			return;
		}
		for (int index = 0; index < movementTypes.length; ++index)
		{
			if (index > 0)
			{
				txt.append(',');
			}

			if ((movementTypes[index] != null) && (!movementTypes[index].isEmpty()))
			{
				txt.append(movementTypes[index]).append(',');
			}

			if (!movementMultOp[index].isEmpty())
			{
				txt.append(movementMultOp[index]).append(movementMult[index]);
			}
			else
			{
				txt.append(new DecimalFormat("###0").format(movements[index]));
			}
		}
	}

	/**
	 * Returns a Movement object initialized from the given string. This string
	 * can be any legal string for the MOVE or MOVECLONE tags. The object which
	 * calls getMovementFrom MUST subsequently assign the move rates flag of the
	 * returned Movement in order for the Movement to function properly. (The
	 * default move rates flag is zero, so assignment in that case is not
	 * necessary)
	 * 
	 * @param moveparse
	 *            The String from which a new Movement should be initialized
	 * @return A new Movement initialized from the given String.
	 */
	public static Movement getMovementFrom(final String moveparse)
	{
		Objects.requireNonNull(moveparse, "Null initialization String illegal");
		final StringTokenizer moves = new StringTokenizer(moveparse, ",");
		Movement cm;

		if (moves.countTokens() == 1)
		{
			cm = new Movement(1);
			cm.assignMovement(0, "Walk", moves.nextToken());
		}
		else
		{
			cm = new Movement(moves.countTokens() / 2);

			int x = 0;
			while (moves.countTokens() > 1)
			{
				cm.assignMovement(x++, moves.nextToken(), moves.nextToken());
			}
			if (moves.countTokens() != 0)
			{
				Logging.errorPrint("Badly formed MOVE token " + "(extra value at end of list): " + moveparse);
			}
		}
		return cm;
	}

	public void assignMovement(int x, String type, String mod)
	{
		movementTypes[x] = type; // e.g. "Walk"
		movementMult[x] = 0.0d;
		movementMultOp[x] = "";

		if ((!mod.isEmpty()) && ((mod.charAt(0) == '*') || (mod.charAt(0) == '/')))
		{
			movements[x] = 0.0d;
			try
			{
				double multValue = Double.parseDouble(mod.substring(1));
				if (multValue < 0)
				{
					Logging.errorPrint("Illegal movement multiplier: " + multValue + " in movement string " + mod);
				}
				movementMult[x] = multValue;
				movementMultOp[x] = mod.substring(0, 1);
			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Badly formed MOVE token: " + mod);
				movementMult[x] = 0.0d;
				movementMultOp[x] = "";
			}
		}
		else if (!mod.isEmpty())
		{
			movementMult[x] = 0.0d;
			movementMultOp[x] = "";

			try
			{
				movements[x] = Double.parseDouble(mod);
			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Badly formed MOVE token: " + mod);
				movements[x] = 0.0d;
			}

			if ("Walk".equals(movementTypes[x]))
			{
				movement = x;
			}
		}
	}
}
