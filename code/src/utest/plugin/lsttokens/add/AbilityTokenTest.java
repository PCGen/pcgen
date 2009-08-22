/*
 * 
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.add;

import java.net.URISyntaxException;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.AddLst;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;

public class AbilityTokenTest extends AbstractTokenTestCase<CDOMObject>
{

	static AddLst token = new AddLst();
	static AbilityToken subtoken = new AbilityToken();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<CDOMObject>(
			CDOMObject.class);

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(getSubToken());
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
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

	public CDOMSecondaryToken<?> getSubToken()
	{
		return subtoken;
	}

	public Class<Ability> getTargetClass()
	{
		return Ability.class;
	}

	public boolean isAllLegal()
	{
		return true;
	}

	public boolean isTypeLegal()
	{
		return true;
	}

	public boolean allowsParenAsSub()
	{
		return true;
	}

	protected void construct(LoadContext loadContext, String one)
	{
		Ability obj = loadContext.ref.constructCDOMObject(Ability.class, one);
		loadContext.ref.reassociateCategory(AbilityCategory.FEAT, obj);
	}

	public String getSubTokenName()
	{
		return getSubToken().getTokenName();
	}

	public char getJoinCharacter()
	{
		return ',';
	}

	@Test
	public void testInvalidInputEmptyString() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOnlySubToken() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOnlySubTokenPipe()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|'));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputJoinOnly() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|'
				+ Character.toString(getJoinCharacter())));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputStringOnlyCat()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "FEAT"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputStringOnlyCatPipe()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "FEAT" + '|'));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputStringOnlyCatNature()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "FEAT" + '|' + "NORMAL"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNature() throws PersistenceLayerException
	{
		try
		{
			assertFalse(parse(getSubTokenName() + '|' + "FEAT" + '|' + "NORM"
					+ '|' + "FeatName"));
			assertNoSideEffects();
		}
		catch (IllegalArgumentException e)
		{
			// This is okay too
		}
	}

	@Test
	public void testInvalidCategory() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "InvalidCat" + '|'
				+ "NORMAL" + '|' + "FeatName"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputUnconstructed()
			throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenName() + '|' + "FEAT" + '|' + "NORMAL"
				+ '|' + "TestType"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidInputJoinedDot() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse(getSubTokenName() + '|' + "FEAT" + '|' + "NORMAL"
				+ '|' + "TestWP1.TestWP2"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidInputNegativeFormula()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "-1|FEAT|NORMAL|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputZeroFormula() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "0|FEAT|NORMAL|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputAnyNature() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "FEAT|ANY|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTypeEmpty() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TYPE="));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputTypeUnterminated()
			throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TYPE=One."));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputStacksNaN() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|'
				+ "FEAT|NORMAL|STACKS=x,TestWP1" + getJoinCharacter()
				+ "TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOnlyStacks() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "FEAT|NORMAL|STACKS=4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputMultTarget() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|'
				+ "FEAT|NORMAL|TestWP1(Foo,Bar)" + getJoinCharacter()
				+ "TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputClearDotTypeDoubleSeparator()
			throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(parse(getSubTokenName() + '|'
					+ "FEAT|NORMAL|TYPE=One..Two"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputClearDotTypeFalseStart()
			throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TYPE=.One"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputAll() throws PersistenceLayerException
	{
		if (!isAllLegal())
		{
			try
			{
				boolean parse = parse(getSubTokenName() + '|'
						+ "FEAT|NORMAL|ALL");
				if (parse)
				{
					// Only need to check if parsed as true
					assertFalse(primaryContext.ref.validate(null));
				}
				else
				{
					assertNoSideEffects();
				}
			}
			catch (IllegalArgumentException e)
			{
				// This is okay too
				assertNoSideEffects();
			}
		}
	}

	@Test
	public void testInvalidInputTypeEquals() throws PersistenceLayerException
	{
		if (!isTypeLegal())
		{
			try
			{
				boolean parse = parse(getSubTokenName() + '|'
						+ "FEAT|NORMAL|TYPE=Foo");
				if (parse)
				{
					// Only need to check if parsed as true
					assertFalse(primaryContext.ref.validate(null));
				}
				else
				{
					assertNoSideEffects();
				}
			}
			catch (IllegalArgumentException e)
			{
				// This is okay too
				assertNoSideEffects();
			}
		}
	}

	@Test
	public void testInvalidListEnd() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1"
				+ getJoinCharacter()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListStart() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "FEAT|NORMAL|"
				+ getJoinCharacter() + "TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP2"
				+ getJoinCharacter() + getJoinCharacter() + "TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputCheckMult() throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2
		construct(primaryContext, "TestWP1");
		assertTrue(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1"
				+ getJoinCharacter() + "TestWP2"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidInputCheckTypeEqualLength()
			throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		// consume the |
		if (isTypeLegal())
		{
			construct(primaryContext, "TestWP1");
			assertTrue(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1"
					+ getJoinCharacter() + "TYPE=TestType" + getJoinCharacter()
					+ "TestWP2"));
			assertFalse(primaryContext.ref.validate(null));
		}
	}

	@Test
	public void testInvalidInputCheckTypeDotLength()
			throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		// consume the |
		if (isTypeLegal())
		{
			construct(primaryContext, "TestWP1");
			assertTrue(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1"
					+ getJoinCharacter() + "TYPE.TestType.OtherTestType"
					+ getJoinCharacter() + "TestWP2"));
			assertFalse(primaryContext.ref.validate(null));
		}
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1"));
		assertTrue(primaryContext.ref.validate(null));
		assertTrue(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1"
				+ getJoinCharacter() + "TestWP2"));
		assertTrue(primaryContext.ref.validate(null));
		if (isTypeLegal())
		{
			assertTrue(parse(getSubTokenName() + '|'
					+ "FEAT|NORMAL|TYPE=TestType"));
			assertTrue(primaryContext.ref.validate(null));
			assertTrue(parse(getSubTokenName() + '|'
					+ "FEAT|NORMAL|TYPE.TestType"));
			assertTrue(primaryContext.ref.validate(null));
			assertTrue(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1"
					+ getJoinCharacter() + "TestWP2" + getJoinCharacter()
					+ "TYPE=TestType"));
			assertTrue(primaryContext.ref.validate(null));
			assertTrue(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1"
					+ getJoinCharacter() + "TestWP2" + getJoinCharacter()
					+ "TYPE=TestType.OtherTestType"));
			assertTrue(primaryContext.ref.validate(null));
		}
		if (isAllLegal())
		{
			assertTrue(parse(getSubTokenName() + '|' + "FEAT|NORMAL|ALL"));
			assertTrue(primaryContext.ref.validate(null));
		}
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1");
	}

	@Test
	public void testRoundRobinParen() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1 (Test)");
		construct(secondaryContext, "TestWP1 (Test)");
		runRoundRobin(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1 (Test)");
	}

	@Test
	public void testRoundRobinCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1 (Test)");
		construct(secondaryContext, "TestWP1 (Test)");
		runRoundRobin(getSubTokenName() + '|' + "4|FEAT|NORMAL|TestWP1 (Test)");
	}

	@Test
	public void testRoundRobinFormulaCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1 (Test)");
		construct(secondaryContext, "TestWP1 (Test)");
		runRoundRobin(getSubTokenName() + '|'
				+ "INT|FEAT|NORMAL|TestWP1 (Test)");
	}

	@Test
	public void testRoundRobinParenSub() throws PersistenceLayerException
	{
		if (allowsParenAsSub())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			runRoundRobin(getSubTokenName() + '|'
					+ "FEAT|NORMAL|TestWP1 (Test)");
		}
	}

	@Test
	public void testRoundRobinParenDoubleSub() throws PersistenceLayerException
	{
		if (allowsParenAsSub())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			runRoundRobin(getSubTokenName() + '|'
					+ "FEAT|NORMAL|TestWP1 (Test(Two))");
		}
	}

	@Test
	public void testRoundRobinThree() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1"
				+ getJoinCharacter() + "TestWP2" + getJoinCharacter()
				+ "TestWP3");
	}

	@Test
	public void testRoundRobinWithEqualType() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP1");
			construct(secondaryContext, "TestWP2");
			runRoundRobin(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1"
					+ getJoinCharacter() + "TestWP2" + getJoinCharacter()
					+ "TYPE=OtherTestType" + getJoinCharacter()
					+ "TYPE=TestType");
		}
	}

	@Test
	public void testRoundRobinTestEquals() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin(getSubTokenName() + '|' + "FEAT|NORMAL|TYPE=TestType");
		}
	}

	@Test
	public void testRoundRobinTestEqualThree() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin(getSubTokenName() + '|'
					+ "FEAT|NORMAL|TYPE=TestAltType.TestThirdType.TestType");
		}
	}

	@Test
	public void testInvalidInputAnyItem() throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + "FEAT|NORMAL|ALL"
					+ getJoinCharacter() + "TestWP1"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputItemAny() throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1"
					+ getJoinCharacter() + "ALL"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputAnyType() throws PersistenceLayerException
	{
		if (isTypeLegal() && isAllLegal())
		{
			assertFalse(parse(getSubTokenName() + '|' + "FEAT|NORMAL|ALL"
					+ getJoinCharacter() + "TYPE=TestType"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputTypeAny() throws PersistenceLayerException
	{
		if (isTypeLegal() && isAllLegal())
		{
			assertFalse(parse(getSubTokenName() + '|'
					+ "FEAT|NORMAL|TYPE=TestType" + getJoinCharacter() + "ALL"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInputInvalidAddsTypeNoSideEffect()
			throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP2");
			construct(primaryContext, "TestWP3");
			construct(secondaryContext, "TestWP3");
			assertTrue(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1"
					+ getJoinCharacter() + "TestWP2"));
			assertTrue(parseSecondary(getSubTokenName() + '|'
					+ "FEAT|NORMAL|TestWP1" + getJoinCharacter() + "TestWP2"));
			assertFalse(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP3"
					+ getJoinCharacter() + "TYPE="));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInputInvalidAddsBasicNoSideEffect()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP3");
		construct(primaryContext, "TestWP4");
		construct(secondaryContext, "TestWP4");
		assertTrue(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1"
				+ getJoinCharacter() + "TestWP2"));
		assertTrue(parseSecondary(getSubTokenName() + '|'
				+ "FEAT|NORMAL|TestWP1" + getJoinCharacter() + "TestWP2"));
		assertFalse(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP3"
				+ getJoinCharacter() + getJoinCharacter() + "TestWP4"));
		assertNoSideEffects();
	}

	@Test
	public void testInputInvalidAddsAllNoSideEffect()
			throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP2");
			construct(primaryContext, "TestWP3");
			construct(secondaryContext, "TestWP3");
			assertTrue(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1"
					+ getJoinCharacter() + "TestWP2"));
			assertTrue(parseSecondary(getSubTokenName() + '|'
					+ "FEAT|NORMAL|TestWP1" + getJoinCharacter() + "TestWP2"));
			assertFalse(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP3"
					+ getJoinCharacter() + "ALL"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testRoundRobinStacks() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin(getSubTokenName() + '|' + "FEAT|VIRTUAL|STACKS,TestWP1"
				+ getJoinCharacter() + "TestWP2" + getJoinCharacter()
				+ "TestWP3");
	}

	@Test
	public void testRoundRobinStacksValue() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin(getSubTokenName() + '|' + "FEAT|NORMAL|STACKS=5,TestWP1"
				+ getJoinCharacter() + "TestWP2" + getJoinCharacter()
				+ "TestWP3");
	}

	@Test
	public void testInvalidInputDoubleStacks() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|'
				+ "FEAT|NORMAL|STACKS,STACKS,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoubleStack() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|'
				+ "FEAT|NORMAL|STACKS=3,STACKS=2,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputStacksStack() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|'
				+ "FEAT|NORMAL|STACKS,STACKS=2,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNegativeStack()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|'
				+ "FEAT|NORMAL|STACKS=-4,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputZeroStack() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|'
				+ "FEAT|NORMAL|STACKS=0,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinDupe() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin(getSubTokenName() + "|FEAT|NORMAL|TestWP1",
				getSubTokenName() + "|FEAT|NORMAL|TestWP1");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1";
	}

	@Override
	protected String getLegalValue()
	{
		return getSubTokenName() + '|' + "FEAT|NORMAL|STACKS=2,TestWP1";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.SEPARATE;
	}
}
