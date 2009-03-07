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
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
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
	public void testInvalidClearDotPre() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT|NORMAL|.CLEAR.Abil1|PRELEVEL:MIN=4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidClearPre() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT|NORMAL|.CLEAR|PRELEVEL:MIN=4"));
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
	public void testInvalidAnyNature() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT|ANY|Abil1"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinJustSpell() throws PersistenceLayerException
	{
		construct(primaryContext, "Abil1");
		construct(secondaryContext, "Abil1");
		runRoundRobin("Feat|NORMAL|Abil1");
	}

	@Test
	public void testRoundRobinJustTwoPrereq() throws PersistenceLayerException
	{
		construct(primaryContext, "Abil1");
		construct(secondaryContext, "Abil1");
		runRoundRobin("Feat|NORMAL|Abil1|PRELEVEL:MIN=5|PRERACE:1,Human");
	}

	@Test
	public void testRoundRobinTwoSpell() throws PersistenceLayerException
	{
		construct(primaryContext, "Abil1");
		construct(secondaryContext, "Abil1");
		construct(primaryContext, "Abil2");
		construct(secondaryContext, "Abil2");
		runRoundRobin("Feat|NORMAL|Abil1|Abil2");
	}

	@Test
	public void testRoundRobinTwoNature() throws PersistenceLayerException
	{
		construct(primaryContext, "Abil1");
		construct(secondaryContext, "Abil1");
		construct(primaryContext, "Abil2");
		construct(secondaryContext, "Abil2");
		construct(primaryContext, "Abil3");
		construct(secondaryContext, "Abil3");
		construct(primaryContext, "Abil4");
		construct(secondaryContext, "Abil4");
		runRoundRobin("Feat|NORMAL|Abil1|Abil2", "Feat|VIRTUAL|Abil3|Abil4");
	}

	@Test
	public void testRoundRobinTwoCategory() throws PersistenceLayerException
	{
		construct(primaryContext, "Abil1");
		construct(secondaryContext, "Abil1");
		construct(primaryContext, "Abil2");
		construct(secondaryContext, "Abil2");
		AbilityCategory ac = new AbilityCategory("NEWCAT");
		SettingsHandler.getGame().addAbilityCategory(ac);
		Ability ab = primaryContext.ref.constructCDOMObject(Ability.class, "Abil3");
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
		construct(primaryContext, "Abil1");
		construct(secondaryContext, "Abil1");
		runRoundRobin("Feat|VIRTUAL|Abil1|Abil1");
	}

	@Test
	public void testRoundRobinDupeDiffNature() throws PersistenceLayerException
	{
		construct(primaryContext, "Abil1");
		construct(secondaryContext, "Abil1");
		runRoundRobin("Feat|NORMAL|Abil1", "Feat|VIRTUAL|Abil1");
	}

	@Test
	public void testRoundRobinDupeOnePrereq() throws PersistenceLayerException
	{
		construct(primaryContext, "Abil1");
		construct(secondaryContext, "Abil1");
		runRoundRobin("Feat|VIRTUAL|Abil1|Abil1|PRERACE:1,Human");
		assertTrue(primaryContext.ref.validate(null));
		assertTrue(secondaryContext.ref.validate(null));
	}

	@Test
	public void testRoundRobinDupeDiffPrereqs()
			throws PersistenceLayerException
	{
		construct(primaryContext, "Abil1");
		construct(secondaryContext, "Abil1");
		runRoundRobin("Feat|VIRTUAL|Abil1",
				"Feat|VIRTUAL|Abil1|PRERACE:1,Human");
		assertTrue(primaryContext.ref.validate(null));
		assertTrue(secondaryContext.ref.validate(null));
	}

	@Test
	public void testRoundRobinDupeTwoDiffPrereqs()
			throws PersistenceLayerException
	{
		construct(primaryContext, "Abil1");
		construct(secondaryContext, "Abil1");
		runRoundRobin("Feat|VIRTUAL|Abil1|Abil1|PRERACE:1,Elf",
				"Feat|VIRTUAL|Abil1|PRERACE:1,Human");
		assertTrue(primaryContext.ref.validate(null));
		assertTrue(secondaryContext.ref.validate(null));
	}

	private void construct(LoadContext context, String name)
	{
		Ability ab = context.ref.constructCDOMObject(Ability.class, name);
		context.ref.reassociateCategory(AbilityCategory.FEAT, ab);
	}

	@Test
	public void testRoundRobinOneParen() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("Feat|VIRTUAL|TestWP1 (Paren)");
	}

	@Test
	public void testRoundRobinTwoParen() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("Feat|VIRTUAL|TestWP1 (Paren)|TestWP2 (Other)");
	}

	@Test
	public void testRoundRobinDupeParen() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("Feat|VIRTUAL|TestWP1 (Other)|TestWP1 (That)");
	}

	@Test
	public void testInputInvalidAddsTypeNoSideEffect()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP3");
		assertTrue(parse("Feat|VIRTUAL|TestWP1|TestWP2"));
		assertTrue(parseSecondary("Feat|VIRTUAL|TestWP1|TestWP2"));
		assertFalse(parse("Feat|VIRTUAL|TestWP3|TYPE="));
		assertNoSideEffects();
	}

	@Test
	public void testInputInvalidTypeClearDotNoSideEffect()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP3");
		assertTrue(parse("Feat|VIRTUAL|TestWP1|TestWP2"));
		assertTrue(parseSecondary("Feat|VIRTUAL|TestWP1|TestWP2"));
		assertFalse(parse("Feat|VIRTUAL|TestWP3|.CLEAR.TestWP1|.CLEAR.TYPE="));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinTestEquals() throws PersistenceLayerException
	{
		runRoundRobin("Feat|VIRTUAL|TYPE=TestType");
	}

	@Test
	public void testRoundRobinTestEqualThree() throws PersistenceLayerException
	{
		runRoundRobin("Feat|VIRTUAL|TYPE=TestAltType.TestThirdType.TestType");
	}

	@Test
	public void testRoundRobinWithEqualType() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("Feat|VIRTUAL|TestWP1|TestWP2|TYPE=OtherTestType|TYPE=TestType");
	}

	@Test
	public void testInvalidInputCheckTypeEqualLength()
			throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		// consume the |
		construct(primaryContext, "TestWP1");
		assertTrue(parse("Feat|VIRTUAL|TestWP1|TYPE=TestType|TestWP2"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidInputCheckTypeDotLength()
			throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		// consume the |
		construct(primaryContext, "TestWP1");
		assertTrue(parse("Feat|VIRTUAL|TestWP1|TYPE.TestType.OtherTestType|TestWP2"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidInputTypeEmpty() throws PersistenceLayerException
	{
		assertFalse(parse("Feat|VIRTUAL|TYPE="));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTypeUnterminated()
			throws PersistenceLayerException
	{
		assertFalse(parse("Feat|VIRTUAL|TYPE=One."));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTypeDoubleSeparator()
			throws PersistenceLayerException
	{
		assertFalse(parse("Feat|VIRTUAL|TYPE=One..Two"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTypeFalseStart()
			throws PersistenceLayerException
	{
		assertFalse(parse("Feat|VIRTUAL|TYPE=.One"));
		assertNoSideEffects();
	}

	@Override
	protected String getLegalValue()
	{
		return "Feat|VIRTUAL|Abil1|PRERACE:1,Human";
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "Feat|VIRTUAL|TYPE=TestType";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.SEPARATE;
	}
}