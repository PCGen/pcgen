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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.base.formula.DividingFormula;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.processor.HitDieFormula;
import pcgen.cdom.processor.HitDieLock;
import pcgen.cdom.processor.HitDieStep;
import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

class HitDieTokenTest extends AbstractCDOMTokenTestCase<Race>
{

	static HitdieToken token = new HitdieToken();
	static CDOMTokenLoader<Race> loader = new CDOMTokenLoader<>();

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

	@Test
	void testInvalidInputTooManyLimits()
	{
		assertFalse(parse("15|CLASS=Fighter|CLASS.TYPE=Base"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNotALimit()
	{
		assertFalse(parse("15|PRECLASS:1,Fighter"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputEmptyLimit()
	{
		assertFalse(parse("15|CLASS="));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputEmptyTypeLimit()
	{
		assertFalse(parse("15|CLASS.TYPE="));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputStartDotTypeLimit()
	{
		assertFalse(parse("15|CLASS.TYPE=.Strange"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputEndDotTypeLimit()
	{
		assertFalse(parse("15|CLASS.TYPE=Strange."));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoubleDotTypeLimit()
	{
		assertFalse(parse("15|CLASS.TYPE=Prestige..Strange"));
		assertNoSideEffects();
	}

	@Test
	void testValidInputDivideNegative()
	{
		assertFalse(parse("%/-2"));
		assertNoSideEffects();
	}

	@Test
	void testValidInputDivideZero()
	{
		assertFalse(parse("%/0"));
		assertNoSideEffects();
	}

	@Test
	void testValidInputDivide()
	{
		assertTrue(parse("%/4"));
	}

	@Test
	void testInvalidInputAddNegative()
	{
		assertFalse(parse("%+-3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputAddZero()
	{
		assertFalse(parse("%+0"));
		assertNoSideEffects();
	}

	@Test
	void testValidInputAdd()
	{
		assertTrue(parse("%+4"));
	}

	@Test
	void testInvalidInputMultiplyNegative()
	{
		assertFalse(parse("%*-3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputMultiplyZero()
	{
		assertFalse(parse("%*0"));
		assertNoSideEffects();
	}

	@Test
	void testValidInputMultiply()
	{
		assertTrue(parse("%*4"));
	}

	@Test
	void testInvalidInputSubtractNegative()
	{
		assertFalse(parse("%--3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputSubtractZero()
	{
		assertFalse(parse("%-0"));
		assertNoSideEffects();
	}

	@Test
	void testValidInputSubtract()
	{
		assertTrue(parse("%-4"));
	}

	@Test
	void testInvalidInputUpNegative()
	{
		assertFalse(parse("%up-3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputUpZero()
	{
		assertFalse(parse("%up0"));
		assertNoSideEffects();
	}

	@Test
	void testValidInputUp()
	{
		assertTrue(parse("%up4"));
	}

	@Test
	void testInvalidInputUpTooBig()
	{
		assertFalse(parse("%up5"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputUpReallyTooBig()
	{
		assertFalse(parse("%up15"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputHUpNegative()
	{
		assertFalse(parse("%Hup-3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputHUpZero()
	{
		assertFalse(parse("%Hup0"));
		assertNoSideEffects();
	}

	@Test
	void testValidInputHUp()
	{
		assertTrue(parse("%Hup4"));
	}

	@Test
	void testInvalidInputDownNegative()
	{
		assertFalse(parse("%down-3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDownZero()
	{
		assertFalse(parse("%down0"));
		assertNoSideEffects();
	}

	@Test
	void testValidInputDown()
	{
		assertTrue(parse("%down4"));
	}

	@Test
	void testInvalidInputHdownNegative()
	{
		assertFalse(parse("%Hdown-3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputHdownZero()
	{
		assertFalse(parse("%Hdown0"));
		assertNoSideEffects();
	}

	@Test
	void testValidInputHdown()
	{
		assertTrue(parse("%Hdown4"));
	}

	@Test
	void testInvalidInputDownTooBig()
	{
		assertFalse(parse("%down5"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDownReallyTooBig()
	{
		assertFalse(parse("%down15"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNegative()
	{
		assertFalse(parse("-3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputZero()
	{
		assertFalse(parse("0"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDecimal()
	{
		assertFalse(parse("3.5"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputMisspell()
	{
		assertFalse(parse("%upn5"));
		assertNoSideEffects();
	}

	@Test
	void testRoundRobinInteger() throws PersistenceLayerException
	{
		runRoundRobin("2");
	}

	@Test
	void testRoundRobinIntegerClass() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("2|CLASS=Fighter");
	}

	@Test
	void testRoundRobinIntegerType() throws PersistenceLayerException
	{
		PCClass a = primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		a.addToListFor(ListKey.TYPE, Type.getConstant("Base"));
		PCClass c = secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		c.addToListFor(ListKey.TYPE, Type.getConstant("Base"));
		runRoundRobin("2|CLASS.TYPE=Base");
	}

	@Test
	void testRoundRobinAdd() throws PersistenceLayerException
	{
		runRoundRobin("%+2");
	}

	@Test
	void testRoundRobinAddClass() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("%+2|CLASS=Fighter");
	}

	@Test
	void testRoundRobinAddType() throws PersistenceLayerException
	{
		PCClass a = primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		a.addToListFor(ListKey.TYPE, Type.getConstant("Base"));
		PCClass c = secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		c.addToListFor(ListKey.TYPE, Type.getConstant("Base"));
		runRoundRobin("%+2|CLASS.TYPE=Base");
	}

	@Test
	void testRoundRobinSubtract() throws PersistenceLayerException
	{
		runRoundRobin("%-2");
	}

	@Test
	void testRoundRobinSubtractClass() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("%-2|CLASS=Fighter");
	}

	@Test
	void testRoundRobinSubtractType() throws PersistenceLayerException
	{
		PCClass a = primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		a.addToListFor(ListKey.TYPE, Type.getConstant("Base"));
		PCClass c = secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		c.addToListFor(ListKey.TYPE, Type.getConstant("Base"));
		runRoundRobin("%-2|CLASS.TYPE=Base");
	}

	@Test
	void testRoundRobinMultiply() throws PersistenceLayerException
	{
		runRoundRobin("%*2");
	}

	@Test
	void testRoundRobinMultiplyClass() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("%*2|CLASS=Fighter");
	}

	@Test
	void testRoundRobinMultiplyType() throws PersistenceLayerException
	{
		PCClass a = primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		a.addToListFor(ListKey.TYPE, Type.getConstant("Base"));
		PCClass c = secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		c.addToListFor(ListKey.TYPE, Type.getConstant("Base"));
		runRoundRobin("%*2|CLASS.TYPE=Base");
	}

	@Test
	void testRoundRobinDivide() throws PersistenceLayerException
	{
		runRoundRobin("%/2");
	}

	@Test
	void testRoundRobinDivideClass() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("%/2|CLASS=Fighter");
	}

	@Test
	void testRoundRobinDivideType() throws PersistenceLayerException
	{
		PCClass a = primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		a.addToListFor(ListKey.TYPE, Type.getConstant("Base"));
		PCClass c = secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		c.addToListFor(ListKey.TYPE, Type.getConstant("Base"));
		runRoundRobin("%/2|CLASS.TYPE=Base");
	}

	@Test
	void testRoundRobinUp() throws PersistenceLayerException
	{
		runRoundRobin("%up2");
	}

	@Test
	void testRoundRobinUpClass() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("%up2|CLASS=Fighter");
	}

	@Test
	void testRoundRobinUpType() throws PersistenceLayerException
	{
		PCClass a = primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		a.addToListFor(ListKey.TYPE, Type.getConstant("Base"));
		PCClass c = secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		c.addToListFor(ListKey.TYPE, Type.getConstant("Base"));
		runRoundRobin("%up2|CLASS.TYPE=Base");
	}

	@Test
	void testRoundRobinHup() throws PersistenceLayerException
	{
		runRoundRobin("%Hup2");
	}

	@Test
	void testRoundRobinHupClass() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("%Hup2|CLASS=Fighter");
	}

	@Test
	void testRoundRobinHupType() throws PersistenceLayerException
	{
		PCClass a = primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		a.addToListFor(ListKey.TYPE, Type.getConstant("Base"));
		PCClass c = secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		c.addToListFor(ListKey.TYPE, Type.getConstant("Base"));
		runRoundRobin("%Hup2|CLASS.TYPE=Base");
	}

	@Test
	void testRoundRobinDown() throws PersistenceLayerException
	{
		runRoundRobin("%down2");
	}

	@Test
	void testRoundRobinDownClass() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("%down2|CLASS=Fighter");
	}

	@Test
	void testRoundRobinDownType() throws PersistenceLayerException
	{
		PCClass a = primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		a.addToListFor(ListKey.TYPE, Type.getConstant("Base"));
		PCClass c = secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		c.addToListFor(ListKey.TYPE, Type.getConstant("Base"));
		runRoundRobin("%down2|CLASS.TYPE=Base");
	}

	@Test
	void testRoundRobinHdown() throws PersistenceLayerException
	{
		runRoundRobin("%Hdown2");
	}

	@Test
	void testRoundRobinHdownClass() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("%Hdown2|CLASS=Fighter");
	}

	@Test
	void testRoundRobinHdownType() throws PersistenceLayerException
	{
		PCClass a = primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		a.addToListFor(ListKey.TYPE, Type.getConstant("Base"));
		PCClass c = secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
		c.addToListFor(ListKey.TYPE, Type.getConstant("Base"));
		runRoundRobin("%Hdown2|CLASS.TYPE=Base");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "%Hdown2";
	}

	@Override
	protected String getLegalValue()
	{
		return "%*2|CLASS.TYPE=Base";
	}

	@Test
	void testUnparseNull()
	{
		primaryProf.put(ObjectKey.HITDIE, null);
		assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	@Test
	void testUnparseLegal()
	{
		primaryProf.put(ObjectKey.HITDIE, new HitDieLock(new HitDie(1)));
		expectSingle(getToken().unparse(primaryContext, primaryProf), "1");
	}

	@SuppressWarnings("unchecked")
	@Test
	void testUnparseGenericsFail()
	{
		ObjectKey objectKey = ObjectKey.HITDIE;
		primaryProf.put(objectKey, new Object());
		try
		{
			getToken().unparse(primaryContext, primaryProf);
			fail();
		}
		catch (ClassCastException e)
		{
			//Yep!
		}
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}

	@Test
	void testUnparseZeroSteps()
	{
		try
		{
			primaryProf.put(ObjectKey.HITDIE,
					new HitDieStep(0, new HitDie(12)));
			assertBadUnparse();
		}
		catch (IllegalArgumentException e)
		{
			// Good here too :)
		}
	}

	@Test
	void testUnparseNegativeLevel()
	{
		try
		{
			primaryProf.put(ObjectKey.HITDIE, new HitDieLock(new HitDie(-1)));
			assertBadUnparse();
		}
		catch (IllegalArgumentException e)
		{
			// Good here too :)
		}
	}

	@Test
	void testUnparseZeroDivide()
	{
		try
		{
			primaryProf.put(ObjectKey.HITDIE, new HitDieFormula(
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
	// primaryProf.put(ObjectKey.HITDIE, new HitDieFormula(new
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
	// primaryProf.put(ObjectKey.HITDIE, new HitDieFormula(new
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
	// primaryProf.put(ObjectKey.HITDIE, new HitDieFormula(new
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
	// primaryProf.put(ObjectKey.HITDIE, new HitDieFormula(new
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
	// primaryProf.put(ObjectKey.HITDIE, new HitDieFormula(new
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
	// primaryProf.put(ObjectKey.HITDIE, new HitDieFormula(new
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
	// primaryProf.put(ObjectKey.HITDIE, new HitDieFormula(new
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
	// primaryProf.put(ObjectKey.HITDIE, new HitDieStep(8, new HitDie(12)));
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
	// primaryProf.put(ObjectKey.HITDIE, new HitDieStep(-8, new HitDie(12)));
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
	// primaryProf.put(ObjectKey.HITDIE, new HitDieStep(1, new HitDie(6)));
	//			assertBadUnparse();
	//		}
	//		catch (IllegalArgumentException e)
	//		{
	//			//Good here too :)
	//		}
	//	}
}
