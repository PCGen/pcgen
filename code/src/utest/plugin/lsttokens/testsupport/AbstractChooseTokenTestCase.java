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
import pcgen.rules.persistence.token.QualifierToken;
import plugin.qualifier.pobject.QualifiedToken;

public abstract class AbstractChooseTokenTestCase<T extends CDOMObject, TC extends CDOMObject>
		extends AbstractTokenTestCase<T>
{

	private static QualifierToken<CDOMObject> pcqualifier = new QualifiedToken<CDOMObject>();

	public abstract CDOMSecondaryToken<?> getSubToken();

	public String getSubTokenName()
	{
		return getSubToken().getTokenName();
	}

	public abstract Class<TC> getTargetClass();

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(getSubToken());
		TokenRegistration.register(pcqualifier);
	}

	protected void construct(LoadContext loadContext, String one)
	{
		loadContext.ref.constructCDOMObject(getTargetClass(), one);
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return getSubTokenName() + '|' + "TestWP1|TestWP2|TestWP3";
	}

	@Override
	protected String getLegalValue()
	{
		return getSubTokenName() + '|' + "TestWP1|TestWP2";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}

	protected abstract boolean allowsQualifier();

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
		assertFalse(parse(getSubTokenName() + "|,"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputString() throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenName() + '|' + "String"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidInputJoinedDot() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse(getSubTokenName() + '|' + "TestWP1.TestWP2"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidInputTypeEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "TYPE="));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNotTypeEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "!TYPE="));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTypeUnterminated()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "TYPE=One."));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNotTypeUnterminated()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "!TYPE=One."));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTypeDoubleSeparator()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "TYPE=One..Two"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNotTypeDoubleSeparator()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "!TYPE=One..Two"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTypeFalseStart()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "TYPE=.One"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNotTypeFalseStart()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "!TYPE=.One"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListEndPipe() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "TestWP1|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListEndComma() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "TestWP1,"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListStartPipe() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListStartComma() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + ",TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListDoubleJoinPipe()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse(getSubTokenName() + '|' + "TestWP2||TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListDoubleJoinComma()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "TYPE=Foo,,!TYPE=Bar"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNotBuilt() throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2
		construct(primaryContext, "TestWP1");
		assertTrue(parse(getSubTokenName() + '|' + "TestWP1|TestWP2"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidInputCheckTypeEqualLengthBar()
			throws PersistenceLayerException
	{
		/*
		 * Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		 * consume the |
		 */
		construct(primaryContext, "TestWP1");
		assertTrue(parse(getSubTokenName() + '|'
				+ "TestWP1|TYPE=TestType|TestWP2"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidInputCheckTypeDotLengthPipe()
			throws PersistenceLayerException
	{
		/*
		 * Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		 * consume the |
		 */
		construct(primaryContext, "TestWP1");
		assertTrue(parse(getSubTokenName() + '|' + "TestWP1|"
				+ "TYPE.TestType.OtherTestType|TestWP2"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse(getSubTokenName() + '|' + "TestWP1"));
		assertTrue(primaryContext.ref.validate(null));
		assertTrue(parse(getSubTokenName() + '|' + "TestWP1|TestWP2"));
		assertTrue(primaryContext.ref.validate(null));
		assertTrue(parse(getSubTokenName() + '|' + "TYPE=TestType"));
		assertTrue(primaryContext.ref.validate(null));
		assertTrue(parse(getSubTokenName() + '|' + "TYPE.TestType"));
		assertTrue(primaryContext.ref.validate(null));
		assertTrue(parse(getSubTokenName() + '|'
				+ "TestWP1|TestWP2|TYPE=TestType"));
		assertTrue(primaryContext.ref.validate(null));
		assertTrue(parse(getSubTokenName() + '|'
				+ "TestWP1|TestWP2|TYPE=TestType.OtherTestType"));
		assertTrue(primaryContext.ref.validate(null));
		assertTrue(parse(getSubTokenName() + "|ALL"));
		assertTrue(primaryContext.ref.validate(null));
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
	public void testRoundRobinThree() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin(getSubTokenName() + '|' + "TestWP1|TestWP2|TestWP3");
	}

	@Test
	public void testRoundRobinWithEqualType() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(getSubTokenName() + '|'
				+ "TestWP1|TestWP2|TYPE=OtherTestType|TYPE=TestType");
	}

	@Test
	public void testRoundRobinTestEquals() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + "TYPE=TestType");
	}

	@Test
	public void testRoundRobinTestEqualThree() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|'
				+ "TYPE=TestAltType.TestThirdType.TestType");
	}

	/*
	 * TODO This really need to check the object is also not modified, not just
	 * that the graph is empty (same with other tests here)
	 */
	@Test
	public void testInvalidInputAnyItem() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + "|ALL|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputItemAny() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "TestWP1|ALL"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputAnyType() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "ALL|TYPE=TestType"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTypeAny() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "TYPE=TestType|ALL"));
		assertNoSideEffects();
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
		assertTrue(parse(getSubTokenName() + '|' + "TestWP1|TestWP2"));
		assertTrue(parseSecondary(getSubTokenName() + '|' + "TestWP1|TestWP2"));
		assertFalse(parse(getSubTokenName() + '|' + "TestWP3|TYPE="));
		assertNoSideEffects();
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
		assertTrue(parse(getSubTokenName() + '|' + "TestWP1|TestWP2"));
		assertTrue(parseSecondary(getSubTokenName() + '|' + "TestWP1|TestWP2"));
		assertFalse(parse(getSubTokenName() + '|' + "TestWP3||TestWP4"));
		assertNoSideEffects();
	}

	@Test
	public void testInputInvalidAddsAllNoSideEffect()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP3");
		assertTrue(parse(getSubTokenName() + '|' + "TestWP1|TestWP2"));
		assertTrue(parseSecondary(getSubTokenName() + '|' + "TestWP1|TestWP2"));
		assertFalse(parse(getSubTokenName() + '|' + "TestWP3|ALL"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinTestAll() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + "|ALL");
	}

	@Test
	public void testInvalidInputJoinedDotQualifier()
			throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenName() + '|' + "PC.QUALIFIED"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidInputQualifierOpenBracket()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED["));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputQualifierCloseBracket()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "PC]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputQualifierEmptyBracket()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputQualifierQualifier()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			assertTrue(parse(getSubTokenName() + '|' + "QUALIFIED[QUALIFIED]"));
			assertFalse(primaryContext.ref.validate(null));
		}
	}

	@Test
	public void testInvalidInputJoinQualifiedOnlyPipe()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			assertFalse(parse(getSubTokenName() + "|QUALIFIED[|]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputJoinQualifiedOnlyComma()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			assertFalse(parse(getSubTokenName() + "|QUALIFIED[,]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputStringQualified()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			assertTrue(parse(getSubTokenName() + '|' + "QUALIFIED[String]"));
			assertFalse(primaryContext.ref.validate(null));
		}
	}

	@Test
	public void testInvalidInputJoinedDotQualified()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			assertTrue(parse(getSubTokenName() + '|'
					+ "QUALIFIED[TestWP1.TestWP2]"));
			assertFalse(primaryContext.ref.validate(null));
		}
	}

	@Test
	public void testInvalidInputQualifiedTypeEmpty()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[TYPE=]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputQualifiedNotTypeEmpty()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[!TYPE=]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputQualifiedTypeUnterminated()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[TYPE=One.]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputQualifiedNotTypeUnterminated()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[!TYPE=One.]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputQualifiedTypeDoubleSeparator()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|'
					+ "QUALIFIED[TYPE=One..Two]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputQualifiedNotTypeDoubleSeparator()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|'
					+ "QUALIFIED[!TYPE=One..Two]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputQualifiedTypeFalseStart()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[TYPE=.One]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputQualifiedNotTypeFalseStart()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[!TYPE=.One]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidQualifiedListEndPipe()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[TestWP1|]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidQualifiedListEndComma()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[TestWP1,]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidQualifiedListStartPipe()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[|TestWP1]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidQualifiedListStartComma()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[,TestWP1]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidQualifiedListDoubleJoinPipe()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			assertFalse(parse(getSubTokenName() + '|'
					+ "QUALIFIED[TestWP2||TestWP1]]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidQualifiedListDoubleJoinComma()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|'
					+ "QUALIFIED[TYPE=Foo,,!TYPE=Bar]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidQualifiedInputNotBuilt()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			// Explicitly do NOT build TestWP2
			construct(primaryContext, "TestWP1");
			assertTrue(parse(getSubTokenName() + '|'
					+ "QUALIFIED[TestWP1|TestWP2]"));
			assertFalse(primaryContext.ref.validate(null));
		}
	}

	@Test
	public void testInvalidQualifiedDanglingType()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[TestWP1]TYPE=Foo"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidQualifiedDanglingPrimitive()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[TYPE=Foo]TestWP1"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidQualifiedDanglingTypePipe()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[TestWP1]TYPE=Foo|TYPE=Bar"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidQualifiedDanglingPrimitiveComma()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + "QUALIFIED[TYPE=Foo]TestWP1,TYPE=Bar"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testValidQualifiedInputLotsOr()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			runRoundRobin(getSubTokenName()
					+ '|'
					+ "QUALIFIED[TYPE=Bar|TYPE=Goo]|QUALIFIED[TYPE=Foo|TYPE=Yea]");
		}
	}

	@Test
	public void testValidQualifiedInputLotsAnd()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			runRoundRobin(getSubTokenName()
					+ '|'
					+ "QUALIFIED[TYPE=Bar,TYPE=Goo],QUALIFIED[TYPE=Foo,TYPE=Yea]");
		}
	}

	@Test
	public void testInvalidQualifiedInputCheckTypeEqualLengthBar()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			/*
			 * Explicitly do NOT build TestWP2 (this checks that the TYPE=
			 * doesn't consume the |
			 */
			construct(primaryContext, "TestWP1");
			assertTrue(parse(getSubTokenName() + '|'
					+ "QUALIFIED[TestWP1|TYPE=TestType|TestWP2]"));
			assertFalse(primaryContext.ref.validate(null));
		}
	}

	@Test
	public void testInvalidQualifiedInputCheckTypeDotLengthPipe()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			/*
			 * Explicitly do NOT build TestWP2 (this checks that the TYPE=
			 * doesn't consume the |
			 */
			construct(primaryContext, "TestWP1");
			assertTrue(parse(getSubTokenName() + '|' + "QUALIFIED[TestWP1|"
					+ "TYPE.TestType.OtherTestType|TestWP2]"));
			assertFalse(primaryContext.ref.validate(null));
		}
	}

	@Test
	public void testRoundRobinQualifiedOne() throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			runRoundRobin(getSubTokenName() + '|' + "QUALIFIED[TestWP1]");
		}
	}

	@Test
	public void testRoundRobinQualifiedParen() throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			construct(primaryContext, "TestWP1 (Test)");
			construct(secondaryContext, "TestWP1 (Test)");
			runRoundRobin(getSubTokenName() + '|' + "QUALIFIED[TestWP1 (Test)]");
		}
	}

	@Test
	public void testRoundRobinQualifiedThreeOr()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(primaryContext, "TestWP3");
			construct(secondaryContext, "TestWP1");
			construct(secondaryContext, "TestWP2");
			construct(secondaryContext, "TestWP3");
			runRoundRobin(getSubTokenName() + '|'
					+ "QUALIFIED[TestWP1|TestWP2|TestWP3]");
		}
	}

	@Test
	public void testRoundRobinQualifiedThreeAnd()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			runRoundRobin(getSubTokenName() + '|'
					+ "QUALIFIED[!TYPE=Type1,TYPE=Type2,TYPE=Type3]");
		}
	}

	@Test
	public void testRoundRobinQualifiedFourAndOr()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			runRoundRobin(getSubTokenName()
					+ '|'
					+ "QUALIFIED[!TYPE=Type1,TYPE=Type2|!TYPE=Type3,TYPE=Type4]");
		}
	}

	@Test
	public void testRoundRobinQualifiedWithEqualType()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP1");
			construct(secondaryContext, "TestWP2");
			runRoundRobin(getSubTokenName()
					+ '|'
					+ "QUALIFIED[TestWP1|TestWP2|TYPE=OtherTestType|TYPE=TestType]");
		}
	}

	@Test
	public void testRoundRobinQualifiedTestEquals()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			runRoundRobin(getSubTokenName() + '|' + "QUALIFIED[TYPE=TestType]");
		}
	}

	@Test
	public void testRoundRobinQualifiedTestEqualThree()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			runRoundRobin(getSubTokenName() + '|'
					+ "QUALIFIED[TYPE=TestAltType.TestThirdType.TestType]");
		}
	}

	/*
	 * TODO This really need to check the object is also not modified, not just
	 * that the graph is empty (same with other tests here)
	 */
	@Test
	public void testInvalidQualifiedInputAnyItem()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse(getSubTokenName() + "|QUALIFIED[ALL|TestWP1]"));
			assertNoSideEffects();
		}

	}

	@Test
	public void testInvalidQualifiedInputItemAny()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse(getSubTokenName() + '|'
					+ "QUALIFIED[TestWP1|ALL]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidQualifiedInputAnyType()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|'
					+ "QUALIFIED[ALL|TYPE=TestType]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidQualifiedInputTypeAny()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|'
					+ "QUALIFIED[TYPE=TestType|ALL]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInputInvalidQualifiedAddsTypeNoSideEffect()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP2");
			construct(primaryContext, "TestWP3");
			construct(secondaryContext, "TestWP3");
			assertTrue(parse(getSubTokenName() + '|'
					+ "QUALIFIED[TestWP1|TestWP2]"));
			assertTrue(parseSecondary(getSubTokenName() + '|'
					+ "QUALIFIED[TestWP1|TestWP2]"));
			assertFalse(parse(getSubTokenName() + '|'
					+ "QUALIFIED[TestWP3|TYPE=]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInputInvalidQualifiedAddsBasicNoSideEffect()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP2");
			construct(primaryContext, "TestWP3");
			construct(secondaryContext, "TestWP3");
			construct(primaryContext, "TestWP4");
			construct(secondaryContext, "TestWP4");
			assertTrue(parse(getSubTokenName() + '|'
					+ "QUALIFIED[TestWP1|TestWP2]"));
			assertTrue(parseSecondary(getSubTokenName() + '|'
					+ "QUALIFIED[TestWP1|TestWP2]"));
			assertFalse(parse(getSubTokenName() + '|'
					+ "QUALIFIED[TestWP3||TestWP4]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInputInvalidQualifiedAddsAllNoSideEffect()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			construct(primaryContext, "TestWP1");
			construct(secondaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP2");
			construct(primaryContext, "TestWP3");
			construct(secondaryContext, "TestWP3");
			assertTrue(parse(getSubTokenName() + '|'
					+ "QUALIFIED[TestWP1|TestWP2]"));
			assertTrue(parseSecondary(getSubTokenName() + '|'
					+ "QUALIFIED[TestWP1|TestWP2]"));
			assertFalse(parse(getSubTokenName() + '|'
					+ "QUALIFIED[TestWP3|ALL]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testRoundRobinTestQualifiedAll()
			throws PersistenceLayerException
	{
		if (allowsQualifier())
		{
			runRoundRobin(getSubTokenName() + "|QUALIFIED[ALL]");
		}
	}

}
