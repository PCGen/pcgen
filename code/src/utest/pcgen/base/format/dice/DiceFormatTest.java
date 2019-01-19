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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.BeforeEach;

import pcgen.base.format.StringManager;
import pcgen.base.util.FormatManager;

public class DiceFormatTest
{
	private static final FormatManager<Dice> MANAGER = new DiceFormat();

	@Test(expected = NullPointerException.class)
	public void testConvertFailNull()
	{
		MANAGER.convert(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertFailNotNumeric()
	{
		MANAGER.convert("SomeString");
	}

	@Test(expected = NullPointerException.class)
	public void testUnconvertFailNull()
	{
		MANAGER.unconvert(null);
	}

	@Test(expected = NullPointerException.class)
	public void testConvertIndirectFailNull()
	{
		MANAGER.convertIndirect(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertIndirectFailNotNumeric()
	{
		MANAGER.convertIndirect("SomeString");
	}

	@Test
	public void testConvert()
	{
		assertEquals(new Dice(1, new Die(1)), MANAGER.convert("1"));
		assertEquals(new Dice(1, new Die(1)), MANAGER.convert("1d1"));
		assertEquals(new Dice(3, new Die(8)), MANAGER.convert("3d8"));
		assertEquals(new Dice(1, new Die(4)), MANAGER.convert("d4"));
	}

	@Test
	public void testUnconvert()
	{
		assertEquals("1", MANAGER.unconvert(new Dice(1, new Die(1))));
		assertEquals("3d6", MANAGER.unconvert(new Dice(3, new Die(6))));
		assertEquals("1d4", MANAGER.unconvert(new Dice(1, new Die(4))));
	}

	@Test
	public void testConvertIndirect()
	{
		assertEquals(new Dice(1, new Die(1)), MANAGER.convertIndirect("1").get());
		assertEquals(new Dice(1, new Die(1)), MANAGER.convertIndirect("1d1").get());
		assertEquals(new Dice(3, new Die(8)), MANAGER.convertIndirect("3d8").get());
		assertEquals(new Dice(1, new Die(4)), MANAGER.convertIndirect("d4").get());
	}

	@Test
	public void testGetIdentifier()
	{
		assertEquals("DICE", MANAGER.getIdentifierType());
	}

	@Test
	public void testHashCodeEquals()
	{
		assertEquals(new DiceFormat().hashCode(), MANAGER.hashCode());
		assertNotEquals(new Object(), MANAGER);
		assertNotEquals(MANAGER, new StringManager());
		assertEquals(MANAGER, new DiceFormat());
	}

	@Test
	public void testGetComponent()
	{
		assertTrue(MANAGER.getComponentManager().isEmpty());
	}
}
