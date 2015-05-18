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
package plugin.format;

import java.util.Collection;

import plugin.format.NumberManager;
import junit.framework.TestCase;

public class NumberManagerTest extends TestCase
{
	private NumberManager manager = new NumberManager();

	public void testConvertFailNull()
	{
		try
		{
			manager.convert(null, null);
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

	public void testConvertFailNotNumeric()
	{
		try
		{
			manager.convert(null, "SomeString");
			fail("null value should fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok as well
		}
	}

	public void testUnconvertFailNull()
	{
		try
		{
			manager.unconvert(null);
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

	public void testConvertIndirectFailNull()
	{
		try
		{
			manager.convertIndirect(null, null);
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

	public void testConvertIndirectFailNotNumeric()
	{
		try
		{
			manager.convertIndirect(null, "SomeString");
			fail("null value should fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok as well
		}
	}

	public void testConvertObjectContainerFailNull()
	{
		try
		{
			manager.convertObjectContainer(null, null);
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

	public void testConvertObjectContainerFailNotNumeric()
	{
		try
		{
			manager.convertObjectContainer(null, "SomeString");
			fail("null value should fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok as well
		}
	}

	public void testConvert()
	{
		assertEquals(Integer.valueOf(1), manager.convert(null, "1"));
		assertEquals(Integer.valueOf(-3), manager.convert(null, "-3"));
		assertEquals(Double.valueOf(1.4), manager.convert(null, "1.4"));
	}

	public void testUnconvert()
	{
		assertEquals("1", manager.unconvert(Integer.valueOf(1)));
		assertEquals("-3", manager.unconvert(Integer.valueOf(-3)));
		assertEquals("1.4", manager.unconvert(Double.valueOf(1.4)));
	}

	public void testConvertIndirect()
	{
		assertEquals(Integer.valueOf(1), manager.convertIndirect(null, "1")
			.resolvesTo());
		assertEquals(Integer.valueOf(-3), manager.convertIndirect(null, "-3")
			.resolvesTo());
		assertEquals(Double.valueOf(1.4), manager.convertIndirect(null, "1.4")
			.resolvesTo());
	}

	public void testConvertObjectContainer()
	{
		Collection<? extends Number> co =
				manager.convertObjectContainer(null, "1").getContainedObjects();
		assertEquals(1, co.size());
		assertEquals(Integer.valueOf(1), co.iterator().next());
		co = manager.convertObjectContainer(null, "-3").getContainedObjects();
		assertEquals(1, co.size());
		assertEquals(Integer.valueOf(-3), co.iterator().next());
		co = manager.convertObjectContainer(null, "1.4").getContainedObjects();
		assertEquals(1, co.size());
		assertEquals(Double.valueOf(1.4), co.iterator().next());
	}

}
