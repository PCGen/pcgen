/*
 * Copyright 2004 (C) Ross M. Lodge
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
package plugin.initiative;

/**
 * <p>
 * This class models a general dice roll.  It can be used to express
 * any kind of roll or expression that DJEP can read, and consists of a name
 * or short description and the dice expression.
 * </p>
 * 
 *  
 */
public class DiceRollModel extends PObjectModel
{
	/** Constant for decoding object string */
	private static final int SEGMENT_POSITION_DICE_EXPRESSION = 1;
	/** The dice roller expression */
	private String m_expression = null;

	/**
	 * <p>
	 * Constructs a new dice model based on a string. The string should have
	 * the following values, in the following order, separated by backslashes:
	 * </p>
	 * <ol>
	 * <li>A short descriptin or name of the roll or check</li>
	 * <li>A dice expression parseable by DJEP.</li>
	 * </ol>
	 * 
	 * @param objectString
	 *            String description of dice roll
	 */
	DiceRollModel(String objectString)
	{
		super(objectString);
		m_expression = getStringValue(outputTokens, DiceRollModel.SEGMENT_POSITION_DICE_EXPRESSION);
	}

	/**
	 * <p>
	 * Gets dice expression
	 * </p>
	 * 
	 * @return Returns the expression.
	 */
	public String getExpression()
	{
		return m_expression;
	}

	/**
	 * <p>
	 * Sets dice expression
	 * </p>
	 * 
	 * @param expression
	 *            The expression to set.
	 */
	public void setExpression(String expression)
	{
		m_expression = expression;
	}

}
