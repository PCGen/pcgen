/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.util;


import junit.framework.TestCase;

import org.junit.Test;

import pcgen.testsupport.NoPublicZeroArgConstructorMap;
import pcgen.testsupport.NoZeroArgConstructorMap;
import pcgen.testsupport.StrangeMap;

public class GenericMapToListTest extends TestCase
{
	@SuppressWarnings("unused")
	@Test
	public void testConstructorNoZeroArg()
	{
		try
		{
			new GenericMapToList<>(NoZeroArgConstructorMap.class);
			fail("Expected InstantiationException");
		}
		catch (ReflectiveOperationException e)
		{
			//expected
		}
	}

	@SuppressWarnings("unused")
	@Test
	public void testConstructorPrivate()
	{
		try
		{
			new GenericMapToList<>(NoPublicZeroArgConstructorMap.class);
			fail("Expected IllegalAccessException");
		}
		catch (NoSuchMethodException e)
		{
			// OK - please keep this separate!
		}
		catch (ReflectiveOperationException e)
		{
			fail(e.getMessage());
		}
	}

	@SuppressWarnings("unused")
	public void testBadClassInConstructor()
	{
		try
		{
			new GenericMapToList<>(StrangeMap.class);
			fail();
		}
		catch (NoSuchMethodException e)
		{
			//OK, expected
		}
		catch (ReflectiveOperationException e)
		{
			fail(e.getMessage());
		}
	}
}
