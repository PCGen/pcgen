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
package pcgen.base.formula.operator.string;

import junit.framework.TestCase;

public class StringAddTest extends TestCase
{

	private static final Class<Number> NUMBER_CLASS = Number.class;
	private static final Class<Boolean> BOOLEAN_CLASS = Boolean.class;
	private static final Class<String> STRING_CLASS = String.class;

	private final StringAdd op = new StringAdd();

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
			assertNull(op.abstractEvaluate(STRING_CLASS, null, null));
		}
		catch (NullPointerException e)
		{
			//okay too
		}
		try
		{
			assertNull(op.abstractEvaluate(null, STRING_CLASS, null));
		}
		catch (NullPointerException e)
		{
			//okay too
		}
	}

	public void testAbstractEvaluateMismatch()
	{
		assertNull(op.abstractEvaluate(BOOLEAN_CLASS, STRING_CLASS, null));
		assertNull(op.abstractEvaluate(STRING_CLASS, NUMBER_CLASS, null));
	}

	public void testAbstractEvaluateLegal()
	{
		assertEquals(STRING_CLASS,
			op.abstractEvaluate(STRING_CLASS, STRING_CLASS, null).get().getManagedClass());
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
			assertNull(op.evaluate("ABC", null));
			fail();
		}
		catch (NullPointerException e)
		{
			//expected
		}
		try
		{
			assertNull(op.evaluate(null, "DEF"));
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
			assertNull(op.evaluate("ABC", true));
			fail();
		}
		catch (RuntimeException e)
		{
			//expected
		}
		try
		{
			assertNull(op.evaluate(new Object(), "DEF"));
			fail();
		}
		catch (RuntimeException e)
		{
			//expected
		}
	}

	public void testEvaluateLegal()
	{
		assertEquals("ABCDEF", op.evaluate("ABC", "DEF"));
	}
}
