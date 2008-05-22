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
package plugin.lsttokens.template;

import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pcgen.core.PCTemplate;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class SizeTokenTest extends AbstractTokenTestCase<PCTemplate>
{

	static SizeToken token = new SizeToken();
	static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<PCTemplate>(
			PCTemplate.class);

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public CDOMLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<PCTemplate> getToken()
	{
		return token;
	}

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		SizeAdjustment ps = primaryContext.ref.constructCDOMObject(SizeAdjustment.class, "Small");
		primaryContext.ref.registerAbbreviation(ps, "S");
		SizeAdjustment pm = primaryContext.ref.constructCDOMObject(SizeAdjustment.class, "Medium");
		primaryContext.ref.registerAbbreviation(pm, "M");
		SizeAdjustment ss = secondaryContext.ref.constructCDOMObject(SizeAdjustment.class, "Small");
		secondaryContext.ref.registerAbbreviation(ss, "S");
		SizeAdjustment sm = secondaryContext.ref.constructCDOMObject(SizeAdjustment.class, "Medium");
		secondaryContext.ref.registerAbbreviation(sm, "M");
	}

	@Override
	@After
	public void tearDown() throws Exception
	{
		super.tearDown();
	}

	@Test
	public void testRoundRobinS() throws PersistenceLayerException
	{
		runRoundRobin("S");
	}

	@Test
	public void testRoundRobinM() throws PersistenceLayerException
	{
		runRoundRobin("M");
	}

	@Test
	public void testRoundRobinFormula() throws PersistenceLayerException
	{
		runRoundRobin("max(4,String)");
	}

}
