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

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

class LevelsPerFeatTokenTest extends AbstractCDOMTokenTestCase<PCClass>
{
	static LevelsperfeatToken token = new LevelsperfeatToken();
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

	public static ObjectKey<?> getObjectKey()
	{
		return ObjectKey.ALIGNMENT;
	}

	@Test
	void testInvalidEmpty()
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	void testInvalidFormula()
	{
		assertFalse(parse("1+3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidNonLevelType()
	{
		assertFalse(parse("4|Foo=bar"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidMissingLevelType1()
	{
		assertFalse(parse("4|"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidMissingLevelType()
	{
		assertFalse(parse("4|Foo"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidMissingLevelType2()
	{
		assertFalse(parse("4|LEVELTYPE"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidMissingLevelType3()
	{
		assertFalse(parse("4|LEVELTYPE="));
		assertNoSideEffects();
	}

	@Test
	void testInvalidMissingFormula()
	{
		assertFalse(parse("|LEVELTYPE=Foo"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidTooManyPipes()
	{
		assertFalse(parse("4|LEVELTYPE=Foo|LEVELTYPE=Bar"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidTooManyMiddlePipes()
	{
		assertFalse(parse("4||LEVELTYPE=Foo"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidMissingLevelType4()
	{
		assertFalse(parse("4|=Foo"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidString()
	{
		assertFalse(parse("String"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidNegative()
	{
		assertFalse(parse("-1"));
		assertNoSideEffects();
	}

	@Test
	void testRoundRobinInteger() throws PersistenceLayerException
	{
		runRoundRobin("4");
	}

	@Test
	void testRoundRobinZero() throws PersistenceLayerException
	{
		runRoundRobin("0");
	}

	@Test
	void testRoundRobinWithLevelType() throws PersistenceLayerException
	{
		runRoundRobin("3|LEVELTYPE=Foo");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "4";
	}

	@Override
	protected String getLegalValue()
	{
		return "3|LEVELTYPE=Foo";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}

	@Test
	void testUnparseOne()
	{
		expectSingle(setAndUnparse(1), Integer.toString(1));
	}

	@Test
	void testUnparseZero()
	{
		expectSingle(setAndUnparse(0), Integer.toString(0));
	}

	@Test
	void testUnparseNegative()
	{
		primaryProf.put(getIntegerKey(), -3);
		assertBadUnparse();
	}

	private static IntegerKey getIntegerKey()
	{
		return IntegerKey.LEVELS_PER_FEAT;
	}

	@Test
	void testUnparseNull()
	{
		primaryProf.put(getIntegerKey(), null);
		assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	protected String[] setAndUnparse(int val)
	{
		primaryProf.put(getIntegerKey(), val);
		return getToken().unparse(primaryContext, primaryProf);
	}
	
	@Test
	void testUnparseOneTyped()
	{
		primaryProf.put(getIntegerKey(), 1);
		primaryProf.put(StringKey.LEVEL_TYPE, "Foo");
		expectSingle(getToken().unparse(primaryContext, primaryProf), "1|LEVELTYPE=Foo");
	}

	@Test
	void testUnparseInvalidOnlyType()
	{
		primaryProf.put(StringKey.LEVEL_TYPE, "Foo");
		assertBadUnparse();
	}

	//StringKey.LEVEL_TYPE
}
