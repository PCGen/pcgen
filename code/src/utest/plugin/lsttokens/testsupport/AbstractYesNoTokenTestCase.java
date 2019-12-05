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
package plugin.lsttokens.testsupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.persistence.PersistenceLayerException;

import org.junit.jupiter.api.Test;

public abstract class AbstractYesNoTokenTestCase<T extends CDOMObject> extends
        AbstractCDOMTokenTestCase<T>
{

    public abstract ObjectKey<Boolean> getObjectKey();

    @Test
    public void testInvalidInputNullString()
    {
        internalTestInvalidInputString(null);
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmptyString()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputStringSet()
    {
        assertTrue(parse("YES"));
        assertTrue(parseSecondary("YES"));
        assertEquals(Boolean.TRUE, primaryProf.get(getObjectKey()));
        internalTestInvalidInputString(Boolean.TRUE);
        assertNoSideEffects();
    }

    public void internalTestInvalidInputString(Object val)
    {
        assertEquals(val, primaryProf.get(getObjectKey()));
        assertFalse(parse("String"));
        assertEquals(val, primaryProf.get(getObjectKey()));
        assertFalse(parse("TYPE=TestType"));
        assertEquals(val, primaryProf.get(getObjectKey()));
        assertFalse(parse("TYPE.TestType"));
        assertEquals(val, primaryProf.get(getObjectKey()));
        assertFalse(parse("ALL"));
        assertEquals(val, primaryProf.get(getObjectKey()));
        assertFalse(parse("Yo!"));
        assertEquals(val, primaryProf.get(getObjectKey()));
        assertFalse(parse("Now"));
        assertEquals(val, primaryProf.get(getObjectKey()));
    }

    @Test
    public void testValidInputs()
    {
        assertTrue(parse("YES"));
        assertEquals(Boolean.TRUE, primaryProf.get(getObjectKey()));
        assertTrue(parse("NO"));
        assertEquals(Boolean.FALSE, primaryProf.get(getObjectKey()));
        // We're nice enough to be case insensitive here...
        assertTrue(parse("YeS"));
        assertEquals(Boolean.TRUE, primaryProf.get(getObjectKey()));
        assertTrue(parse("Yes"));
        assertEquals(Boolean.TRUE, primaryProf.get(getObjectKey()));
        assertTrue(parse("No"));
        assertEquals(Boolean.FALSE, primaryProf.get(getObjectKey()));
        // Allow abbreviations
        assertTrue(parse("Y"));
        assertEquals(Boolean.TRUE, primaryProf.get(getObjectKey()));
        assertTrue(parse("N"));
        assertEquals(Boolean.FALSE, primaryProf.get(getObjectKey()));
    }

    @Test
    public void testRoundRobinYes() throws PersistenceLayerException
    {
        runRoundRobin("YES");
    }

    @Test
    public void testRoundRobinNo() throws PersistenceLayerException
    {
        runRoundRobin("NO");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "YES";
    }

    @Override
    protected String getLegalValue()
    {
        return "NO";
    }

    @Test
    public void testUnparseYes()
    {
        expectSingle(setAndUnparse(true), "YES");
    }

    @Test
    public void testUnparseNo()
    {
        expectSingle(setAndUnparse(false), "NO");
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(getObjectKey(), null);
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        assertNull(unparsed);
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

    protected String[] setAndUnparse(boolean val)
    {
        primaryProf.put(getObjectKey(), val);
        return getToken().unparse(primaryContext, primaryProf);
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }
}
