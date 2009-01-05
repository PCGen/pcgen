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
package plugin.lsttokens.testsupport;

import java.net.URISyntaxException;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;

public abstract class AbstractAddTokenTestCase<T extends CDOMObject, TC extends CDOMObject>
		extends AbstractTokenTestCase<T>
{

	public abstract CDOMSecondaryToken<?> getSubToken();

	public String getSubTokenName()
	{
		return getSubToken().getTokenName();
	}

	public abstract Class<TC> getTargetClass();

	public abstract boolean isTypeLegal();

	public abstract boolean isAllLegal();
	
	public String getAllString()
	{
		return "ALL";
	}

	public String getTypePrefix()
	{
		return "";
	}

	public abstract boolean allowsParenAsSub();

	public abstract boolean allowsFormula();

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(getSubToken());
	}

	public char getJoinCharacter()
	{
		return ',';
	}

	protected void construct(LoadContext loadContext, String one)
	{
		loadContext.ref.constructCDOMObject(getTargetClass(), one);
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
	public void testInvalidInputString() throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenName() + '|' + "String"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidInputType() throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenName() + '|' + "TestType"));
		assertFalse(primaryContext.ref.validate(null));
	}

	// TODO Allow this once a method checks to exist if TestWP1 is a formula vs.
	// an object
	// @Test
	// public void testInvalidInputJoinedPipe() throws PersistenceLayerException
	// {
	// construct(primaryContext, "TestWP1");
	// construct(primaryContext, "TestWP2");
	// boolean parse = parse(getSubTokenName() + '|' + "TestWP1|TestWP2");
	// if (parse)
	// {
	// assertFalse(primaryContext.ref.validate());
	// }
	// else
	// {
	// assertNoSideEffects();
	// }
	// }

	@Test
	public void testInvalidInputJoinedDot() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse(getSubTokenName() + '|' + "TestWP1.TestWP2"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidInputNegativeFormula()
		throws PersistenceLayerException
	{
		if (allowsFormula())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + "-1|TestWP1"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputZeroFormula() throws PersistenceLayerException
	{
		if (allowsFormula())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + "0|TestWP1"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputTypeEmpty() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(parse(getSubTokenName() + '|' + getTypePrefix() + "TYPE="));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputTypeUnterminated()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(parse(getSubTokenName() + '|' + getTypePrefix() + "TYPE=One."));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputClearDotTypeDoubleSeparator()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(parse(getSubTokenName() + '|' + getTypePrefix() + "TYPE=One..Two"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputClearDotTypeFalseStart()
		throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(parse(getSubTokenName() + '|' + getTypePrefix() + "TYPE=.One"));
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
				boolean parse = parse(getSubTokenName() + '|' + "ALL");
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
				boolean parse = parse(getSubTokenName() + '|' + getTypePrefix() + "TYPE=Foo");
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

	// FIXME These are invalid due to RC being overly protective at the moment
	// @Test
	// public void testInvalidInputAny()
	// {
	// assertTrue(parse( "ANY"));
	// assertFalse(primaryContext.ref.validate());
	// }
	// @Test
	// public void testInvalidInputCheckType()
	// {
	// if (!isTypeLegal())
	// {
	// assertTrue(token.parse(primaryContext, primaryProf, getTypePrefix() + "TYPE=TestType"));
	// assertFalse(primaryContext.ref.validate());
	// }
	// }
	//

	@Test
	public void testInvalidDoubleList() throws PersistenceLayerException
	{
		if (allowsParenAsSub())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			assertFalse(parse(getSubTokenName() + '|'
					+ "TestWP1 (Test,TestTwo)"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidListEnd() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "TestWP1"
			+ getJoinCharacter()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListStart() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + getJoinCharacter()
			+ "TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse(getSubTokenName() + '|' + "TestWP2"
			+ getJoinCharacter() + getJoinCharacter() + "TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputCheckMult() throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2
		construct(primaryContext, "TestWP1");
		assertTrue(parse(getSubTokenName() + '|' + "TestWP1"
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
			assertTrue(parse(getSubTokenName() + '|' + "TestWP1"
				+ getJoinCharacter() + getTypePrefix() + "TYPE=TestType" + getJoinCharacter()
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
			assertTrue(parse(getSubTokenName() + '|' + "TestWP1"
				+ getJoinCharacter() + getTypePrefix() + "TYPE.TestType.OtherTestType"
				+ getJoinCharacter() + "TestWP2"));
			assertFalse(primaryContext.ref.validate(null));
		}
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse(getSubTokenName() + '|' + "TestWP1"));
		assertTrue(primaryContext.ref.validate(null));
		assertTrue(parse(getSubTokenName() + '|' + "TestWP1"
			+ getJoinCharacter() + "TestWP2"));
		assertTrue(primaryContext.ref.validate(null));
		if (isTypeLegal())
		{
			assertTrue(parse(getSubTokenName() + '|' + getTypePrefix() + "TYPE=TestType"));
			assertTrue(primaryContext.ref.validate(null));
			assertTrue(parse(getSubTokenName() + '|' + getTypePrefix() + "TYPE.TestType"));
			assertTrue(primaryContext.ref.validate(null));
			assertTrue(parse(getSubTokenName() + '|' + "TestWP1"
				+ getJoinCharacter() + "TestWP2" + getJoinCharacter()
				+ getTypePrefix() + "TYPE=TestType"));
			assertTrue(primaryContext.ref.validate(null));
			assertTrue(parse(getSubTokenName() + '|' + "TestWP1"
				+ getJoinCharacter() + "TestWP2" + getJoinCharacter()
				+ getTypePrefix() + "TYPE=TestType.OtherTestType"));
			assertTrue(primaryContext.ref.validate(null));
		}
		if (isAllLegal())
		{
			assertTrue(parse(getSubTokenName() + '|' + getAllString()));
			assertTrue(primaryContext.ref.validate(null));
		}
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin(getSubTokenName() + '|' + "TestWP1");
	}

	@Test
	public void testRoundRobinParen() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1 (Test)");
		construct(secondaryContext, "TestWP1 (Test)");
		runRoundRobin(getSubTokenName() + '|' + "TestWP1 (Test)");
	}

	@Test
	public void testRoundRobinCount() throws PersistenceLayerException
	{
		if (allowsFormula())
		{
			construct(primaryContext, "TestWP1 (Test)");
			construct(secondaryContext, "TestWP1 (Test)");
			runRoundRobin(getSubTokenName() + '|' + "4|TestWP1 (Test)");
		}
	}

	@Test
	public void testRoundRobinFormulaCount() throws PersistenceLayerException
	{
		if (allowsFormula())
		{
			construct(primaryContext, "TestWP1 (Test)");
			construct(secondaryContext, "TestWP1 (Test)");
			runRoundRobin(getSubTokenName() + '|' + "INT|TestWP1 (Test)");
		}
	}

	@Test
	public void testRoundRobinParenSub() throws PersistenceLayerException
	{
		if (allowsParenAsSub())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			runRoundRobin(getSubTokenName() + '|' + "TestWP1 (Test)");
		}
	}

	@Test
	public void testRoundRobinParenDoubleSub() throws PersistenceLayerException
	{
		if (allowsParenAsSub())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			runRoundRobin(getSubTokenName() + '|' + "TestWP1 (Test(Two))");
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
		runRoundRobin(getSubTokenName() + '|' + "TestWP1" + getJoinCharacter()
			+ "TestWP2" + getJoinCharacter() + "TestWP3");
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
			runRoundRobin(getSubTokenName() + '|' + "TestWP1"
				+ getJoinCharacter() + "TestWP2" + getJoinCharacter()
				+ getTypePrefix() + "TYPE=OtherTestType" + getJoinCharacter() + getTypePrefix() + "TYPE=TestType");
		}
	}

	@Test
	public void testRoundRobinTestEquals() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin(getSubTokenName() + '|' + getTypePrefix() + "TYPE=TestType");
		}
	}

	@Test
	public void testRoundRobinTestEqualThree() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin(getSubTokenName() + '|'
				+ getTypePrefix() + "TYPE=TestAltType.TestThirdType.TestType");
		}
	}

	// TODO This really need to check the object is also not modified, not just
	// that the graph is empty (same with other tests here)
	@Test
	public void testInvalidInputAnyItem() throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + getAllString()
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
			assertFalse(parse(getSubTokenName() + '|' + "TestWP1"
				+ getJoinCharacter() + getAllString()));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputAnyType() throws PersistenceLayerException
	{
		if (isTypeLegal() && isAllLegal())
		{
			assertFalse(parse(getSubTokenName() + '|' + getAllString()
				+ getJoinCharacter() + getTypePrefix() + "TYPE=TestType"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputTypeAny() throws PersistenceLayerException
	{
		if (isTypeLegal() && isAllLegal())
		{
			assertFalse(parse(getSubTokenName() + '|' + getTypePrefix() + "TYPE=TestType"
				+ getJoinCharacter() + getAllString()));
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
			assertTrue(parse(getSubTokenName() + '|' + "TestWP1"
				+ getJoinCharacter() + "TestWP2"));
			assertTrue(parseSecondary(getSubTokenName() + '|' + "TestWP1"
				+ getJoinCharacter() + "TestWP2"));
			assertFalse(parse(getSubTokenName() + '|' + "TestWP3"
				+ getJoinCharacter() + getTypePrefix() + "TYPE="));
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
		assertTrue(parse(getSubTokenName() + '|' + "TestWP1"
			+ getJoinCharacter() + "TestWP2"));
		assertTrue(parseSecondary(getSubTokenName() + '|' + "TestWP1"
			+ getJoinCharacter() + "TestWP2"));
		assertFalse(parse(getSubTokenName() + '|' + "TestWP3"
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
			assertTrue(parse(getSubTokenName() + '|' + "TestWP1"
				+ getJoinCharacter() + "TestWP2"));
			assertTrue(parseSecondary(getSubTokenName() + '|' + "TestWP1"
				+ getJoinCharacter() + "TestWP2"));
			assertFalse(parse(getSubTokenName() + '|' + "TestWP3"
				+ getJoinCharacter() + getAllString()));
			assertNoSideEffects();
		}
	}

	@Test
	public void testRoundRobinTestAll() throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			runRoundRobin(getSubTokenName() + '|' + getAllString());
		}
	}

}
