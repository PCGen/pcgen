/*
 * pcgen - DESCRIPTION OF PACKAGE Copyright (C) 2004 Ross M. Lodge
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 675 Mass
 * Ave, Cambridge, MA 02139, USA.
 * 
 * The author of this program grants you the ability to use this code in
 * conjunction with code that is covered under the Open Gaming License
 * 
 * DiceRollModel.java
 * 
 * Created on Jan 31, 2004, 11:52:34 AM
 */
package plugin.initiative;

/**
 * <p>
 * This class models a general dice roll.  It can be used to express
 * any kind of roll or expression that DJEP can read, and consists of a name
 * or short description and the dice expression.
 * </p>
 * 
 * @author Ross M. Lodge
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
	public DiceRollModel(String objectString)
	{
		super(objectString);
		setExpression(
			getStringValue(outputTokens, SEGMENT_POSITION_DICE_EXPRESSION));
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
