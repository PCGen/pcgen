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

import pcgen.core.Race;
import pcgen.core.kit.KitRace;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractKitTokenTestCase;

public class RaceTokenTest extends AbstractKitTokenTestCase<KitRace>
{

	static RaceToken token = new RaceToken();
	static CDOMSubLineLoader<KitRace> loader = new CDOMSubLineLoader<>(
			"SKILL", KitRace.class);

	@Override
	public Class<KitRace> getCDOMClass()
	{
		return KitRace.class;
	}

	@Override
	public CDOMSubLineLoader<KitRace> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<KitRace> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmptyCount() throws PersistenceLayerException
	{
		assertTrue(parse("Fireball"));
		assertConstructionError();
	}

	@Test
	public void testInvalidInputOnlyOne() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(Race.class, "Fireball");
		secondaryContext.getReferenceContext().constructCDOMObject(Race.class, "Fireball");
		primaryContext.getReferenceContext().constructCDOMObject(Race.class, "English");
		secondaryContext.getReferenceContext().constructCDOMObject(Race.class, "English");
		assertTrue(parse("Fireball,English"));
		assertConstructionError();
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(Race.class, "Fireball");
		secondaryContext.getReferenceContext().constructCDOMObject(Race.class, "Fireball");
		runRoundRobin("Fireball");
	}
}
