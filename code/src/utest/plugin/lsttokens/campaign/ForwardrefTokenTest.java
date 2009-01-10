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
package plugin.lsttokens.campaign;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.Campaign;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.GameReferenceContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class ForwardrefTokenTest extends AbstractTokenTestCase<Campaign>
{

	static CDOMPrimaryToken<Campaign> token = new ForwardRefToken();
	static CDOMTokenLoader<Campaign> loader = new CDOMTokenLoader<Campaign>(
			Campaign.class);

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		primaryContext = new RuntimeLoadContext(new GameReferenceContext(), new ConsolidatedListCommitStrategy());
		secondaryContext = new RuntimeLoadContext(new GameReferenceContext(), new ConsolidatedListCommitStrategy());
		URI testURI = testCampaign.getURI();
		primaryContext.getObjectContext().setSourceURI(testURI);
		primaryContext.getObjectContext().setExtractURI(testURI);
		secondaryContext.getObjectContext().setSourceURI(testURI);
		secondaryContext.getObjectContext().setExtractURI(testURI);
		primaryProf = getPrimary("TestObj");
		secondaryProf = getSecondary("TestObj");
		expectedPrimaryMessageCount = 0;
	}

	@Override
	public CDOMLoader<Campaign> getLoader()
	{
		return loader;
	}

	@Override
	public Class<Campaign> getCDOMClass()
	{
		return Campaign.class;
	}

	@Override
	public CDOMPrimaryToken<Campaign> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTypeOnly() throws PersistenceLayerException
	{
		assertFalse(parse("SPELL"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTypeBarOnly() throws PersistenceLayerException
	{
		assertFalse(parse("SPELL|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyType() throws PersistenceLayerException
	{
		assertFalse(parse("|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadType() throws PersistenceLayerException
	{
		assertFalse(parse("CAMPAIGN|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadLeadingComma() throws PersistenceLayerException
	{
		assertFalse(parse("SPELL|,Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadTrailingComma() throws PersistenceLayerException
	{
		assertFalse(parse("SPELL|Fireball,"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadDoubleComma() throws PersistenceLayerException
	{
		assertFalse(parse("SPELL|Fireball,,LightningBolt"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidCatTypeNoEqual() throws PersistenceLayerException
	{
		assertFalse(parse("ABILITY|Abil"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNonCatTypeEquals() throws PersistenceLayerException
	{
		assertFalse(parse("SPELL=Arcane|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDoubleEquals() throws PersistenceLayerException
	{
		assertFalse(parse("ABILITY=FEAT=Mutation|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidSpellbookAndSpellBarOnly()
			throws PersistenceLayerException
	{
		assertFalse(parse("SPELL|Fireball|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidSpellBarStarting() throws PersistenceLayerException
	{
		assertFalse(parse("SPELL||Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinJustSpell() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		runRoundRobin("SPELL|Fireball");
	}

	@Test
	public void testRoundRobinJustAbility() throws PersistenceLayerException
	{
		runRoundRobin("ABILITY=NEWCAT|Abil3");
	}

	@Test
	public void testRoundRobinTwoSpell() throws PersistenceLayerException
	{
		runRoundRobin("SPELL|Fireball,Lightning Bolt");
	}

	@Test
	public void testRoundRobinAbilitySpell()
			throws PersistenceLayerException
	{
		runRoundRobin("ABILITY=NEWCAT|Abil3", "SPELL|Lightning Bolt");
	}

	@Test
	public void testRoundRobinFeatSpell()
			throws PersistenceLayerException
	{
		runRoundRobin("FEAT|My Feat", "SPELL|Lightning Bolt");
	}
}