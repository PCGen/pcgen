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

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.persistence.PersistenceLayerException;
import plugin.lsttokens.testsupport.ConsolidationRule.AppendingConsolidation;

public abstract class AbstractGlobalTypeSafeListTestCase<T> extends
		AbstractGlobalTokenTestCase
{

	public abstract T getConstant(String string);

	public abstract char getJoinCharacter();

	public abstract ListKey<T> getListKey();

	@Test
	public void testValidInputSimple() throws PersistenceLayerException
	{
		List<?> coll;
		Assert.assertTrue(parse("Rheinhessen"));
		coll = primaryProf.getListFor(getListKey());
		Assert.assertEquals(1, coll.size());
		Assert.assertTrue(coll.contains(getConstant("Rheinhessen")));
	}

	@Test
	public void testValidInputNonEnglish() throws PersistenceLayerException
	{
		List<?> coll;
		Assert.assertTrue(parse("Niederösterreich"));
		coll = primaryProf.getListFor(getListKey());
		Assert.assertEquals(1, coll.size());
		Assert.assertTrue(coll.contains(getConstant("Niederösterreich")));
	}

	@Test
	public void testValidInputSpace() throws PersistenceLayerException
	{
		List<?> coll;
		Assert.assertTrue(parse("Finger Lakes"));
		coll = primaryProf.getListFor(getListKey());
		Assert.assertEquals(1, coll.size());
		Assert.assertTrue(coll.contains(getConstant("Finger Lakes")));
	}

	@Test
	public void testValidInputHyphen() throws PersistenceLayerException
	{
		List<?> coll;
		Assert.assertTrue(parse("Languedoc-Roussillon"));
		coll = primaryProf.getListFor(getListKey());
		Assert.assertEquals(1, coll.size());
		Assert.assertTrue(coll.contains(getConstant("Languedoc-Roussillon")));
	}

	@Test
	public void testValidInputY() throws PersistenceLayerException
	{
		List<?> coll;
		Assert.assertTrue(parse("Yarra Valley"));
		coll = primaryProf.getListFor(getListKey());
		Assert.assertEquals(1, coll.size());
		Assert.assertTrue(coll.contains(getConstant("Yarra Valley")));
	}

	@Test
	public void testValidInputList() throws PersistenceLayerException
	{
		List<?> coll;
		Assert.assertTrue(parse("Niederösterreich" + getJoinCharacter()
				+ "Finger Lakes"));
		coll = primaryProf.getListFor(getListKey());
		Assert.assertEquals(2, coll.size());
		Assert.assertTrue(coll.contains(getConstant("Niederösterreich")));
		Assert.assertTrue(coll.contains(getConstant("Finger Lakes")));
	}

	@Test
	public void testValidInputMultList() throws PersistenceLayerException
	{
		List<?> coll;
		Assert.assertTrue(parse("Niederösterreich" + getJoinCharacter()
				+ "Finger Lakes"));
		Assert.assertTrue(parse("Languedoc-Roussillon" + getJoinCharacter()
				+ "Rheinhessen"));
		coll = primaryProf.getListFor(getListKey());
		Assert.assertEquals(4, coll.size());
		Assert.assertTrue(coll.contains(getConstant("Niederösterreich")));
		Assert.assertTrue(coll.contains(getConstant("Finger Lakes")));
		Assert.assertTrue(coll.contains(getConstant("Languedoc-Roussillon")));
		Assert.assertTrue(coll.contains(getConstant("Rheinhessen")));
	}

	// FIXME Someday, when PCGen doesn't write out crappy stuff into custom
	// items
	// @Test
	// public void testInvalidListEmpty() throws PersistenceLayerException
	// {
	// primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
	// assertFalse(parse( ""));
	// }

	@Test
	public void testInvalidEmpty() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
		Assert.assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListEnd() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
		Assert.assertFalse(parse("TestWP1" + getJoinCharacter()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListStart() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
		Assert.assertFalse(parse(getJoinCharacter() + "TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP1");
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "TestWP2");
		Assert.assertFalse(parse("TestWP2" + getJoinCharacter() + getJoinCharacter()
				+ "TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinBase() throws PersistenceLayerException
	{
		runRoundRobin("Rheinhessen");
	}

	@Test
	public void testRoundRobinWithSpace() throws PersistenceLayerException
	{
		runRoundRobin("Finger Lakes");
	}

	@Test
	public void testRoundRobinNonEnglishAndN() throws PersistenceLayerException
	{
		runRoundRobin("Niederösterreich");
	}

	@Test
	public void testRoundRobinHyphen() throws PersistenceLayerException
	{
		runRoundRobin("Languedoc-Roussillon");
	}

	@Test
	public void testRoundRobinY() throws PersistenceLayerException
	{
		runRoundRobin("Yarra Valley");
	}

	@Test
	public void testRoundRobinThree() throws PersistenceLayerException
	{
		runRoundRobin("Rheinhessen" + getJoinCharacter() + "Yarra Valley"
				+ getJoinCharacter() + "Languedoc-Roussillon");
	}

	public static String[] getConstants()
	{
		return new String[] { "Niederösterreich", "Finger Lakes",
				"Languedoc-Roussillon", "Rheinhessen", "Yarra Valley" };
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
			assertNoSideEffects();
		}
	}

	@Test
	public void testInputInvalidClearDot() throws PersistenceLayerException
	{
		if (isClearDotLegal() && requiresPreconstruction())
		{
			// DoNotConstruct TestWP1
			Assert.assertTrue(parse(".CLEAR.TestWP1"));
			assertConstructionError();
		}
	}

	protected abstract boolean requiresPreconstruction();

	@Test
	public void testInputInvalidAddsAfterClearDotNoSideEffect()
			throws PersistenceLayerException
	{
		if (isClearDotLegal())
		{
			Assert.assertTrue(parse("TestWP1" + getJoinCharacter() + "TestWP2"));
			Assert.assertTrue(parseSecondary("TestWP1" + getJoinCharacter()
					+ "TestWP2"));
			Assert.assertFalse(parse("TestWP3" + getJoinCharacter() + ".CLEAR.TestWP2"
					+ getJoinCharacter() + "ALL"));
			assertNoSideEffects();
		}
	}

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

	@Test
	public void testInputInvalidAddsAfterClearNoSideEffect()
			throws PersistenceLayerException
	{
		if (isClearLegal() && isAllLegal())
		{
			Assert.assertTrue(parse("TestWP1" + getJoinCharacter() + "TestWP2"));
			Assert.assertTrue(parseSecondary("TestWP1" + getJoinCharacter()
					+ "TestWP2"));
			Assert.assertFalse(parse(Constants.LST_DOT_CLEAR + getJoinCharacter() + "TestWP3"
					+ getJoinCharacter() + "ALL"));
			assertNoSideEffects();
		}
	}

	protected abstract boolean isAllLegal();

	@Test
	public void testRoundRobinTestAll() throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			runRoundRobin("ALL");
		}
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

	@Test
	public void testUnparseNull() throws PersistenceLayerException
	{
		primaryProf.removeListFor(getListKey());
		Assert.assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	@Test
	public void testUnparseSingle() throws PersistenceLayerException
	{
		primaryProf.addToListFor(getListKey(),
				getConstant(getLegalValue()));
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		expectSingle(unparsed, getLegalValue());
	}

	@Test
	public void testUnparseNullInList() throws PersistenceLayerException
	{
		primaryProf.addToListFor(getListKey(), null);
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
		primaryProf.addToListFor(getListKey(),
				getConstant(getLegalValue()));
		primaryProf.addToListFor(getListKey(),
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
