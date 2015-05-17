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

import junit.framework.TestCase;

public class GridPointTest extends TestCase
{
	public void testValueOfNull()
	{
		try
		{
			GridPoint.valueOf(null);
			fail("null value should fail");
		}
		catch (NullPointerException e)
		{
			//ok
		}
		catch (IllegalArgumentException e)
		{
			//ok as well
		}
	}

	public void testValueOfNotNumeric()
	{
		try
		{
			GridPoint.valueOf("SomeString");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testValueOfTooManyCommas()
	{
		try
		{
			GridPoint.valueOf("1,3,4");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testValueOfNoTrailingNumber()
	{
		try
		{
			GridPoint.valueOf("1,");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testValueOfNoLeadingNumber()
	{
		try
		{
			GridPoint.valueOf(",4");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}


	public void testValueOfBadFirstNumber()
	{
		try
		{
			GridPoint.valueOf("x,4");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			GridPoint.valueOf("3-0,4");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}


	public void testValueOfBadSecondNumber()
	{
		try
		{
			GridPoint.valueOf("5,x");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			GridPoint.valueOf("5,5..6");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}
	
	public void testValueOf()
	{
		GridPoint gp = GridPoint.valueOf("4,6");
		assertEquals(Integer.valueOf(4), gp.getPreciseX());
		assertEquals(Integer.valueOf(6), gp.getPreciseY());
		assertEquals("4,6", gp.toString());
	}

}
