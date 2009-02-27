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

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractListTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

public class FeatTokenTest extends AbstractListTokenTestCase<Race, Ability>
{
	static FeatToken token = new FeatToken();
	static CDOMTokenLoader<Race> loader = new CDOMTokenLoader<Race>(Race.class);

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

	@Override
	protected void construct(LoadContext loadContext, String one)
	{
		Ability obj = loadContext.ref.constructCDOMObject(Ability.class, one);
		loadContext.ref.reassociateCategory(AbilityCategory.FEAT, obj);
	}

	@Test
	public void testInvalidInputEmpty()
	{
		assertFalse(token.parse(primaryContext, primaryProf, ""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOnlyPre()
	{
		construct(primaryContext, "TestWP1");
		try
		{
			assertFalse(token.parse(primaryContext, primaryProf,
					"PRECLASS:1,Fighter=1"));
		}
		catch (IllegalArgumentException e)
		{
			// this is okay too :)
		}
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmbeddedPre()
	{
		construct(primaryContext, "TestWP1");
		try
		{
			assertFalse(token.parse(primaryContext, primaryProf,
					"TestWP1|PRECLASS:1,Fighter=1|TestWP2"));
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoublePipePre()
	{
		construct(primaryContext, "TestWP1");
		assertFalse(token.parse(primaryContext, primaryProf,
				"TestWP1||PRECLASS:1,Fighter=1"));
		assertNoSideEffects();
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

	@Override
	public boolean allowDups()
	{
		return true;
	}

	//TODO Need to enable this to get 5.14 behavior
//	@Override
//	protected ConsolidationRule getConsolidationRule()
//	{
//		return ConsolidationRule.OVERWRITE;
//	}
}
