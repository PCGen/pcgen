/*
 * Copyright 2008 (C) James Dempsey
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.PCTemplate;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

/**
 * The Class {@code SpellknownLstTest} is responsible for testing the
 * function of the spellknownlst class.
 * 
 * 
 */
public class SpellknownLstTest extends AbstractGlobalTokenTestCase
{

	static CDOMPrimaryToken<CDOMObject> token = new SpellknownLst();
	static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

	/**
	 * @see plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase#getLoader()
	 */
	@Override
	public CDOMLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	/**
	 * @see plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase#getCDOMClass()
	 */
	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	/**
	 * @see plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase#getToken()
	 */
	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

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
		TokenRegistration.register(prerace);
		TokenRegistration.register(preclasswriter);
		TokenRegistration.register(preracewriter);
	}

	/**
	 * Test invalid empty.
	 * 
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	/**
	 * Round robin test of a single spell added to a spell list.
	 * 
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	public void testRoundRobinSingleSpell() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Bless");
		secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Bless");
		primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.getReferenceContext()
				.constructCDOMObject(ClassSpellList.class, "Wizard");
		runRoundRobin("CLASS|Wizard=3|Bless");
	}

	@Test
	public void testInvalidDoublePipe()
	{
		assertFalse(parse("CLASS||Cleric=1|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoSpell()
	{
		assertFalse(parse("CLASS|Cleric=1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoLevel()
	{
		assertFalse(parse("CLASS|Cleric=|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidClassOnly()
	{
		assertFalse(parse("CLASS|Cleric|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidPrefix()
	{
		assertFalse(parse("SKILL|Cleric=2|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoPrefix()
	{
		assertFalse(parse("|Cleric=2|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoClass()
	{
		assertFalse(parse("CLASS|=2|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidOnlyPre1()
	{
		assertFalse(parse("PRECLASS:1,Fighter"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidOnlyPre2()
	{
		assertFalse(parse("CLASS|PRECLASS:1,Fighter"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidOnlyPre3()
	{
		assertFalse(parse("CLASS|Cleric=2|PRECLASS:1,Fighter"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadCasterComma1()
	{
		assertFalse(parse("CLASS|,Cleric=2|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadCasterComma2()
	{
		assertFalse(parse("CLASS|Cleric,=2|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadCasterComma3()
	{
		assertFalse(parse("CLASS|Cleric,,Druid=2|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadCasterComma4()
	{
		assertFalse(parse("CLASS|Druid=2,|Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadComma1()
	{
		assertFalse(parse("CLASS|Cleric=2|,Fireball"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadComma2()
	{
		assertFalse(parse("CLASS|Cleric=2|Fireball,"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidBadComma3()
	{
		assertFalse(parse("CLASS|Cleric=2|Fireball,,Lightning Bolt"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinClass() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
		primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Cleric");
		secondaryContext.getReferenceContext()
				.constructCDOMObject(ClassSpellList.class, "Cleric");
		runRoundRobin("CLASS|Cleric=2|Fireball|PRECLASS:1,Fighter=2");
	}

	@Test
	public void testRoundRobinSpellCaster() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
		ClassSpellList a = primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Wizard");
		a.addType(Type.getConstant("Arcane"));
		ClassSpellList b = secondaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Wizard");
		b.addType(Type.getConstant("Arcane"));
		runRoundRobin("CLASS|SPELLCASTER.Arcane=2|Fireball|PRECLASS:1,Fighter=2");
	}

	@Test
	public void testInvalidDomain()
	{
		primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
		primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Lightning Bolt");
		secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Lightning Bolt");
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		assertFalse(parse("DOMAIN|Fire=2|Fireball,Lightning Bolt|PRECLASS:1,Fighter=2"));
		assertNoSideEffects();
	}

	@Override
	protected String getLegalValue()
	{
		return "CLASS|Cleric=2|Lightning Bolt";
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "CLASS|SPELLCASTER.Arcane=2|Fireball|PRECLASS:1,Fighter=2";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.SEPARATE;
	}
}
