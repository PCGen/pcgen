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

import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.core.Equipment;
import pcgen.core.kit.KitGear;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractKitTokenTestCase;

import org.junit.jupiter.api.Test;

public class GearTokenTest extends AbstractKitTokenTestCase<KitGear>
{

    static GearToken token = new GearToken();
    static CDOMSubLineLoader<KitGear> loader = new CDOMSubLineLoader<>(
            "SKILL", KitGear.class);

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
    public CDOMPrimaryToken<KitGear> getToken()
    {
        return token;
    }

    @Test
    public void testInvalidInputEmptyCount()
    {
        assertTrue(parse("Fireball"));
        assertConstructionError();
    }

    @Test
    public void testInvalidInputOnlyOne()
    {
        primaryContext.getReferenceContext().constructCDOMObject(Equipment.class, "Fireball");
        secondaryContext.getReferenceContext().constructCDOMObject(Equipment.class, "Fireball");
        primaryContext.getReferenceContext().constructCDOMObject(Equipment.class, "English");
        secondaryContext.getReferenceContext().constructCDOMObject(Equipment.class, "English");
        assertTrue(parse("Fireball,English"));
        assertConstructionError();
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(Equipment.class, "Fireball");
        secondaryContext.getReferenceContext().constructCDOMObject(Equipment.class, "Fireball");
        runRoundRobin("Fireball");
    }
}
