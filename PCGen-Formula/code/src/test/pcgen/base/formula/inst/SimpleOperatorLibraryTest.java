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

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.base.formula.base.OperatorAction;
import pcgen.base.formula.base.UnaryAction;
import pcgen.base.formula.operator.generic.GenericEquals;
import pcgen.base.formula.operator.number.NumberAdd;
import pcgen.base.formula.operator.number.NumberEquals;
import pcgen.base.formula.operator.number.NumberMinus;
import pcgen.base.formula.parse.Operator;

public class SimpleOperatorLibraryTest extends TestCase
{
	private static final Class<Number> NUMBER_CLASS = Number.class;
	private static final Class<Integer> INTEGER_CLASS = Integer.class;

	private SimpleOperatorLibrary library;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		library = new SimpleOperatorLibrary();
	}

	@Test
	public void testInvalidNull()
	{
		try
		{
			library.addAction((OperatorAction) null);
			fail("Expected null action to be rejected");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//Yep
		}
		try
		{
			library.addAction((UnaryAction) null);
			fail("Expected null action to be rejected");
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			//Yep
		}
	}

	@Test
	public void testEmpty()
	{
		assertNull(
			library.processAbstract(Operator.ADD, NUMBER_CLASS, INTEGER_CLASS, null));
		assertNull(library.processAbstract(Operator.MINUS, NUMBER_CLASS));
		try
		{
			library.evaluate(Operator.ADD, 1, 2, null);
			fail();
		}
		catch (IllegalStateException e)
		{
			//Wasn't defined yet
		}
		try
		{
			library.evaluate(Operator.MINUS, 1);
			fail();
		}
		catch (IllegalStateException e)
		{
			//Wasn't defined yet
		}
	}

	@Test
	public void testSimpleBinary()
	{
		library.addAction(new NumberAdd());
		assertEquals(Number.class,
			library.processAbstract(Operator.ADD, NUMBER_CLASS, INTEGER_CLASS, null).get().getManagedClass());
		assertEquals(Integer.valueOf(3), library.evaluate(Operator.ADD, 1, 2, null));
		try
		{
			library.evaluate(Operator.ADD, true, false, null);
			fail();
		}
		catch (IllegalStateException e)
		{
			//Isn't defined 
		}
	}

	@Test
	public void testSimpleUnary()
	{
		library.addAction(new NumberMinus());
		assertEquals(Number.class,
			library.processAbstract(Operator.MINUS, INTEGER_CLASS).get().getManagedClass());
		assertEquals(Integer.valueOf(3), library.evaluate(Operator.MINUS, -3));
		try
		{
			library.evaluate(Operator.MINUS, true);
			fail();
		}
		catch (IllegalStateException e)
		{
			//Isn't defined 
		}
	}

	@Test
	public void testMultiple()
	{
		library.addAction(new GenericEquals());
		library.addAction(new NumberEquals());
		assertEquals(Boolean.class,
			library.processAbstract(Operator.EQ, NUMBER_CLASS, INTEGER_CLASS, null).get().getManagedClass());
		assertEquals(Boolean.FALSE, library.evaluate(Operator.EQ, 1, 2, null));
	}

}
