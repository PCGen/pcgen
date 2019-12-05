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
package plugin.function.testsupport;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;

import pcgen.base.formula.parse.FormulaParser;
import pcgen.base.formula.parse.ParseException;
import pcgen.base.formula.parse.SimpleNode;

public final class TestUtilities
{

    private TestUtilities()
    {
    }

    public static SimpleNode doParse(String formula)
    {
        try
        {
            return new FormulaParser(new StringReader(formula)).query();
        } catch (ParseException e)
        {
            fail("Encountered Unexpected Exception: " + e.getMessage());
            return null;
        }
    }

    static final double SMALL_ERROR = Math.pow(10, -10);

    static boolean doubleEqual(double d1, double d2, double delta)
    {
        if (delta < 0)
        {
            throw new IllegalArgumentException(
                    "Delta for doubleEqual cannot be < 0: " + delta);
        }
        double diff = d1 - d2;
        return ((diff >= 0) && (diff < delta))
                || ((diff < 0) && (diff > -delta));
    }


}
