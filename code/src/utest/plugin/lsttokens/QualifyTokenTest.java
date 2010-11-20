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
import pcgen.core.EquipmentModifier;
import pcgen.core.PCTemplate;
import pcgen.core.SettingsHandler;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class QualifyTokenTest extends AbstractGlobalTokenTestCase
{

	static CDOMPrimaryToken<CDOMObject> token = new QualifyToken();
	static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<PCTemplate>(
			PCTemplate.class);

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
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
	public void testInvalidObject() throws PersistenceLayerException
	{
		assertFalse(token.parseToken(primaryContext, new EquipmentModifier(),
				"SPELL|Fireball").passed());
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
	public void testInvalidUnbuiltCategory() throws PersistenceLayerException
	{
		try
		{
			assertFalse(parse("ABILITY=Crazy|Fireball"));
		}
		catch (IllegalArgumentException e)
		{
			//OK as well
		}
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
		AbilityCategory ac = new AbilityCategory("NEWCAT");
		SettingsHandler.getGame().addAbilityCategory(ac);
		Ability ab = primaryContext.ref.constructCDOMObject(Ability.class, "Abil3");
		primaryContext.ref.reassociateCategory(ac, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class,
				"Abil3");
		secondaryContext.ref.reassociateCategory(ac, ab);
		runRoundRobin("ABILITY=NEWCAT|Abil3");
	}

	@Test
	public void testRoundRobinTwoSpell() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Spell.class, "Fireball");
		primaryContext.ref.constructCDOMObject(Spell.class,
				"Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(Spell.class,
				"Lightning Bolt");
		runRoundRobin("SPELL|Fireball|Lightning Bolt");
	}

	@Test
	public void testRoundRobinAbilitySpell()
			throws PersistenceLayerException
	{
		AbilityCategory ac = new AbilityCategory("NEWCAT");
		SettingsHandler.getGame().addAbilityCategory(ac);
		Ability ab = primaryContext.ref.constructCDOMObject(Ability.class, "Abil3");
		primaryContext.ref.reassociateCategory(ac, ab);
		ab = secondaryContext.ref.constructCDOMObject(Ability.class,
				"Abil3");
		secondaryContext.ref.reassociateCategory(ac, ab);
		primaryContext.ref.constructCDOMObject(Spell.class,
				"Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(Spell.class,
				"Lightning Bolt");
		runRoundRobin("ABILITY=NEWCAT|Abil3", "SPELL|Lightning Bolt");
	}

	@Test
	public void testRoundRobinFeatSpell()
			throws PersistenceLayerException
	{
		Ability a = primaryContext.ref.constructCDOMObject(
				Ability.class, "My Feat");
		primaryContext.ref.reassociateCategory(AbilityCategory.FEAT, a);
		a = secondaryContext.ref.constructCDOMObject(Ability.class,
				"My Feat");
		secondaryContext.ref.reassociateCategory(AbilityCategory.FEAT, a);
		primaryContext.ref.constructCDOMObject(Spell.class,
				"Lightning Bolt");
		secondaryContext.ref.constructCDOMObject(Spell.class,
				"Lightning Bolt");
		runRoundRobin("FEAT|My Feat", "SPELL|Lightning Bolt");
	}

	@Override
	protected String getLegalValue()
	{
		return "FEAT|My Feat";
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "SPELL|Lightning Bolt";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.SEPARATE;
	}
}