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
package plugin.lsttokens.equipmentmodifier;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

class ChargesTokenTest extends AbstractCDOMTokenTestCase<EquipmentModifier>
{

	static ChargesToken token = new ChargesToken();
	static CDOMTokenLoader<EquipmentModifier> loader = new CDOMTokenLoader<>();

	@Override
	public Class<EquipmentModifier> getCDOMClass()
	{
		return EquipmentModifier.class;
	}

	@Override
	public CDOMLoader<EquipmentModifier> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<EquipmentModifier> getToken()
	{
		return token;
	}

	@Test
	void testInvalidEmpty()
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	void testInvalidNoPipe()
	{
		assertFalse(parse("4"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidTwoPipe()
	{
		assertFalse(parse("4|5|6"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidMinNaN()
	{
		assertFalse(parse("String|4"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidMaxNaN()
	{
		assertFalse(parse("3|Str"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidMinNegative()
	{
		assertFalse(parse("-4|5"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidMaxNegative()
	{
		assertFalse(parse("6|-7"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidMaxLTMin()
	{
		assertFalse(parse("7|3"));
		assertNoSideEffects();
	}

	@Test
	void testRoundRobinSimple() throws PersistenceLayerException
	{
		runRoundRobin("4|10");
	}

	@Test
	void testRoundRobinMatching() throws PersistenceLayerException
	{
		runRoundRobin("10|10");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "6|15";
	}

	@Override
	protected String getLegalValue()
	{
		return "4|7";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}

	@Test
	void testUnparseMinNull()
	{
		primaryProf.put(IntegerKey.MIN_CHARGES, null);
		primaryProf.put(IntegerKey.MAX_CHARGES, 1);
		assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	@Test
	void testUnparseMaxNull()
	{
		primaryProf.put(IntegerKey.MIN_CHARGES, 1);
		primaryProf.put(IntegerKey.MAX_CHARGES, null);
		assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	@Test
	void testUnparseNormal()
	{
		expectSingle(setAndUnparse(5, 10), "5|10");
	}

	@Test
	void testUnparseEqual()
	{
		expectSingle(setAndUnparse(5, 5), "5|5");
	}

	@Test
	void testUnparseZeroMin()
	{
		expectSingle(setAndUnparse(0, 5), "0|5");
	}

	@Test
	void testUnparseZeroMinMax()
	{
		expectSingle(setAndUnparse(0, 0), "0|0");
	}

	@Test
	void testUnparseMaxLTMin()
	{
		assertNull(setAndUnparse(10, 5));
	}

	@Test
	void testUnparseNegativeMin()
	{
		assertNull(setAndUnparse(-5, 10));
	}

	@Test
	void testUnparseNegativeMax()
	{
		assertNull(setAndUnparse(5, -10));
	}

	protected String[] setAndUnparse(int min, int max)
	{
		primaryProf.put(IntegerKey.MIN_CHARGES, min);
		primaryProf.put(IntegerKey.MAX_CHARGES, max);
		return getToken().unparse(primaryContext, primaryProf);
	}
}
