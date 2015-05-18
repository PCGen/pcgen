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
package plugin.lsttokens;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalListTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreLevelParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreLevelWriter;

public class VFeatLstTest extends AbstractGlobalListTokenTestCase<Ability>
{

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(new PreLevelParser());
		TokenRegistration.register(new PreClassParser());
		TokenRegistration.register(new PreLevelWriter());
		TokenRegistration.register(new PreClassWriter());
	}

	@Override
	public char getJoinCharacter()
	{
		return '|';
	}

	@Override
	public Class<Ability> getTargetClass()
	{
		return Ability.class;
	}

	@Override
	public boolean isTypeLegal()
	{
		return true;
	}

	@Override
	public boolean isAllLegal()
	{
		return false;
	}

	@Override
	public boolean isClearDotLegal()
	{
		return false;
	}

	@Override
	public boolean isClearLegal()
	{
		return true;
	}

	static CDOMPrimaryToken<CDOMObject> token = new VFeatLst();
	static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<PCTemplate>();

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

	@Override
	protected CDOMObject construct(LoadContext loadContext, String one)
	{
		Ability obj = loadContext.getReferenceContext().constructCDOMObject(Ability.class, one);
		loadContext.getReferenceContext().reassociateCategory(AbilityCategory.FEAT, obj);
		return obj;
	}

	@Test
	public void testRoundRobinDupe() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("TestWP1|TestWP1");
	}

	@Test
	public void testRoundRobinDupeOnePrereq() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("TestWP1|TestWP1|PRELEVEL:MIN=5");
	}

	@Test
	public void testRoundRobinDupeDiffPrereqs()
		throws PersistenceLayerException
	{
		System.err.println("=");
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("TestWP1", "TestWP1|PRELEVEL:MIN=5");
	}

	@Test
	public void testRoundRobinDupeTwoDiffPrereqs()
		throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("TestWP1|TestWP1|PRELEVEL:MIN=5",
			"TestWP2|TestWP2|PRECLASS:1,Fighter=1");
	}

	@Test
	public void testInvalidOnlyPre() throws PersistenceLayerException
	{
		assertFalse(parse("!PRELEVEL:3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmbeddedNotPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("TestWP1|!PRELEVEL:3|TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("TestWP1,TestWP2|PREFOO:3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNotBadPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("TestWP1,TestWP2|!PREFOO:3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmbeddedPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("TestWP1|PRELEVEL:4|TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidClearDotPre() throws PersistenceLayerException
	{
		assertFalse(parse(".CLEAR.TestWP1|PRELEVEL:4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidClearPre() throws PersistenceLayerException
	{
		assertFalse(parse(".CLEAR|PRELEVEL:4"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("TestWP1|TestWP2|PRELEVEL:MIN=5");
	}

	@Test
	public void testRoundRobinNotPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("TestWP1|TestWP2|!PRELEVEL:MIN=5");
	}

	@Test
	public void testRoundRobinDoublePre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("TestWP1|TestWP2|PRECLASS:1,Fighter=1|PRELEVEL:MIN=5");
	}

	@Test
	public void testRoundRobinDupeTwoPrereqs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("TestWP1|PRECLASS:1,Fighter=1",
			"TestWP1|PRECLASS:1,Wizard=1");
	}

	@Test
	public void testRoundRobinTwoPrereqs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("TestWP1|PRECLASS:1,Fighter=1",
			"TestWP2|PRECLASS:1,Wizard=1");
	}

	@Test
	public void testRoundRobinOneParen() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("TestWP1 (Paren)");
	}

	@Test
	public void testRoundRobinTwoParen() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("TestWP1 (Paren)|TestWP2 (Other)");
	}

	@Test
	public void testRoundRobinDupeParen() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("TestWP1 (Other)|TestWP1 (That)");
	}

	@Test
	public void testRoundRobinListParen() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("TestWP1 (%LIST)");
	}


	@Test
	public void testListTargetClearWorking() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		assertTrue(parse("TestWP1(%LIST)"));
		assertTrue(parse(getClearString()));
		assertNoSideEffects();
	}

	@Test
	public void testClearMixedWorking() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP2");
		assertTrue(parse("TestWP2|TestWP1(%LIST)"));
		assertTrue(parse(getClearString()));
		assertNoSideEffects();
	}
}
