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
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Equipment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTypeSafeListTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class AltTypeTokenTest extends AbstractTypeSafeListTestCase<Equipment, Type>
{

	static AlttypeToken token = new AlttypeToken();
	static CDOMTokenLoader<Equipment> loader = new CDOMTokenLoader<>();

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

	@Override
	public CDOMPrimaryToken<Equipment> getToken()
	{
		return token;
	}

	@Override
	public Type getConstant(String string)
	{
		return Type.getConstant(string);
	}

	@Override
	public char getJoinCharacter()
	{
		return '.';
	}

	@Override
	public ListKey<Type> getListKey()
	{
		return ListKey.TYPE;
	}

	@Override
	public boolean isClearDotLegal()
	{
		return false;
	}

	@Override
	public boolean isClearLegal()
	{
		return true;
	}

	@Test
	public void testReplacementRemove() throws PersistenceLayerException
	{
		String[] unparsed;
		assertTrue(parse("REMOVE.TestWP1"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertNull("Expected item to be equal", unparsed);

		assertTrue(parse("TestWP1"));
		assertTrue(parse("ADD.TestWP2"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertEquals("Expected item to be equal", "TestWP1"
			+ getJoinCharacter() + "TestWP2", unparsed[0]);
		if (isClearLegal())
		{
			assertTrue(parse(Constants.LST_DOT_CLEAR));
			unparsed = getToken().unparse(primaryContext, primaryProf);
			assertNull("Expected item to be null", unparsed);
		}
	}

	@Test
	public void testReplacementRemoveTwo() throws PersistenceLayerException
	{
		String[] unparsed;
		assertTrue(parse("TestWP1"));
		assertTrue(parse("TestWP2"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertEquals("Expected item to be equal", "TestWP1"
			+ getJoinCharacter() + "TestWP2", unparsed[0]);
		assertTrue(parse("REMOVE.TestWP1"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertEquals("Expected item to be equal", "TestWP2", unparsed[0]);
	}

	@Test
	public void testInputInvalidRemoveNoTrailing()
		throws PersistenceLayerException
	{
		assertFalse(parse("TestWP1.REMOVE"));
		assertNoSideEffects();
	}

	@Test
	public void testInputInvalidAddNoTrailing()
		throws PersistenceLayerException
	{
		assertFalse(parse("TestWP1.ADD"));
		assertNoSideEffects();
	}

	@Test
	public void testInputInvalidAddRemove() throws PersistenceLayerException
	{
		assertFalse(parse("TestWP1.ADD.REMOVE.TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInputInvalidRemoveAdd() throws PersistenceLayerException
	{
		assertFalse(parse("TestWP1.REMOVE.ADD.TestWP2"));
		assertNoSideEffects();
	}

	@Test
    @Override
	public void testInputInvalidClearDot() throws PersistenceLayerException
	{
		assertFalse(parse(".CLEAR."));
		assertNoSideEffects();
	}

	@Test
	public void testInputInvalidEmbeddedClear() throws PersistenceLayerException
	{
		assertFalse(parse("Type1.CLEAR"));
		assertNoSideEffects();
	}

	@Test
	public void testInputInvalidClearDirect() throws PersistenceLayerException
	{
		assertFalse(parse(".CLEARType1"));
		assertNoSideEffects();
	}

	@Test
	public void testValidClearDot() throws PersistenceLayerException
	{
		assertTrue(parse(".CLEAR.TestWP1"));
		assertNoSideEffects();
	}

	@Override
	protected CDOMObject getUnparseTarget()
	{
		return primaryProf.getEquipmentHead(2);
	}

	@Override
	protected boolean requiresPreconstruction()
	{
		return true;
	}
}
