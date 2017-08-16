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
package plugin.lsttokens.pcclass.level;

import org.junit.Test;

import pcgen.base.formula.DividingFormula;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.processor.HitDieFormula;
import pcgen.cdom.processor.HitDieLock;
import pcgen.cdom.processor.HitDieStep;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class HitDieTokenTest extends AbstractPCClassLevelTokenTestCase
{

	static HitdieLst token = new HitdieLst();

	@Override
	public CDOMPrimaryToken<PCClassLevel> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputTooManyLimits()
			throws PersistenceLayerException
	{
		assertFalse(parse("15|CLASS=Fighter|CLASS.TYPE=Base", 2));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNotALimit() throws PersistenceLayerException
	{
		assertFalse(parse("15|PRECLASS:1,Fighter", 2));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyLimit() throws PersistenceLayerException
	{
		assertFalse(parse("15|CLASS=", 2));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyTypeLimit()
			throws PersistenceLayerException
	{
		assertFalse(parse("15|CLASS.TYPE=", 2));
		assertNoSideEffects();
	}

	@Test
	public void testValidInputDivideNegative() throws PersistenceLayerException
	{
		assertFalse(parse("%/-2", 2));
		assertNoSideEffects();
	}

	@Test
	public void testValidInputDivideZero() throws PersistenceLayerException
	{
		assertFalse(parse("%/0", 2));
		assertNoSideEffects();
	}

	@Test
	public void testValidInputDivide() throws PersistenceLayerException
	{
		assertTrue(parse("%/4", 2));
	}

	@Test
	public void testInvalidInputAddNegative() throws PersistenceLayerException
	{
		assertFalse(parse("%+-3", 2));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputAddZero() throws PersistenceLayerException
	{
		assertFalse(parse("%+0", 2));
		assertNoSideEffects();
	}

	@Test
	public void testValidInputAdd() throws PersistenceLayerException
	{
		assertTrue(parse("%+4", 2));
	}

	@Test
	public void testInvalidInputMultiplyNegative()
			throws PersistenceLayerException
	{
		assertFalse(parse("%*-3", 2));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputMultiplyZero() throws PersistenceLayerException
	{
		assertFalse(parse("%*0", 2));
		assertNoSideEffects();
	}

	@Test
	public void testValidInputMultiply() throws PersistenceLayerException
	{
		assertTrue(parse("%*4", 2));
	}

	@Test
	public void testInvalidInputSubtractNegative()
			throws PersistenceLayerException
	{
		assertFalse(parse("%--3", 2));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputSubtractZero() throws PersistenceLayerException
	{
		assertFalse(parse("%-0", 2));
		assertNoSideEffects();
	}

	@Test
	public void testValidInputSubtract() throws PersistenceLayerException
	{
		assertTrue(parse("%-4", 2));
	}

	@Test
	public void testInvalidInputUpNegative() throws PersistenceLayerException
	{
		assertFalse(parse("%up-3", 2));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputUpZero() throws PersistenceLayerException
	{
		assertFalse(parse("%up0", 2));
		assertNoSideEffects();
	}

	@Test
	public void testValidInputUp() throws PersistenceLayerException
	{
		assertTrue(parse("%up4", 2));
	}

	@Test
	public void testInvalidInputUpTooBig() throws PersistenceLayerException
	{
		assertFalse(parse("%up5", 2));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputUpReallyTooBig()
			throws PersistenceLayerException
	{
		assertFalse(parse("%up15", 2));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputHUpNegative() throws PersistenceLayerException
	{
		assertFalse(parse("%Hup-3", 2));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputHUpZero() throws PersistenceLayerException
	{
		assertFalse(parse("%Hup0", 2));
		assertNoSideEffects();
	}

	@Test
	public void testValidInputHUp() throws PersistenceLayerException
	{
		assertTrue(parse("%Hup4", 2));
	}

	@Test
	public void testInvalidInputDownNegative() throws PersistenceLayerException
	{
		assertFalse(parse("%down-3", 2));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDownZero() throws PersistenceLayerException
	{
		assertFalse(parse("%down0", 2));
		assertNoSideEffects();
	}

	@Test
	public void testValidInputDown() throws PersistenceLayerException
	{
		assertTrue(parse("%down4", 2));
	}

	@Test
	public void testInvalidInputDownTooBig() throws PersistenceLayerException
	{
		assertFalse(parse("%down5", 3));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDownReallyTooBig()
			throws PersistenceLayerException
	{
		assertFalse(parse("%down15", 3));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputHdownNegative()
			throws PersistenceLayerException
	{
		assertFalse(parse("%Hdown-3", 2));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputHdownZero() throws PersistenceLayerException
	{
		assertFalse(parse("%Hdown0", 2));
		assertNoSideEffects();
	}

	@Test
	public void testValidInputHdown() throws PersistenceLayerException
	{
		assertTrue(parse("%Hdown4", 2));
	}

	@Test
	public void testInvalidInputNegative() throws PersistenceLayerException
	{
		assertFalse(parse("-3", 2));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputZero() throws PersistenceLayerException
	{
		assertFalse(parse("0", 2));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDecimal() throws PersistenceLayerException
	{
		assertFalse(parse("3.5", 2));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputMisspell() throws PersistenceLayerException
	{
		assertFalse(parse("%upn5", 2));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinInteger() throws PersistenceLayerException
	{
		runRoundRobin("2");
	}

	@Test
	public void testRoundRobinAdd() throws PersistenceLayerException
	{
		runRoundRobin("%+2");
	}

	@Test
	public void testRoundRobinSubtract() throws PersistenceLayerException
	{
		runRoundRobin("%-2");
	}

	@Test
	public void testRoundRobinMultiply() throws PersistenceLayerException
	{
		runRoundRobin("%*2");
	}

	@Test
	public void testRoundRobinDivide() throws PersistenceLayerException
	{
		runRoundRobin("%/2");
	}

	@Test
	public void testRoundRobinUp() throws PersistenceLayerException
	{
		runRoundRobin("%up2");
	}

	@Test
	public void testRoundRobinHup() throws PersistenceLayerException
	{
		runRoundRobin("%Hup2");
	}

	@Test
	public void testRoundRobinDown() throws PersistenceLayerException
	{
		runRoundRobin("%down2");
	}

	@Test
	public void testRoundRobinHdown() throws PersistenceLayerException
	{
		runRoundRobin("%Hdown2");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "%down2";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}

	@Override
	protected String getLegalValue()
	{
		return "%Hup2";
	}

	@Test
	public void testUnparseNull() throws PersistenceLayerException
	{
		primaryProf1.put(ObjectKey.HITDIE, null);
		assertNull(getToken().unparse(primaryContext, primaryProf1));
	}

	@Test
	public void testUnparseLegal() throws PersistenceLayerException
	{
		primaryProf1.put(ObjectKey.HITDIE, new HitDieLock(new HitDie(1)));
		expectSingle(getToken().unparse(primaryContext, primaryProf1), "1");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUnparseGenericsFail() throws PersistenceLayerException
	{
		ObjectKey objectKey = ObjectKey.HITDIE;
		primaryProf1.put(objectKey, new Object());
		try
		{
			getToken().unparse(primaryContext, primaryProf1);
			fail();
		}
		catch (ClassCastException e)
		{
			//Yep!
		}
	}

	@Test
	public void testUnparseZeroSteps() throws PersistenceLayerException
	{
		try
		{
			primaryProf1.put(ObjectKey.HITDIE,
					new HitDieStep(0, new HitDie(12)));
			assertBadUnparse();
		}
		catch (IllegalArgumentException e)
		{
			// Good here too :)
		}
	}

	@Test
	public void testUnparseNegativeLevel() throws PersistenceLayerException
	{
		try
		{
			primaryProf1.put(ObjectKey.HITDIE, new HitDieLock(new HitDie(-1)));
			assertBadUnparse();
		}
		catch (IllegalArgumentException e)
		{
			// Good here too :)
		}
	}

	@Test
	public void testUnparseZeroDivide() throws PersistenceLayerException
	{
		try
		{
			primaryProf1.put(ObjectKey.HITDIE, new HitDieFormula(
					new DividingFormula(0)));
			assertBadUnparse();
		}
		catch (IllegalArgumentException e)
		{
			// Good here too :)
		}
	}

	/*
	 * TODO Need to find owner for this responsibility
	 */
	// @Test
	// public void testUnparseNegativeDivide() throws PersistenceLayerException
	// {
	// try
	// {
	// primaryProf1.put(ObjectKey.HITDIE, new HitDieFormula(new
	// DividingFormula(-3)));
	// assertBadUnparse();
	// }
	// catch (IllegalArgumentException e)
	// {
	// //Good here too :)
	// }
	// }
	//
	// @Test
	// public void testUnparseZeroMult() throws PersistenceLayerException
	// {
	// try
	// {
	// primaryProf1.put(ObjectKey.HITDIE, new HitDieFormula(new
	// MultiplyingFormula(0)));
	// assertBadUnparse();
	// }
	// catch (IllegalArgumentException e)
	// {
	// //Good here too :)
	// }
	// }
	//
	// @Test
	// public void testUnparseNegativeMult() throws PersistenceLayerException
	// {
	// try
	// {
	// primaryProf1.put(ObjectKey.HITDIE, new HitDieFormula(new
	// MultiplyingFormula(-3)));
	// assertBadUnparse();
	// }
	// catch (IllegalArgumentException e)
	// {
	// //Good here too :)
	// }
	// }
	//
	// @Test
	// public void testUnparseZeroAdd() throws PersistenceLayerException
	// {
	// try
	// {
	// primaryProf1.put(ObjectKey.HITDIE, new HitDieFormula(new
	// AddingFormula(0)));
	// assertBadUnparse();
	// }
	// catch (IllegalArgumentException e)
	// {
	// //Good here too :)
	// }
	// }
	//
	// @Test
	// public void testUnparseNegativeAdd() throws PersistenceLayerException
	// {
	// try
	// {
	// primaryProf1.put(ObjectKey.HITDIE, new HitDieFormula(new
	// AddingFormula(-3)));
	// assertBadUnparse();
	// }
	// catch (IllegalArgumentException e)
	// {
	// //Good here too :)
	// }
	// }
	//
	// @Test
	// public void testUnparseZeroSub() throws PersistenceLayerException
	// {
	// try
	// {
	// primaryProf1.put(ObjectKey.HITDIE, new HitDieFormula(new
	// SubtractingFormula(0)));
	// assertBadUnparse();
	// }
	// catch (IllegalArgumentException e)
	// {
	// //Good here too :)
	// }
	// }
	//
	// @Test
	// public void testUnparseNegativeSub() throws PersistenceLayerException
	// {
	// try
	// {
	// primaryProf1.put(ObjectKey.HITDIE, new HitDieFormula(new
	// SubtractingFormula(-3)));
	// assertBadUnparse();
	// }
	// catch (IllegalArgumentException e)
	// {
	// //Good here too :)
	// }
	// }
	//
	// @Test
	// public void testUnparseBigSteps() throws PersistenceLayerException
	// {
	// try
	// {
	// primaryProf1.put(ObjectKey.HITDIE, new HitDieStep(8, new HitDie(12)));
	// assertBadUnparse();
	// }
	// catch (IllegalArgumentException e)
	// {
	// //Good here too :)
	// }
	// }
	//
	// @Test
	// public void testUnparseBigNegativeSteps() throws
	// PersistenceLayerException
	// {
	// try
	// {
	// primaryProf1.put(ObjectKey.HITDIE, new HitDieStep(-8, new HitDie(12)));
	// assertBadUnparse();
	// }
	// catch (IllegalArgumentException e)
	// {
	// //Good here too :)
	// }
	// }
	//
	// @Test
	// public void testUnparseBadBase() throws PersistenceLayerException
	// {
	// try
	// {
	// primaryProf1.put(ObjectKey.HITDIE, new HitDieStep(1, new HitDie(6)));
	//			assertBadUnparse();
	//		}
	//		catch (IllegalArgumentException e)
	//		{
	//			//Good here too :)
	//		}
	//	}

	private void assertBadUnparse()
	{
		assertNull(getToken().unparse(primaryContext, primaryProf1));
		assertTrue(primaryContext.getWriteMessageCount() > 0);
	}

}
