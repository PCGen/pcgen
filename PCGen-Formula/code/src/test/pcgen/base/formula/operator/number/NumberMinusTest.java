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
package pcgen.base.formula.operator.number;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.testsupport.TestUtilities;

public class NumberMinusTest
{
	@Test
	public void testOperator()
	{
		NumberMinus op = new NumberMinus();
		assertNotNull(op.getOperator());
		assertTrue(op.getOperator().getSymbol().equals("-"));
	}

	@Test
	public void testAbstractEvaluateNulls()
	{
		NumberMinus op = new NumberMinus();
		try
		{
			assertNull(op.abstractEvaluate(null));
		}
		catch (NullPointerException e)
		{
			//okay too
		}
	}

	@Test
	public void testAbstractEvaluateMismatch()
	{
		NumberMinus op = new NumberMinus();
		assertTrue(op.abstractEvaluate(Boolean.class).isEmpty());
	}

	@Test
	public void testAbstractEvaluateLegal()
	{
		NumberMinus op = new NumberMinus();
		assertEquals(FormatUtilities.NUMBER_CLASS, op.abstractEvaluate(FormatUtilities.NUMBER_CLASS).get().getManagedClass());
		assertEquals(FormatUtilities.NUMBER_CLASS, op.abstractEvaluate(TestUtilities.DOUBLE_CLASS).get().getManagedClass());
		assertEquals(FormatUtilities.NUMBER_CLASS, op.abstractEvaluate(TestUtilities.FLOAT_CLASS).get().getManagedClass());
		assertEquals(FormatUtilities.NUMBER_CLASS, op.abstractEvaluate(TestUtilities.INTEGER_CLASS).get().getManagedClass());
	}

	@Test
	public void testEvaluateFailNull()
	{
		NumberMinus op = new NumberMinus();
		assertThrows(NullPointerException.class, () -> op.evaluate(null));
	}

	@Test
	public void testEvaluateMismatch()
	{
		NumberMinus op = new NumberMinus();
		assertThrows(ClassCastException.class, () -> op.evaluate(true));
	}

	@Test
	public void testEvaluateLegal()
	{
		NumberMinus op = new NumberMinus();
		assertEquals(Integer.valueOf(-2), op.evaluate(Integer.valueOf(2)));
		assertEquals(Double.valueOf(1.3), op.evaluate(Double.valueOf(-1.3)));
		assertEquals(Double.valueOf(-1.3f), op.evaluate(Float.valueOf(1.3f)));
	}
}
