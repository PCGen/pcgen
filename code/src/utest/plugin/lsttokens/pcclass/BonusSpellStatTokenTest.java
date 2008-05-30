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

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class BonusSpellStatTokenTest extends AbstractTokenTestCase<PCClass>
{

	static BonusspellstatToken token = new BonusspellstatToken();
	static CDOMTokenLoader<PCClass> loader = new CDOMTokenLoader<PCClass>(
			PCClass.class);

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		PCStat ps = primaryContext.ref.constructCDOMObject(PCStat.class,
				"Strength");
		primaryContext.ref.registerAbbreviation(ps, "STR");
		PCStat ss = secondaryContext.ref.constructCDOMObject(PCStat.class,
				"Strength");
		secondaryContext.ref.registerAbbreviation(ss, "STR");
		PCStat pi = primaryContext.ref.constructCDOMObject(PCStat.class,
				"Intelligence");
		primaryContext.ref.registerAbbreviation(pi, "INT");
		PCStat si = secondaryContext.ref.constructCDOMObject(PCStat.class,
				"Intelligence");
		secondaryContext.ref.registerAbbreviation(si, "INT");

	}

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
	public void testInvalidNotAStat() throws PersistenceLayerException
	{
		assertFalse(parse("NAN"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidMultipleStatComma() throws PersistenceLayerException
	{
		assertFalse(parse("STR,INT"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidMultipleStatBar() throws PersistenceLayerException
	{
		assertFalse(parse("STR|INT"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidMultipleStatDot() throws PersistenceLayerException
	{
		assertFalse(parse("STR.INT"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinStr() throws PersistenceLayerException
	{
		runRoundRobin("STR");
	}

	@Test
	public void testRoundRobinNone() throws PersistenceLayerException
	{
		runRoundRobin("NONE");
	}

}
