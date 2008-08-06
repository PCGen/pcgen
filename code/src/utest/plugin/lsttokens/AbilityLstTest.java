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

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PCTemplate;
import pcgen.core.SettingsHandler;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreLevelParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreLevelWriter;
import plugin.pretokens.writer.PreRaceWriter;

public class AbilityLstTest extends AbstractGlobalTokenTestCase
{

	static CDOMPrimaryToken<CDOMObject> token = new AbilityLst();
	static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<PCTemplate>(
			PCTemplate.class);

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(new PreRaceParser());
		TokenRegistration.register(new PreRaceWriter());
		TokenRegistration.register(new PreLevelParser());
		TokenRegistration.register(new PreLevelWriter());
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
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNotANature() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT|NotANature|,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNotaCategory() throws PersistenceLayerException
	{
		assertFalse(parse("NotaCategory|NORMAL|,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoAbility() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT|NORMAL"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidCategoryOnly() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidCategoryBarOnly() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyCategory() throws PersistenceLayerException
	{
		assertFalse(parse("|NORMAL|Abil"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyNature() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT||Abil"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyAbility() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT|NORMAL|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidOnlyPre() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT|NORMAL|PRERACE:1,Human"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDoubleBarAbility() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT|NORMAL|Abil1||Abil2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInsertedPre() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT|NORMAL|Abil1|PRELEVEL:MIN=4|Abil2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDoubleBarStartAbility()
			throws PersistenceLayerException
	{
		assertFalse(parse("FEAT|NORMAL||Abil1|Abil2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBarEndAbility() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT|NORMAL|Abil1|"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinJustSpell() throws PersistenceLayerException
	{
		Ability ab = primaryContext.ref.constructCDOMObject(
				Ability.class, "Abil1");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class,
				"Abil1");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		runRoundRobin("Feat|NORMAL|Abil1");
	}

	@Test
	public void testRoundRobinJustTwoPrereq() throws PersistenceLayerException
	{
		Ability ab = primaryContext.ref.constructCDOMObject(
				Ability.class, "Abil1");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class,
				"Abil1");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		runRoundRobin("Feat|NORMAL|Abil1|PRELEVEL:MIN=5|PRERACE:1,Human");
	}

	@Test
	public void testRoundRobinTwoSpell() throws PersistenceLayerException
	{
		Ability ab = primaryContext.ref.constructCDOMObject(
				Ability.class, "Abil1");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class,
				"Abil1");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = primaryContext.ref.constructCDOMObject(Ability.class, "Abil2");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class,
				"Abil2");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		runRoundRobin("Feat|NORMAL|Abil1|Abil2");
	}

	@Test
	public void testRoundRobinTwoNature() throws PersistenceLayerException
	{
		Ability ab = primaryContext.ref.constructCDOMObject(
				Ability.class, "Abil1");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class,
				"Abil1");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = primaryContext.ref.constructCDOMObject(Ability.class, "Abil2");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class,
				"Abil2");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = primaryContext.ref.constructCDOMObject(Ability.class, "Abil3");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class,
				"Abil3");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = primaryContext.ref.constructCDOMObject(Ability.class, "Abil4");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class,
				"Abil4");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		runRoundRobin("Feat|NORMAL|Abil1|Abil2", "Feat|VIRTUAL|Abil3|Abil4");
	}

	@Test
	public void testRoundRobinTwoCategory() throws PersistenceLayerException
	{
		Ability ab = primaryContext.ref.constructCDOMObject(
				Ability.class, "Abil1");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class,
				"Abil1");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = primaryContext.ref.constructCDOMObject(Ability.class, "Abil2");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class,
				"Abil2");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		AbilityCategory ac = new AbilityCategory("NEWCAT");
		SettingsHandler.getGame().addAbilityCategory(ac);
		ab = primaryContext.ref.constructCDOMObject(Ability.class, "Abil3");
		primaryContext.ref.reassociateCategory(ac, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class,
				"Abil3");
		secondaryContext.ref.reassociateCategory(ac, ab);
		ab = primaryContext.ref.constructCDOMObject(Ability.class, "Abil4");
		primaryContext.ref.reassociateCategory(ac, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class,
				"Abil4");
		secondaryContext.ref.reassociateCategory(ac, ab);
		runRoundRobin("Feat|VIRTUAL|Abil1|Abil2", "NEWCAT|VIRTUAL|Abil3|Abil4");
	}

	@Test
	public void testRoundRobinDupe() throws PersistenceLayerException
	{
		Ability ab = primaryContext.ref.constructCDOMObject(
				Ability.class, "Abil1");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class,
				"Abil1");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		runRoundRobin("Feat|VIRTUAL|Abil1|Abil1");
	}

	@Test
	public void testRoundRobinDupeDiffNature() throws PersistenceLayerException
	{
		Ability ab = primaryContext.ref.constructCDOMObject(
				Ability.class, "Abil1");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class,
				"Abil1");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		runRoundRobin("Feat|NORMAL|Abil1", "Feat|VIRTUAL|Abil1");
	}

	@Test
	public void testRoundRobinDupeOnePrereq() throws PersistenceLayerException
	{
		Ability ab = primaryContext.ref.constructCDOMObject(
				Ability.class, "Abil1");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class,
				"Abil1");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		runRoundRobin("Feat|VIRTUAL|Abil1|Abil1|PRERACE:1,Human");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinDupeDiffPrereqs()
			throws PersistenceLayerException
	{
		Ability ab = primaryContext.ref.constructCDOMObject(
				Ability.class, "Abil1");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class,
				"Abil1");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		runRoundRobin("Feat|VIRTUAL|Abil1",
				"Feat|VIRTUAL|Abil1|PRERACE:1,Human");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinDupeTwoDiffPrereqs()
			throws PersistenceLayerException
	{
		Ability ab = primaryContext.ref.constructCDOMObject(
				Ability.class, "Abil1");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class,
				"Abil1");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, ab);
		runRoundRobin("Feat|VIRTUAL|Abil1|Abil1|PRERACE:1,Elf",
				"Feat|VIRTUAL|Abil1|PRERACE:1,Human");
		assertTrue(primaryContext.ref.validate());
		assertTrue(secondaryContext.ref.validate());
	}
}