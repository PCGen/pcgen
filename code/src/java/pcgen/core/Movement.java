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
 *
 * Created on July 22, 2005.
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.core;

import pcgen.util.Logging;

import java.util.StringTokenizer;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 */
public class Movement
{

	private static final Double DOUBLE_ZERO = new Double(0.0);

	/**
	 * Contains the movement Types for this Movement (e.g. "Walk", "Fly")
	 */
	private String[] movementTypes;

	/**
	 * Contains the associated movement rate (in feet) for the movement type of
	 * the same index. A movement rate must be greater than or equal to zero.
	 *
	 * REFACTOR This should be changed to double[] once PlayerCharacter can
	 * handle it
	 */
	private Double[] movements;

	/**
	 * The movement multiplier for the movement type of the same index. A
	 * movement Multiplier be greater than zero.
	 *
	 * REFACTOR This should be changed to double[] once PlayerCharacter can
	 * handle it
	 */
	private Double[] movementMult;

	/**
	 * The movement operation for the movement type of the same index. (e.g. "*"
	 * or "/")
	 */
	private String[] movementMultOp;

	/**
	 * The Movement Rates flag indicating which type of Movement object this is
	 * 0 indicates a basic assignment
	 * 1 indicates the movement rates are added to the existing movement rate 
	 * for the contained types
	 * 2 indicates this clones one movement rate into another movement rate
	 */
	private int moveRatesFlag;

	/*
	 * A class invariant is that the four above arrays should always have the
	 * same length.
	 */

	/*
	 * CONSIDER I don't know why this variable exists?? - it seems to me it's
	 * duplicate of movements[0]
	 */
	private Double movement;

	/*
	 * REFACTOR Once PlayerCharacter is capable of using a CompositeMovement to
	 * do movement resolution, then this should be refactored to
	 * ConcreteMovement and implement the Movement interface (because
	 * CompositeMovement will be a BasicMovement)
	 */

	/**
	 * Creates a Movement object with arrays of length zero.
	 */
	public Movement()
	{
		this(0);
	}

	/**
	 * Creates a Movement object with arrays of the given length. It is assumed
	 * that the user of this constructor will initialize all of the arrays, as
	 * this constructor does not perform initialization.
	 *
	 * @param i
	 *            The length of the movement arrays to be assigned.
	 */
	private Movement(int i)
	{
		if (i < 0)
		{
			throw new IllegalArgumentException(
					"Argument of array length to ConcreteMovement"
							+ "constructor cannot be negative");
		}
		movementTypes = new String[i];
		movements = new Double[i];
		movementMult = new Double[i];
		movementMultOp = new String[i];
	}

	/**
	 * Sets the Move Rates Flag on this Movement object.
	 *
	 * @param i
	 *            The move rates flag.
	 */
	public void setMoveRatesFlag(int i)
	{
		/*
		 * CONSIDER Should any check be done here on the value of i?
		 */
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

	/*
	 * REFACTOR Some of these methods might need to be rebuilt - should this be
	 * dependent upon an index, or keyed off of a String name of a movementType?
	 */

	public Double getDoubleMovement()
	{
		return movement;
	}

	public Double getMovementMult(int index)
	{
		return movementMult[index];
	}

	public String getMovementMultOp(int index)
	{
		return movementMultOp[index];
	}

	public Double[] getMovementMult()
	{
		return (Double[]) movementMult.clone();
	}

	public String[] getMovementMultOp()
	{
		return (String[]) movementMultOp.clone();
	}

	public int getNumberOfMovementTypes()
	{
		return (movementTypes != null) ? movementTypes.length : 0;
	}

	public void setMovementTypes(String[] arrayString)
	{
		movementTypes = arrayString;
	}

	public String getMovementType(int i)
	{
		if ((movementTypes != null) && (i < movementTypes.length))
		{
			return movementTypes[i];
		}

		return "";
	}

	public String getMovementTypeAt(int x)
	{
		return movementTypes[x];
	}

	public String[] getMovementTypes()
	{
		return (String[]) movementTypes.clone();
	}

	public double getMovementAt(int x)
	{
		return movements[x].doubleValue();
	}

	public Double getMovement(int i)
	{
		if ((movements != null) && (i < movements.length))
		{
			return movements[i];
		}

		return new Double(0);
	}

	public int getNumberOfMovements()
	{
		return (movements != null) ? movements.length : 0;
	}

	public boolean isInitialized()
	{
		return movements != null;
	}

	public Double[] getMovements()
	{
		return (Double[]) movements.clone();
	}

	/**
	 * Provides a String representation of this Movement object, suitable for
	 * display to a user.
	 * @return String
	 */
	public String toString()
	{
		final StringBuffer movelabel = new StringBuffer();
		movelabel.append(movementTypes[0]);
		movelabel.append(' ').append(
				Globals.getGameModeUnitSet().convertDistanceToUnitSet(
						movements[0].doubleValue()));
		movelabel.append(Globals.getGameModeUnitSet().getDistanceUnit());
		if (movementMult[0].doubleValue() != 0)
		{
			movelabel.append('(').append(movementMultOp[0]).append(
					movementMult[0]).append(')');
		}

		for (int i = 1; i < movementTypes.length; ++i)
		{
			movelabel.append(", ");
			movelabel.append(movementTypes[i]);
			movelabel.append(' ').append(
					Globals.getGameModeUnitSet().convertDistanceToUnitSet(
							movements[i].doubleValue()));
			movelabel.append(Globals.getGameModeUnitSet().getDistanceUnit());
			if (movementMult[i].doubleValue() != 0)
			{
				movelabel.append('(').append(movementMultOp[i]).append(
						movementMult[i]).append(')');
			}
		}
		return movelabel.toString();
	}

	/**
	 * Converts this Movement object into a format suitable for storage in an
	 * LST or equivalent file. This method should be the complement of the
	 * static getMovementFrom() method.
	 *
	 * @return a String in LST/PCC file format, suitable for persistent storage
	 */
	public String toLSTString()
	{
		StringBuffer txt = new StringBuffer();
		txt.append("\tMOVE");
		switch (moveRatesFlag)
		{
		case 1: // MOVEA:
			txt.append('A');
			break;

		case 2: // MOVECLONE:
			txt.append("CLONE");
			break;

		default: // MOVE:
			break;
		}
		txt.append(':');
		for (int index = 0; index < movementTypes.length; ++index)
		{
			if (index > 0)
			{
				txt.append(',');
			}

			if ((movementTypes[index] != null)
					&& (movementTypes[index].length() > 0))
			{
				txt.append(movementTypes[index]).append(',');
			}

			if (movementMultOp[index].length() > 0)
			{
				txt.append(movementMultOp[index]).append(movementMult[index]);
			}
			else
			{
				txt.append(movements[index]);
			}
		}
		return txt.toString();
	}

	/**
	 * Returns a ConcreteMovement object initialized from the given string. This
	 * string can be any legal string for the MOVE, MOVEA, or MOVECLONE tags.
	 * The object which calls getMovementFrom MUST subsequently assign the move
	 * rates flag of the returned ConcreteMovement in order for the
	 * ConcreteMovement to function properly. (The default move rates flag is
	 * zero, so assignment in that case is not necessary)
	 *
	 * @param moveparse
	 *            The String from which a new ConcreteMovement should be
	 *            initialized
	 * @return A new ConcreteMovement initialized from the given String.
	 */
	public static Movement getMovementFrom(final String moveparse)
	{
		if (moveparse == null)
		{
			throw new IllegalArgumentException(
					"Null initialization String illegal");
		}
		final StringTokenizer moves = new StringTokenizer(moveparse, ",");
		String tok;
		Movement cm;

		if (moves.countTokens() == 1)
		{
			tok = moves.nextToken();

			cm = new Movement(1);
			if ((tok.length() > 0)
					&& ((tok.charAt(0) == '*') || (tok.charAt(0) == '/')))
			{
				cm.movements[0] = DOUBLE_ZERO;
				cm.movement = DOUBLE_ZERO;
				try
				{
					double multValue = Double.parseDouble(tok.substring(1));
					if (multValue <= 0)
					{
						Logging.errorPrint("Illegal movement multiplier: "
								+ multValue + " in movement string " + tok);
					}
					cm.movementMult[0] = new Double(multValue);
					cm.movementMultOp[0] = tok.substring(0, 1);
				}
				catch (NumberFormatException e)
				{
					Logging.errorPrint("Badly formed MOVE token: " + tok);
					cm.movementMult[0] = DOUBLE_ZERO;
					cm.movementMultOp[0] = "";
				}
			}
			else if (tok.length() > 0)
			{
				try
				{
					cm.movement = new Double(tok);
					cm.movements[0] = cm.movement;
				}
				catch (NumberFormatException e)
				{
					Logging.errorPrint("Badly formed movement string: " + tok);
					cm.movements[0] = DOUBLE_ZERO;
				}

				cm.movementMult[0] = DOUBLE_ZERO;
				cm.movementMultOp[0] = "";
			}

			cm.movementTypes[0] = "Walk";
		}
		else
		{
			cm = new Movement(moves.countTokens() / 2);

			int x = 0;

			while (moves.countTokens() > 1)
			{
				cm.movementTypes[x] = moves.nextToken(); // e.g. "Walk"
				cm.movementMult[x] = DOUBLE_ZERO;
				cm.movementMultOp[x] = "";

				tok = moves.nextToken();

				if ((tok.length() > 0)
						&& ((tok.charAt(0) == '*') || (tok.charAt(0) == '/')))
				{
					cm.movements[x] = DOUBLE_ZERO;
					try
					{
						double multValue = Double.parseDouble(tok.substring(1));
						if (multValue <= 0)
						{
							Logging.errorPrint("Illegal movement multiplier: "
									+ multValue + " in movement string " + tok);
						}
						cm.movementMult[x] = new Double(multValue);
						cm.movementMultOp[x] = tok.substring(0, 1);
					}
					catch (NumberFormatException e)
					{
						Logging.errorPrint("Badly formed MOVE token: " + tok);
						cm.movementMult[x] = DOUBLE_ZERO;
						cm.movementMultOp[x] = "";
					}
				}
				else if (tok.length() > 0)
				{
					cm.movementMult[x] = DOUBLE_ZERO;
					cm.movementMultOp[x] = "";

					try
					{
						cm.movements[x] = new Double(tok);
					}
					catch (NumberFormatException e)
					{
						Logging.errorPrint("Badly formed MOVE token: " + tok);
						cm.movements[x] = DOUBLE_ZERO;
					}

					if ("Walk".equals(cm.movementTypes[x]))
					{
						cm.movement = cm.movements[x];
					}
				}

				x++;
			}
			if (moves.countTokens() != 0)
			{
				Logging.errorPrint("Badly formed MOVE token "
						+ "(extra value at end of list): " + moveparse);
			}
		}
		return cm;
	}
}
