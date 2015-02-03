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

import junit.framework.TestCase;
import pcgen.base.geom.GridPoint;
import plugin.format.GridPointManager;

public class GridPointManagerTest extends TestCase
{
	private GridPointManager manager = new GridPointManager();

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
		assertEquals(new GridPoint(1, 1), manager.convert(null, "1,1"));
		assertEquals(new GridPoint(-3, 4), manager.convert(null, "-3,4"));
		assertEquals(new GridPoint(1.4, 6.5), manager.convert(null, "1.4,6.5"));
	}

	public void testUnconvert()
	{
		assertEquals("1,2", manager.unconvert(new GridPoint(1, 2)));
		assertEquals("-3,4", manager.unconvert(new GridPoint(-3, 4)));
		assertEquals("1.4,6.5", manager.unconvert(new GridPoint(1.4, 6.5)));
	}

	public void testConvertIndirect()
	{
		assertEquals(new GridPoint(1, 1), manager.convertIndirect(null, "1,1")
			.resolvesTo());
		assertEquals(new GridPoint(-3, 4), manager.convertIndirect(null, "-3,4")
			.resolvesTo());
		assertEquals(new GridPoint(1.4, 6.5), manager
			.convertIndirect(null, "1.4,6.5").resolvesTo());
	}

	public void testConvertObjectContainer()
	{
		Collection<? extends GridPoint> co =
				manager.convertObjectContainer(null, "1,2").getContainedObjects();
		assertEquals(1, co.size());
		assertEquals(new GridPoint(1, 2), co.iterator().next());
		co = manager.convertObjectContainer(null, "-3,4").getContainedObjects();
		assertEquals(1, co.size());
		assertEquals(new GridPoint(-3, 4), co.iterator().next());
		co = manager.convertObjectContainer(null, "1.4,6.5").getContainedObjects();
		assertEquals(1, co.size());
		assertEquals(new GridPoint(1.4, 6.5), co.iterator().next());
	}

}
