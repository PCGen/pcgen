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
 * FormulaInvalidReport represents a specific report about why a formula is not
 * valid.
 * 
 * Note that if there is more than one issue, only one issue needs to be
 * returned (fast fail is acceptable).
 * 
 * This class only needs to be present in a FormulaSemantics when
 * FormulaValidity reports isValid false.
 */
public class FormulaInvalidReport
{

	/**
	 * The String indicating why a formula is not valid.
	 */
	private final String report;

	/**
	 * Creates a new FormulaInvalidReport with the given non-null, non-empty
	 * report.
	 * 
	 * @param text
	 *            A non-null, non-empty String reporting the reason a Formula is
	 *            invalid
	 */
	public FormulaInvalidReport(String text)
	{
		if (text == null)
		{
			throw new IllegalArgumentException("Report text cannot be null");
		}
		if (text.trim().length() == 0)
		{
			throw new IllegalArgumentException("Report text cannot be empty");
		}
		report = text;
	}

	/**
	 * Returns a report describing the reason a formula is invalid.
	 * 
	 * @return A non-null String representing a report describing the reason the
	 *         formula being reported upon is not valid.
	 */
	public String getReport()
	{
		return report;
	}
}
