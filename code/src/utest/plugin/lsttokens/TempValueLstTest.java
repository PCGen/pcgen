/*
 * Copyright (c) 2015 James Dempsey <jdmepsey@users.sourceforge.net>
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
package plugin.lsttokens;

import java.net.URISyntaxException;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

/**
 * Token parse and unparsing tests for TempValueLst
 *
 * <br/>
 */
public class TempValueLstTest extends AbstractGlobalTokenTestCase
{

	static CDOMPrimaryToken<CDOMObject> token = new TempValueLst();
	static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		PCStat ps = BuildUtilities.createStat("Strength", "STR");
		primaryContext.getReferenceContext().importObject(ps);
		PCStat ss = BuildUtilities.createStat("Strength", "STR");
		secondaryContext.getReferenceContext().importObject(ss);
	}

	@Override
	public CDOMLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
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
	public void testInvalidInputSingle() throws PersistenceLayerException
	{
		assertFalse(parse("buffalo"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTwo() throws PersistenceLayerException
	{
		assertFalse(parse("a|b"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputThree() throws PersistenceLayerException
	{
		assertFalse(parse("a|b|c"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOrder() throws PersistenceLayerException
	{
		assertFalse(parse("MAX=7|MIN=1|TITLE=Foo"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOrder2() throws PersistenceLayerException
	{
		assertFalse(parse("MIN=1|TITLE=Foo|MAX=7"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoTitle() throws PersistenceLayerException
	{
		assertFalse(parse("MIN=1|MAX=7|Foo"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputFour() throws PersistenceLayerException
	{
		assertFalse(parse("MIN=1|MAX=7|TITLE=Foo|Extra"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		runRoundRobin("MIN=1|MAX=2|TITLE=test");
	}

	@Override
	protected String getLegalValue()
	{
		return "MIN=1|MAX=2|TITLE=test";
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "MIN=20|MAX=32|TITLE=test 2";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}
	
}
