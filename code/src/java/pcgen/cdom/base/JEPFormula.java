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
	 * @param in
	 *            The String value of this JEPFormula.
	 */
	public JEPFormula(String s)
	{
		formula = s;
	}

	/**
	 * Returns a String representation of this JEPFormula.
	 */
	@Override
	public String toString()
	{
		return formula;
	}

	/**
	 * Returns the consistent-with-equals hashCode for this JEPFormula
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return formula.hashCode();
	}

	/**
	 * Returns true if this JEPFormula is equal to the given Object. Equality is
	 * defined as being another JEPFormula object with equal value.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		return o instanceof JEPFormula
				&& ((JEPFormula) o).formula.equals(formula);
	}

	/**
	 * Resolves this JEPFormula, returning the value of this JEPFormula in the
	 * context of the given PlayerCharacter and source.
	 * 
	 * @return The value of this JEPFormula in the context of the given
	 *         PlayerCharacter and source.
	 * @throws NullPointerException
	 *             if the given PlayerCharacter is null
	 */
	public Float resolve(PlayerCharacter character, String source)
	{
		return character.getVariableValue(formula, source);
	}
}
