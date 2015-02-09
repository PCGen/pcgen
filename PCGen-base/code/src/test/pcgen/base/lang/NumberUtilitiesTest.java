/*
 * Copyright (c) 2015 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.lang;

import java.math.BigDecimal;

import junit.framework.TestCase;
import pcgen.testsupport.TestSupport;

public class NumberUtilitiesTest extends TestCase
{

	public void testConstructor()
	{
		TestSupport.invokePrivateConstructor(NumberUtilities.class);
	}

	public void testGetNumberNull()
	{
		try
		{
			NumberUtilities.getNumber(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			
		}
		catch (NullPointerException e)
		{
			
		}
	}

	public void testGetNumberBad()
	{
		try
		{
			NumberUtilities.getNumber("1..5");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			
		}
		catch (NullPointerException e)
		{
			
		}
	}

	public void testGetPreciseNumberNull()
	{
		try
		{
			NumberUtilities.getPreciseNumber(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			
		}
		catch (NullPointerException e)
		{
			
		}
	}

	public void testInteger()
	{
		assertEquals(1, NumberUtilities.getNumber("1"));
	}

	public void testLargeInteger()
	{
		assertEquals(Double.valueOf("3141592653"), NumberUtilities.getNumber("3141592653"));
	}

	public void testDouble()
	{
		assertEquals(1.5, NumberUtilities.getNumber("1.5"));
	}

	public void testPreciseInteger()
	{
		assertEquals(1, NumberUtilities.getPreciseNumber("1"));
	}

	public void testPreciseDouble()
	{
		assertEquals(new BigDecimal(1.5), NumberUtilities.getPreciseNumber("1.5"));
	}

	public void testPreciseLargeInteger()
	{
		assertEquals(new BigDecimal("3141592653"), NumberUtilities.getPreciseNumber("3141592653"));
	}


}
