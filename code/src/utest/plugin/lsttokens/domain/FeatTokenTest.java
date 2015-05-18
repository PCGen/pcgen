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
package plugin.lsttokens.domain;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Domain;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractListContextTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

public class FeatTokenTest extends
		AbstractListContextTokenTestCase<Domain, Ability>
{
	static FeatToken token = new FeatToken();
	static CDOMTokenLoader<Domain> loader = new CDOMTokenLoader<Domain>();

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

	@Override
	public Class<Domain> getCDOMClass()
	{
		return Domain.class;
	}

	@Override
	public CDOMLoader<Domain> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<Domain> getToken()
	{
		return token;
	}

	@Override
	protected Ability construct(LoadContext loadContext, String one)
	{
		Ability obj = loadContext.getReferenceContext().constructCDOMObject(Ability.class, one);
		loadContext.getReferenceContext().reassociateCategory(AbilityCategory.FEAT, obj);
		return obj;
	}

	@Test
	public void testInvalidInputEmpty()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "").passed());
		assertNoSideEffects();
	}

	// @Test
	// public void testInvalidInputOnlyPre()
	// {
	// construct(primaryContext, "TestWP1");
	// assertFalse(token.parse(primaryContext, primaryProf,
	// "PRECLASS:1,Fighter=1"));
	// assertNoSideEffects();
	// }
	//
	// @Test
	// public void testInvalidInputEmbeddedPre()
	// {
	// construct(primaryContext, "TestWP1");
	// assertFalse(token.parse(primaryContext, primaryProf,
	// "TestWP1|PRECLASS:1,Fighter=1|TestWP2"));
	// assertNoSideEffects();
	// }
	//
	// @Test
	// public void testInvalidInputDoublePipePre()
	// {
	// construct(primaryContext, "TestWP1");
	// assertFalse(token.parse(primaryContext, primaryProf,
	// "TestWP1||PRECLASS:1,Fighter=1"));
	// assertNoSideEffects();
	// }
	//
	// @Test
	// public void testInvalidInputPostPrePipe()
	// {
	// construct(primaryContext, "TestWP1");
	// assertFalse(token.parse(primaryContext, primaryProf,
	// "TestWP1|PRECLASS:1,Fighter=1|"));
	// assertNoSideEffects();
	// }
	//
	// @Test
	// public void testRoundRobinPre() throws PersistenceLayerException
	// {
	// construct(primaryContext, "TestWP1");
	// construct(secondaryContext, "TestWP1");
	// runRoundRobin("TestWP1|PRECLASS:1,Fighter=1");
	// }
	//
	// @Test
	// public void testRoundRobinTwoPre() throws PersistenceLayerException
	// {
	// construct(primaryContext, "TestWP1");
	// construct(secondaryContext, "TestWP1");
	// runRoundRobin("TestWP1|!PRERACE:1,Human|PRECLASS:1,Fighter=1");
	// }
	//
	// @Test
	// public void testRoundRobinNotPre() throws PersistenceLayerException
	// {
	// construct(primaryContext, "TestWP1");
	// construct(secondaryContext, "TestWP1");
	// runRoundRobin("TestWP1|!PRECLASS:1,Fighter=1");
	// }
	//
	// @Test
	// public void testRoundRobinWWoPre() throws PersistenceLayerException
	// {
	// construct(primaryContext, "TestWP1");
	// construct(primaryContext, "TestWP2");
	// construct(secondaryContext, "TestWP1");
	// construct(secondaryContext, "TestWP2");
	// runRoundRobin("TestWP1|PRECLASS:1,Fighter=1", "TestWP2");
	// }
	//
	// @Test
	// public void testRoundRobinDupeOnePrereq() throws
	// PersistenceLayerException {
	// construct(primaryContext, "TestWP1");
	// construct(secondaryContext, "TestWP1");
	// runRoundRobin("TestWP1|TestWP1|PRERACE:1,Human");
	// }
	//
	// @Test
	// public void testRoundRobinDupeDiffPrereqs()
	// throws PersistenceLayerException {
	// System.err.println("=");
	// construct(primaryContext, "TestWP1");
	// construct(secondaryContext, "TestWP1");
	// runRoundRobin("TestWP1", "TestWP1|PRERACE:1,Human");
	// }
	//
	// @Test
	// public void testRoundRobinDupeTwoDiffPrereqs()
	// throws PersistenceLayerException {
	// construct(primaryContext, "TestWP1");
	// construct(secondaryContext, "TestWP1");
	// construct(primaryContext, "TestWP2");
	// construct(secondaryContext, "TestWP2");
	// runRoundRobin("TestWP1|TestWP1|PRERACE:1,Human",
	// "TestWP2|TestWP2|PRERACE:1,Elf");
	// }

	@Test
	public void testRoundRobinOneParen() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("TestWP1 (Paren)");
	}

	@Test
	public void testRoundRobinListParen() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("TestWP1 (%LIST)");
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

	@Override
	public boolean allowDups()
	{
		return true;
	}

	@Override
	protected CDOMGroupRef<Ability> getTypeReference()
	{
		return primaryContext.getReferenceContext().getCDOMTypeReference(getTargetClass(),
				AbilityCategory.FEAT, "Type1");
	}

	@Override
	protected CDOMGroupRef<Ability> getAllReference()
	{
		return primaryContext.getReferenceContext().getCDOMAllReference(getTargetClass(),
				AbilityCategory.FEAT);
	}

	@Override
	protected CDOMReference<? extends CDOMList<? extends PrereqObject>> getListReference()
	{
		return Ability.FEATLIST;
	}

	@Test
	public void testListTargetClearWorking() throws PersistenceLayerException
	{
		if (isClearLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			assertTrue(parse("TestWP1(%LIST)"));
			assertTrue(parse(getClearString()));
			assertNoSideEffects();
		}
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
