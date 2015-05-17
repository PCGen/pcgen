/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
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

	public void testGetNumberNotNumeric()
	{
		try
		{
			NumberUtilities.getNumber("SomeString");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testGetNumberNotNumeric2()
	{
		try
		{
			NumberUtilities.getNumber("3..4");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testGetNumberNotNumeric3()
	{
		try
		{
			NumberUtilities.getNumber("3-4");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testGetNumberNotNumeric4()
	{
		try
		{
			NumberUtilities.getNumber("3.4.5");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testGetNumber()
	{
		assertEquals(Integer.valueOf(1), NumberUtilities.getNumber("1"));
		assertEquals(Integer.valueOf(-3), NumberUtilities.getNumber("-3"));
		assertEquals(Double.valueOf(1.4), NumberUtilities.getNumber("1.4"));
	}

}
