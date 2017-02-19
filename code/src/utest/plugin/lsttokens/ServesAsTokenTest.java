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
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class ServesAsTokenTest extends AbstractGlobalTokenTestCase
{

	static CDOMPrimaryToken<CDOMObject> token = new ServesAsToken();
	static CDOMTokenLoader<Skill> loader = new CDOMTokenLoader<>();

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
	}

	@Override
	public CDOMLoader<Skill> getLoader()
	{
		return loader;
	}

	@Override
	public Class<Skill> getCDOMClass()
	{
		return Skill.class;
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
				"Fireball").passed());
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
		assertFalse(parse("SKILL"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTypeBarOnly() throws PersistenceLayerException
	{
		assertFalse(parse("SKILL|"));
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
		assertFalse(parse("SKILL=Arcane|Fireball"));
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
		assertFalse(parse("SKILL|Fireball|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidSpellBarStarting() throws PersistenceLayerException
	{
		assertFalse(parse("SKILL||Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidWrongType() throws PersistenceLayerException
	{
		assertFalse(parse("SPELL|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinJustSkill() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(Skill.class, "Fireball");
		secondaryContext.getReferenceContext().constructCDOMObject(Skill.class, "Fireball");
		runRoundRobin("SKILL|Fireball");
	}

	@Test
	public void testRoundRobinJustAbility() throws PersistenceLayerException
	{
		primaryProf = new Ability();
		secondaryProf = new Ability();
		AbilityCategory pac = primaryContext.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "NEWCAT");
		AbilityCategory sac = secondaryContext.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "NEWCAT");
		Ability ab = primaryContext.getReferenceContext().constructCDOMObject(Ability.class, "Abil3");
		primaryContext.getReferenceContext().reassociateCategory(pac, ab);
		ab = secondaryContext.getReferenceContext().constructCDOMObject(Ability.class,
				"Abil3");
		secondaryContext.getReferenceContext().reassociateCategory(sac, ab);
		runRoundRobin("ABILITY=NEWCAT|Abil3");
	}


	// @Test
	// public void testRoundRobinJustSubClass() throws PersistenceLayerException
	// {
	// primaryProf = new SubClass();
	// secondaryProf = new SubClass();
	// primaryContext.ref.constructCDOMObject(PCClass.class, "Fireball");
	// secondaryContext.ref.constructCDOMObject(PCClass.class, "Fireball");
	// runRoundRobin("CLASS|Fireball");
	//	}

	@Test
	public void testRoundRobinTwoSpell() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(Skill.class, "Fireball");
		secondaryContext.getReferenceContext().constructCDOMObject(Skill.class, "Fireball");
		primaryContext.getReferenceContext().constructCDOMObject(Skill.class,
				"Lightning Bolt");
		secondaryContext.getReferenceContext().constructCDOMObject(Skill.class,
				"Lightning Bolt");
		runRoundRobin("SKILL|Fireball|Lightning Bolt");
	}

	@Override
	protected String getLegalValue()
	{
		return "SKILL|Jump";
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "SKILL|Fireball|Lightning Bolt";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return strings -> new String[] { "SKILL|Fireball|Jump|Lightning Bolt" };
	}
}