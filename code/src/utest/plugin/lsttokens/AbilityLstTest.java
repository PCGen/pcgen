/*
 * Copyright (c) 2007-12 Tom Parker <thpr@users.sourceforge.net>
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
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreLevelParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreLevelWriter;
import plugin.pretokens.writer.PreRaceWriter;

public class AbilityLstTest extends AbstractGlobalTokenTestCase
{

	static CDOMPrimaryToken<CDOMObject> token = new AbilityLst();
	static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(new PreRaceParser());
		TokenRegistration.register(new PreRaceWriter());
		TokenRegistration.register(new PreLevelParser());
		TokenRegistration.register(new PreLevelWriter());
		TokenRegistration.register(new PreClassParser());
		TokenRegistration.register(new PreClassWriter());
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
	public void testInvalidListPre() throws PersistenceLayerException
	{
		assertFalse(parse("FEAT|AUTOMATIC|%LIST|PRERACE:1,Human"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinJustSpell() throws PersistenceLayerException
	{
		construct(primaryContext, "Abil1");
		construct(secondaryContext, "Abil1");
		runRoundRobin("FEAT|NORMAL|Abil1");
	}

	@Test
	public void testRoundRobinJustTwoPrereq() throws PersistenceLayerException
	{
		construct(primaryContext, "Abil1");
		construct(secondaryContext, "Abil1");
		runRoundRobin("FEAT|NORMAL|Abil1|PRELEVEL:MIN=5|PRERACE:1,Human");
	}

	@Test
	public void testRoundRobinTwoSpell() throws PersistenceLayerException
	{
		construct(primaryContext, "Abil1");
		construct(secondaryContext, "Abil1");
		construct(primaryContext, "Abil2");
		construct(secondaryContext, "Abil2");
		runRoundRobin("FEAT|NORMAL|Abil1|Abil2");
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
		runRoundRobin("FEAT|NORMAL|Abil1|Abil2", "FEAT|VIRTUAL|Abil3|Abil4");
	}

	@Test
	public void testRoundRobinTwoCategory() throws PersistenceLayerException
	{
		construct(primaryContext, "Abil1");
		construct(secondaryContext, "Abil1");
		construct(primaryContext, "Abil2");
		construct(secondaryContext, "Abil2");
		AbilityCategory pac = primaryContext.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "NEWCAT");
		AbilityCategory sac = secondaryContext.getReferenceContext().constructCDOMObject(
				AbilityCategory.class, "NEWCAT");
		Ability ab = primaryContext.getReferenceContext().constructCDOMObject(Ability.class, "Abil3");
		primaryContext.getReferenceContext().reassociateCategory(pac, ab);
		ab = secondaryContext.getReferenceContext().constructCDOMObject(Ability.class,
				"Abil3");
		secondaryContext.getReferenceContext().reassociateCategory(sac, ab);
		ab = primaryContext.getReferenceContext().constructCDOMObject(Ability.class, "Abil4");
		primaryContext.getReferenceContext().reassociateCategory(pac, ab);
		ab = secondaryContext.getReferenceContext().constructCDOMObject(Ability.class,
				"Abil4");
		secondaryContext.getReferenceContext().reassociateCategory(sac, ab);
		runRoundRobin("FEAT|VIRTUAL|Abil1|Abil2", "NEWCAT|VIRTUAL|Abil3|Abil4");
	}

	@Test
	public void testRoundRobinDupe() throws PersistenceLayerException
	{
		construct(primaryContext, "Abil1");
		construct(secondaryContext, "Abil1");
		runRoundRobin("FEAT|VIRTUAL|Abil1|Abil1");
	}

	@Test
	public void testRoundRobinList() throws PersistenceLayerException
	{
		runRoundRobin("FEAT|VIRTUAL|%LIST");
	}

	@Test
	public void testRoundRobinDupeDiffNature() throws PersistenceLayerException
	{
		construct(primaryContext, "Abil1");
		construct(secondaryContext, "Abil1");
		runRoundRobin("FEAT|NORMAL|Abil1", "FEAT|VIRTUAL|Abil1");
	}

	@Test
	public void testRoundRobinDupeOnePrereq() throws PersistenceLayerException
	{
		construct(primaryContext, "Abil1");
		construct(secondaryContext, "Abil1");
		runRoundRobin("FEAT|VIRTUAL|Abil1|Abil1|PRERACE:1,Human");
	}

	@Test
	public void testRoundRobinDupeDiffPrereqs()
			throws PersistenceLayerException
	{
		construct(primaryContext, "Abil1");
		construct(secondaryContext, "Abil1");
		runRoundRobin("FEAT|VIRTUAL|Abil1",
				"FEAT|VIRTUAL|Abil1|PRERACE:1,Human");
	}

	@Test
	public void testRoundRobinDupeTwoDiffPrereqs()
			throws PersistenceLayerException
	{
		construct(primaryContext, "Abil1");
		construct(secondaryContext, "Abil1");
		runRoundRobin("FEAT|VIRTUAL|Abil1|Abil1|PRERACE:1,Elf",
				"FEAT|VIRTUAL|Abil1|PRERACE:1,Human");
	}

	@Test
	public void testRoundRobinListPrereq()
			throws PersistenceLayerException
	{
		construct(primaryContext, "Improved Critical");
		construct(secondaryContext, "Improved Critical");
		runRoundRobin("FEAT|AUTOMATIC|Improved Critical(%LIST)|PRECLASS:1,Oracle=8");
	}

	//  
	private static Ability construct(LoadContext context, String name)
	{
		Ability ab = context.getReferenceContext().constructCDOMObject(Ability.class, name);
		context.getReferenceContext().reassociateCategory(AbilityCategory.FEAT, ab);
		return ab;
	}

	@Test
	public void testRoundRobinOneParen() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("FEAT|VIRTUAL|TestWP1 (Paren)");
	}

	@Test
	public void testRoundRobinTwoParen() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("FEAT|VIRTUAL|TestWP1 (Paren)|TestWP2 (Other)");
	}

	@Test
	public void testRoundRobinDupeParen() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("FEAT|VIRTUAL|TestWP1 (Other)|TestWP1 (That)");
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
		Ability a = construct(primaryContext, "TestWP1");
		a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
		Ability b = construct(secondaryContext, "TestWP1");
		b.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
		runRoundRobin("FEAT|VIRTUAL|TYPE=TestType");
	}

	@Test
	public void testRoundRobinTestEqualThree() throws PersistenceLayerException
	{
		Ability a = construct(primaryContext, "TestWP1");
		a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
		a.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
		a.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
		Ability b = construct(secondaryContext, "TestWP1");
		b.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
		b.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
		b.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
		runRoundRobin("FEAT|VIRTUAL|TYPE=TestAltType.TestThirdType.TestType");
	}

	@Test
	public void testRoundRobinWithEqualType() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		Ability a = construct(primaryContext, "Typed1");
		a.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
		Ability b = construct(secondaryContext, "Typed1");
		b.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
		Ability c = construct(primaryContext, "Typed2");
		c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
		Ability d = construct(secondaryContext, "Typed2");
		d.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
		runRoundRobin("FEAT|VIRTUAL|TestWP1|TestWP2|TYPE=OtherTestType|TYPE=TestType");
	}

	@Test
	public void testInvalidInputCheckTypeEqualLength()
			throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		// consume the |
		construct(primaryContext, "TestWP1");
		assertTrue(parse("Feat|VIRTUAL|TestWP1|TYPE=TestType|TestWP2"));
		assertConstructionError();
	}

	@Test
	public void testInvalidInputCheckTypeDotLength()
			throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		// consume the |
		construct(primaryContext, "TestWP1");
		assertTrue(parse("Feat|VIRTUAL|TestWP1|TYPE.TestType.OtherTestType|TestWP2"));
		assertConstructionError();
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

	@Test
	public void testRoundRobinListParen() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("FEAT|VIRTUAL|TestWP1 (%LIST)");
	}

	@Override
	protected String getLegalValue()
	{
		return "FEAT|VIRTUAL|Abil1|PRERACE:1,Human";
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "FEAT|VIRTUAL|TYPE=TestType";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.SEPARATE;
	}

	@Test
	public void testValidInputClearWorking() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		assertTrue(parse("FEAT|VIRTUAL|TestWP1"));
		assertTrue(parse("FEAT|VIRTUAL|" + getClearString()));
		assertNoSideEffects();
	}

	@Test
	public void testValidInputClearJoinWorking()
		throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		assertTrue(parse("FEAT|VIRTUAL|" + getClearString()
			+ getJoinCharacter() + "TestWP1"));
		assertTrue(parseSecondary("FEAT|VIRTUAL|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testListTargetClearWorking() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		assertTrue(parse("FEAT|VIRTUAL|TestWP1(%LIST)"));
		assertTrue(parse("FEAT|VIRTUAL|" + getClearString()));
		assertNoSideEffects();
	}

	@Test
	public void testClearMixedWorking() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP2");
		assertTrue(parse("FEAT|VIRTUAL|TestWP2|TestWP1(%LIST)"));
		assertTrue(parse("FEAT|VIRTUAL|" + getClearString()));
		assertNoSideEffects();
	}

	private static String getJoinCharacter()
	{
		return Constants.PIPE;
	}

	protected String getClearString()
	{
		return Constants.LST_DOT_CLEAR;
	}
}
