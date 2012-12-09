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
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.PCStat;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SourceFileLoader;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.ReferenceContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.QualifierToken;
import plugin.lsttokens.AutoLst;
import plugin.lsttokens.TypeLst;
import plugin.lsttokens.ability.MultToken;
import plugin.lsttokens.ability.VisibleToken;
import plugin.lsttokens.auto.LangToken;
import plugin.lsttokens.equipment.ProficiencyToken;
import plugin.primitive.language.LangBonusToken;
import plugin.qualifier.pobject.QualifiedToken;

public abstract class AbstractQualifierTokenTestCase<T extends CDOMObject, TC extends CDOMObject>
		extends AbstractTokenTestCase<T>
{

	private static QualifierToken<CDOMObject> qt = new QualifiedToken<CDOMObject>();

	public abstract CDOMSecondaryToken<?> getSubToken();

	private final String qualifier;
	private final String target;
	private final String token;
	private final boolean negate;

	protected AbstractQualifierTokenTestCase(String tok, String tgt, boolean allowNegation)
	{
		token = tok;
		target = tgt;
		if (tgt == null)
		{
			qualifier = token;
		}
		else
		{
			qualifier = token + "=" + target;
		}
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

	protected CDOMObject construct(LoadContext loadContext, String one)
	{
		return construct(loadContext, getTargetClass(), one);
	}

	protected CDOMObject construct(LoadContext loadContext,
			Class<? extends CDOMObject> cl, String one)
	{
		return loadContext.ref.constructCDOMObject(cl, one);
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
		assertConstructionError();
	}

	@Test
	public void testQualifierAsPrim() throws PersistenceLayerException
	{
		try
		{
			boolean parse = parse(getSubTokenName() + '|' + qualifier + "[" + qualifier
				+ "]");
			if (parse)
			{
				assertConstructionError();
			}
			else
			{
				assertNoSideEffects();
			}
		}
		catch (IllegalArgumentException e)
		{
			assertNoSideEffects();
		}
	}

	@Test
	public void testQualifierBadPrim() throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenName() + '|' + qualifier + "[String]"));
		assertConstructionError();
	}

	@Test
	public void testQualifierNoConstruct() throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		// Explicitly do NOT build TestWP0
		assertTrue(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP0|TestWP1]"));
		assertConstructionError();
	}

	@Test
	public void testQualifierTypeCheck() throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		// this checks that the TYPE= doesn't consume the |
		assertTrue(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP1|TYPE=TestType|TestWP0]"));
		assertConstructionError();
	}

	@Test
	public void testQualifierTypeDotCheck() throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		// this checks that the TYPE. doesn't consume the |
		assertTrue(parse(getSubTokenName() + '|' + qualifier + "[TestWP1|"
				+ "TYPE.TestType.OtherTestType|TestWP0]"));
		assertConstructionError();
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
		assertConstructionError();
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
		try
		{
			boolean parse = parse(getSubTokenName() + '|' + qualifier + "[" + qualifier
				+ "]");
			if (parse)
			{
				assertConstructionError();
			}
			else
			{
				assertNoSideEffects();
			}
		}
		catch (IllegalArgumentException e)
		{
			assertNoSideEffects();
		}
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
		assertConstructionError();
	}

	@Test
	public void testInvalidInputJoinedDotQualified()
			throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(primaryContext, getTargetClass(), "TestWP2");
		assertTrue(parse(getSubTokenName() + '|' + qualifier
				+ "[TestWP1.TestWP2]"));
		assertConstructionError();
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
		assertConstructionError();
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
		CDOMObject a = construct(primaryContext, "Typed1");
		a.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
		CDOMObject b = construct(primaryContext, "Typed2");
		b.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
		CDOMObject c = construct(secondaryContext, "Typed1");
		c.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
		CDOMObject d = construct(secondaryContext, "Typed2");
		d.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
		CDOMObject e = construct(primaryContext, "Typed3");
		e.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
		CDOMObject f = construct(primaryContext, "Typed4");
		f.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
		CDOMObject g = construct(secondaryContext, "Typed3");
		g.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
		CDOMObject h = construct(secondaryContext, "Typed4");
		h.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
		runRoundRobin(getSubTokenName() + '|' + qualifier
				+ "[TYPE=Bar|TYPE=Goo]|" + qualifier + "[TYPE=Foo|TYPE=Yea]");
	}

	@Test
	public void testValidQualifiedInputLotsAnd()
			throws PersistenceLayerException
	{
		CDOMObject a = construct(primaryContext, "Typed1");
		a.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
		CDOMObject b = construct(primaryContext, "Typed2");
		b.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
		CDOMObject c = construct(secondaryContext, "Typed1");
		c.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
		CDOMObject d = construct(secondaryContext, "Typed2");
		d.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
		CDOMObject e = construct(primaryContext, "Typed3");
		e.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
		CDOMObject f = construct(primaryContext, "Typed4");
		f.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
		CDOMObject g = construct(secondaryContext, "Typed3");
		g.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
		CDOMObject h = construct(secondaryContext, "Typed4");
		h.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
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
		assertConstructionError();
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
		assertConstructionError();
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
		CDOMObject a = construct(primaryContext, "Typed1");
		a.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
		CDOMObject b = construct(primaryContext, "Typed2");
		b.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
		CDOMObject c = construct(secondaryContext, "Typed1");
		c.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
		CDOMObject d = construct(secondaryContext, "Typed2");
		d.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
		CDOMObject e = construct(primaryContext, "Typed3");
		e.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
		CDOMObject g = construct(secondaryContext, "Typed3");
		g.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
		runRoundRobin(getSubTokenName() + '|' + qualifier
				+ "[!TYPE=Type1,TYPE=Type2,TYPE=Type3]");
	}

	@Test
	public void testRoundRobinQualifiedFourAndOr()
			throws PersistenceLayerException
	{
		CDOMObject a = construct(primaryContext, "Typed1");
		a.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
		CDOMObject b = construct(primaryContext, "Typed2");
		b.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
		CDOMObject c = construct(secondaryContext, "Typed1");
		c.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
		CDOMObject d = construct(secondaryContext, "Typed2");
		d.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
		CDOMObject e = construct(primaryContext, "Typed3");
		e.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
		CDOMObject f = construct(primaryContext, "Typed4");
		f.addToListFor(ListKey.TYPE, Type.getConstant("Type4"));
		CDOMObject g = construct(secondaryContext, "Typed3");
		g.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
		CDOMObject h = construct(secondaryContext, "Typed4");
		h.addToListFor(ListKey.TYPE, Type.getConstant("Type4"));
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
		CDOMObject a = construct(primaryContext, "Typed1");
		a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
		CDOMObject b = construct(primaryContext, "Typed2");
		b.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
		CDOMObject c = construct(secondaryContext, "Typed1");
		c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
		CDOMObject d = construct(secondaryContext, "Typed2");
		d.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
		runRoundRobin(getSubTokenName() + '|' + qualifier
				+ "[TestWP1|TestWP2|TYPE=OtherTestType|TYPE=TestType]");
	}

	@Test
	public void testRoundRobinQualifiedTestEquals()
			throws PersistenceLayerException
	{
		CDOMObject a = construct(primaryContext, "Typed1");
		a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
		CDOMObject c = construct(secondaryContext, "Typed1");
		c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
		runRoundRobin(getSubTokenName() + '|' + qualifier + "[TYPE=TestType]");
	}

	@Test
	public void testRoundRobinQualifiedTestEqualThree()
			throws PersistenceLayerException
	{
		CDOMObject a = construct(primaryContext, "Typed1");
		a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
		a.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
		a.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
		CDOMObject c = construct(secondaryContext, "Typed1");
		c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
		c.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
		c.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
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
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		runRoundRobin(getSubTokenName() + "|" + qualifier + "[ALL]");
	}

	@Test
	public void testInvalidInputJoinedDotNotQualifierAlone()
			throws PersistenceLayerException
	{
		assertTrue(parse(getSubTokenName() + '|' + "PC.!" + qualifier + ""));
		assertConstructionError();
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
			assertConstructionError();
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
			assertConstructionError();
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
			assertConstructionError();
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
			assertConstructionError();
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
			CDOMObject a = construct(primaryContext, "Typed1");
			a.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
			CDOMObject b = construct(primaryContext, "Typed2");
			b.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
			CDOMObject c = construct(secondaryContext, "Typed1");
			c.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
			CDOMObject d = construct(secondaryContext, "Typed2");
			d.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
			CDOMObject e = construct(primaryContext, "Typed3");
			e.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
			CDOMObject f = construct(primaryContext, "Typed4");
			f.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
			CDOMObject g = construct(secondaryContext, "Typed3");
			g.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
			CDOMObject h = construct(secondaryContext, "Typed4");
			h.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
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
			CDOMObject a = construct(primaryContext, "Typed1");
			a.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
			CDOMObject b = construct(primaryContext, "Typed2");
			b.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
			CDOMObject c = construct(secondaryContext, "Typed1");
			c.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
			CDOMObject d = construct(secondaryContext, "Typed2");
			d.addToListFor(ListKey.TYPE, Type.getConstant("Yea"));
			CDOMObject e = construct(primaryContext, "Typed3");
			e.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
			CDOMObject f = construct(primaryContext, "Typed4");
			f.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
			CDOMObject g = construct(secondaryContext, "Typed3");
			g.addToListFor(ListKey.TYPE, Type.getConstant("Bar"));
			CDOMObject h = construct(secondaryContext, "Typed4");
			h.addToListFor(ListKey.TYPE, Type.getConstant("Goo"));
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
			assertConstructionError();
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
			assertConstructionError();
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
			CDOMObject a = construct(primaryContext, "Typed1");
			a.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
			CDOMObject b = construct(primaryContext, "Typed2");
			b.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
			CDOMObject c = construct(secondaryContext, "Typed1");
			c.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
			CDOMObject d = construct(secondaryContext, "Typed2");
			d.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
			CDOMObject e = construct(primaryContext, "Typed3");
			e.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
			CDOMObject g = construct(secondaryContext, "Typed3");
			g.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
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
			CDOMObject a = construct(primaryContext, "Typed1");
			a.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
			CDOMObject b = construct(primaryContext, "Typed2");
			b.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
			CDOMObject c = construct(secondaryContext, "Typed1");
			c.addToListFor(ListKey.TYPE, Type.getConstant("Type1"));
			CDOMObject d = construct(secondaryContext, "Typed2");
			d.addToListFor(ListKey.TYPE, Type.getConstant("Type2"));
			CDOMObject e = construct(primaryContext, "Typed3");
			e.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
			CDOMObject f = construct(primaryContext, "Typed4");
			f.addToListFor(ListKey.TYPE, Type.getConstant("Type4"));
			CDOMObject g = construct(secondaryContext, "Typed3");
			g.addToListFor(ListKey.TYPE, Type.getConstant("Type3"));
			CDOMObject h = construct(secondaryContext, "Typed4");
			h.addToListFor(ListKey.TYPE, Type.getConstant("Type4"));
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
			CDOMObject a = construct(primaryContext, "Typed1");
			a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
			CDOMObject b = construct(primaryContext, "Typed2");
			b.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
			CDOMObject c = construct(secondaryContext, "Typed1");
			c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
			CDOMObject d = construct(secondaryContext, "Typed2");
			d.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
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
			CDOMObject a = construct(primaryContext, "Typed1");
			a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
			CDOMObject c = construct(secondaryContext, "Typed1");
			c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
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
			CDOMObject a = construct(primaryContext, "Typed1");
			a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
			a.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
			a.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
			CDOMObject c = construct(secondaryContext, "Typed1");
			c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
			c.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
			c.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
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
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		if (allowsNotQualifier())
		{
			runRoundRobin(getSubTokenName() + "|!" + qualifier + "[ALL]");
		}
		else
		{
			boolean parse = parse(getSubTokenName() + "|!" + qualifier + "[ALL]");
			if (parse)
			{
				assertConstructionError();
			}
			else
			{
				assertNoSideEffects();
			}
		}
	}

	@Test
	public void testRoundRobinTestQualifierRaw()
		throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		if (allowsLoneQualifier())
		{
			runRoundRobin(getSubTokenName() + '|' + qualifier);
		}
		else
		{
			boolean parse = parse(getSubTokenName() + '|' + qualifier);
			if (parse)
			{
				assertConstructionError();
			}
			else
			{
				assertNoSideEffects();
			}
		}
	}

	@Test
	public void testRoundRobinTestNotQualifierRaw()
		throws PersistenceLayerException
	{
		construct(primaryContext, getTargetClass(), "TestWP1");
		construct(secondaryContext, getTargetClass(), "TestWP1");
		if (allowsNotQualifier() && allowsLoneQualifier())
		{
			runRoundRobin(getSubTokenName() + "|!" + qualifier);
		}
		else
		{
			boolean parse = parse(getSubTokenName() + "|!" + qualifier);
			if (parse)
			{
				assertConstructionError();
			}
			else
			{
				assertNoSideEffects();
			}
		}
	}

	protected boolean allowsLoneQualifier()
	{
		return true;
	}

	@Test
	public void testRoundRobinMultTypes()
			throws PersistenceLayerException
	{
		CDOMObject a = construct(primaryContext, "Typed1");
		a.addToListFor(ListKey.TYPE, Type.getConstant("Buckler"));
		CDOMObject b = construct(primaryContext, "Typed2");
		b.addToListFor(ListKey.TYPE, Type.getConstant("Heavy"));
		CDOMObject c = construct(secondaryContext, "Typed1");
		c.addToListFor(ListKey.TYPE, Type.getConstant("Buckler"));
		CDOMObject d = construct(secondaryContext, "Typed2");
		d.addToListFor(ListKey.TYPE, Type.getConstant("Heavy"));
		CDOMObject e = construct(primaryContext, "Typed3");
		e.addToListFor(ListKey.TYPE, Type.getConstant("Light"));
		CDOMObject g = construct(secondaryContext, "Typed3");
		g.addToListFor(ListKey.TYPE, Type.getConstant("Light"));
		runRoundRobin(getSubTokenName() + '|' + qualifier + "[TYPE=Buckler|TYPE=Heavy|TYPE=Light]");
	}
	
	@Test
	public void testTargetCheck() throws PersistenceLayerException
	{
		if (target == null)
		{
			assertFalse(parse(getSubTokenName() + '|' + token + "=Tgt"
				+ "[TYPE=TestType|ALL]"));
		}
		else
		{
			assertFalse(parse(getSubTokenName() + '|' + token 
				+ "[TYPE=TestType|ALL]"));
		}
		assertNoSideEffects();
	}

	private static final MultToken ABILITY_MULT_TOKEN = new plugin.lsttokens.ability.MultToken();
	private static final plugin.lsttokens.choose.LangToken CHOOSE_LANG_TOKEN = new plugin.lsttokens.choose.LangToken();
	private static final VisibleToken ABILITY_VISIBLE_TOKEN = new plugin.lsttokens.ability.VisibleToken();
	private static final AutoLst AUTO_TOKEN = new plugin.lsttokens.AutoLst();
	private static final LangToken AUTO_LANG_TOKEN = new plugin.lsttokens.auto.LangToken();
	private static final ProficiencyToken EQUIP_PROFICIENCY_TOKEN = new plugin.lsttokens.equipment.ProficiencyToken();
	private static final TypeLst EQUIP_TYPE_TOKEN = new plugin.lsttokens.TypeLst();
	private static final LangBonusToken LANGBONUS_PRIM = new plugin.primitive.language.LangBonusToken();
	private static final plugin.qualifier.language.PCToken PC_QUAL = new plugin.qualifier.language.PCToken();

	protected void finishLoad()
	{
		primaryContext.ref.buildDeferredObjects();
		primaryContext.ref.buildDerivedObjects();
		primaryContext.resolveDeferredTokens();
		assertTrue(primaryContext.ref.resolveReferences(null));
		primaryContext.resolvePostDeferredTokens();
	}

	protected PCStat str;
	protected PCStat cha;
	protected PCStat dex;
	protected PCStat wis;
	protected PCStat intel;
	protected PCAlignment lg;
	protected PCAlignment ln;
	protected PCAlignment le;
	protected PCAlignment ng;
	protected PCAlignment tn;
	protected PCAlignment ne;
	protected PCAlignment cg;
	protected PCAlignment cn;
	protected PCAlignment ce;
	protected SizeAdjustment colossal;
	protected SizeAdjustment gargantuan;
	protected SizeAdjustment huge;
	protected SizeAdjustment large;
	protected SizeAdjustment medium;
	protected SizeAdjustment small;
	protected SizeAdjustment tiny;
	protected SizeAdjustment diminutive;
	protected SizeAdjustment fine;

	protected void setUpPC() throws PersistenceLayerException
	{
		TokenRegistration.register(AUTO_LANG_TOKEN);
		TokenRegistration.register(ABILITY_VISIBLE_TOKEN);
		TokenRegistration.register(AUTO_TOKEN);
		TokenRegistration.register(CHOOSE_LANG_TOKEN);
		TokenRegistration.register(ABILITY_MULT_TOKEN);
		TokenRegistration.register(EQUIP_TYPE_TOKEN);
		TokenRegistration.register(EQUIP_PROFICIENCY_TOKEN);
		TokenRegistration.register(LANGBONUS_PRIM);
		TokenRegistration.register(PC_QUAL);
				
		Globals.createEmptyRace();
		Globals.setUseGUI(false);
		Globals.emptyLists();
		GameMode gamemode = SettingsHandler.getGame();
		
		str = createStat("Strength", "STR");
		str.put(VariableKey.getConstant("LOADSCORE"), FormulaFactory
			.getFormulaFor("STRSCORE"));
		str.put(VariableKey.getConstant("OFFHANDLIGHTBONUS"), FormulaFactory
			.getFormulaFor(2));
		dex = createStat("Dexterity", "DEX");
		PCStat con = createStat("Constitution", "CON");
		intel = createStat("Intelligence", "INT");
		wis = createStat("Wisdom", "WIS");
		cha = createStat("Charisma", "CHA");

		ReferenceContext ref = Globals.getContext().ref;
		lg = createAlignment("Lawful Good", "LG");
		ref.importObject(lg);
		ln = createAlignment("Lawful Neutral", "LN");
		ref.importObject(ln);
		le = createAlignment("Lawful Evil", "LE");
		ref.importObject(le);
		ng = createAlignment("Neutral Good", "NG");
		ref.importObject(ng);
		tn = createAlignment("True Neutral", "TN");
		ref.importObject(tn);
		ne = createAlignment("Neutral Evil", "NE");
		ref.importObject(ne);
		cg = createAlignment("Chaotic Good", "CG");		ref.importObject(cg);
		cn = createAlignment("Chaotic Neutral", "CN");
		ref.importObject(cn);
		ce = createAlignment("Chaotic Evil", "CE");
		ref.importObject(ce);
		ref.importObject(createAlignment("None", "NONE"));
		ref.importObject(createAlignment("Deity's", "Deity"));

		gamemode.setBonusFeatLevels("3|3");

		SettingsHandler.setGame("3.5");

		ref.importObject(str);
		ref.importObject(dex);
		ref.importObject(con);
		ref.importObject(intel);
		ref.importObject(wis);
		ref.importObject(cha);

		fine = createSize("Fine");
		diminutive = createSize("Diminutive");
		tiny = createSize("Tiny");
		small = createSize("Small");
		medium = createSize("Medium");
		medium.put(ObjectKey.IS_DEFAULT_SIZE, true);
		large = createSize("Large");
		huge = createSize("Huge");
		gargantuan = createSize("Gargantuan");
		colossal = createSize("Colossal");

		for (PCStat stat : ref.getOrderSortedCDOMObjects(PCStat.class))
		{
			ref.registerAbbreviation(stat, stat.getAbb());
		}
		for (PCAlignment al : ref.getOrderSortedCDOMObjects(PCAlignment.class))
		{
			ref.registerAbbreviation(al, al.getAbb());
		}
		SourceFileLoader.createLangBonusObject(Globals.getContext());
	}

	private PCStat createStat(String name, String abb)
	{
		PCStat stat = new PCStat();
		stat.setName(name);
		stat.put(StringKey.ABB, abb);
		stat.put(FormulaKey.STAT_MOD, FormulaFactory.getFormulaFor("floor(SCORE/2)-5"));
		stat.put(VariableKey.getConstant("MAXLEVELSTAT=" + stat.getAbb()),
				FormulaFactory.getFormulaFor(stat.getAbb() + "SCORE-10"));
		return stat;
	}

	private SizeAdjustment createSize(String name)
	{
		final String abb  = name.substring(0, 1);

		final SizeAdjustment sa = new SizeAdjustment();

		sa.setName(name);
		sa.put(StringKey.ABB, abb);

		Globals.getContext().ref.importObject(sa);
		Globals.getContext().ref.registerAbbreviation(sa, sa.getAbbreviation());
		return sa;
	}

	public static PCAlignment createAlignment(final String longName,
		final String shortName)
	{
		final PCAlignment align = new PCAlignment();
		align.setName(longName);
		align.put(StringKey.ABB, shortName);
		return align;
	}

	public void testEmptyIdentity() throws InstantiationException,
			IllegalAccessException
	{
		QualifierToken<?> one = getQualifierClass().newInstance();
		QualifierToken<?> two = getQualifierClass().newInstance();
		assertTrue(one.equals(two));
	}

	protected abstract Class<? extends QualifierToken> getQualifierClass();
}
