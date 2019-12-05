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
 *
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
     * trimBigDecimal ( (BigDecimal) a) to cut off all trailing zeros.
     * It's a terrible hack.
     *
     * @param n the BigDecimal to trim all trailing zeros from
     * @return the trimmed BigDecimal
     */
    public static BigDecimal trimBigDecimal(BigDecimal n)
    {
        if (n.unscaledValue().intValue() == 0)
        {
            // Java 1.5 will not throw an ArthmeticException if you change the
            // scale of 0.0 to 0, so it will keep going through the loop below
            // forever. To get around this we test for the special case here.
            return BigDecimal.ZERO;
        }

        if (n.scale() <= 0)
        {
            return n;
        }

        BigDecimal stripped = n.stripTrailingZeros();
        if (stripped.scale() < 0)
        {
            stripped = n.setScale(0);
        }

        return stripped;
    }

    /**
     * Returns a string with the trimmed number.
     * E.g. {@literal numberToTrim=3.1000 > 3.1 }
     * If numberToTrim is non-numeric, 0 is returned (should be changed.)
     *
     * @param numberToTrim The value to trim.
     * @return String
     */
    public static String trimZeros(String numberToTrim)
    {
        BigDecimal aBigD = BigDecimal.ZERO;

        try
        {
            aBigD = new BigDecimal(numberToTrim);
        } catch (NumberFormatException exc)
        {
            Logging.errorPrint("Cannot trim zeroes from " + numberToTrim + " as is not a number. Using 0 instead.");
        }

        return trimBigDecimal(aBigD).toString();
    }

    /**
     * Trims the zeros.
     *
     * @param n bigdecimal to trim
     * @return String without zeros
     */
    public static String trimZeros(BigDecimal n)
    {
        return trimBigDecimal(n).toString();
    }
}
