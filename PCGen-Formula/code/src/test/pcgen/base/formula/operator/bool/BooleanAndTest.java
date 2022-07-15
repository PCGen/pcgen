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
package pcgen.base.formula.operator.bool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.testsupport.TestUtilities;

public class BooleanAndTest
{
	@Test
	public void testOperator()
	{
		BooleanAnd op = new BooleanAnd();
		assertNotNull(op.getOperator());
		assertTrue(op.getOperator().getSymbol().equals("&&"));
	}

	@Test
	public void testAbstractEvaluateNulls()
	{
		BooleanAnd op = new BooleanAnd();
		try
		{
			assertNull(op.abstractEvaluate(null, null, null));
		}
		catch (NullPointerException e)
		{
			//okay too
		}
		try
		{
			assertNull(op.abstractEvaluate(FormatUtilities.BOOLEAN_CLASS, null, null));
		}
		catch (NullPointerException e)
		{
			//okay too
		}
		try
		{
			assertNull(op.abstractEvaluate(null, FormatUtilities.BOOLEAN_CLASS, null));
		}
		catch (NullPointerException e)
		{
			//okay too
		}
	}

	@Test
	public void testAbstractEvaluateMismatch()
	{
		BooleanAnd op = new BooleanAnd();
		assertTrue(op.abstractEvaluate(FormatUtilities.BOOLEAN_CLASS, TestUtilities.INTEGER_CLASS, null).isEmpty());
		assertTrue(op.abstractEvaluate(FormatUtilities.NUMBER_CLASS, FormatUtilities.BOOLEAN_CLASS, null).isEmpty());
	}

	@Test
	public void testAbstractEvaluateLegal()
	{
		BooleanAnd op = new BooleanAnd();
		assertEquals(FormatUtilities.BOOLEAN_CLASS,
			op.abstractEvaluate(FormatUtilities.BOOLEAN_CLASS, FormatUtilities.BOOLEAN_CLASS, null).get().getManagedClass());
	}

	@Test
	public void testEvaluateFailNull()
	{
		BooleanAnd op = new BooleanAnd();
		assertThrows(NullPointerException.class, () -> op.evaluate(null, null));
		assertThrows(NullPointerException.class, () -> op.evaluate(Boolean.TRUE, null));
		assertThrows(NullPointerException.class, () -> op.evaluate(null, Boolean.FALSE));
	}

	@Test
	public void testEvaluateMismatch()
	{
		BooleanAnd op = new BooleanAnd();
		assertThrows(ClassCastException.class, () -> op.evaluate(Boolean.FALSE, Double.valueOf(4.5)));
		assertThrows(ClassCastException.class, () -> op.evaluate(new Object(), Boolean.TRUE));
	}

	@Test
	public void testEvaluateLegal()
	{
		BooleanAnd op = new BooleanAnd();
		assertEquals(Boolean.TRUE, op.evaluate(Boolean.TRUE, Boolean.TRUE));
		assertEquals(Boolean.FALSE, op.evaluate(Boolean.FALSE, Boolean.TRUE));
		assertEquals(Boolean.FALSE, op.evaluate(Boolean.TRUE, Boolean.FALSE));
		assertEquals(Boolean.FALSE, op.evaluate(Boolean.FALSE, Boolean.FALSE));
	}
}
