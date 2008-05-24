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

import java.math.BigDecimal;

import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class FaceTokenTest extends AbstractTokenTestCase<Race>
{

	static FaceToken token = new FaceToken();
	static CDOMTokenLoader<Race> loader = new CDOMTokenLoader<Race>(Race.class);

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
	public void testInvalidInputUnset() throws PersistenceLayerException
	{
		internalTestInvalidInputs(null, null);
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputSet() throws PersistenceLayerException
	{
		assertTrue(parse("15,20"));
		assertTrue(parseSecondary("15,20"));
		BigDecimal w = new BigDecimal(15);
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		BigDecimal h = new BigDecimal(20);
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		internalTestInvalidInputs(w, h);
		assertNoSideEffects();
	}

	public void internalTestInvalidInputs(BigDecimal w, BigDecimal h)
			throws PersistenceLayerException
	{
		// Always ensure get is unchanged
		// since no invalid item should set or reset the value
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse("TestWP"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse("String"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse("TYPE=TestType"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse("TYPE.TestType"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse("ALL"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse("ANY"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse("FIVE"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse("1/2"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse("1+3"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse("-1"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse("-2, 4"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse("6, -3"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse("x, 4"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse("6, y"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse("+, 4"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse("6, +"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse(" , 4"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse("6,  "));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse("1,"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse(",1"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse("1,2,3"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse("1,2,"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertFalse(parse(",2,3"));
		assertEquals(w, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(h, primaryProf.get(ObjectKey.FACE_HEIGHT));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(parse("5"));
		assertEquals(new BigDecimal(5), primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(BigDecimal.ZERO, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertTrue(parse("1"));
		assertEquals(new BigDecimal(1), primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(BigDecimal.ZERO, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertTrue(parse("0"));
		assertEquals(BigDecimal.ZERO, primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(BigDecimal.ZERO, primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertTrue(parse("5,10"));
		assertEquals(new BigDecimal(5), primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(new BigDecimal(10), primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertTrue(parse("10,7"));
		assertEquals(new BigDecimal(10), primaryProf.get(ObjectKey.FACE_WIDTH));
		assertEquals(new BigDecimal(7), primaryProf.get(ObjectKey.FACE_HEIGHT));
		assertTrue(parse("18.1,45.2"));
		assertEquals(new BigDecimal("18.1"), primaryProf
				.get(ObjectKey.FACE_WIDTH));
		assertEquals(new BigDecimal("45.2"), primaryProf
				.get(ObjectKey.FACE_HEIGHT));
	}

	@Test
	public void testInvalidOutputWidthNegative()
			throws PersistenceLayerException
	{
		assertTrue(primaryContext.getWriteMessageCount() == 0);
		primaryProf.put(ObjectKey.FACE_WIDTH, new BigDecimal(-5));
		primaryProf.put(ObjectKey.FACE_HEIGHT, new BigDecimal(5));
		assertNull(token.unparse(primaryContext, primaryProf));
		assertFalse(primaryContext.getWriteMessageCount() == 0);
	}

	@Test
	public void testInvalidOutputHeightNegative()
			throws PersistenceLayerException
	{
		assertTrue(primaryContext.getWriteMessageCount() == 0);
		primaryProf.put(ObjectKey.FACE_WIDTH, new BigDecimal(5));
		primaryProf.put(ObjectKey.FACE_HEIGHT, new BigDecimal(-4));
		assertNull(token.unparse(primaryContext, primaryProf));
		assertFalse(primaryContext.getWriteMessageCount() == 0);
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		runRoundRobin("1");
	}

	@Test
	public void testRoundRobinZero() throws PersistenceLayerException
	{
		runRoundRobin("0");
	}

	@Test
	public void testRoundRobinZeroX() throws PersistenceLayerException
	{
		runRoundRobin("0,5");
	}

	// Note: Can't do this because if Height is zero, then it is not written
	// out.
	// - Tom Parker 2/23/2007
	// @Test
	// public void testRoundRobinZeroY() throws PersistenceLayerException
	// {
	// testRoundRobin("5,0");
	// }

	@Test
	public void testRoundRobinDecimal() throws PersistenceLayerException
	{
		runRoundRobin("5.1,6.3");
	}
}
