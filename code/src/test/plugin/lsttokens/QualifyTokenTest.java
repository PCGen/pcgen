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

import static org.junit.jupiter.api.Assertions.assertFalse;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.EquipmentModifier;
import pcgen.core.PCTemplate;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

class QualifyTokenTest extends AbstractGlobalTokenTestCase
{

	static CDOMPrimaryToken<CDOMObject> token = new QualifyToken();
	static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

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
	public CDOMPrimaryToken<CDOMObject> getReadToken()
	{
		return token;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getWriteToken()
	{
		return token;
	}

	@Test
	void testInvalidObject()
	{
		assertFalse(token.parseToken(primaryContext, new EquipmentModifier(),
				"SPELL|Fireball").passed());
	}

	@Test
	void testInvalidEmpty()
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	void testInvalidTypeOnly()
	{
		assertFalse(parse("SPELL"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidTypeBarOnly()
	{
		assertFalse(parse("SPELL|"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidEmptyType()
	{
		assertFalse(parse("|Fireball"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidBadType()
	{
		assertFalse(parse("CAMPAIGN|Fireball"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidCatTypeNoEqual()
	{
		assertFalse(parse("ABILITY|Abil"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidNonCatTypeEquals()
	{
		assertFalse(parse("SPELL=Arcane|Fireball"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidDoubleEquals()
	{
		assertFalse(parse("ABILITY=FEAT=Mutation|Fireball"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidUnbuiltCategory()
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
	void testInvalidSpellbookAndSpellBarOnly()
	{
		assertFalse(parse("SPELL|Fireball|"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidSpellBarStarting()
	{
		assertFalse(parse("SPELL||Fireball"));
		assertNoSideEffects();
	}

	@Test
	void testRoundRobinJustSpell() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
		runRoundRobin("SPELL|Fireball");
	}

	@Test
	void testRoundRobinJustAbility() throws PersistenceLayerException
	{
		AbilityCategory pac = primaryContext.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "NEWCAT");
		AbilityCategory sac = secondaryContext.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "NEWCAT");
		BuildUtilities.buildAbility(primaryContext, pac, "Abil3");
		BuildUtilities.buildAbility(secondaryContext, sac, "Abil3");
		runRoundRobin("ABILITY=NEWCAT|Abil3");
	}

	@Test
	void testRoundRobinTwoSpell() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
		primaryContext.getReferenceContext().constructCDOMObject(Spell.class,
				"Lightning Bolt");
		secondaryContext.getReferenceContext().constructCDOMObject(Spell.class,
				"Lightning Bolt");
		runRoundRobin("SPELL|Fireball|Lightning Bolt");
	}

	@Test
	void testRoundRobinAbilitySpell()
			throws PersistenceLayerException
	{
		AbilityCategory pac = primaryContext.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "NEWCAT");
		AbilityCategory sac = secondaryContext.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "NEWCAT");
		BuildUtilities.buildAbility(primaryContext, pac, "Abil3");
		BuildUtilities.buildAbility(secondaryContext, sac, "Abil3");
		primaryContext.getReferenceContext().constructCDOMObject(Spell.class,
				"Lightning Bolt");
		secondaryContext.getReferenceContext().constructCDOMObject(Spell.class,
				"Lightning Bolt");
		runRoundRobin("ABILITY=NEWCAT|Abil3", "SPELL|Lightning Bolt");
	}

	@Test
	void testRoundRobinFeatSpell()
			throws PersistenceLayerException
	{
		BuildUtilities.buildAbility(primaryContext, BuildUtilities.getFeatCat(), "My Feat");
		BuildUtilities.buildAbility(secondaryContext, BuildUtilities.getFeatCat(), "My Feat");
		primaryContext.getReferenceContext().constructCDOMObject(Spell.class,
				"Lightning Bolt");
		secondaryContext.getReferenceContext().constructCDOMObject(Spell.class,
				"Lightning Bolt");
		runRoundRobin("ABILITY=FEAT|My Feat", "SPELL|Lightning Bolt");
	}

	@Override
	protected String getLegalValue()
	{
		return "ABILITY=FEAT|My Feat";
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

	@Override
	protected void additionalSetup(LoadContext context)
	{
		super.additionalSetup(context);
		Ability a = BuildUtilities.getFeatCat().newInstance();
		a.setName("Dummy");
		context.getReferenceContext().importObject(a);
	}
	
	
}
