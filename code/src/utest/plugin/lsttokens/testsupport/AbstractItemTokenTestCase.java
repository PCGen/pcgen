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
package plugin.lsttokens.testsupport;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;

public abstract class AbstractItemTokenTestCase<T extends CDOMObject, TC extends CDOMObject>
		extends AbstractTokenTestCase<T>
{

	public abstract Class<TC> getTargetClass();

	public abstract boolean isClearLegal();

	public abstract ObjectKey<?> getObjectKey();

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNull(primaryProf.get(getObjectKey()));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputString() throws PersistenceLayerException
	{
		assertTrue(parse("String"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputType() throws PersistenceLayerException
	{
		assertTrue(parse("TestType"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputJoinedComma() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		boolean ret = parse("TestWP1,TestWP2");
		if (ret)
		{
			assertFalse(primaryContext.ref.validate());
		}
		else
		{
			assertNull(primaryProf.get(getObjectKey()));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputJoinedPipe() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		boolean ret = parse("TestWP1|TestWP2");
		if (ret)
		{
			assertFalse(primaryContext.ref.validate());
		}
		else
		{
			assertNull(primaryProf.get(getObjectKey()));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputJoinedDot() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		boolean ret = parse("TestWP1.TestWP2");
		if (ret)
		{
			assertFalse(primaryContext.ref.validate());
		}
		else
		{
			assertNull(primaryProf.get(getObjectKey()));
			assertNoSideEffects();
		}
	}

	// FIXME These are invalid due to RC being overly protective at the moment
	// @Test
	// public void testInvalidInputAll()
	// {
	// assertTrue(parse( "ALL"));
	// assertFalse(primaryContext.ref.validate());
	// }
	//
	// @Test
	// public void testInvalidInputAny()
	// {
	// assertTrue(parse( "ANY"));
	// assertFalse(primaryContext.ref.validate());
	// }
	// @Test
	// public void testInvalidInputCheckType()
	// {
	// if (!isTypeLegal())
	// {
	// assertTrue(token.parse(primaryContext, primaryProf, "TYPE=TestType"));
	// assertFalse(primaryContext.ref.validate());
	// }
	// }
	//

	@Test
	public void testReplacementInputs() throws PersistenceLayerException
	{
		String[] unparsed;
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		if (isClearLegal())
		{
			assertTrue(parse(".CLEAR"));
			unparsed = getToken().unparse(primaryContext, primaryProf);
			assertNull("Expected item to be equal", unparsed);
		}
		assertTrue(parse("TestWP1"));
		assertTrue(parse("TestWP2"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertEquals("Expected item to be equal", "TestWP2", unparsed[0]);
		if (isClearLegal())
		{
			assertTrue(parse(".CLEAR"));
			unparsed = getToken().unparse(primaryContext, primaryProf);
			assertNull("Expected item to be equal", unparsed);
		}
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertTrue(parse("TestWP1"));
		assertTrue(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin("TestWP1");
	}

	protected void construct(LoadContext loadContext, String one)
	{
		loadContext.ref.constructCDOMObject(getTargetClass(), one);
	}
}
