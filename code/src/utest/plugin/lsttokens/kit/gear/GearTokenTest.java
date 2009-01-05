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
package plugin.lsttokens.kit.gear;

import org.junit.Test;

import pcgen.core.Equipment;
import pcgen.core.kit.KitGear;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.testsupport.AbstractSubTokenTestCase;

public class GearTokenTest extends AbstractSubTokenTestCase<KitGear>
{

	static GearToken token = new GearToken();
	static CDOMSubLineLoader<KitGear> loader = new CDOMSubLineLoader<KitGear>(
			"*KITTOKEN", "SKILL", KitGear.class);

	@Override
	public Class<KitGear> getCDOMClass()
	{
		return KitGear.class;
	}

	@Override
	public CDOMSubLineLoader<KitGear> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMSecondaryToken<KitGear> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmptyCount() throws PersistenceLayerException
	{
		assertTrue(parse("Fireball"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testInvalidInputOnlyOne() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Equipment.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Equipment.class, "Fireball");
		primaryContext.ref.constructCDOMObject(Equipment.class, "English");
		secondaryContext.ref.constructCDOMObject(Equipment.class, "English");
		assertTrue(parse("Fireball,English"));
		assertFalse(primaryContext.ref.validate(null));
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(Equipment.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(Equipment.class, "Fireball");
		runRoundRobin("Fireball");
	}
}
