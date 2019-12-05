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
package plugin.lsttokens.equipment;

import static org.junit.jupiter.api.Assertions.assertFalse;

import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import plugin.lsttokens.testsupport.AbstractListInputTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public abstract class AbstractEqModTokenTestCase
        extends AbstractListInputTokenTestCase<Equipment, EquipmentModifier>
{

    static CDOMTokenLoader<Equipment> loader = new CDOMTokenLoader<>();

    @Override
    public Class<Equipment> getCDOMClass()
    {
        return Equipment.class;
    }

    @Override
    public CDOMLoader<Equipment> getLoader()
    {
        return loader;
    }

    @Override
    public Class<EquipmentModifier> getTargetClass()
    {
        return EquipmentModifier.class;
    }

    @Override
    public boolean isTypeLegal()
    {
        return false;
    }

    @Override
    public boolean isAllLegal()
    {
        return false;
    }

    @Override
    public boolean isClearDotLegal()
    {
        return false;
    }

    @Override
    public boolean isClearLegal()
    {
        return false;
    }

    @Override
    public char getJoinCharacter()
    {
        return '.';
    }

    @Override
    public void testInvalidInputJoinedPipe()
    {
        // This is not invalid, because EqMod uses | for associations
    }

    // TODO Implement after 5.16
    // @Test
    // public void testInvalidMiddleNone() throws PersistenceLayerException
    // {
    // assertFalse(parse("EQMOD1.NONE.EQMOD2"));
    // assertNoSideEffects();
    // }
    //
    // @Test
    // public void testInvalidStartingNone() throws PersistenceLayerException
    // {
    // assertFalse(parse("NONE.EQMOD2"));
    // assertNoSideEffects();
    // }
    //
    // @Test
    // public void testInvalidEndingNone() throws PersistenceLayerException
    // {
    // assertFalse(parse("EQMOD2.NONE"));
    // assertNoSideEffects();
    // }

    @Test
    public void testInvalidEmptyAssociation()
    {
        assertFalse(parse("EQMOD2|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTrailingAssociation()
    {
        assertFalse(parse("EQMOD2|Assoc|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyModAssociation()
    {
        assertFalse(parse("|Assoc|Assoc2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptySecondModAssociation()
    {
        assertFalse(parse("MOD1.|Assoc|Assoc2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptySecondModAfterAssociation()
    {
        assertFalse(parse("MOD1|ModAssoc.|Assoc|Assoc2"));
        assertNoSideEffects();
    }

    // TODO Implement checking after 5.16?
    // @Test
    // public void testInvalidEmptyComplexAssociation()
    // throws PersistenceLayerException
    // {
    // assertFalse(parse("MOD1|ModAssoc[]"));
    // assertNoSideEffects();
    // }
    //
    // @Test
    // public void testInvalidNoOpenBracketComplexAssociation()
    // throws PersistenceLayerException
    // {
    // assertFalse(parse("MOD1|ModAssoc Assoc]"));
    // assertNoSideEffects();
    // }
    //
    // @Test
    // public void testInvalidTwoOpenBracketComplexAssociation()
    // throws PersistenceLayerException
    // {
    // assertFalse(parse("MOD1|ModAssoc[[Assoc]"));
    // assertNoSideEffects();
    // }

    @Test
    public void testInvalidDoubleBarAssociation()
    {
        assertFalse(parse("EQMOD2|Assoc||Assoc2"));
        assertNoSideEffects();
    }

    public void testRoundRobinOnlyAssociation()
            throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(EquipmentModifier.class,
                "EQMOD2");
        secondaryContext.getReferenceContext().constructCDOMObject(EquipmentModifier.class,
                "EQMOD2");
        runRoundRobin("EQMOD2|9500");
    }

    public void testRoundRobinComplexAssociation()
            throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(EquipmentModifier.class,
                "EQMOD2");
        secondaryContext.getReferenceContext().constructCDOMObject(EquipmentModifier.class,
                "EQMOD2");
        runRoundRobin("EQMOD2|COST[9500]");
    }

    // public void testRoundRobinInnerBracketAssociation()
    // throws PersistenceLayerException
    // {
    // runRoundRobin("EQMOD2|COST[[9500]]");
    // }

    public void testRoundRobinComplexMultipleAssociation()
            throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(EquipmentModifier.class,
                "EQMOD2");
        secondaryContext.getReferenceContext().constructCDOMObject(EquipmentModifier.class,
                "EQMOD2");
        runRoundRobin("EQMOD2|COST[9500]PLUS[+1]");
    }

    public void testRoundRobinWeightAssociation()
            throws PersistenceLayerException
    {
        runRoundRobin("_WEIGHTADD|9500");
    }

    public void testRoundRobinDamageAssociation()
            throws PersistenceLayerException
    {
        runRoundRobin("_DAMAGE|4d6");
    }

    @Override
    public boolean allowDups()
    {
        return false;
    }

    @Test
    public void testOverwriteDamageWeightAdd()
    {
        parse("_DAMAGE|4d6");
        validateUnparsed(primaryContext, primaryProf, "_DAMAGE|4d6");
        parse("_WEIGHTADD|9500");
        validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
                .getAnswer("_DAMAGE|4d6", "_WEIGHTADD|9500"));
    }
}
