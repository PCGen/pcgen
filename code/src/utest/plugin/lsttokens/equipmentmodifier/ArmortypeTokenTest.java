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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.processor.ChangeArmorType;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class ArmortypeTokenTest extends
        AbstractCDOMTokenTestCase<EquipmentModifier>
{

    static ArmortypeToken token = new ArmortypeToken();
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
    public void testInvalidInputEmpty()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNoResult()
    {
        assertFalse(parse("Medium"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmptyResult()
    {
        assertFalse(parse("Medium|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmptySource()
    {
        assertFalse(parse("|Medium"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDoublePipe()
    {
        assertFalse(parse("Light||Medium"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputTwoPipe()
    {
        assertFalse(parse("Light|Medium|Heavy"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinLightMedium() throws PersistenceLayerException
    {
        runRoundRobin("Light|Medium");
    }

    @Test
    public void testRoundRobinMediumLight() throws PersistenceLayerException
    {
        runRoundRobin("Medium|Light");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "Medium|Light";
    }

    @Override
    protected String getLegalValue()
    {
        return "Heavy|Medium";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.removeListFor(ListKey.ARMORTYPE);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseSingle()
    {
        primaryProf.addToListFor(ListKey.ARMORTYPE, new ChangeArmorType(
                "Light", "Medium"));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, "Light|Medium");
    }

    @Test
    public void testUnparseNullInList()
    {
        primaryProf.addToListFor(ListKey.ARMORTYPE, null);
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (NullPointerException e)
        {
            // Yep!
        }
    }

    @Test
    public void testUnparseMultiple()
    {
        primaryProf.addToListFor(ListKey.ARMORTYPE, new ChangeArmorType(
                "Medium", "Light"));
        primaryProf.addToListFor(ListKey.ARMORTYPE, new ChangeArmorType(
                "Heavy", "Medium"));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        assertNotNull(unparsed);
        assertEquals(2, unparsed.length);
        List<String> upList = Arrays.asList(unparsed);
        assertTrue(upList.contains("Medium|Light"));
        assertTrue(upList.contains("Heavy|Medium"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFail()
    {
        ListKey objectKey = ListKey.ARMORTYPE;
        primaryProf.addToListFor(objectKey, new Object());
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (ClassCastException e)
        {
            // Yep!
        }
    }

    @Test
    public void testUnparseNullSource()
    {
        try
        {
            primaryProf.addToListFor(ListKey.ARMORTYPE, new ChangeArmorType(
                    null, "Medium"));
            assertBadUnparse();
        } catch (NullPointerException e)
        {
            // Good here too :)
        }
    }

    @Test
    public void testUnparseNullTarget()
    {
        try
        {
            primaryProf.addToListFor(ListKey.ARMORTYPE, new ChangeArmorType(
                    "Heavy", null));
            assertBadUnparse();
        } catch (NullPointerException e)
        {
            // Good here too :)
        }
    }

}
