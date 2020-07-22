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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.testsupport.TestUtilities;

public class NumberRemainderTest
{

	@Test
	public void testOperator()
	{
		NumberRemainder op = new NumberRemainder();
		assertNotNull(op.getOperator());
		assertTrue(op.getOperator().getSymbol().equals("%"));
	}

	@Test
	public void testAbstractEvaluateNulls()
	{
		NumberRemainder op = new NumberRemainder();
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
			assertNull(op.abstractEvaluate(FormatUtilities.NUMBER_CLASS, null, null));
		}
		catch (NullPointerException e)
		{
			//okay too
		}
		try
		{
			assertNull(op.abstractEvaluate(null, FormatUtilities.NUMBER_CLASS, null));
		}
		catch (NullPointerException e)
		{
			//okay too
		}
	}

	@Test
	public void testAbstractEvaluateMismatch()
	{
		NumberRemainder op = new NumberRemainder();
		assertTrue(op.abstractEvaluate(FormatUtilities.BOOLEAN_CLASS, TestUtilities.INTEGER_CLASS, null).isEmpty());
		assertTrue(op.abstractEvaluate(FormatUtilities.NUMBER_CLASS, FormatUtilities.BOOLEAN_CLASS, null).isEmpty());
	}

	@Test
	public void testAbstractEvaluateLegal()
	{
		NumberRemainder op = new NumberRemainder();
		assertEquals(FormatUtilities.NUMBER_CLASS,
			op.abstractEvaluate(FormatUtilities.NUMBER_CLASS, FormatUtilities.NUMBER_CLASS, null).get().getManagedClass());
		assertEquals(FormatUtilities.NUMBER_CLASS,
			op.abstractEvaluate(TestUtilities.DOUBLE_CLASS, TestUtilities.DOUBLE_CLASS, null).get().getManagedClass());
		assertEquals(FormatUtilities.NUMBER_CLASS,
			op.abstractEvaluate(TestUtilities.INTEGER_CLASS, TestUtilities.INTEGER_CLASS, null).get().getManagedClass());
		//mixed okay too
		assertEquals(FormatUtilities.NUMBER_CLASS,
			op.abstractEvaluate(FormatUtilities.NUMBER_CLASS, TestUtilities.DOUBLE_CLASS, null).get().getManagedClass());
		assertEquals(FormatUtilities.NUMBER_CLASS,
			op.abstractEvaluate(TestUtilities.INTEGER_CLASS, TestUtilities.DOUBLE_CLASS, null).get().getManagedClass());
		assertEquals(FormatUtilities.NUMBER_CLASS,
			op.abstractEvaluate(TestUtilities.DOUBLE_CLASS, TestUtilities.INTEGER_CLASS, null).get().getManagedClass());
	}

	@Test
	public void testEvaluateFailNull()
	{
		NumberRemainder op = new NumberRemainder();
		assertThrows(NullPointerException.class, () -> op.evaluate(null, null));
		assertThrows(NullPointerException.class, () -> op.evaluate(Integer.valueOf(0), null));
		assertThrows(NullPointerException.class, () -> op.evaluate(null, Double.valueOf(4.5)));
	}

	@Test
	public void testEvaluateMismatch()
	{
		NumberRemainder op = new NumberRemainder();
		assertThrows(ClassCastException.class, () -> op.evaluate(true, Double.valueOf(4.5)));
		assertThrows(ClassCastException.class, () -> op.evaluate(new Object(), Double.valueOf(4.5)));
	}

	@Test
	public void testEvaluateLegal()
	{
		NumberRemainder op = new NumberRemainder();
		assertEquals(Double.valueOf(0.7),
			op.evaluate(Integer.valueOf(2), Double.valueOf(1.3)));
		assertEquals(Integer.valueOf(0),
			op.evaluate(Integer.valueOf(4), Integer.valueOf(2)));
		assertEquals(Double.valueOf(2.0),
			op.evaluate(Integer.valueOf(2), Double.valueOf(3.3)));
		Number val =
				(Number) op.evaluate(Integer.valueOf(2), Float.valueOf(0.3f));
		assertTrue(Math.abs(val.doubleValue() - 0.2) < (Math.pow(10, -7)));
	}
}
