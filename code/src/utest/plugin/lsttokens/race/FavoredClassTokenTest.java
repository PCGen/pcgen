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
package plugin.lsttokens.race;

import org.junit.Test;

import pcgen.cdom.enumeration.SubClassCategory;
import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.core.SubClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractListTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class FavoredClassTokenTest extends
		AbstractListTokenTestCase<Race, PCClass>
{

	static FavclassToken token = new FavclassToken();
	static CDOMTokenLoader<Race> loader = new CDOMTokenLoader<Race>(Race.class);

	@Override
	public Class<Race> getCDOMClass()
	{
		return Race.class;
	}

	@Override
	public CDOMLoader<Race> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<Race> getToken()
	{
		return token;
	}

	@Override
	public Class<PCClass> getTargetClass()
	{
		return PCClass.class;
	}

	@Override
	public boolean isTypeLegal()
	{
		return false;
	}

	@Override
	public boolean isAllLegal()
	{
		return true;
	}

	@Override
	public boolean isClearDotLegal()
	{
		return false;
	}

	@Override
	public boolean isClearLegal()
	{
		return false;
	}

	@Override
	public char getJoinCharacter()
	{
		return '|';
	}

	@Test
	public void dummyTest()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

	@Test
	public void testRoundRobinList() throws PersistenceLayerException
	{
		runRoundRobin("%LIST");
	}

	@Test
	public void testInvalidInputList() throws PersistenceLayerException
	{
		assertFalse(parse("ANY" + getJoinCharacter() + "%LIST"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputSubClassNoSub()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("TestWP1."));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputSubClassNoClass()
			throws PersistenceLayerException
	{
		assertFalse(parse(".TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputSubDoubleSeparator()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("TestWP1..Two"));
		assertNoSideEffects();
	}

	@Test
	public void testCategorizationFail() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertTrue(parse("TestWP1.Two"));
		SubClass obj = primaryContext.ref.constructCDOMObject(SubClass.class,
				"Two");
		SubClassCategory cat = SubClassCategory.getConstant("TestWP2");
		primaryContext.ref.reassociateCategory(cat, obj);
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testCategorizationPass() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertTrue(parse("TestWP1.Two"));
		SubClass obj = primaryContext.ref.constructCDOMObject(SubClass.class,
				"Two");
		SubClassCategory cat = SubClassCategory.getConstant("TestWP2");
		primaryContext.ref.reassociateCategory(cat, obj);
		obj = primaryContext.ref.constructCDOMObject(SubClass.class, "Two");
		cat = SubClassCategory.getConstant("TestWP1");
		primaryContext.ref.reassociateCategory(cat, obj);
		assertTrue(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinThreeSub() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		SubClass obj = primaryContext.ref.constructCDOMObject(SubClass.class,
				"Sub");
		SubClassCategory cat = SubClassCategory.getConstant("TestWP2");
		primaryContext.ref.reassociateCategory(cat, obj);
		obj = secondaryContext.ref.constructCDOMObject(SubClass.class, "Sub");
		secondaryContext.ref.reassociateCategory(cat, obj);
		runRoundRobin("TestWP1" + getJoinCharacter() + "TestWP2.Sub"
				+ getJoinCharacter() + "TestWP3");
	}

	@Test
	public void testInvalidInputAnyItem() throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse("ANY" + getJoinCharacter() + "TestWP1"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputItemAny() throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse("TestWP1" + getJoinCharacter() + "ANY"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputChooseEmptyString()
			throws PersistenceLayerException
	{
		assertFalse(parse("CHOOSE:"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputChooseJoinOnly()
			throws PersistenceLayerException
	{
		assertFalse(parse("CHOOSE:" + Character.toString(getJoinCharacter())));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputChooseString() throws PersistenceLayerException
	{
		assertTrue(parse("CHOOSE:String"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputChooseJoinedComma()
			throws PersistenceLayerException
	{
		if (getJoinCharacter() != ',')
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			assertTrue(parse("CHOOSE:TestWP1,TestWP2"));
			assertFalse(primaryContext.ref.validate());
		}
	}

	@Test
	public void testInvalidInputChooseJoinedPipe()
			throws PersistenceLayerException
	{
		if (getJoinCharacter() != '|')
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			boolean parse = parse("CHOOSE:TestWP1|TestWP2");
			if (parse)
			{
				assertFalse(primaryContext.ref.validate());
			}
			else
			{
				assertNoSideEffects();
			}
		}
	}

	@Test
	public void testInvalidInputChooseJoinedDot()
			throws PersistenceLayerException
	{
		if (getJoinCharacter() != '.')
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			assertTrue(parse("CHOOSE:TestWP1.TestWP2"));
			assertFalse(primaryContext.ref.validate());
		}
	}

	@Test
	public void testInvalidInputChooseTypeEmpty()
			throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(parse("CHOOSE:TYPE="));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputChooseTypeUnterminated()
			throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(parse("CHOOSE:TYPE=One."));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputChooseClearDotTypeDoubleSeparator()
			throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(parse("CHOOSE:TYPE=One..Two"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputChooseClearDotTypeFalseStart()
			throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			assertFalse(parse("CHOOSE:TYPE=.One"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputChooseAll() throws PersistenceLayerException
	{
		if (!isAllLegal())
		{
			try
			{
				boolean parse = parse("CHOOSE:ALL");
				if (parse)
				{
					// Only need to check if parsed as true
					assertFalse(primaryContext.ref.validate());
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
	// assertTrue(token.parse(primaryContext, primaryProf, "TYPE=TestType"));
	// assertFalse(primaryContext.ref.validate());
	// }
	// }
	//

	@Test
	public void testInvalidChooseListEnd() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("CHOOSE:TestWP1" + getJoinCharacter()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidChooseListStart() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("CHOOSE:" + getJoinCharacter() + "TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidChooseListDoubleJoin()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("CHOOSE:TestWP2" + getJoinCharacter()
				+ getJoinCharacter() + "TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputChooseCheckMult()
			throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2
		construct(primaryContext, "TestWP1");
		assertTrue(parse("CHOOSE:TestWP1" + getJoinCharacter() + "TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputChooseCheckTypeEqualLength()
			throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		// consume the |
		if (isTypeLegal())
		{
			construct(primaryContext, "TestWP1");
			assertTrue(parse("CHOOSE:TestWP1" + getJoinCharacter()
					+ "TYPE=TestType" + getJoinCharacter() + "TestWP2"));
			assertFalse(primaryContext.ref.validate());
		}
	}

	@Test
	public void testInvalidInputChooseCheckTypeDotLength()
			throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2 (this checks that the TYPE= doesn't
		// consume the |
		if (isTypeLegal())
		{
			construct(primaryContext, "TestWP1");
			assertTrue(parse("CHOOSE:TestWP1" + getJoinCharacter()
					+ "TYPE.TestType.OtherTestType" + getJoinCharacter()
					+ "TestWP2"));
			assertFalse(primaryContext.ref.validate());
		}
	}

	@Test
	public void testRoundRobinChooseWithEqualType()
			throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			construct(primaryContext, "TestWP1");
			construct(primaryContext, "TestWP2");
			construct(secondaryContext, "TestWP1");
			construct(secondaryContext, "TestWP2");
			runRoundRobin("CHOOSE:TestWP1" + getJoinCharacter() + "TestWP2"
					+ getJoinCharacter() + "TYPE=OtherTestType"
					+ getJoinCharacter() + "TYPE=TestType");
		}
	}

	@Test
	public void testRoundRobinChooseTestEquals()
			throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin("CHOOSE:TYPE=TestType");
		}
	}

	@Test
	public void testRoundRobinChooseTestEqualThree()
			throws PersistenceLayerException
	{
		if (isTypeLegal())
		{
			runRoundRobin("CHOOSE:TYPE=TestAltType.TestThirdType.TestType");
		}
	}

	@Test
	public void testInvalidInputChooseAllItem()
			throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse("CHOOSE:ALL" + getJoinCharacter() + "TestWP1"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputChooseItemAll()
			throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse("CHOOSE:TestWP1" + getJoinCharacter() + "ALL"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputChooseAnyType()
			throws PersistenceLayerException
	{
		if (isTypeLegal() && isAllLegal())
		{
			assertFalse(parse("CHOOSE:ALL" + getJoinCharacter()
					+ "TYPE=TestType"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputChooseTypeAny()
			throws PersistenceLayerException
	{
		if (isTypeLegal() && isAllLegal())
		{
			assertFalse(parse("CHOOSE:TYPE=TestType" + getJoinCharacter()
					+ "ALL"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInputInvalidChooseAddsTypeNoSideEffect()
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
			assertTrue(parse("CHOOSE:TestWP1" + getJoinCharacter() + "TestWP2"));
			assertTrue(parseSecondary("CHOOSE:TestWP1" + getJoinCharacter()
					+ "TestWP2"));
			assertFalse(parse("CHOOSE:TestWP3" + getJoinCharacter() + "TYPE="));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInputInvalidChooseAddsBasicNoSideEffect()
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
		assertTrue(parse("CHOOSE:TestWP1" + getJoinCharacter() + "TestWP2"));
		assertTrue(parseSecondary("CHOOSE:TestWP1" + getJoinCharacter()
				+ "TestWP2"));
		assertFalse(parse("CHOOSE:TestWP3" + getJoinCharacter()
				+ getJoinCharacter() + "TestWP4"));
		assertNoSideEffects();
	}

	@Test
	public void testInputInvalidChooseAddsAllNoSideEffect()
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
			assertTrue(parse("CHOOSE:TestWP1" + getJoinCharacter() + "TestWP2"));
			assertTrue(parseSecondary("CHOOSE:TestWP1" + getJoinCharacter()
					+ "TestWP2"));
			assertFalse(parse("CHOOSE:TestWP3" + getJoinCharacter() + "ALL"));
			assertNoSideEffects();
		}
	}

	@Override
	public boolean allowDups()
	{
		return false;
	}
}
