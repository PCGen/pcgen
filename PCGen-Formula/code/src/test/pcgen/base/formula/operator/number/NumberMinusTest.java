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

import junit.framework.TestCase;

public class NumberMinusTest extends TestCase
{

	private static final Class<Number> NUMBER_CLASS = Number.class;
	private static final Class<Integer> INTEGER_CLASS = Integer.class;
	private static final Class<Double> DOUBLE_CLASS = Double.class;
	private static final Class<Float> FLOAT_CLASS = Float.class;

	private final NumberMinus op = new NumberMinus();

	public void testOperator()
	{
		assertNotNull(op.getOperator());
		assertTrue(op.getOperator().getSymbol().equals("-"));
	}

	public void testAbstractEvaluateNulls()
	{
		try
		{
			assertNull(op.abstractEvaluate(null));
		}
		catch (NullPointerException e)
		{
			//okay too
		}
	}

	public void testAbstractEvaluateMismatch()
	{
		assertTrue(op.abstractEvaluate(Boolean.class).isEmpty());
	}

	public void testAbstractEvaluateLegal()
	{
		assertEquals(NUMBER_CLASS, op.abstractEvaluate(NUMBER_CLASS).get().getManagedClass());
		assertEquals(NUMBER_CLASS, op.abstractEvaluate(DOUBLE_CLASS).get().getManagedClass());
		assertEquals(NUMBER_CLASS, op.abstractEvaluate(FLOAT_CLASS).get().getManagedClass());
		assertEquals(NUMBER_CLASS, op.abstractEvaluate(INTEGER_CLASS).get().getManagedClass());
	}

	public void testEvaluateFailNull()
	{
		try
		{
			assertNull(op.evaluate(null));
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
			assertNull(op.evaluate(true));
			fail();
		}
		catch (RuntimeException e)
		{
			//expected
		}
	}

	public void testEvaluateLegal()
	{
		assertEquals(Integer.valueOf(-2), op.evaluate(Integer.valueOf(2)));
		assertEquals(Double.valueOf(1.3), op.evaluate(Double.valueOf(-1.3)));
		assertEquals(Double.valueOf(-1.3f), op.evaluate(Float.valueOf(1.3f)));
	}
}
