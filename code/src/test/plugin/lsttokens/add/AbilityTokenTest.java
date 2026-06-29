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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet.AbilityChoiceSet;
import pcgen.cdom.base.ConcretePersistentTransitionChoice;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.choiceset.AbilityRefChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.core.Ability;
import pcgen.core.AbilityUtilities;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.AddLst;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbilityTokenTest extends AbstractCDOMTokenTestCase<CDOMObject>
{

	static AddLst token = new AddLst();
	static AbilityToken subtoken = new AbilityToken();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

	@BeforeEach
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

	protected Ability construct(LoadContext loadContext, String one)
	{
		Ability a = BuildUtilities.getFeatCat().newInstance();
		a.setName(one);
		loadContext.getReferenceContext().importObject(a);
		return a;
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
	void testInvalidInputEmptyString()
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputOnlySubToken()
	{
		assertFalse(parse(getSubTokenName()));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputOnlySubTokenPipe()
	{
		assertFalse(parse(getSubTokenName() + '|'));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputJoinOnly()
	{
		assertFalse(parse(getSubTokenName() + '|'
				+ Character.toString(getJoinCharacter())));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputStringOnlyCat()
	{
		assertFalse(parse(getSubTokenName() + '|' + "FEAT"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputStringOnlyCatPipe()
	{
		assertFalse(parse(getSubTokenName() + '|' + "FEAT" + '|'));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputStringOnlyCatNature()
	{
		assertFalse(parse(getSubTokenName() + '|' + "FEAT" + '|' + "NORMAL"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidNature()
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
	void testInvalidAutomaticNature()
	{
		try
		{
			assertFalse(parse(getSubTokenName() + '|' + "FEAT" + '|' + "AUTOMATIC"
					+ '|' + "FeatName"));
			assertNoSideEffects();
		}
		catch (IllegalArgumentException e)
		{
			// This is okay too
		}
	}

	@Test
	void testInvalidCategory()
	{
		assertFalse(parse(getSubTokenName() + '|' + "InvalidCat" + '|'
				+ "NORMAL" + '|' + "FeatName"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputUnconstructed()
	{
		System.err.println("!");
		assertTrue(parse(getSubTokenName() + '|' + "FEAT" + '|' + "NORMAL"
				+ '|' + "TestType"));
		assertConstructionError();
	}

	@Test
	void testInvalidInputJoinedDot()
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse(getSubTokenName() + '|' + "FEAT" + '|' + "NORMAL"
				+ '|' + "TestWP1.TestWP2"));
		assertConstructionError();
	}

	@Test
	void testInvalidInputNegativeFormula()
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "-1|FEAT|NORMAL|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputZeroFormula()
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "0|FEAT|NORMAL|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputAnyNature()
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "FEAT|ANY|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputTypeEmpty()
	{
		if (isTypeLegal())
		{
			assertFalse(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TYPE="));
			assertNoSideEffects();
		}
	}

	@Test
	void testInvalidInputTypeUnterminated()
	{
		if (isTypeLegal())
		{
			assertFalse(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TYPE=One."));
			assertNoSideEffects();
		}
	}

	@Test
	void testInvalidInputStacksNaN()
	{
		assertFalse(parse(getSubTokenName() + '|'
				+ "FEAT|NORMAL|STACKS=x,TestWP1" + getJoinCharacter()
				+ "TestWP2"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputOnlyStacks()
	{
		assertFalse(parse(getSubTokenName() + '|' + "FEAT|NORMAL|STACKS=4"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputMultTarget()
	{
		boolean ret = parse(getSubTokenName() + '|'
				+ "FEAT|NORMAL|TestWP1(Foo,Bar)" + getJoinCharacter()
				+ "TestWP2");
		if (ret)
		{
			assertConstructionError();
		}
		else
		{
			assertNoSideEffects();
		}
	}

	@Test
	void testInvalidInputClearDotTypeDoubleSeparator()
	{
		if (isTypeLegal())
		{
			assertFalse(parse(getSubTokenName() + '|'
					+ "FEAT|NORMAL|TYPE=One..Two"));
			assertNoSideEffects();
		}
	}

	@Test
	void testInvalidInputClearDotTypeFalseStart()
	{
		if (isTypeLegal())
		{
			assertFalse(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TYPE=.One"));
			assertNoSideEffects();
		}
	}

	@Test
	void testInvalidInputAll()
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
					assertConstructionError();
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
	void testInvalidInputTypeEquals()
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
					assertConstructionError();
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
	void testInvalidListEnd()
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1"
				+ getJoinCharacter()));
		assertNoSideEffects();
	}

	@Test
	void testInvalidListStart()
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "FEAT|NORMAL|"
				+ getJoinCharacter() + "TestWP1"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidListDoubleJoin()
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP2"
				+ getJoinCharacter() + getJoinCharacter() + "TestWP1"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputCheckMult()
	{
		// Explicitly do NOT build TestWP2
		construct(primaryContext, "TestWP1");
		assertTrue(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1"
				+ getJoinCharacter() + "TestWP2"));
		assertConstructionError();
	}

	@Test
	void testInvalidInputCheckTypeEqualLength()
	{
		// Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		// consume the |
		if (isTypeLegal())
		{
			construct(primaryContext, "TestWP1");
			assertTrue(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1"
					+ getJoinCharacter() + "TYPE=TestType" + getJoinCharacter()
					+ "TestWP2"));
			assertConstructionError();
		}
	}

	@Test
	void testInvalidInputCheckTypeDotLength()
	{
		// Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		// consume the |
		if (isTypeLegal())
		{
			construct(primaryContext, "TestWP1");
			assertTrue(parse(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1"
					+ getJoinCharacter() + "TYPE.TestType.OtherTestType"
					+ getJoinCharacter() + "TestWP2"));
			assertConstructionError();
		}
	}

	@Test
	void testValidInputTypeDot()
	{
		if (isTypeLegal())
		{
			CDOMObject a = construct(primaryContext, "TestWP1");
			a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
			CDOMObject b = construct(secondaryContext, "TestWP1");
			b.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
			assertTrue(parse(getSubTokenName() + '|'
					+ "FEAT|NORMAL|TYPE.TestType"));
			assertCleanConstruction();
		}
	}

	@Test
	void testRoundRobinOne() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1");
	}

	@Test
	void testRoundRobinParen() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1 (Test)");
		construct(secondaryContext, "TestWP1 (Test)");
		runRoundRobin(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1 (Test)");
	}

	@Test
	void testRoundRobinCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1 (Test)");
		construct(secondaryContext, "TestWP1 (Test)");
		runRoundRobin(getSubTokenName() + '|' + "4|FEAT|NORMAL|TestWP1 (Test)");
	}

	@Test
	void testRoundRobinFormulaCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1 (Test)");
		construct(secondaryContext, "TestWP1 (Test)");
		runRoundRobin(getSubTokenName() + '|'
				+ "INT|FEAT|NORMAL|TestWP1 (Test)");
	}

	@Test
	void testRoundRobinParenSub() throws PersistenceLayerException
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
	void testRoundRobinParenDoubleSub() throws PersistenceLayerException
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
	void testRoundRobinThree() throws PersistenceLayerException
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
	void testRoundRobinWithEqualType() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP1");
			construct(secondaryContext, "TestWP2");
			CDOMObject a = construct(primaryContext, "Typed1");
			a.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
			CDOMObject b = construct(secondaryContext, "Typed1");
			b.addToListFor(ListKey.TYPE, Type.getConstant("OtherTestType"));
			CDOMObject c = construct(primaryContext, "Typed2");
			c.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
			CDOMObject d = construct(secondaryContext, "Typed2");
			d.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
			runRoundRobin(getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1"
					+ getJoinCharacter() + "TestWP2" + getJoinCharacter()
					+ "TYPE=OtherTestType" + getJoinCharacter()
					+ "TYPE=TestType");
		}
	}

	@Test
	void testRoundRobinTestEquals() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			CDOMObject b = construct(primaryContext, "TestWP4");
			b.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
			CDOMObject d = construct(secondaryContext, "TestWP4");
			d.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
			runRoundRobin(getSubTokenName() + '|' + "FEAT|NORMAL|TYPE=TestType");
		}
	}

	@Test
	void testRoundRobinTestEqualThree() throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			CDOMObject a = construct(primaryContext, "TestWP1");
			a.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
			a.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
			a.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
			CDOMObject b = construct(secondaryContext, "TestWP1");
			b.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
			b.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
			b.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
			runRoundRobin(getSubTokenName() + '|'
					+ "FEAT|NORMAL|TYPE=TestAltType.TestThirdType.TestType");
		}
	}

	@Test
	void testInvalidInputAnyItem()
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
	void testInvalidInputItemAny()
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
	void testInvalidInputAnyType()
	{
		if (isTypeLegal() && isAllLegal())
		{
			assertFalse(parse(getSubTokenName() + '|' + "FEAT|NORMAL|ALL"
					+ getJoinCharacter() + "TYPE=TestType"));
			assertNoSideEffects();
		}
	}

	@Test
	void testInvalidInputTypeAny()
	{
		if (isTypeLegal() && isAllLegal())
		{
			assertFalse(parse(getSubTokenName() + '|'
					+ "FEAT|NORMAL|TYPE=TestType" + getJoinCharacter() + "ALL"));
			assertNoSideEffects();
		}
	}

	@Test
	void testInputInvalidAddsTypeNoSideEffect()
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
	void testInputInvalidAddsBasicNoSideEffect()
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
	void testInputInvalidAddsAllNoSideEffect()
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
	void testRoundRobinStacks() throws PersistenceLayerException
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
	void testRoundRobinStacksValue() throws PersistenceLayerException
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
	void testInvalidInputDoubleStacks()
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|'
				+ "FEAT|NORMAL|STACKS,STACKS,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoubleStack()
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|'
				+ "FEAT|NORMAL|STACKS=3,STACKS=2,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputStacksStack()
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|'
				+ "FEAT|NORMAL|STACKS,STACKS=2,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNegativeStack()
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|'
				+ "FEAT|NORMAL|STACKS=-4,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputZeroStack()
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|'
				+ "FEAT|NORMAL|STACKS=0,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	void testRoundRobinDupe() throws PersistenceLayerException
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

	@Test
	void testUnparseSingle()
	{
		List<CDOMReference<Ability>> refs = createSingle("TestWP1");
		createTC(refs, FormulaFactory.ONE);
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, getSubTokenName() + '|' + "FEAT|NORMAL|TestWP1");
	}

	private List<CDOMReference<Ability>> createSingle(String name)
	{
		List<CDOMReference<Ability>> refs = new ArrayList<>();
		Ability a = construct(primaryContext, name);
		CDOMDirectSingleRef<Ability> ar = CDOMDirectSingleRef.getRef(a);
		refs.add(ar);
		if (name.indexOf('(') != -1)
		{
			List<String> choices = new ArrayList<>();
			AbilityUtilities.getUndecoratedName(name, choices);
			assertEquals(1, choices.size());
			ar.setChoice(choices.get(0));
		}
		return refs;
	}

	@Test
	void testUnparseType()
	{
		List<CDOMReference<Ability>> refs = new ArrayList<>();
		CDOMGroupRef<Ability> ref = primaryContext.getReferenceContext()
			.getManufacturerId(BuildUtilities.getFeatCat())
			.getTypeReference(new String[]{"Foo", "Bar"});
		refs.add(ref);

		createTC(refs, FormulaFactory.ONE);
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, getSubTokenName() + '|' + "FEAT|NORMAL|TYPE=Bar.Foo");
	}

	private void createTC(List<CDOMReference<Ability>> refs, Formula count)
	{
		AbilityRefChoiceSet rcs = new AbilityRefChoiceSet(CDOMDirectSingleRef.getRef(BuildUtilities.getFeatCat()),
				refs, Nature.NORMAL);
		// TODO: Should this be present for the unit tests?
		//assertTrue("Invalid grouping state " + rcs.getGroupingState(), rcs.getGroupingState().isValid());
		AbilityChoiceSet cs = new AbilityChoiceSet(
				getSubToken().getTokenName(), rcs);
		cs.setTitle("Virtual Feat Selection");
		PersistentTransitionChoice<CNAbilitySelection> tc = new ConcretePersistentTransitionChoice<>(
				cs, count);
		tc.allowStack(false);
		// if (dupChoices != 0)
		// {
		// tc.setStackLimit(dupChoices);
		// }
		tc.setChoiceActor(subtoken);
		primaryProf.addToListFor(ListKey.ADD, tc);
	}

	@Test
	void testUnparseSingleThree()
	{
		List<CDOMReference<Ability>> refs = createSingle("TestWP1");
		createTC(refs, FormulaFactory.getFormulaFor(3));
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, getSubTokenName() + '|' + "3|FEAT|NORMAL|TestWP1");
	}

	@Test
	void testUnparseSingleNegative()
	{
		List<CDOMReference<Ability>> refs = createSingle("TestWP1");
		createTC(refs, FormulaFactory.getFormulaFor(-2));
		assertBadUnparse();
	}

	@Test
	void testUnparseSingleZero()
	{
		List<CDOMReference<Ability>> refs = createSingle("TestWP1");
		createTC(refs, FormulaFactory.getFormulaFor(0));
		assertBadUnparse();
	}

	@Test
	void testUnparseSingleVariable()
	{
		List<CDOMReference<Ability>> refs = createSingle("TestWP1");
		createTC(refs, FormulaFactory.getFormulaFor("Formula"));
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, getSubTokenName() + '|' + "Formula|FEAT|NORMAL|TestWP1");
	}

	@Test
	void testUnparseSingleAll()
	{
		if (isAllLegal())
		{
			List<CDOMReference<Ability>> refs = createSingle("TestWP1");
			CDOMGroupRef<Ability> ref = primaryContext.getReferenceContext()
				.getManufacturerId(BuildUtilities.getFeatCat()).getAllReference();
			refs.add(ref);
			createTC(refs, FormulaFactory.ONE);
			assertBadUnparse();
		}
	}

	@Test
	void testUnparseAll()
	{
		if (isAllLegal())
		{
			List<CDOMReference<Ability>> refs = new ArrayList<>();
			CDOMGroupRef<Ability> ref = primaryContext.getReferenceContext()
				.getManufacturerId(BuildUtilities.getFeatCat()).getAllReference();
			refs.add(ref);
			createTC(refs, FormulaFactory.ONE);
			String[] unparsed = getToken().unparse(primaryContext, primaryProf);
			expectSingle(unparsed, getSubTokenName() + '|' + "FEAT|NORMAL|ALL");
		}
	}

	@Test
	void testUnparseTypeAll()
	{
		if (isAllLegal())
		{
			List<CDOMReference<Ability>> refs = new ArrayList<>();
			CDOMGroupRef<Ability> ref = primaryContext.getReferenceContext()
				.getManufacturerId(BuildUtilities.getFeatCat())
				.getTypeReference(new String[]{"Foo", "Bar"});
			refs.add(ref);
			ref = primaryContext.getReferenceContext()
				.getManufacturerId(BuildUtilities.getFeatCat()).getAllReference();
			refs.add(ref);
			createTC(refs, FormulaFactory.ONE);
			assertBadUnparse();
		}
	}

	@Test
	void testUnparseComplex()
	{
		List<CDOMReference<Ability>> refs = createSingle("TestWP1");
		AbilityRefChoiceSet rcs = new AbilityRefChoiceSet(CDOMDirectSingleRef.getRef(BuildUtilities.getFeatCat()),
				refs, Nature.VIRTUAL);
		assert (rcs.getGroupingState().isValid());
		AbilityChoiceSet cs = new AbilityChoiceSet(
				getSubToken().getTokenName(), rcs);
		cs.setTitle("Virtual Feat Selection");
		PersistentTransitionChoice<CNAbilitySelection> tc = new ConcretePersistentTransitionChoice<>(
				cs, FormulaFactory.getFormulaFor(3));
		tc.allowStack(true);
		tc.setStackLimit(2);
		tc.setChoiceActor(subtoken);
		primaryProf.addToListFor(ListKey.ADD, tc);
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, getSubTokenName() + '|' + "3|FEAT|VIRTUAL|STACKS=2,TestWP1");
	}

	@Override
	protected void additionalSetup(LoadContext context)
	{
		super.additionalSetup(context);
		construct(context, "Dummy");
	}
}
