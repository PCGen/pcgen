/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.geom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.base.math.OrderedPair;

import org.junit.jupiter.api.Test;

class OrderedPairTest
{
    @Test
    public void testValueOfNull()
    {
        try
        {
            OrderedPair.valueOf(null);
            fail("null value should fail");
        } catch (NullPointerException | IllegalArgumentException e)
        {
            //ok
        }
    }

    @Test
    public void testValueOfNotNumeric()
    {
        try
        {
            OrderedPair.valueOf("SomeString");
            fail();
        } catch (IllegalArgumentException e)
        {
            //ok
        }
    }

    @Test
    public void testValueOfTooManyCommas()
    {
        try
        {
            OrderedPair.valueOf("1,3,4");
            fail();
        } catch (IllegalArgumentException e)
        {
            //ok
        }
    }

    @Test
    public void testValueOfNoTrailingNumber()
    {
        try
        {
            OrderedPair.valueOf("1,");
            fail();
        } catch (IllegalArgumentException e)
        {
            //ok
        }
    }

    @Test
    public void testValueOfNoLeadingNumber()
    {
        try
        {
            OrderedPair.valueOf(",4");
            fail();
        } catch (IllegalArgumentException e)
        {
            //ok
        }
    }


    @Test
    public void testValueOfBadFirstNumber()
    {
        try
        {
            OrderedPair.valueOf("x,4");
            fail();
        } catch (IllegalArgumentException e)
        {
            //ok
        }
        try
        {
            OrderedPair.valueOf("3-0,4");
            fail();
        } catch (IllegalArgumentException e)
        {
            //ok
        }
    }


    @Test
    public void testValueOfBadSecondNumber()
    {
        try
        {
            OrderedPair.valueOf("5,x");
            fail();
        } catch (IllegalArgumentException e)
        {
            //ok
        }
        try
        {
            OrderedPair.valueOf("5,5..6");
            fail();
        } catch (IllegalArgumentException e)
        {
            //ok
        }
    }

    @Test
    public void testValueOf()
    {
        OrderedPair gp = OrderedPair.valueOf("4,6");
        assertEquals(4, gp.getPreciseX());
        assertEquals(6, gp.getPreciseY());
        assertEquals("4,6", gp.toString());
    }

}
