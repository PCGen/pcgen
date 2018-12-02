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
package pcgen.base.formula.operator.number;

import junit.framework.TestCase;

public class NumberLessThanTest extends TestCase
{

	private static final Class<Number> NUMBER_CLASS = Number.class;
	private static final Class<Boolean> BOOLEAN_CLASS = Boolean.class;
	private static final Class<Integer> INTEGER_CLASS = Integer.class;
	private static final Class<Double> DOUBLE_CLASS = Double.class;

	private final NumberLessThan op = new NumberLessThan();

	public void testOperator()
	{
		assertNotNull(op.getOperator());
		assertTrue(op.getOperator().getSymbol().equals("<"));
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
			assertNull(op.abstractEvaluate(NUMBER_CLASS, null, null));
		}
		catch (NullPointerException e)
		{
			//okay too
		}
		try
		{
			assertNull(op.abstractEvaluate(null, NUMBER_CLASS, null));
		}
		catch (NullPointerException e)
		{
			//okay too
		}
	}

	public void testAbstractEvaluateMismatch()
	{
		assertTrue(op.abstractEvaluate(BOOLEAN_CLASS, INTEGER_CLASS, null).isEmpty());
		assertTrue(op.abstractEvaluate(NUMBER_CLASS, BOOLEAN_CLASS, null).isEmpty());
	}

	public void testAbstractEvaluateLegal()
	{
		assertEquals(BOOLEAN_CLASS,
			op.abstractEvaluate(NUMBER_CLASS, NUMBER_CLASS, null).get().getManagedClass());
		assertEquals(BOOLEAN_CLASS,
			op.abstractEvaluate(DOUBLE_CLASS, DOUBLE_CLASS, null).get().getManagedClass());
		assertEquals(BOOLEAN_CLASS,
			op.abstractEvaluate(INTEGER_CLASS, INTEGER_CLASS, null).get().getManagedClass());
		//mixed okay too
		assertEquals(BOOLEAN_CLASS,
			op.abstractEvaluate(NUMBER_CLASS, DOUBLE_CLASS, null).get().getManagedClass());
		assertEquals(BOOLEAN_CLASS,
			op.abstractEvaluate(INTEGER_CLASS, DOUBLE_CLASS, null).get().getManagedClass());
		assertEquals(BOOLEAN_CLASS,
			op.abstractEvaluate(DOUBLE_CLASS, INTEGER_CLASS, null).get().getManagedClass());
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
			assertNull(op.evaluate(Integer.valueOf(0), null));
			fail();
		}
		catch (NullPointerException e)
		{
			//expected
		}
		try
		{
			assertNull(op.evaluate(null, Double.valueOf(4.5)));
			fail();
		}
		catch (NullPointerException e)
		{
			//expected
		}
	}

	public void testEvaluateMismatch()
	{
		try
		{
			assertNull(op.evaluate(true, Double.valueOf(4.5)));
			fail();
		}
		catch (RuntimeException e)
		{
			//expected
		}
		try
		{
			assertNull(op.evaluate(new Object(), Double.valueOf(4.5)));
			fail();
		}
		catch (RuntimeException e)
		{
			//expected
		}
	}

	public void testEvaluateLegal()
	{
		assertEquals(Boolean.FALSE,
			op.evaluate(Integer.valueOf(2), Double.valueOf(1.3)));
		assertEquals(Boolean.FALSE,
			op.evaluate(Integer.valueOf(2), Integer.valueOf(2)));
		assertEquals(Boolean.FALSE,
			op.evaluate(Double.valueOf(1.3), Double.valueOf(-1.3)));
		assertEquals(Boolean.TRUE,
			op.evaluate(Integer.valueOf(-2), Integer.valueOf(4)));
		assertEquals(Boolean.TRUE,
			op.evaluate(Double.valueOf(-1.3), Double.valueOf(1.3)));
		assertEquals(Boolean.TRUE,
			op.evaluate(Integer.valueOf(2), Double.valueOf(2.1)));
	}
}
