/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.operator.array;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.testsupport.TestUtilities;

public class ArrayEqualsTest
{
	@Test
	public void testOperator()
	{
		ArrayEquals op = new ArrayEquals();
		assertNotNull(op.getOperator());
		assertTrue(op.getOperator().getSymbol().equals("=="));
	}

	@Test
	public void testAbstractEvaluateNulls()
	{
		ArrayEquals op = new ArrayEquals();
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
			assertNull(op.abstractEvaluate(TestUtilities.NUMBER_ARRAY_CLASS, null, null));
		}
		catch (NullPointerException e)
		{
			//okay too
		}
		try
		{
			assertNull(op.abstractEvaluate(null, TestUtilities.NUMBER_ARRAY_CLASS, null));
		}
		catch (NullPointerException e)
		{
			//okay too
		}
	}

	@Test
	public void testAbstractEvaluateMismatch()
	{
		ArrayEquals op = new ArrayEquals();
		assertTrue(op.abstractEvaluate(TestUtilities.NUMBER_ARRAY_CLASS, TestUtilities.BOOLEAN_ARRAY_CLASS,
			Optional.empty()).isEmpty());
		assertTrue(op.abstractEvaluate(TestUtilities.BOOLEAN_ARRAY_CLASS, TestUtilities.INTEGER_ARRAY_CLASS,
			Optional.of(FormatUtilities.BOOLEAN_MANAGER)).isEmpty());
		assertTrue(op.abstractEvaluate(TestUtilities.BOOLEAN_ARRAY_CLASS, TestUtilities.INTEGER_CLASS,
			Optional.of(FormatUtilities.BOOLEAN_MANAGER)).isEmpty());
		assertTrue(op.abstractEvaluate(TestUtilities.NUMBER_ARRAY_CLASS, FormatUtilities.NUMBER_CLASS,
			Optional.of(FormatUtilities.BOOLEAN_MANAGER)).isEmpty());
	}

	@Test
	public void testAbstractEvaluateLegal()
	{
		ArrayEquals op = new ArrayEquals();
		assertEquals(FormatUtilities.BOOLEAN_CLASS, op.abstractEvaluate(TestUtilities.NUMBER_ARRAY_CLASS,
			TestUtilities.NUMBER_ARRAY_CLASS, Optional.of(TestUtilities.NUMBER_ARRAY_MANAGER)).get().getManagedClass());
		assertEquals(FormatUtilities.BOOLEAN_CLASS, op.abstractEvaluate(TestUtilities.BOOLEAN_ARRAY_CLASS,
			TestUtilities.BOOLEAN_ARRAY_CLASS, Optional.of(TestUtilities.BOOLEAN_ARRAY_MANAGER)).get().getManagedClass());
		assertEquals(FormatUtilities.BOOLEAN_CLASS,
			op.abstractEvaluate(TestUtilities.NUMBER_ARRAY_CLASS, TestUtilities.NUMBER_ARRAY_CLASS, Optional.empty())
				.get().getManagedClass());
		assertEquals(FormatUtilities.BOOLEAN_CLASS, op
			.abstractEvaluate(TestUtilities.BOOLEAN_ARRAY_CLASS, TestUtilities.BOOLEAN_ARRAY_CLASS, Optional.empty())
			.get().getManagedClass());
	}

	@Test
	public void testEvaluateFailNull()
	{
		ArrayEquals op = new ArrayEquals();
		assertThrows(NullPointerException.class, () -> op.evaluate(null, null));
		assertThrows(NullPointerException.class, () -> op.evaluate(Boolean.TRUE, null));
		assertThrows(NullPointerException.class, () -> op.evaluate(new Boolean[0], null));
		assertThrows(NullPointerException.class, () -> op.evaluate(null, Boolean.FALSE));
	}

	@Test
	public void testEvaluateLegalArrayArray()
	{
		ArrayEquals op = new ArrayEquals();
		Number[] iArray = new Number[]{1, 2};
		Object result = op.evaluate(iArray, new Number[]{4.5, -6});
		assertTrue(result.getClass().equals(FormatUtilities.BOOLEAN_CLASS));
		assertFalse((Boolean) result);
		result = op.evaluate(iArray, new Number[]{1, -6});
		assertTrue(result.getClass().equals(FormatUtilities.BOOLEAN_CLASS));
		assertFalse((Boolean) result);
		result = op.evaluate(iArray, new Number[]{2, -6});
		assertTrue(result.getClass().equals(FormatUtilities.BOOLEAN_CLASS));
		assertFalse((Boolean) result);
		result = op.evaluate(iArray, new Number[]{1, 2});
		assertTrue(result.getClass().equals(FormatUtilities.BOOLEAN_CLASS));
		assertTrue((Boolean) result);
		result = op.evaluate(iArray, new Number[]{1, -6, 2});
		assertTrue(result.getClass().equals(FormatUtilities.BOOLEAN_CLASS));
		assertFalse((Boolean) result);
		/*
		 * Today we respect order... this is up for debate, though
		 * 
		 * It is likely needed both ways. Ordered for numbers, unordered for objects. Do
		 * we need == and === as operators?
		 */
		iArray = new Number[]{1, 2, 1};
		result = op.evaluate(iArray, new Number[]{1, 1, 2});
		assertTrue(result.getClass().equals(FormatUtilities.BOOLEAN_CLASS));
		assertFalse((Boolean) result);
	}
}
