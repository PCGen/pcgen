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

import org.junit.Test;

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

public class ExchangeLevelTokenTest extends AbstractCDOMTokenTestCase<PCClass>
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
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoLevels() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(parse("Paladin"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyMin() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(parse("Paladin||5|3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyMax() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(parse("Paladin|5||3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyRem() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(parse("Paladin|6|5|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNotEnoughPipes()
			throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(parse("Paladin|6|5"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTooManyPipes() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(parse("Paladin|6|5|4|3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyClass() throws PersistenceLayerException
	{
		assertFalse(parse("|4|3|2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOpenStart() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(parse("|Paladin|6|5|4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOpenEnd() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(parse("Paladin|6|5|4|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoublePipeTypeOne()
			throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(parse("Paladin||6|5|4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoublePipeTypeTwo()
			throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(getTargetClass(), "Paladin");
		assertFalse(parse("Paladin|6||5|4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoublePipeTypeThree()
			throws PersistenceLayerException
	{
		assertFalse(parse("Paladin|6|5||4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputRemTooLow() throws PersistenceLayerException
	{
		assertFalse(parse("Paladin|5|2|1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNegativeMax() throws PersistenceLayerException
	{
		assertFalse(parse("Paladin|-5|2|1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputZeroMax() throws PersistenceLayerException
	{
		assertFalse(parse("Paladin|0|2|1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNegativeDonate()
			throws PersistenceLayerException
	{
		assertFalse(parse("Paladin|5|-2|1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputZeroDonate() throws PersistenceLayerException
	{
		assertFalse(parse("Paladin|5|0|1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNegativeRemaining()
			throws PersistenceLayerException
	{
		assertFalse(parse("Paladin|4|5|-1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputRemainingNaN() throws PersistenceLayerException
	{
		assertFalse(parse("Paladin|11|10|x"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDonateNaN() throws PersistenceLayerException
	{
		assertFalse(parse("Paladin|11|x|1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputMaxNaN() throws PersistenceLayerException
	{
		assertFalse(parse("Paladin|x|10|1"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Paladin");
		secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Paladin");
		runRoundRobin("Paladin|11|10|1");
	}

	@Test
	public void testRoundRobinZeroRem() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Paladin");
		secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Paladin");
		runRoundRobin("Paladin|10|10|0");
	}

	@Test
	public void testRoundRobinHighMax() throws PersistenceLayerException
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
	public void testUnparseOne() throws PersistenceLayerException
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
	public void testUnparseNull() throws PersistenceLayerException
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
	public void testUnparseNegativeMinLevel() throws PersistenceLayerException
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
	public void testUnparseNegativeMaxLevel() throws PersistenceLayerException
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
	public void testUnparseBadRemainMathLevel()
			throws PersistenceLayerException
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
	public void testUnparseNegRemainingLevel() throws PersistenceLayerException
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
	public void testUnparseNullClass() throws PersistenceLayerException
	{
		try
		{
			LevelExchange le = new LevelExchange(null, 4, 3, 2);
			primaryProf.put(ObjectKey.EXCHANGE_LEVEL, le);
			assertBadUnparse();
		}
		catch (IllegalArgumentException e)
		{
			// Good here too :)
		}
	}
}
