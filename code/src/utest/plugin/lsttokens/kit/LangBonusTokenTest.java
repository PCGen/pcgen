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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.core.Language;
import pcgen.core.kit.KitLangBonus;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractKitTokenTestCase;

import org.junit.jupiter.api.Test;

public class LangBonusTokenTest extends AbstractKitTokenTestCase<KitLangBonus>
{

    static LangBonusToken token = new LangBonusToken();
    static CDOMSubLineLoader<KitLangBonus> loader = new CDOMSubLineLoader<>(
            "SKILL", KitLangBonus.class);

    @Override
    public Class<KitLangBonus> getCDOMClass()
    {
        return KitLangBonus.class;
    }

    @Override
    public CDOMSubLineLoader<KitLangBonus> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<KitLangBonus> getToken()
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
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(Language.class, "Fireball");
        secondaryContext.getReferenceContext().constructCDOMObject(Language.class, "Fireball");
        runRoundRobin("Fireball");
    }

    @Test
    public void testRoundRobinTwo() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(Language.class, "Fireball");
        secondaryContext.getReferenceContext().constructCDOMObject(Language.class, "Fireball");
        primaryContext.getReferenceContext().constructCDOMObject(Language.class, "English");
        secondaryContext.getReferenceContext().constructCDOMObject(Language.class, "English");
        runRoundRobin("Fireball" + getJoinCharacter() + "English");
    }

    @Test
    public void testInvalidListEnd()
    {
        assertFalse(parse("TestWP1" + getJoinCharacter()));
    }

    private static char getJoinCharacter()
    {
        return '|';
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
