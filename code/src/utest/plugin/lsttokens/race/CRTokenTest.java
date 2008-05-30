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
package plugin.lsttokens.race;

import org.junit.Test;

import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class CRTokenTest extends AbstractTokenTestCase<Race>
{

	static CrToken token = new CrToken();
	static CDOMTokenLoader<Race> loader = new CDOMTokenLoader<Race>(Race.class);

	@Override
	public Class<Race> getCDOMClass()
	{
		return Race.class;
	}

	@Override
	public CDOMLoader<Race> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<Race> getToken()
	{
		return token;
	}

	@Test
	public void testBadInputNegative() throws PersistenceLayerException
	{
		try
		{
			boolean parse = parse("-1");
			assertFalse(parse);
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		assertNoSideEffects();
	}

	@Test
	public void testBadInputNonFloat() throws PersistenceLayerException
	{
		try
		{
			boolean parse = parse("1/x");
			assertFalse(parse);
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinZero() throws PersistenceLayerException
	{
		runRoundRobin("0");
	}

	@Test
	public void testRoundRobinFraction() throws PersistenceLayerException
	{
		runRoundRobin("1/3");
	}

	// @Test
	// public void testRoundRobinFractionFormula()
	// throws PersistenceLayerException
	// {
	// runRoundRobin("1/Formula");
	// }
	//
	// @Test
	// public void testRoundRobinFractionFormulaNegative()
	// throws PersistenceLayerException
	// {
	// runRoundRobin("1/-Formula");
	// }
	//
	// @Test
	// public void testRoundRobinFormula() throws PersistenceLayerException
	// {
	// runRoundRobin("Formula");
	// }
	//
	@Test
	public void testRoundRobinFive() throws PersistenceLayerException
	{
		runRoundRobin("5");
	}

	// @Test
	// public void testEmpty()
	// {
	// //Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	// }

}
