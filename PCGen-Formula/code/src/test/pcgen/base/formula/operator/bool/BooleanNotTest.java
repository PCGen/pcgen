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

import junit.framework.TestCase;

public class BooleanNotTest extends TestCase
{

	private static final Class<Number> NUMBER_CLASS = Number.class;
	private static final Class<Boolean> BOOLEAN_CLASS = Boolean.class;
	private static final Class<Integer> INTEGER_CLASS = Integer.class;

	private final BooleanNot op = new BooleanNot();

	public void testOperator()
	{
		assertNotNull(op.getOperator());
		assertTrue(op.getOperator().getSymbol().equals("!"));
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
		assertNull(op.abstractEvaluate(INTEGER_CLASS));
		assertNull(op.abstractEvaluate(NUMBER_CLASS));
	}

	public void testAbstractEvaluateLegal()
	{
		assertEquals(BOOLEAN_CLASS, op.abstractEvaluate(BOOLEAN_CLASS).get().getManagedClass());
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
			assertNull(op.evaluate(Double.valueOf(4.5)));
			fail();
		}
		catch (RuntimeException e)
		{
			//expected
		}
		try
		{
			assertNull(op.evaluate(new Object()));
			fail();
		}
		catch (RuntimeException e)
		{
			//expected
		}
	}

	public void testEvaluateLegal()
	{
		assertEquals(Boolean.TRUE, op.evaluate(Boolean.FALSE));
		assertEquals(Boolean.FALSE, op.evaluate(Boolean.TRUE));
	}
}
