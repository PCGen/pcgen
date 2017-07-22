/*
 * Copyright 2003 (C) Jonas Karlsson <jujutsunerd@sf.net>
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
package pcgen.util;

import java.math.BigDecimal;

/**
 * This contains helper functions for BigDecimal.
 */
public final class BigDecimalHelper
{

	private BigDecimalHelper()
	{
	}

	/**
	 * Returns a string with the trimmed number.
	 * E.g. {@literal numberToTrim=3.1000 > 3.1 }
	 * If numberToTrim is non-numeric, 0 is returned (should be changed.)
	 * @param numberToTrim The value to trim.
	 * @return String
	 */
	public static String trimZeros(String numberToTrim)
	{
		BigDecimal aBigD = BigDecimal.ZERO;

		try
		{
			aBigD = new BigDecimal(numberToTrim);
		}
		catch (NumberFormatException exc)
		{
			Logging.errorPrint("Cannot trim zeroes from " + numberToTrim
				+ " as is not a number. Using 0 instead.");
		}

		return aBigD.stripTrailingZeros().toPlainString();
	}

}
