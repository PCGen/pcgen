/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.solver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.solver.testsupport.AbstractModifier;
import pcgen.base.testsupport.AbstractFormulaTestCase;
import pcgen.base.testsupport.TestUtilities;

public class ArrayComponentModifierTest extends AbstractFormulaTestCase
{
	@Test
	public void testConstructor()
	{
		Modifier<Number> cm = AbstractModifier.setNumber(3, 100);
		assertThrows(NullPointerException.class, () -> new ArrayComponentModifier<>(null, 5, cm));
		assertThrows(NullPointerException.class, () -> new ArrayComponentModifier<>(TestUtilities.NUMBER_ARRAY_MANAGER, 5, null));
		assertThrows(IllegalArgumentException.class, () -> new ArrayComponentModifier<>(TestUtilities.NUMBER_ARRAY_MANAGER, -5, cm));
	}

	@Test
	public void testGetUserPriority()
	{
		Modifier<Number> cm = AbstractModifier.setNumber(3, 100);
		ArrayComponentModifier<Number> acm = new ArrayComponentModifier<>(TestUtilities.NUMBER_ARRAY_MANAGER, 5, cm);
		assertEquals((100L << 32), acm.getPriority());
	}

	@Test
	public void testGetVariableFormat()
	{
		Modifier<Number> cm = AbstractModifier.setNumber(3, 100);
		ArrayComponentModifier<Number> acm = new ArrayComponentModifier<>(TestUtilities.NUMBER_ARRAY_MANAGER, 5, cm);
		assertEquals(TestUtilities.NUMBER_ARRAY_MANAGER, acm.getVariableFormat());
		assertEquals(Number[].class, acm.getVariableFormat().getManagedClass());
	}

	@Test
	public void testGetIdentification()
	{
		Modifier<Number> cm = AbstractModifier.setNumber(3, 100);
		ArrayComponentModifier<Number> acm = new ArrayComponentModifier<>(TestUtilities.NUMBER_ARRAY_MANAGER, 5, cm);
		assertEquals("Set (component)", acm.getIdentification());
	}

	@Test
	public void testGetInstructions()
	{
		Modifier<Number> cm = AbstractModifier.setNumber(3, 100);
		ArrayComponentModifier<Number> acm = new ArrayComponentModifier<>(TestUtilities.NUMBER_ARRAY_MANAGER, 5, cm);
		assertEquals("To [5]: +3", acm.toString());
	}

	@Test
	public void testProcess()
	{
		Modifier<Number> cm = AbstractModifier.setNumber(8, 100);
		ArrayComponentModifier<Number> acm = new ArrayComponentModifier<>(TestUtilities.NUMBER_ARRAY_MANAGER, 5, cm);
		Number[] array = {1, 2, 3, 4, 5, 6, 7};
		EvaluationManager manager = generateManager(array);
		Object[] result = acm.process(manager);
		assertFalse(array == result);
		array[5] = 8;
		assertTrue(Arrays.deepEquals(array, result));
	}


	@Test
	public void testProcessOutOfBounds()
	{
		Modifier<Number> cm = AbstractModifier.setNumber(77, 100);
		ArrayComponentModifier<Number> acm = new ArrayComponentModifier<>(TestUtilities.NUMBER_ARRAY_MANAGER, 5, cm);
		Number[] array = {1, 2, 3, 4};
		//Should be no effect
		EvaluationManager manager = generateManager(array);
		Object[] result = acm.process(manager);
		assertTrue(Arrays.deepEquals(array, result));
	}
}
