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
package pcgen.base.formula.operator.generic;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.testsupport.TestUtilities;

public class GenericNotEqualTest
{
	@Test
	public void testOperator()
	{
		GenericNotEqual op = new GenericNotEqual();
		assertNotNull(op.getOperator());
		assertTrue(op.getOperator().getSymbol().equals("!="));
	}

	@Test
	public void testAbstractEvaluateNulls()
	{
		GenericNotEqual op = new GenericNotEqual();
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
		GenericNotEqual op = new GenericNotEqual();
		assertTrue(op.abstractEvaluate(FormatUtilities.BOOLEAN_CLASS, TestUtilities.INTEGER_CLASS, null).isEmpty());
		assertTrue(op.abstractEvaluate(FormatUtilities.NUMBER_CLASS, FormatUtilities.BOOLEAN_CLASS, null).isEmpty());
		//Don't handle arrays
		assertTrue(op.abstractEvaluate(TestUtilities.NUMBER_ARRAY_CLASS, TestUtilities.NUMBER_ARRAY_CLASS, null).isEmpty());
		assertTrue(op.abstractEvaluate(FormatUtilities.NUMBER_CLASS, TestUtilities.NUMBER_ARRAY_CLASS, null).isEmpty());
		assertTrue(op.abstractEvaluate(TestUtilities.NUMBER_ARRAY_CLASS, FormatUtilities.NUMBER_CLASS, null).isEmpty());
	}

	@Test
	public void testAbstractEvaluateLegal()
	{
		GenericNotEqual op = new GenericNotEqual();
		assertEquals(FormatUtilities.BOOLEAN_CLASS,
			op.abstractEvaluate(FormatUtilities.BOOLEAN_CLASS, FormatUtilities.BOOLEAN_CLASS, null).get().getManagedClass());
	}

	@Test
	public void testEvaluateFailNull()
	{
		GenericNotEqual op = new GenericNotEqual();
		assertThrows(NullPointerException.class, () -> op.evaluate(null, null));
		assertThrows(NullPointerException.class, () -> op.evaluate(Boolean.TRUE, null));
		assertThrows(NullPointerException.class, () -> op.evaluate(null, Boolean.FALSE));
	}

	@Test
	public void testEvaluateMismatch()
	{
		GenericNotEqual op = new GenericNotEqual();
		try
		{
			Object result = op.evaluate(Boolean.TRUE, Double.valueOf(4.5));
			if (!((Boolean) result))
			{
				fail("Unequal types cannot be equal");
			}
		}
		catch (RuntimeException e)
		{
			//expected
		}
		try
		{
			Object result = op.evaluate(new Object(), Boolean.FALSE);
			if (!((Boolean) result))
			{
				fail("Unequal types cannot be equal");
			}
		}
		catch (RuntimeException e)
		{
			//expected
		}
	}

	@Test
	public void testEvaluateLegal()
	{
		GenericNotEqual op = new GenericNotEqual();
		assertEquals(Boolean.FALSE, op.evaluate(Boolean.TRUE, Boolean.TRUE));
		assertEquals(Boolean.TRUE, op.evaluate(Boolean.FALSE, Boolean.TRUE));
		assertEquals(Boolean.TRUE, op.evaluate(Boolean.TRUE, Boolean.FALSE));
		assertEquals(Boolean.FALSE, op.evaluate(Boolean.FALSE, Boolean.FALSE));
	}
}
