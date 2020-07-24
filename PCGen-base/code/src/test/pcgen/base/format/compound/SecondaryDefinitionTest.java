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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formatmanager.SimpleFormatManagerLibrary;

/**
 * Test the SecondaryDefinitio class
 */
public class SecondaryDefinitionTest
{

	@Test
	public void testValueOf()
	{
		SimpleFormatManagerLibrary library = new SimpleFormatManagerLibrary();
		library.addFormatManager(FormatUtilities.NUMBER_MANAGER);
		library.addFormatManager(FormatUtilities.BOOLEAN_MANAGER);
		library.addFormatManager(FormatUtilities.STRING_MANAGER);
		SecondaryDefinition def = SecondaryDefinition.valueOf(library, "NUMBER=b");
		assertEquals(def.getFormatManager(), FormatUtilities.NUMBER_MANAGER);
		assertEquals(def.getName(), "b");
		assertTrue(def.isRequired());
	}

	@Test
	public void testValueOfOptional()
	{
		SimpleFormatManagerLibrary library = new SimpleFormatManagerLibrary();
		library.addFormatManager(FormatUtilities.NUMBER_MANAGER);
		library.addFormatManager(FormatUtilities.BOOLEAN_MANAGER);
		library.addFormatManager(FormatUtilities.STRING_MANAGER);
		SecondaryDefinition def = SecondaryDefinition.valueOf(library, "NUMBER?=b");
		assertEquals(def.getFormatManager(), FormatUtilities.NUMBER_MANAGER);
		assertEquals(def.getName(), "b");
		assertFalse(def.isRequired());
	}

	@Test
	public void testValueOfFail()
	{
		SimpleFormatManagerLibrary library = new SimpleFormatManagerLibrary();
		library.addFormatManager(FormatUtilities.NUMBER_MANAGER);
		library.addFormatManager(FormatUtilities.BOOLEAN_MANAGER);
		library.addFormatManager(FormatUtilities.STRING_MANAGER);
		assertThrows(IllegalArgumentException.class, () -> SecondaryDefinition.valueOf(library, "A=b"));
		assertThrows(IllegalArgumentException.class, () -> SecondaryDefinition.valueOf(library, "NUMBER==b"));
		assertThrows(IllegalArgumentException.class, () -> SecondaryDefinition.valueOf(library, "NUMBER=b=c"));
		assertThrows(IllegalArgumentException.class, () -> SecondaryDefinition.valueOf(library, "NUMBER"));
		assertThrows(IllegalArgumentException.class, () -> SecondaryDefinition.valueOf(library, "NUMBER="));
		assertThrows(IllegalArgumentException.class, () -> SecondaryDefinition.valueOf(library, "=B"));
	}
}
