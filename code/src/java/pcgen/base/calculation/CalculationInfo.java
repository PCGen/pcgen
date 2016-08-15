/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.calculation;

/**
 * CalculationInfo is a shared interface used to collect common information
 * necessary for various calculation objects.
 * 
 * @param <T>
 *            The format of object processed by this CalculationInfo
 */
public interface CalculationInfo<T>
{

	/**
	 * Returns a String identifying the calculation. May be "ADD" for a
	 * calculation that performs Addition.
	 * 
	 * @return A String identifying the behavior of the calculation
	 */
	public String getIdentification();

	/**
	 * Returns the Format (Class) of object upon which this calculation can
	 * operate. May be a parent class if the calculation can act upon various
	 * related classes such as java.lang.Number.
	 * 
	 * @return The Class of object upon which this calculation can operate
	 */
	public Class<T> getVariableFormat();

	/**
	 * Returns the inherent priority of this calculation. This is defined by the
	 * developer, and sets the order of operations for a calculation where the
	 * User Priority does not set an appropriate order.
	 * 
	 * A lower priority is acted upon first.
	 * 
	 * An inherent priority MUST NOT be less than zero.
	 * 
	 * For example, a calculation that performs Multiplication would want to
	 * have a lower priority (acting first) than a calculation that performs
	 * addition (since multiplication before addition is the natural order of
	 * operations in mathematics)
	 * 
	 * @return The inherent priority of this calculation
	 */
	public int getInherentPriority();
}
