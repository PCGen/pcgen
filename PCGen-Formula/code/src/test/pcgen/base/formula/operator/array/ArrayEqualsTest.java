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

public class ArrayEqualsTest extends TestCase
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
	private final BooleanManager booleanManager = new BooleanManager();
	private final FormatManager<Number[]> numberArrayManager =
			new ArrayFormatManager<>(new NumberManager(), ',', '|');
	private final FormatManager<Boolean[]> booleanArrayManager =
			new ArrayFormatManager<>(booleanManager, ',', '|');

	private final ArrayEquals op = new ArrayEquals();

	public void testOperator()
	{
		assertNotNull(op.getOperator());
		assertTrue(op.getOperator().getSymbol().equals("=="));
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
	}

	public void testAbstractEvaluateMismatch()
	{
		assertNull(op.abstractEvaluate(NUMBER_ARRAY_CLASS, BOOLEAN_ARRAY_CLASS,
			Optional.empty()));
		assertNull(op.abstractEvaluate(BOOLEAN_ARRAY_CLASS, INTEGER_ARRAY_CLASS,
			Optional.of(booleanManager)));
		assertNull(op.abstractEvaluate(BOOLEAN_ARRAY_CLASS, INTEGER_CLASS,
			Optional.of(booleanManager)));
		assertNull(op.abstractEvaluate(NUMBER_ARRAY_CLASS, NUMBER_CLASS,
			Optional.of(booleanManager)));
	}

	public void testAbstractEvaluateLegal()
	{
		assertEquals(BOOLEAN_CLASS, op.abstractEvaluate(NUMBER_ARRAY_CLASS,
			NUMBER_ARRAY_CLASS, Optional.of(numberArrayManager)).getManagedClass());
		assertEquals(BOOLEAN_CLASS, op.abstractEvaluate(BOOLEAN_ARRAY_CLASS,
			BOOLEAN_ARRAY_CLASS, Optional.of(booleanArrayManager)).getManagedClass());
		assertEquals(BOOLEAN_CLASS,
			op.abstractEvaluate(NUMBER_ARRAY_CLASS, NUMBER_ARRAY_CLASS, Optional.empty())
				.getManagedClass());
		assertEquals(BOOLEAN_CLASS, op
			.abstractEvaluate(BOOLEAN_ARRAY_CLASS, BOOLEAN_ARRAY_CLASS, Optional.empty())
			.getManagedClass());
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

	public void testEvaluateLegalArrayArray()
	{
		Number[] iArray = new Number[]{1, 2};
		Object result = op.evaluate(iArray, new Number[]{4.5, -6});
		assertTrue(result.getClass().equals(BOOLEAN_CLASS));
		assertFalse((Boolean) result);
		result = op.evaluate(iArray, new Number[]{1, -6});
		assertTrue(result.getClass().equals(BOOLEAN_CLASS));
		assertFalse((Boolean) result);
		result = op.evaluate(iArray, new Number[]{2, -6});
		assertTrue(result.getClass().equals(BOOLEAN_CLASS));
		assertFalse((Boolean) result);
		result = op.evaluate(iArray, new Number[]{1, 2});
		assertTrue(result.getClass().equals(BOOLEAN_CLASS));
		assertTrue((Boolean) result);
		result = op.evaluate(iArray, new Number[]{1, -6, 2});
		assertTrue(result.getClass().equals(BOOLEAN_CLASS));
		assertFalse((Boolean) result);
		/*
		 * Today we respect order... this is up for debate, though
		 * 
		 * It is likely needed both ways. Ordered for numbers, unordered for objects. Do
		 * we need == and === as operators?
		 */
		iArray = new Number[]{1, 2, 1};
		result = op.evaluate(iArray, new Number[]{1, 1, 2});
		assertTrue(result.getClass().equals(BOOLEAN_CLASS));
		assertFalse((Boolean) result);
	}
}
