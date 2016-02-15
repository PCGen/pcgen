/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.analysis;

/**
 * FormulaValidity represents a report that a formula is valid.
 */
public class FormulaValidity
{
	/**
	 * Indicates if the formula reported on by this FormulaValidity was valid.
	 */
	private final boolean isValid;

	/**
	 * Constructs a new FormulaValidity with the given value indicating if the
	 * formula is valid.
	 * 
	 * @param valid
	 *            The boolean value indicating if the formula for this this
	 *            FormulaValidity is valid
	 */
	public FormulaValidity(boolean valid)
	{
		isValid = valid;
	}

	/**
	 * Returns true if the Formula evaluated was valid. If this method returns
	 * false, then a FormulaSemantics should contain a non-empty
	 * FormulaInvalidReport.
	 * 
	 * @return true if the formula evaluated was valid; false otherwise
	 */
	public boolean isValid()
	{
		return isValid;
	}
}
