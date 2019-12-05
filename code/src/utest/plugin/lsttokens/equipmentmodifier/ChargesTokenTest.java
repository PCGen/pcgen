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
package plugin.lsttokens.equipmentmodifier;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class ChargesTokenTest extends AbstractCDOMTokenTestCase<EquipmentModifier>
{

    static ChargesToken token = new ChargesToken();
    static CDOMTokenLoader<EquipmentModifier> loader = new CDOMTokenLoader<>();

    @Override
    public Class<EquipmentModifier> getCDOMClass()
    {
        return EquipmentModifier.class;
    }

    @Override
    public CDOMLoader<EquipmentModifier> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<EquipmentModifier> getToken()
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
    public void testInvalidNoPipe()
    {
        assertFalse(parse("4"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTwoPipe()
    {
        assertFalse(parse("4|5|6"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidMinNaN()
    {
        assertFalse(parse("String|4"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidMaxNaN()
    {
        assertFalse(parse("3|Str"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidMinNegative()
    {
        assertFalse(parse("-4|5"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidMaxNegative()
    {
        assertFalse(parse("6|-7"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidMaxLTMin()
    {
        assertFalse(parse("7|3"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        runRoundRobin("4|10");
    }

    @Test
    public void testRoundRobinMatching() throws PersistenceLayerException
    {
        runRoundRobin("10|10");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "6|15";
    }

    @Override
    protected String getLegalValue()
    {
        return "4|7";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    @Test
    public void testUnparseMinNull()
    {
        primaryProf.put(IntegerKey.MIN_CHARGES, null);
        primaryProf.put(IntegerKey.MAX_CHARGES, 1);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseMaxNull()
    {
        primaryProf.put(IntegerKey.MIN_CHARGES, 1);
        primaryProf.put(IntegerKey.MAX_CHARGES, null);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseNormal()
    {
        expectSingle(setAndUnparse(5, 10), "5|10");
    }

    @Test
    public void testUnparseEqual()
    {
        expectSingle(setAndUnparse(5, 5), "5|5");
    }

    @Test
    public void testUnparseZeroMin()
    {
        expectSingle(setAndUnparse(0, 5), "0|5");
    }

    @Test
    public void testUnparseZeroMinMax()
    {
        expectSingle(setAndUnparse(0, 0), "0|0");
    }

    @Test
    public void testUnparseMaxLTMin()
    {
        assertNull(setAndUnparse(10, 5));
    }

    @Test
    public void testUnparseNegativeMin()
    {
        assertNull(setAndUnparse(-5, 10));
    }

    @Test
    public void testUnparseNegativeMax()
    {
        assertNull(setAndUnparse(5, -10));
    }

    protected String[] setAndUnparse(int min, int max)
    {
        primaryProf.put(IntegerKey.MIN_CHARGES, min);
        primaryProf.put(IntegerKey.MAX_CHARGES, max);
        return getToken().unparse(primaryContext, primaryProf);
    }
}
