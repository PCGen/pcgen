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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.persistence.PersistenceLayerException;
import plugin.lsttokens.testsupport.ConsolidationRule.AppendingConsolidation;

public abstract class AbstractTypeSafeListTestCase<T extends CDOMObject, LT>
		extends AbstractCDOMTokenTestCase<T>
{

	protected abstract boolean requiresPreconstruction();

	public abstract LT getConstant(String string);

	public abstract char getJoinCharacter();

	public abstract ListKey<LT> getListKey();

	public boolean clearsByDefault()
	{
		return false;
	}

	@Test
	public void testValidInputSimple() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
		List<?> coll;
		Assert.assertTrue(parse("Rheinhessen"));
		coll = getUnparseTarget().getListFor(getListKey());
		Assert.assertEquals(1, coll.size());
		Assert.assertTrue(coll.contains(getConstant("Rheinhessen")));
		assertCleanConstruction();
	}

	@Test
	public void testValidInputNonEnglish() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
				"Niederösterreich");
		List<?> coll;
		Assert.assertTrue(parse("Niederösterreich"));
		coll = getUnparseTarget().getListFor(getListKey());
		Assert.assertEquals(1, coll.size());
		Assert.assertTrue(coll.contains(getConstant("Niederösterreich")));
		assertCleanConstruction();
	}

	@Test
	public void testValidInputSpace() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Finger Lakes");
		List<?> coll;
		Assert.assertTrue(parse("Finger Lakes"));
		coll = getUnparseTarget().getListFor(getListKey());
		Assert.assertEquals(1, coll.size());
		Assert.assertTrue(coll.contains(getConstant("Finger Lakes")));
		assertCleanConstruction();
	}

	@Test
	public void testValidInputHyphen() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
				"Languedoc-Roussillon");
		List<?> coll;
		Assert.assertTrue(parse("Languedoc-Roussillon"));
		coll = getUnparseTarget().getListFor(getListKey());
		Assert.assertEquals(1, coll.size());
		Assert.assertTrue(coll.contains(getConstant("Languedoc-Roussillon")));
		assertCleanConstruction();
	}

	@Test
	public void testValidInputY() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Yarra Valley");
		List<?> coll;
		Assert.assertTrue(parse("Yarra Valley"));
		coll = getUnparseTarget().getListFor(getListKey());
		Assert.assertEquals(1, coll.size());
		Assert.assertTrue(coll.contains(getConstant("Yarra Valley")));
		assertCleanConstruction();
	}

	@Test
	public void testValidInputList() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
				"Niederösterreich");
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Finger Lakes");
		List<?> coll;
		Assert.assertTrue(parse("Niederösterreich" + getJoinCharacter()
				+ "Finger Lakes"));
		coll = getUnparseTarget().getListFor(getListKey());
		Assert.assertEquals(2, coll.size());
		Assert.assertTrue(coll.contains(getConstant("Niederösterreich")));
		Assert.assertTrue(coll.contains(getConstant("Finger Lakes")));
		assertCleanConstruction();
	}

	@Test
	public void testValidInputMultList() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
				"Niederösterreich");
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Finger Lakes");
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
				"Languedoc-Roussillon");
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
		List<?> coll;
		Assert.assertTrue(parse("Niederösterreich" + getJoinCharacter()
				+ "Finger Lakes"));
		Assert.assertTrue(parse("Languedoc-Roussillon" + getJoinCharacter()
				+ "Rheinhessen"));
		coll = getUnparseTarget().getListFor(getListKey());
		Assert.assertEquals(clearsByDefault() ? 2 : 4, coll.size());
		if (!clearsByDefault())
		{
			Assert.assertTrue(coll.contains(getConstant("Niederösterreich")));
			Assert.assertTrue(coll.contains(getConstant("Finger Lakes")));
		}
		Assert.assertTrue(coll.contains(getConstant("Languedoc-Roussillon")));
		Assert.assertTrue(coll.contains(getConstant("Rheinhessen")));
		assertCleanConstruction();
	}

	@Test
	public void testInvalidListEmpty() throws PersistenceLayerException
	{
		Assert.assertFalse(parse(""));
		Assert.assertNull(primaryProf.getListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
		Assert.assertFalse(parse(""));
		Assert.assertNull(primaryProf.getListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListEnd() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
		Assert.assertFalse(parse("TestWP1" + getJoinCharacter()));
		Assert.assertNull(primaryProf.getListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListStart() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
		Assert.assertFalse(parse(getJoinCharacter() + "TestWP1"));
		Assert.assertNull(primaryProf.getListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP2");
		Assert.assertFalse(parse("TestWP2" + getJoinCharacter() + getJoinCharacter()
				+ "TestWP1"));
		Assert.assertNull(primaryProf.getListFor(getListKey()));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinBase() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
		secondaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
		runRoundRobin("Rheinhessen");
	}

	@Test
	public void testRoundRobinWithSpace() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Finger Lakes");
		secondaryContext.getReferenceContext()
				.constructCDOMObject(getCDOMClass(), "Finger Lakes");
		runRoundRobin("Finger Lakes");
	}

	@Test
	public void testRoundRobinNonEnglishAndN() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
				"Niederösterreich");
		secondaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
				"Niederösterreich");
		runRoundRobin("Niederösterreich");
	}

	@Test
	public void testRoundRobinHyphen() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
				"Languedoc-Roussillon");
		secondaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
				"Languedoc-Roussillon");
		runRoundRobin("Languedoc-Roussillon");
	}

	@Test
	public void testRoundRobinY() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Yarra Valley");
		secondaryContext.getReferenceContext()
				.constructCDOMObject(getCDOMClass(), "Yarra Valley");
		runRoundRobin("Yarra Valley");
	}

	@Test
	public void testRoundRobinThree() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
		secondaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Yarra Valley");
		secondaryContext.getReferenceContext()
				.constructCDOMObject(getCDOMClass(), "Yarra Valley");
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
				"Languedoc-Roussillon");
		secondaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
				"Languedoc-Roussillon");
		runRoundRobin("Rheinhessen" + getJoinCharacter() + "Yarra Valley"
				+ getJoinCharacter() + "Languedoc-Roussillon");
	}

	@Test
	public void testRoundRobinThreeDupe() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
		secondaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
				"Languedoc-Roussillon");
		secondaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
				"Languedoc-Roussillon");
		runRoundRobin("Rheinhessen" + getJoinCharacter() + "Rheinhessen"
				+ getJoinCharacter() + "Languedoc-Roussillon");
	}

	public abstract boolean isClearLegal();

	public abstract boolean isClearDotLegal();

	@Test
	public void testReplacementInputs() throws PersistenceLayerException
	{
		String[] unparsed;
		if (isClearLegal())
		{
			Assert.assertTrue(parse(Constants.LST_DOT_CLEAR));
			unparsed = getToken().unparse(primaryContext, primaryProf);
			Assert.assertNull("Expected item to be null", unparsed);
		}
		if (isClearDotLegal())
		{
			Assert.assertTrue(parse(".CLEAR.TestWP1"));
			unparsed = getToken().unparse(primaryContext, primaryProf);
			Assert.assertNull("Expected item to be equal", unparsed);
		}
		Assert.assertTrue(parse("TestWP1"));
		Assert.assertTrue(parse("TestWP2"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		Assert.assertEquals("Expected item to be equal", "TestWP1"
				+ getJoinCharacter() + "TestWP2", unparsed[0]);
		if (isClearLegal())
		{
			Assert.assertTrue(parse(Constants.LST_DOT_CLEAR));
			unparsed = getToken().unparse(primaryContext, primaryProf);
			Assert.assertNull("Expected item to be null", unparsed);
		}
	}

	@Test
	public void testReplacementInputsTwo() throws PersistenceLayerException
	{
		String[] unparsed;
		Assert.assertTrue(parse("TestWP1"));
		Assert.assertTrue(parse("TestWP2"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		Assert.assertEquals("Expected item to be equal", "TestWP1"
				+ getJoinCharacter() + "TestWP2", unparsed[0]);
		if (isClearDotLegal())
		{
			Assert.assertTrue(parse(".CLEAR.TestWP1"));
			unparsed = getToken().unparse(primaryContext, primaryProf);
			Assert.assertEquals("Expected item to be equal", "TestWP2", unparsed[0]);
		}
	}

	@Test
	public void testInputInvalidClear() throws PersistenceLayerException
	{
		if (isClearLegal())
		{
			Assert.assertFalse(parse("TestWP1" + getJoinCharacter() + Constants.LST_DOT_CLEAR));
			Assert.assertNull(getUnparseTarget().getListFor(getListKey()));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInputInvalidClearDot() throws PersistenceLayerException
	{
		if (isClearDotLegal())
		{
			Assert.assertTrue(parse(".CLEAR.TestWP1"));
			if (requiresPreconstruction())
			{
				assertConstructionError();
			}
		}
	}

	// TODO This is only invalid if ALL is legal
	// @Test
	// public void testInputInvalidAddsAfterClearDotNoSideEffect()
	// throws PersistenceLayerException
	// {
	// if (isClearDotLegal() && isAllLegal())
	// {
	// assertTrue(parse("TestWP1" + getJoinCharacter() + "TestWP2"));
	// assertTrue(parseSecondary("TestWP1" + getJoinCharacter()
	// + "TestWP2"));
	// assertFalse(parse("TestWP3" + getJoinCharacter() + ".CLEAR.TestWP2"
	// + getJoinCharacter() + "ALL"));
	// assertNoSideEffects();
	// }
	// }

	@Test
	public void testInputInvalidAddsBasicNoSideEffect()
			throws PersistenceLayerException
	{
		Assert.assertTrue(parse("TestWP1" + getJoinCharacter() + "TestWP2"));
		Assert.assertTrue(parseSecondary("TestWP1" + getJoinCharacter() + "TestWP2"));
		Assert.assertFalse(parse("TestWP3" + getJoinCharacter() + getJoinCharacter()
				+ "TestWP4"));
		assertNoSideEffects();
	}

	// TODO This is only invalid if ALL is legal
	// @Test
	// public void testInputInvalidAddsAfterClearNoSideEffect()
	// throws PersistenceLayerException
	// {
	// if (isClearLegal())
	// {
	// assertTrue(parse(
	// "TestWP1" + getJoinCharacter() + "TestWP2"));
	// assertTrue(getToken().parse(secondaryContext, secondaryProf,
	// "TestWP1" + getJoinCharacter() + "TestWP2"));
	// assertEquals("Test setup failed", primaryGraph, secondaryGraph);
	// assertFalse(getToken().parse(
	// primaryContext,
	// primaryProf,
	// Constants.LST_DOT_CLEAR + getJoinCharacter() + "TestWP3" + getJoinCharacter()
	// + "ALL"));
	// assertEquals("Bad Clear had Side Effects", primaryGraph,
	// secondaryGraph);
	// }
	// }

	@Override
	protected String getAlternateLegalValue()
	{
		return "TestWP2";
	}

	@Override
	protected String getLegalValue()
	{
		return "TestWP1";
	}

	protected CDOMObject getUnparseTarget()
	{
		return primaryProf;
	}

	@Test
	public void testUnparseNull() throws PersistenceLayerException
	{
		getUnparseTarget().removeListFor(getListKey());
		Assert.assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	@Test
	public void testUnparseSingle() throws PersistenceLayerException
	{
		getUnparseTarget().addToListFor(getListKey(),
				getConstant(getLegalValue()));
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, getLegalValue());
	}

	@Test
	public void testUnparseNullInList() throws PersistenceLayerException
	{
		getUnparseTarget().addToListFor(getListKey(), null);
		try
		{
			getToken().unparse(primaryContext, primaryProf);
			Assert.fail();
		}
		catch (NullPointerException e)
		{
			// Yep!
		}
	}

	@Test
	public void testUnparseMultiple() throws PersistenceLayerException
	{
		getUnparseTarget().addToListFor(getListKey(),
				getConstant(getLegalValue()));
		getUnparseTarget().addToListFor(getListKey(),
				getConstant(getAlternateLegalValue()));
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, getLegalValue() + getJoinCharacter()
				+ getAlternateLegalValue());
	}

	/*
	 * TODO Need to define the appropriate behavior here - is this the token's responsibility?
	 */
	// @Test
	// public void testUnparseGenericsFail() throws PersistenceLayerException
	// {
	// ListKey objectKey = getListKey();
	// primaryProf.addToListFor(objectKey, new Object());
	// try
	// {
	// String[] unparsed = getToken().unparse(primaryContext, primaryProf);
	// fail();
	// }
	// catch (ClassCastException e)
	// {
	// //Yep!
	//		}
	//	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return new AppendingConsolidation(getJoinCharacter());
	}
}
