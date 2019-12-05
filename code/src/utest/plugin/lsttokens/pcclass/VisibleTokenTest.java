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
package plugin.lsttokens.pcclass;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.enumeration.Visibility;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class VisibleTokenTest extends AbstractCDOMTokenTestCase<PCClass>
{

    static VisibleToken token = new VisibleToken();
    static CDOMTokenLoader<PCClass> loader = new CDOMTokenLoader<>();

    @Override
    public Class<PCClass> getCDOMClass()
    {
        return PCClass.class;
    }

    @Override
    public CDOMLoader<PCClass> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<PCClass> getToken()
    {
        return token;
    }

    @Test
    public void testInvalidOutput()
    {
        assertEquals(0, primaryContext.getWriteMessageCount());
        primaryProf.put(ObjectKey.VISIBILITY, Visibility.QUALIFY);
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
        assertTrue(parse("NO"));
        assertTrue(parseSecondary("NO"));
        assertEquals(Visibility.HIDDEN, primaryProf.get(ObjectKey.VISIBILITY));
        internalTestInvalidInputString(Visibility.HIDDEN);
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
        assertFalse(parse("Yes"));
    }

    @Test
    public void testValidInputs()
    {
        // assertTrue(parse("DISPLAY"));
        // assertEquals(Visibility.DISPLAY,
        // primaryProf.get(ObjectKey.VISIBILITY));
        // assertTrue(parse("EXPORT"));
        // assertEquals(Visibility.EXPORT,
        // primaryProf.get(ObjectKey.VISIBILITY));
        assertTrue(parse("YES"));
        assertEquals(Visibility.DEFAULT, primaryProf.get(ObjectKey.VISIBILITY));
        assertTrue(parse("NO"));
        assertEquals(Visibility.HIDDEN, primaryProf.get(ObjectKey.VISIBILITY));
    }

    //
    // @Test
    // public void testRoundRobinDisplay() throws PersistenceLayerException
    // {
    // runRoundRobin("DISPLAY");
    // }
    //
    // @Test
    // public void testRoundRobinExport() throws PersistenceLayerException
    // {
    // runRoundRobin("EXPORT");
    // }

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
        primaryProf.put(getObjectKey(), Visibility.QUALIFY);
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
