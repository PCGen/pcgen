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

import pcgen.base.math.OrderedPair;

import org.junit.Assert;
import org.junit.Test;

public class OrderedPairTest
{
	@Test
	public void testValueOfNull()
	{
		try
		{
			OrderedPair.valueOf(null);
			Assert.fail("null value should fail");
		}
		catch (NullPointerException | IllegalArgumentException e)
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
			Assert.fail();
		}
		catch (IllegalArgumentException e)
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
			Assert.fail();
		}
		catch (IllegalArgumentException e)
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
			Assert.fail();
		}
		catch (IllegalArgumentException e)
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
			Assert.fail();
		}
		catch (IllegalArgumentException e)
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
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			OrderedPair.valueOf("3-0,4");
			Assert.fail();
		}
		catch (IllegalArgumentException e)
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
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			OrderedPair.valueOf("5,5..6");
			Assert.fail();
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}
	
	@Test
	public void testValueOf()
	{
		OrderedPair gp = OrderedPair.valueOf("4,6");
		Assert.assertEquals(4, gp.getPreciseX());
		Assert.assertEquals(6, gp.getPreciseY());
		Assert.assertEquals("4,6", gp.toString());
	}

}
