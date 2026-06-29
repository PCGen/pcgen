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
package plugin.lsttokens.pcclass;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import pcgen.cdom.content.LevelExchange;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

class ExchangeLevelTokenTest extends AbstractCDOMTokenTestCase<PCClass>
{

	static ExchangelevelToken token = new ExchangelevelToken();
	static CDOMTokenLoader<PCClass> loader = new CDOMTokenLoader<>();

	@Override
	public Class<PCClass> getCDOMClass()
	{
		return PCClass.class;
	}

	@Override
	public CDOMLoader<PCClass> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<PCClass> getToken()
	{
		return token;
	}

	public static Class<PCClass> getTargetClass()
	{
		return PCClass.class;
	}

	@Test
	void testInvalidInputEmpty()
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNoLevels()
	{
		primaryContext.getReferenceContext().constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(parse("Paladin"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputEmptyMin()
	{
		primaryContext.getReferenceContext().constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(parse("Paladin||5|3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputEmptyMax()
	{
		primaryContext.getReferenceContext().constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(parse("Paladin|5||3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputEmptyRem()
	{
		primaryContext.getReferenceContext().constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(parse("Paladin|6|5|"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNotEnoughPipes()
	{
		primaryContext.getReferenceContext().constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(parse("Paladin|6|5"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputTooManyPipes()
	{
		primaryContext.getReferenceContext().constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(parse("Paladin|6|5|4|3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputEmptyClass()
	{
		assertFalse(parse("|4|3|2"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputOpenStart()
	{
		primaryContext.getReferenceContext().constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(parse("|Paladin|6|5|4"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputOpenEnd()
	{
		primaryContext.getReferenceContext().constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(parse("Paladin|6|5|4|"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoublePipeTypeOne()
	{
		primaryContext.getReferenceContext().constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(parse("Paladin||6|5|4"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoublePipeTypeTwo()
	{
		primaryContext.getReferenceContext().constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(parse("Paladin|6||5|4"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoublePipeTypeThree()
	{
		assertFalse(parse("Paladin|6|5||4"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputRemTooLow()
	{
		assertFalse(parse("Paladin|5|2|1"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNegativeMax()
	{
		assertFalse(parse("Paladin|-5|2|1"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputZeroMax()
	{
		assertFalse(parse("Paladin|0|2|1"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNegativeDonate()
	{
		assertFalse(parse("Paladin|5|-2|1"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputZeroDonate()
	{
		assertFalse(parse("Paladin|5|0|1"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNegativeRemaining()
	{
		assertFalse(parse("Paladin|4|5|-1"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputRemainingNaN()
	{
		assertFalse(parse("Paladin|11|10|x"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDonateNaN()
	{
		assertFalse(parse("Paladin|11|x|1"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputMaxNaN()
	{
		assertFalse(parse("Paladin|x|10|1"));
		assertNoSideEffects();
	}

	@Test
	void testRoundRobinSimple() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Paladin");
		secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Paladin");
		runRoundRobin("Paladin|11|10|1");
	}

	@Test
	void testRoundRobinZeroRem() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Paladin");
		secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Paladin");
		runRoundRobin("Paladin|10|10|0");
	}

	@Test
	void testRoundRobinHighMax() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Paladin");
		secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Paladin");
		runRoundRobin("Paladin|5|10|1");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "Paladin|5|10|1";
	}

	@Override
	protected String getLegalValue()
	{
		return "Paladin|10|10|0";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}

	@Test
	void testUnparseOne()
	{
		PCClass fighter = primaryContext.getReferenceContext().constructCDOMObject(PCClass.class,
				"Fighter");
		LevelExchange le = new LevelExchange(CDOMDirectSingleRef
				.getRef(fighter), 4, 12, 2);
		primaryProf.put(ObjectKey.EXCHANGE_LEVEL, le);
		expectSingle(getToken().unparse(primaryContext, primaryProf),
				"Fighter|4|12|2");
	}

	@Test
	void testUnparseNull()
	{
		primaryProf.put(ObjectKey.EXCHANGE_LEVEL, null);
		try
		{
			assertNull(getToken().unparse(primaryContext, primaryProf));
		}
		catch (NullPointerException e)
		{
			// This is okay too
		}
	}

	@Test
	void testUnparseNegativeMinLevel()
	{
		try
		{
			PCClass fighter = primaryContext.getReferenceContext().constructCDOMObject(
					PCClass.class, "Fighter");
			LevelExchange le = new LevelExchange(CDOMDirectSingleRef
					.getRef(fighter), -4, 12, 2);
			primaryProf.put(ObjectKey.EXCHANGE_LEVEL, le);
			assertBadUnparse();
		}
		catch (IllegalArgumentException e)
		{
			// Good here too :)
		}
	}

	@Test
	void testUnparseNegativeMaxLevel()
	{
		try
		{
			PCClass fighter = primaryContext.getReferenceContext().constructCDOMObject(
					PCClass.class, "Fighter");
			LevelExchange le = new LevelExchange(CDOMDirectSingleRef
					.getRef(fighter), 4, -12, 2);
			primaryProf.put(ObjectKey.EXCHANGE_LEVEL, le);
			assertBadUnparse();
		}
		catch (IllegalArgumentException e)
		{
			// Good here too :)
		}
	}

	@Test
	void testUnparseBadRemainMathLevel()
	{
		try
		{
			PCClass fighter = primaryContext.getReferenceContext().constructCDOMObject(
					PCClass.class, "Fighter");
			LevelExchange le = new LevelExchange(CDOMDirectSingleRef
					.getRef(fighter), 4, 2, 1);
			primaryProf.put(ObjectKey.EXCHANGE_LEVEL, le);
			assertBadUnparse();
		}
		catch (IllegalArgumentException e)
		{
			// Good here too :)
		}
	}

	@Test
	void testUnparseNegRemainingLevel()
	{
		try
		{
			PCClass fighter = primaryContext.getReferenceContext().constructCDOMObject(
					PCClass.class, "Fighter");
			LevelExchange le = new LevelExchange(CDOMDirectSingleRef
					.getRef(fighter), 4, 3, -2);
			primaryProf.put(ObjectKey.EXCHANGE_LEVEL, le);
			assertBadUnparse();
		}
		catch (IllegalArgumentException e)
		{
			// Good here too :)
		}
	}

	@Test
	void testUnparseNullClass()
	{
		try
		{
			LevelExchange le = new LevelExchange(null, 4, 3, 2);
			primaryProf.put(ObjectKey.EXCHANGE_LEVEL, le);
			assertBadUnparse();
		}
		catch (NullPointerException e)
		{
			// Good here too :)
		}
	}
}
