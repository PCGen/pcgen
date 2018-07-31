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
package plugin.lsttokens.kit.prof;

import org.junit.Test;

import pcgen.core.WeaponProf;
import pcgen.core.kit.KitProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractKitTokenTestCase;

public class ProfTokenTest extends AbstractKitTokenTestCase<KitProf>
{

	static ProfToken token = new ProfToken();
	static CDOMSubLineLoader<KitProf> loader = new CDOMSubLineLoader<>(
			"PROF", KitProf.class);

	@Override
	public Class<KitProf> getCDOMClass()
	{
		return KitProf.class;
	}

	@Override
	public CDOMSubLineLoader<KitProf> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<KitProf> getToken()
	{
		return token;
	}

	private static char getJoinCharacter()
	{
		return '|';
	}

	@Test
	public void testInvalidInputEmptyCount()
	{
		assertTrue(parse("Fireball"));
		assertConstructionError();
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Fireball");
		secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Fireball");
		runRoundRobin("Fireball");
	}

	@Test
	public void testRoundRobinTwo() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Fireball");
		secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Fireball");
		primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "English");
		secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "English");
		runRoundRobin("Fireball" + getJoinCharacter() + "English");
	}

	@Test
	public void testInvalidListEnd()
	{
		assertFalse(parse("TestWP1" + getJoinCharacter()));
	}

	@Test
	public void testInvalidListStart()
	{
		assertFalse(parse(getJoinCharacter() + "TestWP1"));
	}

	@Test
	public void testInvalidListDoubleJoin()
	{
		assertFalse(parse("TestWP2" + getJoinCharacter() + getJoinCharacter()
				+ "TestWP1"));
	}

}
