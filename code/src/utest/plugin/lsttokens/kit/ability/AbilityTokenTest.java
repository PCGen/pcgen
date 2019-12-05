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
package plugin.lsttokens.kit.ability;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Ability;
import pcgen.core.kit.KitAbilities;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractKitTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AbilityTokenTest extends AbstractKitTokenTestCase<KitAbilities>
{

    static AbilityToken token = new AbilityToken();
    static CDOMSubLineLoader<KitAbilities> loader = new CDOMSubLineLoader<>(
            "SKILL", KitAbilities.class);

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        Ability a = BuildUtilities.getFeatCat().newInstance();
        a.setName("Dummy");
        primaryContext.getReferenceContext().importObject(a);
        Ability b = BuildUtilities.getFeatCat().newInstance();
        b.setName("Dummy");
        secondaryContext.getReferenceContext().importObject(b);
    }

    @Override
    public Class<KitAbilities> getCDOMClass()
    {
        return KitAbilities.class;
    }

    @Override
    public CDOMSubLineLoader<KitAbilities> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<KitAbilities> getToken()
    {
        return token;
    }

    @Test
    public void testInvalidInputEmptyCount()
    {
        assertTrue(parse("CATEGORY=FEAT|Fireball"));
        assertConstructionError();
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        BuildUtilities.buildFeat(primaryContext, "Fireball");
        BuildUtilities.buildFeat(secondaryContext, "Fireball");
        runRoundRobin("CATEGORY=FEAT|Fireball");
    }

    @Test
    public void testRoundRobinType() throws PersistenceLayerException
    {
        Ability ab = BuildUtilities.buildFeat(primaryContext, "Fireball");
        ab.addToListFor(ListKey.TYPE, Type.getConstant("Test"));
        ab = BuildUtilities.buildFeat(secondaryContext, "Fireball");
        ab.addToListFor(ListKey.TYPE, Type.getConstant("Test"));
        runRoundRobin("CATEGORY=FEAT|TYPE=Test");
    }

    @Test
    public void testRoundRobinTwo() throws PersistenceLayerException
    {
        BuildUtilities.buildFeat(primaryContext, "Fireball");
        BuildUtilities.buildFeat(secondaryContext, "Fireball");
        BuildUtilities.buildFeat(primaryContext, "English");
        BuildUtilities.buildFeat(secondaryContext, "English");
        runRoundRobin("CATEGORY=FEAT|English" + getJoinCharacter() + "Fireball");
    }

    @Test
    public void testInvalidListEnd()
    {
        assertFalse(parse("CATEGORY=FEAT|TestWP1" + getJoinCharacter()));
    }

    private static char getJoinCharacter()
    {
        return '|';
    }

    @Test
    public void testInvalidListStart()
    {
        assertFalse(parse("CATEGORY=FEAT|" + getJoinCharacter() + "TestWP1"));
    }

    @Test
    public void testInvalidListDoubleJoin()
    {
        assertFalse(parse("CATEGORY=FEAT|TestWP2" + getJoinCharacter() + getJoinCharacter()
                + "TestWP1"));
    }

    //TODO Doesn't test TYPE=
}
