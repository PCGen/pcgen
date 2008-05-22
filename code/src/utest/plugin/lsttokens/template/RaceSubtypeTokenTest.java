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
package plugin.lsttokens.template;

import java.util.List;

import org.junit.Test;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTypeSafeListTestCase;

public class RaceSubtypeTokenTest extends
		AbstractTypeSafeListTestCase<PCTemplate>
{

	static RacesubtypeToken token = new RacesubtypeToken();
	static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<PCTemplate>(
			PCTemplate.class);

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public CDOMLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<PCTemplate> getToken()
	{
		return token;
	}

	@Override
	public Object getConstant(String string)
	{
		return RaceSubType.getConstant(string);
	}

	@Override
	public char getJoinCharacter()
	{
		return '|';
	}

	@Override
	public ListKey<?> getListKey()
	{
		return ListKey.RACESUBTYPE;
	}

	@Test
	public void testValidRemoveInputSimple() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(parse(".REMOVE.Rheinhessen"));
		coll = primaryProf.getListFor(ListKey.REMOVED_RACESUBTYPE);
		assertEquals(1, coll.size());
		assertTrue(coll.contains(getConstant("Rheinhessen")));
	}

	@Test
	public void testValidRemoveInputNonEnglish()
		throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(parse(".REMOVE.Niederösterreich"));
		coll = primaryProf.getListFor(ListKey.REMOVED_RACESUBTYPE);
		assertEquals(1, coll.size());
		assertTrue(coll.contains(getConstant("Niederösterreich")));
	}

	@Test
	public void testValidRemoveInputSpace() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(parse(".REMOVE.Finger Lakes"));
		coll = primaryProf.getListFor(ListKey.REMOVED_RACESUBTYPE);
		assertEquals(1, coll.size());
		assertTrue(coll.contains(getConstant("Finger Lakes")));
	}

	@Test
	public void testValidRemoveInputHyphen() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(parse(".REMOVE.Languedoc-Roussillon"));
		coll = primaryProf.getListFor(ListKey.REMOVED_RACESUBTYPE);
		assertEquals(1, coll.size());
		assertTrue(coll.contains(getConstant("Languedoc-Roussillon")));
	}

	@Test
	public void testValidRemoveInputList() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(parse(".REMOVE.Niederösterreich" + getJoinCharacter()
			+ ".REMOVE.Finger Lakes"));
		coll = primaryProf.getListFor(ListKey.REMOVED_RACESUBTYPE);
		assertEquals(2, coll.size());
		assertTrue(coll.contains(getConstant("Niederösterreich")));
		assertTrue(coll.contains(getConstant("Finger Lakes")));
	}

	@Test
	public void testValidInputMultRemoveList() throws PersistenceLayerException
	{
		List<?> coll;
		assertTrue(parse(".REMOVE.Niederösterreich" + getJoinCharacter()
			+ ".REMOVE.Finger Lakes"));
		assertTrue(parse(".REMOVE.Languedoc-Roussillon" + getJoinCharacter()
			+ ".REMOVE.Rheinhessen"));
		coll = primaryProf.getListFor(ListKey.REMOVED_RACESUBTYPE);
		assertEquals(4, coll.size());
		assertTrue(coll.contains(getConstant("Niederösterreich")));
		assertTrue(coll.contains(getConstant("Finger Lakes")));
		assertTrue(coll.contains(getConstant("Languedoc-Roussillon")));
		assertTrue(coll.contains(getConstant("Rheinhessen")));
	}

	@Test
	public void testInvalidRemoveEmpty() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
		assertFalse(parse(".REMOVE."));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidRemoveListEnd() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
		assertFalse(parse("TestWP1" + getJoinCharacter() + ".REMOVE."));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidRemoveListStart() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
		assertFalse(parse(".REMOVE." + getJoinCharacter() + "TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidRemoveListDoubleJoin()
		throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP1");
		primaryContext.ref.constructCDOMObject(PCTemplate.class, "TestWP2");
		assertFalse(parse(".REMOVE.TestWP2" + getJoinCharacter()
			+ getJoinCharacter() + ".REMOVE.TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testRemoveRoundRobinBase() throws PersistenceLayerException
	{
		runRoundRobin(".REMOVE.Rheinhessen");
	}

	@Test
	public void testRemoveRoundRobinWithSpace()
		throws PersistenceLayerException
	{
		runRoundRobin(".REMOVE.Finger Lakes");
	}

	@Test
	public void testRemoveRoundRobinNonEnglish()
		throws PersistenceLayerException
	{
		runRoundRobin(".REMOVE.Niederösterreich");
	}

	@Test
	public void testRemoveRoundRobinHyphen() throws PersistenceLayerException
	{
		runRoundRobin(".REMOVE.Languedoc-Roussillon");
	}

	@Test
	public void testRemoveRoundRobinThree() throws PersistenceLayerException
	{
		runRoundRobin(".REMOVE.TestWP1" + getJoinCharacter()
			+ ".REMOVE.TestWP2" + getJoinCharacter() + ".REMOVE.TestWP3");
	}

	@Test
	public void testMixRoundRobinThree() throws PersistenceLayerException
	{
		runRoundRobin(".REMOVE.TestWP3" + getJoinCharacter() + "TestWP1"
			+ getJoinCharacter() + "TestWP2");
	}

	@Test
	public void testMixRoundRobinWithSpace() throws PersistenceLayerException
	{
		runRoundRobin(".REMOVE.Finger Lakes" + getJoinCharacter()
			+ "Languedoc-Roussillon");
	}

	@Override
	public boolean isClearDotLegal()
	{
		return false;
	}

	@Override
	public boolean isClearLegal()
	{
		return false;
	}
}
