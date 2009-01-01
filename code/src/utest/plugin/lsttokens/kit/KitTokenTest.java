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
package plugin.lsttokens.kit;

import org.junit.Test;

import pcgen.core.Kit;
import pcgen.core.kit.KitKit;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.testsupport.AbstractSubTokenTestCase;

public class KitTokenTest extends AbstractSubTokenTestCase<KitKit>
{

	static KitToken token = new KitToken();
	static CDOMSubLineLoader<KitKit> loader = new CDOMSubLineLoader<KitKit>(
			"*KITTOKEN", "SKILL", KitKit.class);

	@Override
	public Class<KitKit> getCDOMClass()
	{
		return KitKit.class;
	}

	@Override
	public CDOMSubLineLoader<KitKit> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMSecondaryToken<KitKit> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmptyCount() throws PersistenceLayerException
	{
		assertTrue(parse("Fireball"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Kit.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Kit.class, "Fireball");
		runRoundRobin("Fireball");
	}

	@Test
	public void testRoundRobinTwo() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Kit.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Kit.class, "Fireball");
		primaryContext.ref.constructCDOMObject(Kit.class, "English");
		secondaryContext.ref.constructCDOMObject(Kit.class, "English");
		runRoundRobin("Fireball" + getJoinCharacter() + "English");
	}

	@Test
	public void testInvalidListEnd() throws PersistenceLayerException
	{
		assertFalse(parse("TestWP1" + getJoinCharacter()));
	}

	private char getJoinCharacter()
	{
		return '|';
	}

	@Test
	public void testInvalidListStart() throws PersistenceLayerException
	{
		assertFalse(parse(getJoinCharacter() + "TestWP1"));
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		assertFalse(parse("TestWP2" + getJoinCharacter() + getJoinCharacter()
				+ "TestWP1"));
	}

}
