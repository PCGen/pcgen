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
package plugin.lsttokens;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.bonustokens.Weapon;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NaturalAttacksLstTest extends AbstractGlobalTokenTestCase
{

    static CDOMPrimaryToken<CDOMObject> token = new NaturalattacksLst();
    static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

    @Override
    @BeforeEach
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        addBonus(Weapon.class);
    }

    @Override
    public CDOMLoader<PCTemplate> getLoader()
    {
        return loader;
    }

    @Override
    public Class<PCTemplate> getCDOMClass()
    {
        return PCTemplate.class;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getReadToken()
    {
        return token;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getWriteToken()
    {
        return token;
    }

    @Test
    public void testInvalidEmpty()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNameOnly()
    {
        assertFalse(parse("Claw"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNameTypeOnly()
    {
        assertFalse(parse("Claw,Nasty"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNameTypeMult()
    {
        assertFalse(parse("Claw,Nasty,*2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyDamage()
    {
        assertFalse(parse("Claw,Nasty,*2,"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyName()
    {
        assertFalse(parse(",Nasty,*2,1d4"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyAttacks()
    {
        assertFalse(parse("Claw,Nasty,,1d4"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidPipeEnding()
    {
        assertFalse(parse("Claw,Nasty,*1,1d4|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidPipeStarting()
    {
        assertFalse(parse("|Claw,Nasty,*1,1d4"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidType1()
    {
        assertFalse(parse("Claw,Nasty.,*1,1d4"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidType2()
    {
        assertFalse(parse("Claw,.Nasty,*1,1d4"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidType3()
    {
        assertFalse(parse("Claw,Nasty..Natural,*1,1d4"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidAttacksNaN()
    {
        assertFalse(parse("Claw,Nasty.Natural,xx,1d4"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidAttacksHandsNaN()
    {
        assertFalse(parse("Claw,Nasty.Natural,2,1d4,xx"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidReservedName()
    {
        assertFalse(parse("None,Nasty,*1,1d4"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidOnlyPre()
    {
        try
        {
            assertFalse(parse("PRERACE:1,Human"));
        } catch (IllegalArgumentException iae)
        {
            // This is ok too
        }
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinOne() throws PersistenceLayerException
    {
        runRoundRobin("Claw,Weapon.Natural.Melee.Piercing.Slashing,1,1d4");
    }

    @Test
    public void testRoundRobinStar() throws PersistenceLayerException
    {
        runRoundRobin("Claw,Weapon.Natural.Melee.Piercing.Slashing,*2,1d4");
    }

    @Test
    public void testRoundRobinOneWithSProp() throws PersistenceLayerException
    {
        runRoundRobin("Claw,Weapon.Natural.Melee.Piercing.Slashing,1,1d4,SPROP=plus poison");
    }

    @Test
    public void testRoundRobinDupe() throws PersistenceLayerException
    {
        runRoundRobin("Claw,Weapon.Natural.Melee.Piercing.Slashing,*2,1d4|"
                + "Claw,Weapon.Natural.Melee.Piercing.Slashing,*2,1d4");
    }

    @Test
    public void testRoundRobinTwoWithHands() throws PersistenceLayerException
    {
        runRoundRobin("Claw,Weapon.Natural.Melee.Piercing.Slashing,*2,1d4,1|"
                + "Bite,Weapon.Natural.Melee.Piercing.Slashing,*2,1d4");
    }

    @Override
    protected String getLegalValue()
    {
        return "Claw,Weapon.Natural.Melee.Piercing.Slashing,*2,1d4,1";
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "Bite,Weapon.Natural.Melee.Piercing.Slashing,*2,1d4";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return new ConsolidationRule.AppendingConsolidation('|');
    }
}
