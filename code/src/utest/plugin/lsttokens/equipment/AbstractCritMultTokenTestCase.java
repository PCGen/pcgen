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
package plugin.lsttokens.equipment;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Equipment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public abstract class AbstractCritMultTokenTestCase extends
		AbstractTokenTestCase<Equipment>
{

	static CDOMTokenLoader<Equipment> loader = new CDOMTokenLoader<Equipment>();

	@Override
	public Class<Equipment> getCDOMClass()
	{
		return Equipment.class;
	}

	@Override
	public CDOMLoader<Equipment> getLoader()
	{
		return loader;
	}

	@Test
	public void testInvalidStringInput() throws PersistenceLayerException
	{
		assertFalse(parse("String"));
	}

	@Test
	public void testInvalidTypeInput() throws PersistenceLayerException
	{
		assertFalse(parse("TYPE=TestType"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDecimalInput() throws PersistenceLayerException
	{
		assertFalse(parse("4.5"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidFractionInput() throws PersistenceLayerException
	{
		assertFalse(parse("1/2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidFunctionInput() throws PersistenceLayerException
	{
		assertFalse(parse("1+3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNegativeInput() throws PersistenceLayerException
	{
		assertFalse(parse("-1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidZeroInput() throws PersistenceLayerException
	{
		assertFalse(parse("0"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTimesNegativeInput()
			throws PersistenceLayerException
	{
		assertFalse(parse("x-1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTimesZeroInput() throws PersistenceLayerException
	{
		assertFalse(parse("x0"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoTimesInput() throws PersistenceLayerException
	{
		assertFalse(parse("3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyInput() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTimesNaNInput() throws PersistenceLayerException
	{
		assertFalse(parse("xY"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinTwo() throws PersistenceLayerException
	{
		runRoundRobin("x2");
	}

	@Test
	public void testRoundRobinFive() throws PersistenceLayerException
	{
		runRoundRobin("x5");
	}

	@Test
	public void testRoundRobinDash() throws PersistenceLayerException
	{
		runRoundRobin("-");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "-";
	}

	@Override
	protected String getLegalValue()
	{
		return "x2";
	}
	
	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}

	@Test
	public void testUnparseOne() throws PersistenceLayerException
	{
		expectSingle(setAndUnparse(1), "x1");
	}

	@Test
	public void testUnparseNone() throws PersistenceLayerException
	{
		expectSingle(setAndUnparse(-1), "-");
	}

	@Test
	public void testUnparseZero() throws PersistenceLayerException
	{
		getUnparseTarget().put(getIntegerKey(), 0);
		assertBadUnparse();
	}

	@Test
	public void testUnparseNegative() throws PersistenceLayerException
	{
		getUnparseTarget().put(getIntegerKey(), -3);
		assertBadUnparse();
	}

	protected abstract CDOMObject getUnparseTarget();

	private IntegerKey getIntegerKey()
	{
		return IntegerKey.CRIT_MULT;
	}

	@Test
	public void testUnparseNull() throws PersistenceLayerException
	{
		getUnparseTarget().put(getIntegerKey(), null);
		assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	protected String[] setAndUnparse(int val)
	{
		getUnparseTarget().put(getIntegerKey(), val);
		return getToken().unparse(primaryContext, primaryProf);
	}
}
