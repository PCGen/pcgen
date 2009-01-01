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
package plugin.lsttokens.kit;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.PCStat;
import pcgen.core.kit.KitStat;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.testsupport.AbstractSubTokenTestCase;

public class StatTokenTest extends AbstractSubTokenTestCase<KitStat>
{

	static StatToken token = new StatToken();
	static CDOMSubLineLoader<KitStat> loader = new CDOMSubLineLoader<KitStat>(
			"*KITTOKEN", "SPELLS", KitStat.class);

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		PCStat ps = primaryContext.ref.constructCDOMObject(PCStat.class, "Strength");
		primaryContext.ref.registerAbbreviation(ps, "STR");
		ps.setAbb("STR");
		PCStat ss = secondaryContext.ref.constructCDOMObject(PCStat.class, "Strength");
		secondaryContext.ref.registerAbbreviation(ss, "STR");
		ss.setAbb("STR");
		PCStat pi = primaryContext.ref.constructCDOMObject(PCStat.class, "Intelligence");
		primaryContext.ref.registerAbbreviation(pi, "INT");
		pi.setAbb("INT");
		PCStat si = secondaryContext.ref.constructCDOMObject(PCStat.class, "Intelligence");
		secondaryContext.ref.registerAbbreviation(si, "INT");
		si.setAbb("INT");
	}

	@Override
	public Class<KitStat> getCDOMClass()
	{
		return KitStat.class;
	}

	@Override
	public CDOMSubLineLoader<KitStat> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMSecondaryToken<KitStat> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmptyValue()
			throws PersistenceLayerException
	{
		assertFalse(parse("STR="));
	}

	@Test
	public void testInvalidInputEmptyStat() throws PersistenceLayerException
	{
		assertFalse(parse("=2"));
	}


	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		runRoundRobin("STR=2");
	}


	@Test
	public void testRoundRobinTwo() throws PersistenceLayerException
	{
		runRoundRobin("INT=Wizard|STR=2");
	}

}
