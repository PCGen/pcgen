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
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.QualifierToken;
import plugin.qualifier.pobject.QualifiedToken;

public abstract class AbstractQualifierTokenTestCase<T extends CDOMObject, TC extends CDOMObject>
		extends AbstractTokenTestCase<T>
{

	private static QualifierToken<CDOMObject> qt = new QualifiedToken<CDOMObject>();

	public abstract CDOMSecondaryToken<?> getSubToken();

	private final String qualifier;
	private final boolean negate;

	protected AbstractQualifierTokenTestCase(String q, boolean allowNegation)
	{
		qualifier = q;
		negate = allowNegation;
	}

	public String getSubTokenName()
	{
		return getSubToken().getTokenName();
	}

	public abstract Class<TC> getTargetClass();

	protected abstract boolean allowsNotQualifier();

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(getSubToken());
		TokenRegistration.register(qt);
	}

	protected void construct(LoadContext loadContext, String one)
	{
		construct(loadContext, getTargetClass(), one);
	}

	protected void construct(LoadContext loadContext,
			Class<? extends CDOMObject> cl, String one)
	{
		loadContext.ref.constructCDOMObject(cl, one);
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return getSubTokenName() + '|' + qualifier + "[TestWP1]";
	}

	@Override
	protected String getLegalValue()
	{
		return getSubTokenName() + '|' + qualifier;
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}

	protected ReferenceManufacturer<TC> getManufacturer()
	{
		return primaryContext.ref.getManufacturer(getTargetClass());
	}

	@Test
	public void testQualifierOpenBracket() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "["));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierCloseBracket() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierEmptyBrackets() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierPipeInBrackets() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + "|" + qualifier + "[|]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierCommaInBrackets() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + "|" + qualifier + "[,]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierEmptyType() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TYPE=]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierEmptyNotType() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[!TYPE=]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierTypeDot() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TYPE=One.]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierNotTypeDot() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[!TYPE=One.]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierNotTypeDoubleDot()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[!TYPE=One..Two]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierTypeEqualDot() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TYPE=.One]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierTypeDoubleDot() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[TYPE=One..Two]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierNotTypeEqualDot() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[!TYPE=.One]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierPrimitivePipe() throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TestWP1|]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierPrimitiveComma() throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TestWP1,]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierPipePrim() throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[|TestWP1]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierCommaPrim() throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[,TestWP1]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierDoublePipe() throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		construct(primaryContext, getTargetClass(), "TestWP2");
		construct(secondaryContext, getTargetClass(), "TestWP2");
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP2||TestWP1]]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierDoubleComma() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[TYPE=Foo,,!TYPE=Bar]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierAllType() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[ALL|TYPE=TestType]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierTypeAll() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[TYPE=TestType|ALL]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierTypePrimBad() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[TYPE=Foo]TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierPrimTypeBadPipe() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP1]TYPE=Foo|TYPE=Bar"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierPrimTypeBad() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP1]TYPE=Foo"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierTypePrimComma() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[TYPE=Foo]TestWP1,TYPE=Bar"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierAllPrim() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + "|" + qualifier + "[ALL|TestWP1]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierPrimAll() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TestWP1|ALL]"));
		assertNoSideEffects();
	}

	@Test
	public void testBadNoSideEffect() throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP1|TestWP2]"));
		assertTrue(parseSecondary(getSubTokenName() + '|' + qualifier
				+ "[TestWP1|TestWP2]"));
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP3|TYPE=]"));
		assertNoSideEffects();
	}

	@Test
	public void testQualifierDot() throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenName() + '|' + qualifier + "." + qualifier));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testQualifierAsPrim() throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenName() + '|' + qualifier + "[" + qualifier
				+ "]"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testQualifierBadPrim() throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenName() + '|' + qualifier + "[String]"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testQualifierNoConstruct() throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		// Explicitly do NOT build TestWP0
		assertTrue(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP0|TestWP1]"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testQualifierTypeCheck() throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		// this checks that the TYPE= doesn't consume the |
		assertTrue(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP1|TYPE=TestType|TestWP0]"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testQualifierTypeDotCheck() throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		// this checks that the TYPE. doesn't consume the |
		assertTrue(parse(getSubTokenName() + '|' + qualifier + "[TestWP1|"
				+ "TYPE.TestType.OtherTestType|TestWP0]"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testQualifierBadAllNoSideEffect()
			throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		construct(primaryContext, getTargetClass(), "TestWP2");
		construct(secondaryContext, getTargetClass(), "TestWP2");
		// Test with All
		assertTrue(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP1|TestWP2]"));
		assertTrue(parseSecondary(getSubTokenName() + '|' + qualifier
				+ "[TestWP1|TestWP2]"));
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TestWP3|ALL]"));
		assertNoSideEffects();
	}

	@Test
	public void testNegatedQualifierPipe() throws PersistenceLayerException
	{
		if (!negate)
		{
			assertFalse(parse(getSubTokenName() + "|!" + qualifier
					+ "[TYPE=Bar|TYPE=Goo]|" + qualifier
					+ "[TYPE=Foo|TYPE=Yea]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testNegatedQualifierPrim() throws PersistenceLayerException
	{
		if (!negate)
		{
			construct(primaryContext, getTargetClass(), "TestWP1");
			construct(secondaryContext, getTargetClass(), "TestWP1");
			assertFalse(parse(getSubTokenName() + "|!" + qualifier
					+ "[TestWP1]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testNegatedQualifierParenPrim()
			throws PersistenceLayerException
	{
		if (!negate)
		{
			construct(primaryContext, getTargetClass(), "TestWP1 (Test)");
			construct(secondaryContext, getTargetClass(), "TestWP1 (Test)");
			assertFalse(parse(getSubTokenName() + "|!" + qualifier
					+ "[TestWP1 (Test)]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testNegatedQualifierAll() throws PersistenceLayerException
	{
		if (!negate)
		{
			assertFalse(parse(getSubTokenName() + "|!" + qualifier + "[ALL]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputJoinedDotQualifier()
			throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenName() + '|' + "PC." + qualifier));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidInputQualifierOpenBracket()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "["));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputQualifierCloseBracket()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputQualifierEmptyBracket()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputQualifierQualifier()
			throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenName() + '|' + qualifier + "[" + qualifier
				+ "]"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidInputJoinQualifiedOnlyPipe()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + "|" + qualifier + "[|]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputJoinQualifiedOnlyComma()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + "|" + qualifier + "[,]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputStringQualified()
			throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenName() + '|' + qualifier + "[String]"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidInputJoinedDotQualified()
			throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(primaryContext, getTargetClass(), "TestWP2");
		assertTrue(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP1.TestWP2]"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidInputQualifiedTypeEmpty()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TYPE=]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputQualifiedNotTypeEmpty()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[!TYPE=]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputQualifiedTypeUnterminated()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TYPE=One.]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputQualifiedNotTypeUnterminated()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[!TYPE=One.]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputQualifiedTypeDoubleSeparator()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[TYPE=One..Two]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputQualifiedNotTypeDoubleSeparator()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[!TYPE=One..Two]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputQualifiedTypeFalseStart()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TYPE=.One]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputQualifiedNotTypeFalseStart()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[!TYPE=.One]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidQualifiedListEndPipe()
			throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TestWP1|]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidQualifiedListEndComma()
			throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TestWP1,]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidQualifiedListStartPipe()
			throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[|TestWP1]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidQualifiedListStartComma()
			throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[,TestWP1]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidQualifiedListDoubleJoinPipe()
			throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(primaryContext, getTargetClass(), "TestWP2");
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP2||TestWP1]]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidQualifiedListDoubleJoinComma()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[TYPE=Foo,,!TYPE=Bar]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidQualifiedInputNotBuilt()
			throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2
		construct(primaryContext, getTargetClass(), "TestWP1");
		assertTrue(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP1|TestWP2]"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidQualifiedDanglingType()
			throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP1]TYPE=Foo"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidQualifiedDanglingPrimitive()
			throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[TYPE=Foo]TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidQualifiedDanglingTypePipe()
			throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP1]TYPE=Foo|TYPE=Bar"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidQualifiedDanglingPrimitiveComma()
			throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[TYPE=Foo]TestWP1,TYPE=Bar"));
		assertNoSideEffects();
	}

	@Test
	public void testValidQualifiedInputLotsOr()
			throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + qualifier
				+ "[TYPE=Bar|TYPE=Goo]|" + qualifier + "[TYPE=Foo|TYPE=Yea]");
	}

	@Test
	public void testValidQualifiedInputLotsAnd()
			throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + qualifier
				+ "[TYPE=Bar,TYPE=Goo]," + qualifier + "[TYPE=Foo,TYPE=Yea]");
	}

	@Test
	public void testInvalidQualifiedInputCheckTypeEqualLengthBar()
			throws PersistenceLayerException
	{
		/*
		 * Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		 * consume the |
		 */
		construct(primaryContext, getTargetClass(), "TestWP1");
		assertTrue(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP1|TYPE=TestType|TestWP2]"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidQualifiedInputCheckTypeDotLengthPipe()
			throws PersistenceLayerException
	{
		/*
		 * Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		 * consume the |
		 */
		construct(primaryContext, getTargetClass(), "TestWP1");
		assertTrue(parse(getSubTokenName() + '|' + qualifier + "[TestWP1|"
				+ "TYPE.TestType.OtherTestType|TestWP2]"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testRoundRobinQualifiedOne() throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		runRoundRobin(getSubTokenName() + '|' + qualifier + "[TestWP1]");
	}

	@Test
	public void testRoundRobinQualifiedParen() throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1 (Test)");
		construct(secondaryContext, getTargetClass(), "TestWP1 (Test)");
		runRoundRobin(getSubTokenName() + '|' + qualifier + "[TestWP1 (Test)]");
	}

	@Test
	public void testRoundRobinQualifiedThreeOr()
			throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(primaryContext, getTargetClass(), "TestWP2");
		construct(primaryContext, getTargetClass(), "TestWP3");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP2");
		construct(secondaryContext, getTargetClass(), "TestWP3");
		runRoundRobin(getSubTokenName() + '|' + qualifier
				+ "[TestWP1|TestWP2|TestWP3]");
	}

	@Test
	public void testRoundRobinQualifiedThreeAnd()
			throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + qualifier
				+ "[!TYPE=Type1,TYPE=Type2,TYPE=Type3]");
	}

	@Test
	public void testRoundRobinQualifiedFourAndOr()
			throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + qualifier
				+ "[!TYPE=Type1,TYPE=Type2|!TYPE=Type3,TYPE=Type4]");
	}

	@Test
	public void testRoundRobinQualifiedWithEqualType()
			throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(primaryContext, getTargetClass(), "TestWP2");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP2");
		runRoundRobin(getSubTokenName() + '|' + qualifier
				+ "[TestWP1|TestWP2|TYPE=OtherTestType|TYPE=TestType]");
	}

	@Test
	public void testRoundRobinQualifiedTestEquals()
			throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + qualifier + "[TYPE=TestType]");
	}

	@Test
	public void testRoundRobinQualifiedTestEqualThree()
			throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + qualifier
				+ "[TYPE=TestAltType.TestThirdType.TestType]");
	}

	@Test
	public void testInvalidQualifiedInputAnyItem()
			throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		assertFalse(parse(getSubTokenName() + "|" + qualifier + "[ALL|TestWP1]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidQualifiedInputItemAny()
			throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TestWP1|ALL]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidQualifiedInputAnyType()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[ALL|TYPE=TestType]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidQualifiedInputTypeAny()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[TYPE=TestType|ALL]"));
		assertNoSideEffects();
	}

	@Test
	public void testInputInvalidQualifiedAddsTypeNoSideEffect()
			throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		construct(primaryContext, getTargetClass(), "TestWP2");
		construct(secondaryContext, getTargetClass(), "TestWP2");
		construct(primaryContext, getTargetClass(), "TestWP3");
		construct(secondaryContext, getTargetClass(), "TestWP3");
		assertTrue(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP1|TestWP2]"));
		assertTrue(parseSecondary(getSubTokenName() + '|' + qualifier
				+ "[TestWP1|TestWP2]"));
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP3|TYPE=]"));
		assertNoSideEffects();
	}

	@Test
	public void testInputInvalidQualifiedAddsBasicNoSideEffect()
			throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		construct(primaryContext, getTargetClass(), "TestWP2");
		construct(secondaryContext, getTargetClass(), "TestWP2");
		construct(primaryContext, getTargetClass(), "TestWP3");
		construct(secondaryContext, getTargetClass(), "TestWP3");
		construct(primaryContext, getTargetClass(), "TestWP4");
		construct(secondaryContext, getTargetClass(), "TestWP4");
		assertTrue(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP1|TestWP2]"));
		assertTrue(parseSecondary(getSubTokenName() + '|' + qualifier
				+ "[TestWP1|TestWP2]"));
		assertFalse(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP3||TestWP4]"));
		assertNoSideEffects();
	}

	@Test
	public void testInputInvalidQualifiedAddsAllNoSideEffect()
			throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		construct(primaryContext, getTargetClass(), "TestWP2");
		construct(secondaryContext, getTargetClass(), "TestWP2");
		construct(primaryContext, getTargetClass(), "TestWP3");
		construct(secondaryContext, getTargetClass(), "TestWP3");
		assertTrue(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP1|TestWP2]"));
		assertTrue(parseSecondary(getSubTokenName() + '|' + qualifier
				+ "[TestWP1|TestWP2]"));
		assertFalse(parse(getSubTokenName() + '|' + qualifier + "[TestWP3|ALL]"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinTestQualifiedAll()
			throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + "|" + qualifier + "[ALL]");
	}

	@Test
	public void testInvalidInputJoinedDotNotQualifierAlone()
			throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenName() + '|' + "PC.!" + qualifier + ""));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidInputNotQualifierOpenBracket()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier + "["));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputNotQualifierCloseBracket()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier + "]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputNotQualifierEmptyBracket()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier + "[]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputNotQualifierNotQualifier()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			assertTrue(parse(getSubTokenName() + '|' + "!" + qualifier + "[!"
					+ qualifier + "]"));
			assertFalse(primaryContext.ref.validate(null));
		}
	}

	@Test
	public void testInvalidInputJoinNotQualifierOnlyPipe()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			assertFalse(parse(getSubTokenName() + "|!" + qualifier + "[|]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputJoinNotQualifierOnlyComma()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			assertFalse(parse(getSubTokenName() + "|!" + qualifier + "[,]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputStringNotQualifier()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			assertTrue(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[String]"));
			assertFalse(primaryContext.ref.validate(null));
		}
	}

	@Test
	public void testInvalidInputJoinedDotNotQualifier()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			construct(primaryContext, getTargetClass(), "TestWP1");
			construct(primaryContext, getTargetClass(), "TestWP2");
			assertTrue(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP1.TestWP2]"));
			assertFalse(primaryContext.ref.validate(null));
		}
	}

	@Test
	public void testInvalidInputNotQualifierTypeEmpty()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TYPE=]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputNotQualifierNotTypeEmpty()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[!TYPE=]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputNotQualifierTypeUnterminated()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TYPE=One.]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputNotQualifierNotTypeUnterminated()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[!TYPE=One.]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputNotQualifierTypeDoubleSeparator()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TYPE=One..Two]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputNotQualifierNotTypeDoubleSeparator()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[!TYPE=One..Two]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputNotQualifierTypeFalseStart()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TYPE=.One]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputNotQualifierNotTypeFalseStart()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[!TYPE=.One]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidNotQualifierListEndPipe()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			construct(primaryContext, getTargetClass(), "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP1|]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidNotQualifierListEndComma()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			construct(primaryContext, getTargetClass(), "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP1,]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidNotQualifierListStartPipe()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			construct(primaryContext, getTargetClass(), "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[|TestWP1]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidNotQualifierListStartComma()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			construct(primaryContext, getTargetClass(), "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[,TestWP1]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidNotQualifierListDoubleJoinPipe()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			construct(primaryContext, getTargetClass(), "TestWP1");
			construct(primaryContext, getTargetClass(), "TestWP2");
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP2||TestWP1]]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidNotQualifierListDoubleJoinComma()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TYPE=Foo,,!TYPE=Bar]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidNotQualifierInputNotBuilt()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			// Explicitly do NOT build TestWP2
			construct(primaryContext, getTargetClass(), "TestWP1");
			assertTrue(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP1|TestWP2]"));
			assertFalse(primaryContext.ref.validate(null));
		}
	}

	@Test
	public void testInvalidNotQualifierDanglingType()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			construct(primaryContext, getTargetClass(), "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP1]TYPE=Foo"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidNotQualifierDanglingPrimitive()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			construct(primaryContext, getTargetClass(), "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TYPE=Foo]TestWP1"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidNotQualifierDanglingTypePipe()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			construct(primaryContext, getTargetClass(), "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP1]TYPE=Foo|TYPE=Bar"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidNotQualifierDanglingPrimitiveComma()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			construct(primaryContext, getTargetClass(), "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TYPE=Foo]TestWP1,TYPE=Bar"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testValidNotQualifierInputLotsOr()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			runRoundRobin(getSubTokenName() + '|' + "!" + qualifier
					+ "[TYPE=Bar|TYPE=Goo]|!" + qualifier
					+ "[TYPE=Foo|TYPE=Yea]");
		}
	}

	@Test
	public void testValidNotQualifierInputLotsAnd()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			runRoundRobin(getSubTokenName() + '|' + "!" + qualifier
					+ "[TYPE=Bar,TYPE=Goo],!" + qualifier
					+ "[TYPE=Foo,TYPE=Yea]");
		}
	}

	@Test
	public void testInvalidNotQualifierInputCheckTypeEqualLengthBar()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			/*
			 * Explicitly do NOT build TestWP2 (this checks that the TYPE=
			 * doesn't consume the |
			 */
			construct(primaryContext, getTargetClass(), "TestWP1");
			assertTrue(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP1|TYPE=TestType|TestWP2]"));
			assertFalse(primaryContext.ref.validate(null));
		}
	}

	@Test
	public void testInvalidNotQualifierInputCheckTypeDotLengthPipe()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			/*
			 * Explicitly do NOT build TestWP2 (this checks that the TYPE=
			 * doesn't consume the |
			 */
			construct(primaryContext, getTargetClass(), "TestWP1");
			assertTrue(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP1|" + "TYPE.TestType.OtherTestType|TestWP2]"));
			assertFalse(primaryContext.ref.validate(null));
		}
	}

	@Test
	public void testRoundRobinNotQualifierOne()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			construct(primaryContext, getTargetClass(), "TestWP1");
			construct(secondaryContext, getTargetClass(), "TestWP1");
			runRoundRobin(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP1]");
		}
	}

	@Test
	public void testRoundRobinNotQualifierParen()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			construct(primaryContext, getTargetClass(), "TestWP1 (Test)");
			construct(secondaryContext, getTargetClass(), "TestWP1 (Test)");
			runRoundRobin(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP1 (Test)]");
		}
	}

	@Test
	public void testRoundRobinNotQualifierThreeOr()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			construct(primaryContext, getTargetClass(), "TestWP1");
			construct(primaryContext, getTargetClass(), "TestWP2");
			construct(primaryContext, getTargetClass(), "TestWP3");
			construct(secondaryContext, getTargetClass(), "TestWP1");
			construct(secondaryContext, getTargetClass(), "TestWP2");
			construct(secondaryContext, getTargetClass(), "TestWP3");
			runRoundRobin(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP1|TestWP2|TestWP3]");
		}
	}

	@Test
	public void testRoundRobinNotQualifierThreeAnd()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			runRoundRobin(getSubTokenName() + '|' + "!" + qualifier
					+ "[!TYPE=Type1,TYPE=Type2,TYPE=Type3]");
		}
	}

	@Test
	public void testRoundRobinNotQualifierFourAndOr()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			runRoundRobin(getSubTokenName() + '|' + "!" + qualifier
					+ "[!TYPE=Type1,TYPE=Type2|!TYPE=Type3,TYPE=Type4]");
		}
	}

	@Test
	public void testRoundRobinNotQualifierWithEqualType()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			construct(primaryContext, getTargetClass(), "TestWP1");
			construct(primaryContext, getTargetClass(), "TestWP2");
			construct(secondaryContext, getTargetClass(), "TestWP1");
			construct(secondaryContext, getTargetClass(), "TestWP2");
			runRoundRobin(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP1|TestWP2|TYPE=OtherTestType|TYPE=TestType]");
		}
	}

	@Test
	public void testRoundRobinNotQualifierTestEquals()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			runRoundRobin(getSubTokenName() + '|' + "!" + qualifier
					+ "[TYPE=TestType]");
		}
	}

	@Test
	public void testRoundRobinNotQualifierTestEqualThree()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			runRoundRobin(getSubTokenName() + '|' + "!" + qualifier
					+ "[TYPE=TestAltType.TestThirdType.TestType]");
		}
	}

	@Test
	public void testInvalidNotQualifierInputAnyItem()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			construct(primaryContext, getTargetClass(), "TestWP1");
			assertFalse(parse(getSubTokenName() + "|!" + qualifier
					+ "[ALL|TestWP1]"));
			assertNoSideEffects();
		}

	}

	@Test
	public void testInvalidNotQualifierInputItemAny()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			construct(primaryContext, getTargetClass(), "TestWP1");
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP1|ALL]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidNotQualifierInputAnyType()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[ALL|TYPE=TestType]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidNotQualifierInputTypeAny()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TYPE=TestType|ALL]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInputInvalidNotQualifierAddsTypeNoSideEffect()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			construct(primaryContext, getTargetClass(), "TestWP1");
			construct(secondaryContext, getTargetClass(), "TestWP1");
			construct(primaryContext, getTargetClass(), "TestWP2");
			construct(secondaryContext, getTargetClass(), "TestWP2");
			construct(primaryContext, getTargetClass(), "TestWP3");
			construct(secondaryContext, getTargetClass(), "TestWP3");
			assertTrue(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP1|TestWP2]"));
			assertTrue(parseSecondary(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP1|TestWP2]"));
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP3|TYPE=]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInputInvalidNotQualifierAddsBasicNoSideEffect()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			construct(primaryContext, getTargetClass(), "TestWP1");
			construct(secondaryContext, getTargetClass(), "TestWP1");
			construct(primaryContext, getTargetClass(), "TestWP2");
			construct(secondaryContext, getTargetClass(), "TestWP2");
			construct(primaryContext, getTargetClass(), "TestWP3");
			construct(secondaryContext, getTargetClass(), "TestWP3");
			construct(primaryContext, getTargetClass(), "TestWP4");
			construct(secondaryContext, getTargetClass(), "TestWP4");
			assertTrue(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP1|TestWP2]"));
			assertTrue(parseSecondary(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP1|TestWP2]"));
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP3||TestWP4]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInputInvalidNotQualifierAddsAllNoSideEffect()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			construct(primaryContext, getTargetClass(), "TestWP1");
			construct(secondaryContext, getTargetClass(), "TestWP1");
			construct(primaryContext, getTargetClass(), "TestWP2");
			construct(secondaryContext, getTargetClass(), "TestWP2");
			construct(primaryContext, getTargetClass(), "TestWP3");
			construct(secondaryContext, getTargetClass(), "TestWP3");
			assertTrue(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP1|TestWP2]"));
			assertTrue(parseSecondary(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP1|TestWP2]"));
			assertFalse(parse(getSubTokenName() + '|' + "!" + qualifier
					+ "[TestWP3|ALL]"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testRoundRobinTestNotQualifierAll()
			throws PersistenceLayerException
	{
		if (allowsNotQualifier())
		{
			runRoundRobin(getSubTokenName() + "|!" + qualifier + "[ALL]");
		}
	}

	
	@Test
	public void testRoundRobinMultTypes()
			throws PersistenceLayerException
	{
		
		runRoundRobin(getSubTokenName() + '|' + qualifier + "[TYPE=Buckler|TYPE=Heavy|TYPE=Light]");
	}

}