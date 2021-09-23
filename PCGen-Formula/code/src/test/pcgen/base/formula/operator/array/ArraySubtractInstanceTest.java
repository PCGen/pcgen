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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.testsupport.TestUtilities;

public class ArraySubtractInstanceTest
{
	@Test
	public void testOperator()
	{
		ArraySubtractInstance op = new ArraySubtractInstance();
		assertNotNull(op.getOperator());
		assertTrue(op.getOperator().getSymbol().equals("%"));
	}

	@Test
	public void testAbstractEvaluateNulls()
	{
		ArraySubtractInstance op = new ArraySubtractInstance();
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
		try
		{
			assertNull(op.abstractEvaluate(TestUtilities.NUMBER_ARRAY_CLASS, TestUtilities.NUMBER_ARRAY_CLASS, null));
		}
		catch (NullPointerException e)
		{
			//okay too
		}
	}

	@Test
	public void testAbstractEvaluateMismatch()
	{
		ArraySubtractInstance op = new ArraySubtractInstance();
		assertTrue(op.abstractEvaluate(TestUtilities.NUMBER_ARRAY_CLASS, TestUtilities.BOOLEAN_ARRAY_CLASS,
			Optional.empty()).isEmpty());
		assertTrue(op.abstractEvaluate(TestUtilities.BOOLEAN_ARRAY_CLASS, TestUtilities.INTEGER_ARRAY_CLASS,
			Optional.of(TestUtilities.BOOLEAN_ARRAY_MANAGER)).isEmpty());
		assertTrue(op.abstractEvaluate(TestUtilities.NUMBER_ARRAY_CLASS, TestUtilities.NUMBER_ARRAY_CLASS,
			Optional.of(TestUtilities.BOOLEAN_ARRAY_MANAGER)).isEmpty());
		assertTrue(op.abstractEvaluate(TestUtilities.BOOLEAN_ARRAY_CLASS, TestUtilities.INTEGER_CLASS,
			Optional.of(TestUtilities.BOOLEAN_ARRAY_MANAGER)).isEmpty());
		assertTrue(op.abstractEvaluate(TestUtilities.NUMBER_ARRAY_CLASS, FormatUtilities.NUMBER_CLASS,
			Optional.of(TestUtilities.BOOLEAN_ARRAY_MANAGER)).isEmpty());
		assertTrue(op.abstractEvaluate(FormatUtilities.NUMBER_CLASS, TestUtilities.NUMBER_ARRAY_CLASS,
			Optional.of(TestUtilities.NUMBER_ARRAY_MANAGER)).isEmpty());
		assertTrue(op.abstractEvaluate(FormatUtilities.BOOLEAN_CLASS, TestUtilities.BOOLEAN_ARRAY_CLASS,
			Optional.of(TestUtilities.BOOLEAN_ARRAY_MANAGER)).isEmpty());
		assertTrue(op.abstractEvaluate(FormatUtilities.NUMBER_CLASS, FormatUtilities.NUMBER_CLASS,
			Optional.of(TestUtilities.NUMBER_ARRAY_MANAGER)).isEmpty());
		assertTrue(op.abstractEvaluate(FormatUtilities.BOOLEAN_CLASS, FormatUtilities.BOOLEAN_CLASS,
			Optional.of(TestUtilities.BOOLEAN_ARRAY_MANAGER)).isEmpty());
	}

	@Test
	public void testAbstractEvaluateLegal()
	{
		ArraySubtractInstance op = new ArraySubtractInstance();
		assertEquals(TestUtilities.NUMBER_ARRAY_CLASS, op.abstractEvaluate(TestUtilities.NUMBER_ARRAY_CLASS,
			TestUtilities.NUMBER_ARRAY_CLASS, Optional.of(TestUtilities.NUMBER_ARRAY_MANAGER)).get().getManagedClass());
		assertEquals(TestUtilities.BOOLEAN_ARRAY_CLASS, op.abstractEvaluate(TestUtilities.BOOLEAN_ARRAY_CLASS,
			TestUtilities.BOOLEAN_ARRAY_CLASS, Optional.of(TestUtilities.BOOLEAN_ARRAY_MANAGER)).get().getManagedClass());
		assertEquals(TestUtilities.NUMBER_ARRAY_CLASS, op.abstractEvaluate(TestUtilities.NUMBER_ARRAY_CLASS,
			FormatUtilities.NUMBER_CLASS, Optional.of(TestUtilities.NUMBER_ARRAY_MANAGER)).get().getManagedClass());
		assertEquals(TestUtilities.BOOLEAN_ARRAY_CLASS, op.abstractEvaluate(TestUtilities.BOOLEAN_ARRAY_CLASS,
			FormatUtilities.BOOLEAN_CLASS, Optional.of(TestUtilities.BOOLEAN_ARRAY_MANAGER)).get().getManagedClass());
		//TODO Interesting that these REQUIRE a format assertion... why?
//		assertEquals(NUMBER_ARRAY_CLASS,
//			op.abstractEvaluate(NUMBER_ARRAY_CLASS, NUMBER_ARRAY_CLASS, Optional.empty())
//				.getManagedClass());
//		assertEquals(BOOLEAN_ARRAY_CLASS, op
//			.abstractEvaluate(BOOLEAN_ARRAY_CLASS, BOOLEAN_ARRAY_CLASS, Optional.empty())
//			.getManagedClass());
	}

	@Test
	public void testEvaluateFailNull()
	{
		ArraySubtractInstance op = new ArraySubtractInstance();
		assertThrows(NullPointerException.class, () -> op.evaluate(null, null));
		assertThrows(IllegalArgumentException.class, () -> op.evaluate(Boolean.TRUE, null));
		assertThrows(NullPointerException.class, () -> op.evaluate(new Boolean[0], null));
		assertThrows(NullPointerException.class, () -> op.evaluate(null, Boolean.FALSE));
	}

	@Test
	public void testEvaluateLegalArrayObject()
	{
		ArraySubtractInstance op = new ArraySubtractInstance();
		Number[] iArray = new Number[]{1, 2, 4.5, -6};
		Object result = op.evaluate(iArray, 2);
		assertTrue(result.getClass().equals(TestUtilities.OBJECT_ARRAY_CLASS));
		Object[] resultArray = (Object[]) result;
		assertEquals(3, resultArray.length);
		assertEquals(1, resultArray[0]);
		assertEquals(4.5, resultArray[1]);
		assertEquals(-6, resultArray[2]);
	}

	@Test
	public void testEvaluateLegalArrayArray()
	{
		ArraySubtractInstance op = new ArraySubtractInstance();
		Number[] iArray = new Number[]{1, 2, 4.5, -6};
		Object result = op.evaluate(iArray, new Number[]{1, 2});
		assertTrue(result.getClass().equals(TestUtilities.OBJECT_ARRAY_CLASS));
		Object[] resultArray = (Object[]) result;
		assertEquals(2, resultArray.length);
		assertEquals(4.5, resultArray[0]);
		assertEquals(-6, resultArray[1]);
		//Respect duplicates
		iArray = new Number[]{1, 2, 4.5, 1};
		result = op.evaluate(iArray, new Number[]{1, 2});
		assertTrue(result.getClass().equals(TestUtilities.OBJECT_ARRAY_CLASS));
		resultArray = (Object[]) result;
		assertEquals(2, resultArray.length);
		assertEquals(4.5, resultArray[0]);
		assertEquals(1, resultArray[1]);
		//Respect identity
		iArray = new Number[]{new Integer(1), 2, 4.5};
		result = op.evaluate(iArray, new Number[]{new Integer(1), 2});
		assertTrue(result.getClass().equals(TestUtilities.OBJECT_ARRAY_CLASS));
		resultArray = (Object[]) result;
		assertEquals(2, resultArray.length);
		assertEquals(1, resultArray[0]);
		assertEquals(4.5, resultArray[1]);
	}
}
