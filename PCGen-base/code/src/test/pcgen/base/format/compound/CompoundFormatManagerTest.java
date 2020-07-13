/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.format.compound;

import junit.framework.TestCase;
import pcgen.base.format.BooleanManager;
import pcgen.base.format.NumberManager;
import pcgen.base.format.StringManager;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.base.util.SimpleValueStore;

/**
 * Test the CompoundFormatManager class
 */
public class CompoundFormatManagerTest extends TestCase
{
	private final NumberManager numberManager = new NumberManager();
	private final BooleanManager booleanManager = new BooleanManager();
	private final StringManager stringManager = new StringManager();


	@SuppressWarnings({"rawtypes", "unchecked"})
	public void testUnconvertFailObject()
	{
		CompoundFormatManager<Number> manager =
				new CompoundFormatManager<>(numberManager, '|');
		try
		{
			//Yes generics are being violated in order to do this test
			FormatManager formatManager = manager;
			formatManager.unconvert(new Object());
			fail("Object should fail");
		}
		catch (ClassCastException e)
		{
			//expected
		}
	}

	@SuppressWarnings("unused")
	public void testConstructor()
	{
		try
		{
			new CompoundFormatManager<>(null, '|');
			fail("Should not be able to use null format");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testRoundRobinIdentifier()
	{
		CompoundFormatManager<Number> manager =
				new CompoundFormatManager<>(numberManager, '|');
		assertEquals("COMPOUND[NUMBER]", manager.getIdentifierType());
		manager.addSecondary(stringManager, "Level", true);
		assertEquals("COMPOUND[NUMBER,STRING=Level]",
			manager.getIdentifierType());
		manager.addSecondary(booleanManager, "Allowed", false);
		assertEquals("COMPOUND[NUMBER,BOOLEAN?=Allowed,STRING=Level]",
			manager.getIdentifierType());
	}

	public void testInvalidConvertSimpleFail()
	{
		CompoundFormatManager<Number> manager =
				new CompoundFormatManager<>(numberManager, '|');
		manager.addSecondary(booleanManager, "Allowed", false);
		manager.addSecondary(stringManager, "Level", true);
		try
		{
			manager.convert(null);
			fail("Should not be able to convert null instructions");
		}
		catch (NullPointerException e)
		{
			//ok
		}
		catch (IllegalArgumentException e)
		{
			//ok too
		}
		try
		{
			manager.convert("");
			fail("Should not be able to convert null instructions");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convert("|");
			fail("Should not be able to convert null instructions");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convert("3");
			fail("Should not be able to convert instructions"
				+ " missing a required association");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}
	
	public void testInvalidConvertBadSeparator()
	{
		CompoundFormatManager<Number> manager =
				new CompoundFormatManager<>(numberManager, '|');
		manager.addSecondary(booleanManager, "Allowed", false);
		manager.addSecondary(stringManager, "Level", true);
		try
		{
			manager.convert("3|LEVEL=Hard|");
			fail("Should not be able to convert instructions"
				+ " with an ending separator");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convert("3|LEVEL=Hard||ALLOWED=false");
			fail("Should not be able to convert instructions"
				+ " with an double separator");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convert("3||LEVEL=Hard|ALLOWED=false");
			fail("Should not be able to convert instructions"
				+ " with an double separator");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}
	
	public void testInvalidConvertBadAssociation()
	{
		CompoundFormatManager<Number> manager =
				new CompoundFormatManager<>(numberManager, '|');
		manager.addSecondary(booleanManager, "Allowed", false);
		manager.addSecondary(stringManager, "Level", true);
		try
		{
			manager.convert("3|LEVEL=Hard|SOUND=Bell");
			fail("Should not be able to convert instructions"
				+ " with an undefined association");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}
	
	public void testConvert()
	{
		CompoundFormatManager<Number> manager =
				new CompoundFormatManager<>(numberManager, '|');
		manager.addSecondary(booleanManager, "Allowed", false);
		manager.addSecondary(stringManager, "Level", true);
		Compound c = manager.convert("3|LEVEL=Hard");
		assertEquals("3", c.getPrimaryUnconverted());
		assertEquals("Hard", c.getSecondary("Level").getUnconverted());
		assertEquals("Hard", c.getSecondary("LEVEL").getUnconverted());
		/*
		 * For now unassigned optional items return null
		 * 
		 * There is an interesting debate to be had here depending on how
		 * optional items are used
		 */
		assertNull(c.getSecondary("Allowed"));
		assertEquals("3|LEVEL=Hard", manager.unconvert(c));
		Compound c2 = manager.convert("4|LEVEL=Easy|ALLOWED=False");
		assertEquals("4", c2.getPrimaryUnconverted());
		assertEquals("Easy", c2.getSecondary("Level").getUnconverted());
		assertEquals("false", c2.getSecondary("ALLOWED").getUnconverted());
		assertEquals("4|ALLOWED=false|LEVEL=Easy", manager.unconvert(c2));
		CompoundFormatManager<String> manager2 =
				new CompoundFormatManager<>(stringManager, '|');
		manager.addSecondary(booleanManager, "Allowed", false);
		manager.addSecondary(numberManager, "Level", true);
		try
		{
			manager2.unconvert(c2);
			fail("Should not be able to unconvert incompatible Compound");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	public void testConvertIndirect()
	{
		CompoundFormatManager<Number> manager =
				new CompoundFormatManager<>(numberManager, '|');
		manager.addSecondary(booleanManager, "Allowed", false);
		manager.addSecondary(stringManager, "Level", true);
		try
		{
			manager.convertIndirect(null);
			fail("Should not be able to convert null instructions");
		}
		catch (NullPointerException e)
		{
			//ok
		}
		catch (IllegalArgumentException e)
		{
			//ok too
		}
		try
		{
			manager.convertIndirect("");
			fail("Should not be able to convert null instructions");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convertIndirect("|");
			fail("Should not be able to convert null instructions");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convertIndirect("3");
			fail("Should not be able to convert instructions"
				+ " missing a required association");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			manager.convertIndirect("3|LEVEL=Hard|SOUND=Bell");
			fail("Should not be able to convert instructions"
				+ " with an undefined association");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		Indirect<Compound> in = manager.convertIndirect("3|LEVEL=Hard");
		Compound c = in.get();
		assertEquals("3", c.getPrimaryUnconverted());
		assertEquals("Hard", c.getSecondary("Level").getUnconverted());
		assertEquals("Hard", c.getSecondary("LEVEL").getUnconverted());
		/*
		 * For now unassigned optional items return null
		 * 
		 * There is an interesting debate to be had here depending on how
		 * optional items are used
		 */
		assertNull(c.getSecondary("Allowed"));
		assertEquals("3|LEVEL=Hard", manager.unconvert(c));
		assertEquals("3|LEVEL=Hard", in.getUnconverted());
		Indirect<Compound> in2 =
				manager.convertIndirect("4|LEVEL=Easy|ALLOWED=False");
		Compound c2 = in2.get();
		assertEquals("4", c2.getPrimaryUnconverted());
		assertEquals("Easy", c2.getSecondary("Level").getUnconverted());
		assertEquals("false", c2.getSecondary("ALLOWED").getUnconverted());
		assertEquals("4|ALLOWED=false|LEVEL=Easy", manager.unconvert(c2));
		assertEquals("4|ALLOWED=false|LEVEL=Easy", in2.getUnconverted());
	}

	public void testInitializeFrom()
	{
		SimpleValueStore valueStore = new SimpleValueStore();
		CompoundFormatManager<Number> manager =
				new CompoundFormatManager<>(numberManager, '|');
		manager.addSecondary(booleanManager, "Allowed", false);
		manager.addSecondary(stringManager, "Level", true);
		Compound c = manager.convert("3|LEVEL=Hard");
		valueStore.addValueFor(numberManager.getIdentifierType(), 3);
		valueStore.addValueFor(stringManager.getIdentifierType(), "Hard");
		Compound value = manager.initializeFrom(valueStore);
		assertEquals("Hard", value.getSecondary("LEVEL").getUnconverted());
		assertEquals(c, value);
		Compound c2 = manager.convert("4|LEVEL=Easy");
		valueStore.addValueFor(numberManager.getIdentifierType(), 4);
		valueStore.addValueFor(stringManager.getIdentifierType(), "Easy");
		value = manager.initializeFrom(valueStore);
		assertEquals(c2, value);
	}
}
