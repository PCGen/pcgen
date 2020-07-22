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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;

public class StringAddTest
{
	@Test
	public void testOperator()
	{
		StringAdd op = new StringAdd();
		assertNotNull(op.getOperator());
		assertTrue(op.getOperator().getSymbol().equals("+"));
	}

	@Test
	public void testAbstractEvaluateNulls()
	{
		StringAdd op = new StringAdd();
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
			assertNull(op.abstractEvaluate(FormatUtilities.STRING_CLASS, null, null));
		}
		catch (NullPointerException e)
		{
			//okay too
		}
		try
		{
			assertNull(op.abstractEvaluate(null, FormatUtilities.STRING_CLASS, null));
		}
		catch (NullPointerException e)
		{
			//okay too
		}
	}

	@Test
	public void testAbstractEvaluateMismatch()
	{
		StringAdd op = new StringAdd();
		assertTrue(op.abstractEvaluate(FormatUtilities.BOOLEAN_CLASS, FormatUtilities.STRING_CLASS, null).isEmpty());
		assertTrue(op.abstractEvaluate(FormatUtilities.STRING_CLASS, FormatUtilities.NUMBER_CLASS, null).isEmpty());
	}

	@Test
	public void testAbstractEvaluateLegal()
	{
		StringAdd op = new StringAdd();
		assertEquals(FormatUtilities.STRING_CLASS,
			op.abstractEvaluate(FormatUtilities.STRING_CLASS, FormatUtilities.STRING_CLASS, null).get().getManagedClass());
	}

	@Test
	public void testEvaluateFailNull()
	{
		StringAdd op = new StringAdd();
		assertThrows(NullPointerException.class, () -> op.evaluate(null, null));
		assertThrows(NullPointerException.class, () -> op.evaluate("ABC", null));
		assertThrows(NullPointerException.class, () -> op.evaluate(null, "DEF"));
	}

	@Test
	public void testEvaluateMismatch()
	{
		StringAdd op = new StringAdd();
		assertThrows(ClassCastException.class, () -> op.evaluate("ABC", true));
		assertThrows(ClassCastException.class, () -> op.evaluate(new Object(), "DEF"));
	}

	@Test
	public void testEvaluateLegal()
	{
		StringAdd op = new StringAdd();
		assertEquals("ABCDEF", op.evaluate("ABC", "DEF"));
	}
}
