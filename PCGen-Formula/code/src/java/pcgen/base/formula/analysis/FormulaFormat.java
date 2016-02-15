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
 * Indicates the Format for the result returned by a Formula.
 */
public class FormulaFormat
{

	/**
	 * The class of object returned by the Formula.
	 */
	private final Class<?> formulaFormat;

	/**
	 * Constructs a new FormulaFormat with the given format.
	 * 
	 * @param format
	 *            The format indicating the class object returned by a formula
	 */
	public FormulaFormat(Class<?> format)
	{
		if (format == null)
		{
			throw new IllegalArgumentException("Format cannot be null");
		}
		formulaFormat = format;
	}

	/**
	 * Returns the format indicating the class object returned by a formula.
	 * 
	 * @return The format indicating the class object returned by a formula
	 */
	public Class<?> getFormat()
	{
		return formulaFormat;
	}
}
