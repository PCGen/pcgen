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
package plugin.lsttokens.kit.skill;

import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.core.PCClass;
import pcgen.core.kit.KitSkill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractKitTokenTestCase;

import org.junit.jupiter.api.Test;

public class ClassTokenTest extends AbstractKitTokenTestCase<KitSkill>
{

    static ClassToken token = new ClassToken();
    static CDOMSubLineLoader<KitSkill> loader = new CDOMSubLineLoader<>(
            "SKILL", KitSkill.class);

    @Override
    public Class<KitSkill> getCDOMClass()
    {
        return KitSkill.class;
    }

    @Override
    public CDOMSubLineLoader<KitSkill> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<KitSkill> getToken()
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
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fireball");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fireball");
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "English");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "English");
        assertTrue(parse("Fireball,English"));
        assertConstructionError();
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fireball");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fireball");
        runRoundRobin("Fireball");
    }
}
