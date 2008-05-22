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

public class JEPFormula implements Formula
{

	private final String formula;

	public JEPFormula(String s)
	{
		formula = s;
	}

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
	public boolean equals(Object o)
	{
		return o instanceof JEPFormula
			&& ((JEPFormula) o).formula.equals(formula);
	}

	public Float resolve(PlayerCharacter character, String source)
	{
		return character.getVariableValue(formula, source);
	}
}
