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
package plugin.lsttokens.kit.table;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.net.URISyntaxException;

import pcgen.core.EquipmentModifier;
import pcgen.core.kit.KitTable;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.kit.basekit.LookupToken;
import plugin.lsttokens.kit.gear.EqmodToken;
import plugin.lsttokens.testsupport.AbstractKitTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ValuesTokenTest extends AbstractKitTokenTestCase<KitTable>
{

    static ValuesToken token = new ValuesToken();
    static EqmodToken eqmodToken = new EqmodToken();
    static LookupToken lookupToken = new LookupToken();
    static CDOMSubLineLoader<KitTable> loader = new CDOMSubLineLoader<>(
            "TABLE", KitTable.class);

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        TokenRegistration.register(eqmodToken);
        TokenRegistration.register(lookupToken);
        super.setUp();
    }

    @Override
    public Class<KitTable> getCDOMClass()
    {
        return KitTable.class;
    }

    @Override
    public CDOMSubLineLoader<KitTable> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<KitTable> getToken()
    {
        return token;
    }

    public static char getJoinCharacter()
    {
        return '.';
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
    public void testInvalidEmptyRange()
    {
        assertFalse(parse("EQMOD:EQMOD2|"));
    }

    @Test
    public void testInvalidRangeTwo()
    {
        assertFalse(parse("EQMOD:MOD1|3,"));
    }

    @Test
    public void testInvalidRangeOne()
    {
        assertFalse(parse("EQMOD:MOD1|,3"));
    }

    @Test
    public void testInvalidEqModDot()
    {
        assertFalse(parse("EQMOD:MOD1.|5,7"));
    }

    @Test
    public void testInvalidEqModOdd()
    {
        assertFalse(parse("EQMOD:MOD1|5,7|EQMOD:MOD2"));
    }

    @Test
    public void testInvalidDotEqMod()
    {
        assertFalse(parse("EQMOD:.MOD1|5,7"));
    }

    // @Test
    // public void testInvalidEmptyComplexAssociation()
    // throws PersistenceLayerException
    // {
    // assertFalse(parse("MOD1|ModAssoc[]"));
    // }
    //
    // @Test
    // public void testInvalidNoOpenBracketComplexAssociation()
    // throws PersistenceLayerException
    // {
    // assertFalse(parse("MOD1|ModAssoc Assoc]"));
    // }
    //
    // @Test
    // public void testInvalidTwoOpenBracketComplexAssociation()
    // throws PersistenceLayerException
    // {
    // assertFalse(parse("MOD1|ModAssoc[[Assoc]"));
    // }

    @Test
    public void testInvalidDoubleComma()
    {
        assertFalse(parse("EQMOD:EQMOD2|5,,8"));
    }

    @Test
    public void testRoundRobinOnlyAssociation()
            throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(EquipmentModifier.class,
                "EQMOD2");
        secondaryContext.getReferenceContext().constructCDOMObject(EquipmentModifier.class,
                "EQMOD2");
        runRoundRobin("EQMOD:EQMOD2|5,10");
    }

    @Test
    public void testRoundRobinComplex() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(EquipmentModifier.class,
                "EQMOD2");
        secondaryContext.getReferenceContext().constructCDOMObject(EquipmentModifier.class,
                "EQMOD2");
        runRoundRobin("EQMOD:EQMOD2|1,99|"
                + "[LOOKUP:Minor Special Ability (B),roll(\"1d100\")]"
                + "[LOOKUP:Minor Special Ability (B),roll(\"1d100\")]|100");
    }

    @Test
    public void testRoundRobinFormulaComplex() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(EquipmentModifier.class,
                "EQMOD2");
        secondaryContext.getReferenceContext().constructCDOMObject(EquipmentModifier.class,
                "EQMOD2");
        runRoundRobin("EQMOD:EQMOD2|1,if(var(\"SIZE==3||SIZE==4\"),5,10)|"
                + "[LOOKUP:Minor Special Ability (B),roll(\"1d100\")]"
                + "[LOOKUP:Minor Special Ability (B),roll(\"1d100\")]"
                + "|1+if(var(\"SIZE==3||SIZE==4\"),5,10),100");
    }


    // public void testRoundRobinComplexAssociation()
    // throws PersistenceLayerException
    // {
    // primaryContext.ref.constructCDOMObject(EquipmentModifier.class,
    // "EQMOD2");
    // secondaryContext.ref.constructCDOMObject(EquipmentModifier.class,
    // "EQMOD2");
    // runRoundRobin("EQMOD2|COST[9500]");
    // }

    // public void testRoundRobinInnerBracketAssociation()
    // throws PersistenceLayerException
    // {
    // runRoundRobin("EQMOD2|COST[[9500]]");
    // }

    @Test
    public void testInvalidInputString()
    {
        assertFalse(parse("String"));
    }

    @Test
    public void testInvalidInputStringColon()
    {
        assertFalse(parse("String:Strung"));
    }
}
