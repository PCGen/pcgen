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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.testsupport.TestUtilities;

public class BooleanNotTest
{
	@Test
	public void testOperator()
	{
		BooleanNot op = new BooleanNot();
		assertNotNull(op.getOperator());
		assertTrue(op.getOperator().getSymbol().equals("!"));
	}

	@Test
	public void testAbstractEvaluateNulls()
	{
		BooleanNot op = new BooleanNot();
		try
		{
			assertNull(op.abstractEvaluate(null));
		}
		catch (NullPointerException e)
		{
			//okay too
		}
	}

	@Test
	public void testAbstractEvaluateMismatch()
	{
		BooleanNot op = new BooleanNot();
		assertTrue(op.abstractEvaluate(TestUtilities.INTEGER_CLASS).isEmpty());
		assertTrue(op.abstractEvaluate(FormatUtilities.NUMBER_CLASS).isEmpty());
	}

	@Test
	public void testAbstractEvaluateLegal()
	{
		BooleanNot op = new BooleanNot();
		assertEquals(FormatUtilities.BOOLEAN_CLASS, op.abstractEvaluate(FormatUtilities.BOOLEAN_CLASS).get().getManagedClass());
	}

	@Test
	public void testEvaluateFailNull()
	{
		BooleanNot op = new BooleanNot();
		assertThrows(NullPointerException.class, () -> op.evaluate(null));
	}

	@Test
	public void testEvaluateMismatch()
	{
		BooleanNot op = new BooleanNot();
		assertThrows(ClassCastException.class, () -> op.evaluate(Double.valueOf(4.5)));
		assertThrows(ClassCastException.class, () -> op.evaluate(new Object()));
	}

	@Test
	public void testEvaluateLegal()
	{
		BooleanNot op = new BooleanNot();
		assertEquals(Boolean.TRUE, op.evaluate(Boolean.FALSE));
		assertEquals(Boolean.FALSE, op.evaluate(Boolean.TRUE));
	}
}
