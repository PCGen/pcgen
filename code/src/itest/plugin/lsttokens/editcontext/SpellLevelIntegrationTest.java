/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.editcontext;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.SpelllevelLst;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

public class SpellLevelIntegrationTest extends
		AbstractIntegrationTestCase<CDOMObject>
{
	static SpelllevelLst token = new SpelllevelLst();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

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

	@Override
	public Class<Ability> getCDOMClass()
	{
		return Ability.class;
	}

	@Override
	public CDOMLoader<CDOMObject> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
		primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Cleric");
		secondaryContext.getReferenceContext()
				.constructCDOMObject(ClassSpellList.class, "Cleric");
		primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Bless");
		secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Bless");
		primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.getReferenceContext()
				.constructCDOMObject(ClassSpellList.class, "Wizard");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc, "CLASS|Wizard=3|Bless");
		commit(modCampaign, tc, "CLASS|Cleric=2|Fireball|PRECLASS:1,Fighter=2");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoSet() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
		verifyCleanStart();
		TestContext tc = new TestContext();
		emptyCommit(testCampaign, tc);
		commit(modCampaign, tc,
				"CLASS|SPELLCASTER.Arcane=2|Fireball|PRECLASS:1,Fighter=2");
		completeRoundRobin(tc);
	}

	@Test
	public void testRoundRobinNoReset() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
		secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Fireball");
		primaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Lightning Bolt");
		secondaryContext.getReferenceContext().constructCDOMObject(Spell.class, "Lightning Bolt");
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		verifyCleanStart();
		TestContext tc = new TestContext();
		commit(testCampaign, tc,
				"DOMAIN|Fire=2|Fireball,Lightning Bolt|PRECLASS:1,Fighter=2");
		emptyCommit(modCampaign, tc);
		completeRoundRobin(tc);
	}

	@Override
	protected Ability construct(LoadContext context, String name)
	{
		Ability a = AbilityCategory.FEAT.newInstance();
		a.setName(name);
		context.getReferenceContext().importObject(a);
		return a;
	}
}
