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
package plugin.lsttokens.pcclass;

import org.junit.Test;

import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class AbbTokenTest extends AbstractTokenTestCase<PCClass>
{
	static AbbToken token = new AbbToken();

	static CDOMTokenLoader<PCClass> loader = new CDOMTokenLoader<PCClass>(
			PCClass.class);

	@Override
	public Class<PCClass> getCDOMClass()
	{
		return PCClass.class;
	}

	@Override
	public CDOMLoader<PCClass> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<PCClass> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertEquals(null, primaryContext.ref.getAbbreviation(primaryProf));
		assertNoSideEffects();
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(parse("Niederösterreich"));
		assertEquals("Niederösterreich", primaryContext.ref
				.getAbbreviation(primaryProf));
		assertTrue(parse("Finger Lakes"));
		assertEquals("Finger Lakes", primaryContext.ref
				.getAbbreviation(primaryProf));
		assertTrue(parse("Rheinhessen"));
		assertEquals("Rheinhessen", primaryContext.ref
				.getAbbreviation(primaryProf));
		assertTrue(parse("Languedoc-Roussillon"));
		assertEquals("Languedoc-Roussillon", primaryContext.ref
				.getAbbreviation(primaryProf));
		assertTrue(parse("Yarra Valley"));
		assertEquals("Yarra Valley", primaryContext.ref
				.getAbbreviation(primaryProf));
	}

	@Test
	public void testReplacementInputs() throws PersistenceLayerException
	{
		String[] unparsed;
		assertTrue(parse("Start"));
		assertTrue(parse("Mod"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertEquals("Expected item to be equal", "Mod", unparsed[0]);
	}

	@Test
	public void testRoundRobinBase() throws PersistenceLayerException
	{
		runRoundRobin("Rheinhessen");
	}

	@Test
	public void testRoundRobinWithSpace() throws PersistenceLayerException
	{
		runRoundRobin("Finger Lakes");
	}

	@Test
	public void testRoundRobinNonEnglishAndN() throws PersistenceLayerException
	{
		runRoundRobin("Niederösterreich");
	}

	@Test
	public void testRoundRobinHyphen() throws PersistenceLayerException
	{
		runRoundRobin("Languedoc-Roussillon");
	}

	@Test
	public void testRoundRobinY() throws PersistenceLayerException
	{
		runRoundRobin("Yarra Valley");
	}
}
