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

import java.lang.reflect.Array;
import java.util.Optional;

import junit.framework.TestCase;
import pcgen.base.format.ArrayFormatManager;
import pcgen.base.format.BooleanManager;
import pcgen.base.format.NumberManager;
import pcgen.base.util.FormatManager;

public class ArrayAddTest extends TestCase
{

	private static final Class<Number> NUMBER_CLASS = Number.class;
	private static final Class<Boolean> BOOLEAN_CLASS = Boolean.class;
	private static final Class<Integer> INTEGER_CLASS = Integer.class;
	private static final Class<Object[]> OBJECT_ARRAY_CLASS =
			(Class<Object[]>) Array.newInstance(Object.class, 0).getClass();
	private static final Class<Number[]> NUMBER_ARRAY_CLASS =
			(Class<Number[]>) Array.newInstance(NUMBER_CLASS, 0).getClass();
	private static final Class<Boolean[]> BOOLEAN_ARRAY_CLASS =
			(Class<Boolean[]>) Array.newInstance(BOOLEAN_CLASS, 0).getClass();
	private static final Class<Integer[]> INTEGER_ARRAY_CLASS =
			(Class<Integer[]>) Array.newInstance(INTEGER_CLASS, 0).getClass();
	FormatManager<Number[]> numberArrayManager =
			new ArrayFormatManager<>(new NumberManager(), ',', '|');
	FormatManager<Boolean[]> booleanArrayManager =
			new ArrayFormatManager<>(new BooleanManager(), ',', '|');

	private final ArrayAdd op = new ArrayAdd();

	public void testOperator()
	{
		assertNotNull(op.getOperator());
		assertTrue(op.getOperator().getSymbol().equals("+"));
	}

	public void testAbstractEvaluateNulls()
	{
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
			assertNull(op.abstractEvaluate(NUMBER_ARRAY_CLASS, null, null));
		}
		catch (NullPointerException e)
		{
			//okay too
		}
		try
		{
			assertNull(op.abstractEvaluate(null, NUMBER_ARRAY_CLASS, null));
		}
		catch (NullPointerException e)
		{
			//okay too
		}
		try
		{
			assertNull(op.abstractEvaluate(NUMBER_ARRAY_CLASS, NUMBER_ARRAY_CLASS, null));
		}
		catch (NullPointerException e)
		{
			//okay too
		}
	}

	public void testAbstractEvaluateMismatch()
	{
		assertNull(op.abstractEvaluate(NUMBER_ARRAY_CLASS, BOOLEAN_ARRAY_CLASS,
			Optional.empty()));
		assertNull(op.abstractEvaluate(BOOLEAN_ARRAY_CLASS, INTEGER_ARRAY_CLASS,
			Optional.of(booleanArrayManager)));
		assertNull(op.abstractEvaluate(NUMBER_ARRAY_CLASS, NUMBER_ARRAY_CLASS,
			Optional.of(booleanArrayManager)));
		assertNull(op.abstractEvaluate(BOOLEAN_ARRAY_CLASS, INTEGER_CLASS,
			Optional.of(booleanArrayManager)));
		assertNull(op.abstractEvaluate(NUMBER_ARRAY_CLASS, NUMBER_CLASS,
			Optional.of(booleanArrayManager)));
	}

	public void testAbstractEvaluateLegal()
	{
		assertEquals(NUMBER_ARRAY_CLASS, op.abstractEvaluate(NUMBER_ARRAY_CLASS,
			NUMBER_ARRAY_CLASS, Optional.of(numberArrayManager)).getManagedClass());
		assertEquals(BOOLEAN_ARRAY_CLASS, op.abstractEvaluate(BOOLEAN_ARRAY_CLASS,
			BOOLEAN_ARRAY_CLASS, Optional.of(booleanArrayManager)).getManagedClass());
		assertEquals(NUMBER_ARRAY_CLASS, op.abstractEvaluate(NUMBER_ARRAY_CLASS,
			NUMBER_CLASS, Optional.of(numberArrayManager)).getManagedClass());
		assertEquals(BOOLEAN_ARRAY_CLASS, op.abstractEvaluate(BOOLEAN_ARRAY_CLASS,
			BOOLEAN_CLASS, Optional.of(booleanArrayManager)).getManagedClass());
		assertEquals(NUMBER_ARRAY_CLASS, op.abstractEvaluate(NUMBER_CLASS,
			NUMBER_ARRAY_CLASS, Optional.of(numberArrayManager)).getManagedClass());
		assertEquals(BOOLEAN_ARRAY_CLASS, op.abstractEvaluate(BOOLEAN_CLASS,
			BOOLEAN_ARRAY_CLASS, Optional.of(booleanArrayManager)).getManagedClass());
		assertEquals(NUMBER_ARRAY_CLASS, op
			.abstractEvaluate(NUMBER_CLASS, NUMBER_CLASS, Optional.of(numberArrayManager))
			.getManagedClass());
		assertEquals(BOOLEAN_ARRAY_CLASS, op.abstractEvaluate(BOOLEAN_CLASS,
			BOOLEAN_CLASS, Optional.of(booleanArrayManager)).getManagedClass());
		//TODO Interesting that these REQUIRE a format assertion... why?
//		assertEquals(NUMBER_ARRAY_CLASS,
//			op.abstractEvaluate(NUMBER_ARRAY_CLASS, NUMBER_ARRAY_CLASS, Optional.empty())
//				.getManagedClass());
//		assertEquals(BOOLEAN_ARRAY_CLASS, op
//			.abstractEvaluate(BOOLEAN_ARRAY_CLASS, BOOLEAN_ARRAY_CLASS, Optional.empty())
//			.getManagedClass());
	}

	public void testEvaluateFailNull()
	{
		try
		{
			assertNull(op.evaluate(null, null));
			fail();
		}
		catch (NullPointerException e)
		{
			//expected
		}
		try
		{
			assertNull(op.evaluate(Boolean.TRUE, null));
			fail();
		}
		catch (NullPointerException e)
		{
			//expected
		}
		try
		{
			assertNull(op.evaluate(new Boolean[0], null));
			fail();
		}
		catch (NullPointerException e)
		{
			//expected
		}
		try
		{
			assertNull(op.evaluate(null, Boolean.FALSE));
			fail();
		}
		catch (NullPointerException e)
		{
			//expected
		}
	}

	//TODO How to catch these? :/
//	public void testEvaluateMismatch()
//	{
//		try
//		{
//			assertNull(op.evaluate(new Boolean[0], Double.valueOf(4.5)));
//			fail();
//		}
//		catch (RuntimeException e)
//		{
//			//expected
//		}
//		try
//		{
//			assertNull(op.evaluate(new Integer[0], Boolean.TRUE));
//			fail();
//		}
//		catch (RuntimeException e)
//		{
//			//expected
//		}
//	}

	public void testEvaluateLegalArrayObject()
	{
		Number[] iArray = new Number[]{1, 2};
		Object result = op.evaluate(iArray, 4.5);
		assertTrue(result.getClass().equals(OBJECT_ARRAY_CLASS));
		Object[] resultArray = (Object[]) result;
		assertEquals(3, resultArray.length);
		assertEquals(1, resultArray[0]);
		assertEquals(2, resultArray[1]);
		assertEquals(4.5, resultArray[2]);
	}

	public void testEvaluateLegalArrayArray()
	{
		Number[] iArray = new Number[]{1, 2};
		Object result = op.evaluate(iArray, new Number[]{4.5, -6});
		assertTrue(result.getClass().equals(OBJECT_ARRAY_CLASS));
		Object[] resultArray = (Object[]) result;
		assertEquals(4, resultArray.length);
		assertEquals(1, resultArray[0]);
		assertEquals(2, resultArray[1]);
		assertEquals(4.5, resultArray[2]);
		assertEquals(-6, resultArray[3]);
	}

	public void testEvaluateLegalObjectArray()
	{
		Object result = op.evaluate(1, new Number[]{4.5, -6});
		assertTrue(result.getClass().equals(OBJECT_ARRAY_CLASS));
		Object[] resultArray = (Object[]) result;
		assertEquals(3, resultArray.length);
		assertEquals(1, resultArray[0]);
		assertEquals(4.5, resultArray[1]);
		assertEquals(-6, resultArray[2]);
	}

	public void testEvaluateLegalObjectObject()
	{
		Object result = op.evaluate(1, -6);
		assertTrue(result.getClass().equals(OBJECT_ARRAY_CLASS));
		Object[] resultArray = (Object[]) result;
		assertEquals(2, resultArray.length);
		assertEquals(1, resultArray[0]);
		assertEquals(-6, resultArray[1]);
	}
}
