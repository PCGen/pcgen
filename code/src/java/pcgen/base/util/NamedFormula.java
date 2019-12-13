/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.util;

import pcgen.base.formula.Formula;

/**
 * A NamedFormula is a String-Formula pair (similar to a Map.Entry). This is
 * designed specifically for use in a setting where a key-value pair is
 * necessary, but a hash isn't appropriate (due to only one entry).
 */
public final class NamedFormula
{
	/**
	 * The name of the NamedFormula
	 */
	private final String name;

	/**
	 * The (formula) value contained in the NamedFormula
	 */
	private final Formula formula;

	/**
	 * Creates a new NamedFormula for the given name and formula value.
	 * 
	 * @param formulaName
	 *            The String to be used as the name of the NamedFormula.
	 * @param value
	 *            The Formula value of the NamedFormula.
	 */
	public NamedFormula(String formulaName, Formula value)
	{
		name = formulaName;
		formula = value;
	}

	/**
	 * Returns the name of the NamedFormula
	 * 
	 * @return The name of the NamedFormula
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the formula of the NamedFormula
	 * 
	 * @return The Formula of the NamedFormula
	 */
	public Formula getFormula()
	{
		return formula;
	}

	/**
	 * Returns a String representation of this NamedFormula
	 */
	@Override
	public String toString()
	{
		return name + ":" + formula;
	}
}
