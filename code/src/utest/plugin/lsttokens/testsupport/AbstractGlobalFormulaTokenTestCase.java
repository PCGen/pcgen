/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.testsupport;

import org.junit.Test;

import pcgen.cdom.enumeration.FormulaKey;
import pcgen.persistence.PersistenceLayerException;

public abstract class AbstractGlobalFormulaTokenTestCase extends
		AbstractGlobalTokenTestCase
{

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(parse("Variable1"));
		assertEquals("Variable1", primaryProf.get(getFormulaKey()).toString());
		assertTrue(parse("3"));
		assertEquals("3", primaryProf.get(getFormulaKey()).toString());
		assertTrue(parse("3+CL(\"Fighter\""));
		assertEquals("3+CL(\"Fighter\"", primaryProf.get(getFormulaKey())
			.toString());
	}

	public abstract FormulaKey getFormulaKey();

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		try
		{
			assertFalse(parse(""));
		}
		catch (IllegalArgumentException e)
		{
			// This is okay too
		}
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinBase() throws PersistenceLayerException
	{
		runRoundRobin("Variable1");
	}

	@Test
	public void testRoundRobinNumber() throws PersistenceLayerException
	{
		runRoundRobin("3");
	}

	@Test
	public void testRoundRobinFormula() throws PersistenceLayerException
	{
		runRoundRobin("3+CL(\"Fighter\"");
	}
}
