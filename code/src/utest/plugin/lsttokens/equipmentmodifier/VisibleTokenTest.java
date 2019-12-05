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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.enumeration.Visibility;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class VisibleTokenTest extends AbstractCDOMTokenTestCase<EquipmentModifier>
{

    static VisibleToken token = new VisibleToken();
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
    public void testInvalidOutput()
    {
        assertEquals(0, primaryContext.getWriteMessageCount());
        primaryProf.put(ObjectKey.VISIBILITY, Visibility.OUTPUT_ONLY);
        assertNull(token.unparse(primaryContext, primaryProf));
        assertFalse(primaryContext.getWriteMessageCount() == 0);
    }

    @Test
    public void testInvalidInputString()
    {
        internalTestInvalidInputString(null);
        assertNoSideEffects();

    }

    @Test
    public void testInvalidInputStringSet()
    {
        assertTrue(parse("QUALIFY"));
        assertTrue(parseSecondary("QUALIFY"));
        assertEquals(Visibility.QUALIFY, primaryProf.get(ObjectKey.VISIBILITY));
        internalTestInvalidInputString(Visibility.QUALIFY);
        assertNoSideEffects();
    }

    public void internalTestInvalidInputString(Object val)
    {
        assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
        assertFalse(parse("Always"));
        assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
        assertFalse(parse("String"));
        assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
        assertFalse(parse("TYPE=TestType"));
        assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
        assertFalse(parse("TYPE.TestType"));
        assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
        assertFalse(parse("ALL"));
        assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
        // Note case sensitivity
        assertFalse(parse("Display"));
    }

    @Test
    public void testValidInputs()
    {
        assertTrue(parse("NO"));
        assertEquals(Visibility.HIDDEN, primaryProf.get(ObjectKey.VISIBILITY));
        assertTrue(parse("QUALIFY"));
        assertEquals(Visibility.QUALIFY, primaryProf.get(ObjectKey.VISIBILITY));
        assertTrue(parse("YES"));
        assertEquals(Visibility.DEFAULT, primaryProf.get(ObjectKey.VISIBILITY));
    }

    @Test
    public void testRoundRobinYes() throws PersistenceLayerException
    {
        runRoundRobin("YES");
    }

    @Test
    public void testRoundRobinQualify() throws PersistenceLayerException
    {
        runRoundRobin("QUALIFY");
    }

    @Test
    public void testRoundRobinNo() throws PersistenceLayerException
    {
        runRoundRobin("NO");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "QUALIFY";
    }

    @Override
    protected String getLegalValue()
    {
        return "NO";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(getObjectKey(), null);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    private static ObjectKey<Visibility> getObjectKey()
    {
        return ObjectKey.VISIBILITY;
    }

    @Test
    public void testUnparseLegal()
    {
        primaryProf.put(getObjectKey(), Visibility.DEFAULT);
        expectSingle(getToken().unparse(primaryContext, primaryProf), Visibility.DEFAULT.getLSTFormat());
    }

    @Test
    public void testUnparseIllegal()
    {
        primaryProf.put(getObjectKey(), Visibility.OUTPUT_ONLY);
        assertBadUnparse();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFail()
    {
        ObjectKey objectKey = getObjectKey();
        primaryProf.put(objectKey, new Object());
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (ClassCastException e)
        {
            //Yep!
        }
    }
}
