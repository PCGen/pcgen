/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.format.compound;

import junit.framework.TestCase;
import pcgen.base.format.BooleanManager;
import pcgen.base.format.NumberManager;
import pcgen.base.format.StringManager;
import pcgen.base.formatmanager.CompoundFormatFactory;
import pcgen.base.formatmanager.SimpleFormatManagerLibrary;

/**
 * Test the SecondaryDefinitio class
 */
public class SecondaryDefinitionTest extends TestCase
{

	private final NumberManager numberManager = new NumberManager();
	private final BooleanManager booleanManager = new BooleanManager();
	private final StringManager stringManager = new StringManager();
	CompoundFormatFactory manager = new CompoundFormatFactory(',', '|');

	public void testValueOf()
	{
		SimpleFormatManagerLibrary library = new SimpleFormatManagerLibrary();
		library.addFormatManager(numberManager);
		library.addFormatManager(booleanManager);
		library.addFormatManager(stringManager);
		SecondaryDefinition def = SecondaryDefinition.valueOf(library, "NUMBER=b");
		assertEquals(def.getFormatManager(), numberManager);
		assertEquals(def.getName(), "b");
		assertTrue(def.isRequired());
	}

	public void testValueOfOptional()
	{
		SimpleFormatManagerLibrary library = new SimpleFormatManagerLibrary();
		library.addFormatManager(numberManager);
		library.addFormatManager(booleanManager);
		library.addFormatManager(stringManager);
		SecondaryDefinition def = SecondaryDefinition.valueOf(library, "NUMBER?=b");
		assertEquals(def.getFormatManager(), numberManager);
		assertEquals(def.getName(), "b");
		assertFalse(def.isRequired());
	}

	public void testValueOfFail()
	{
		SimpleFormatManagerLibrary library = new SimpleFormatManagerLibrary();
		library.addFormatManager(numberManager);
		library.addFormatManager(booleanManager);
		library.addFormatManager(stringManager);
		try
		{
			SecondaryDefinition.valueOf(library, "A=b");
			fail("Bad Format should fail");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			SecondaryDefinition.valueOf(library, "NUMBER==b");
			fail("Double equals should fail");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			SecondaryDefinition.valueOf(library, "NUMBER=b=c");
			fail("Two Equals should fail");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			SecondaryDefinition.valueOf(library, "NUMBER");
			fail("No Equals should fail");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			SecondaryDefinition.valueOf(library, "NUMBER=");
			fail("No Name should fail");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//Expected
		}
		try
		{
			SecondaryDefinition.valueOf(library, "=B");
			fail("No Format should fail");
		}
		catch (NullPointerException | IllegalArgumentException e)
		{
			//Expected
		}
	}
}
