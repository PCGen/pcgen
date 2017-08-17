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
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

public class ProhibitedTokenTest extends AbstractCDOMTokenTestCase<PCClass>
{

	static ProhibitedToken token = new ProhibitedToken();
	static CDOMTokenLoader<PCClass> loader = new CDOMTokenLoader<>();

	PreClassParser preclass = new PreClassParser();
	PreClassWriter preclasswriter = new PreClassWriter();
	PreRaceParser prerace = new PreRaceParser();
	PreRaceWriter preracewriter = new PreRaceWriter();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(preclass);
		TokenRegistration.register(preclasswriter);
		TokenRegistration.register(prerace);
		TokenRegistration.register(preracewriter);
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
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputLeadingComma() throws PersistenceLayerException
	{
		assertFalse(parse(",Good"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTrailingComma()
			throws PersistenceLayerException
	{
		assertFalse(parse("Fireball,"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoubleCommaSeparator()
			throws PersistenceLayerException
	{
		assertFalse(parse("Fireball,,Lightning Bolt"));
		assertNoSideEffects();
	}
	
	@Test
	public void testRoundRobinDescriptorSimple()
			throws PersistenceLayerException
	{
		runRoundRobin("Fire");
	}

	@Test
	public void testRoundRobinDescriptorAnd() throws PersistenceLayerException
	{
		runRoundRobin("Fear,Fire");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "Fire";
	}

	@Override
	protected String getLegalValue()
	{
		return "Fear";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return new ConsolidationRule.AppendingConsolidation(',');
	}
}
