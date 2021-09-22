/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.inst;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.OperatorAction;
import pcgen.base.formula.base.UnaryAction;
import pcgen.base.formula.operator.generic.GenericEquals;
import pcgen.base.formula.operator.number.NumberAdd;
import pcgen.base.formula.operator.number.NumberEquals;
import pcgen.base.formula.operator.number.NumberMinus;
import pcgen.base.formula.parse.Operator;
import pcgen.base.testsupport.TestUtilities;

public class SimpleOperatorLibraryTest
{

	@Test
	public void testInvalidNull()
	{
		SimpleOperatorLibrary library = new SimpleOperatorLibrary();
		assertThrows(NullPointerException.class, () -> library.addAction((OperatorAction) null));
		assertThrows(NullPointerException.class, () -> library.addAction((UnaryAction) null));
	}

	@Test
	public void testEmpty()
	{
		SimpleOperatorLibrary library = new SimpleOperatorLibrary();
		assertTrue(
			library.processAbstract(Operator.ADD, FormatUtilities.NUMBER_CLASS, TestUtilities.INTEGER_CLASS, null).isEmpty());
		assertTrue(library.processAbstract(Operator.MINUS, FormatUtilities.NUMBER_CLASS).isEmpty());
		assertThrows(IllegalStateException.class, () -> library.evaluate(Operator.ADD, 1, 2, null));
		assertThrows(IllegalStateException.class, () -> library.evaluate(Operator.MINUS, 1));
	}

	@Test
	public void testSimpleBinary()
	{
		SimpleOperatorLibrary library = new SimpleOperatorLibrary();
		library.addAction(new NumberAdd());
		assertEquals(Number.class,
			library.processAbstract(Operator.ADD, FormatUtilities.NUMBER_CLASS, TestUtilities.INTEGER_CLASS, null).get().getManagedClass());
		assertEquals(Integer.valueOf(3), library.evaluate(Operator.ADD, 1, 2, null));
		assertThrows(IllegalStateException.class, () -> library.evaluate(Operator.ADD, true, false, null));
	}

	@Test
	public void testSimpleUnary()
	{
		SimpleOperatorLibrary library = new SimpleOperatorLibrary();
		library.addAction(new NumberMinus());
		assertEquals(Number.class,
			library.processAbstract(Operator.MINUS, TestUtilities.INTEGER_CLASS).get().getManagedClass());
		assertEquals(Integer.valueOf(3), library.evaluate(Operator.MINUS, -3));
		assertThrows(IllegalStateException.class, () -> library.evaluate(Operator.MINUS, true));
	}

	@Test
	public void testMultiple()
	{
		SimpleOperatorLibrary library = new SimpleOperatorLibrary();
		library.addAction(new GenericEquals());
		library.addAction(new NumberEquals());
		assertEquals(FormatUtilities.BOOLEAN_CLASS,
			library.processAbstract(Operator.EQ, FormatUtilities.NUMBER_CLASS, TestUtilities.INTEGER_CLASS, null).get().getManagedClass());
		assertEquals(Boolean.FALSE, library.evaluate(Operator.EQ, 1, 2, null));
	}

}
