/*
 * Copyright (c) 2017 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.format.dice;

import junit.framework.TestCase;
import pcgen.base.format.StringManager;

public class DiceFormatTest extends TestCase
{
	private DiceFormat manager = new DiceFormat();

	public void testConvertFailNull()
	{
		try
		{
			manager.convert(null);
			fail("null value should fail");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testConvertFailNotNumeric()
	{
		try
		{
			manager.convert("SomeString");
			fail("invalid value should fail");
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
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testConvertIndirectFailNull()
	{
		try
		{
			manager.convertIndirect(null);
			fail("null value should fail");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testConvertIndirectFailNotNumeric()
	{
		try
		{
			manager.convertIndirect("SomeString");
			fail("invalid value should fail");
		}
		catch (IllegalArgumentException e)
		{
			//ok as well
		}
	}

	public void testConvert()
	{
		assertEquals(new Dice(1, new Die(1)), manager.convert("1"));
		assertEquals(new Dice(1, new Die(1)), manager.convert("1d1"));
		assertEquals(new Dice(3, new Die(8)), manager.convert("3d8"));
		assertEquals(new Dice(1, new Die(4)), manager.convert("d4"));
	}

	public void testUnconvert()
	{
		assertEquals("1", manager.unconvert(new Dice(1, new Die(1))));
		assertEquals("3d6", manager.unconvert(new Dice(3, new Die(6))));
		assertEquals("1d4", manager.unconvert(new Dice(1, new Die(4))));
	}

	public void testConvertIndirect()
	{
		assertEquals(new Dice(1, new Die(1)), manager.convertIndirect("1").get());
		assertEquals(new Dice(1, new Die(1)), manager.convertIndirect("1d1").get());
		assertEquals(new Dice(3, new Die(8)), manager.convertIndirect("3d8").get());
		assertEquals(new Dice(1, new Die(4)), manager.convertIndirect("d4").get());
	}

	public void testGetIdentifier()
	{
		assertEquals("DICE", manager.getIdentifierType());
	}

	public void testHashCodeEquals()
	{
		assertEquals(new DiceFormat().hashCode(), manager.hashCode());
		assertFalse(manager.equals(new Object()));
		assertFalse(manager.equals(new StringManager()));
		assertTrue(manager.equals(new DiceFormat()));
	}

	public void testGetComponent()
	{
		assertNull(manager.getComponentManager());
	}
}