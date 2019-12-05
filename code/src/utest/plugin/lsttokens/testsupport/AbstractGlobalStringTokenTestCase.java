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

import pcgen.cdom.enumeration.StringKey;
import pcgen.persistence.PersistenceLayerException;

import org.junit.jupiter.api.Test;

public abstract class AbstractGlobalStringTokenTestCase extends
        AbstractGlobalTokenTestCase
{

    @Test
    public void testInvalidInputEmpty()
    {
        assertFalse(parse(""));
        assertNull(primaryProf.get(getStringKey()));
        assertNoSideEffects();
    }

    @Test
    public void testValidInputs()
    {
        assertTrue(parse("Niederösterreich"));
        assertEquals("Niederösterreich", primaryProf.get(getStringKey()));
        assertTrue(parse("Finger Lakes"));
        assertEquals("Finger Lakes", primaryProf.get(getStringKey()));
        assertTrue(parse("Rheinhessen"));
        assertEquals("Rheinhessen", primaryProf.get(getStringKey()));
        assertTrue(parse("Languedoc-Roussillon"));
        assertEquals("Languedoc-Roussillon", primaryProf.get(getStringKey()));
        assertTrue(parse("Yarra Valley"));
        assertEquals("Yarra Valley", primaryProf.get(getStringKey()));
    }

    public abstract StringKey getStringKey();

    @Test
    public void testRoundRobinBase() throws PersistenceLayerException
    {
        runRoundRobin("Rheinhessen");
    }

    @Test
    public void testRoundRobinWithSpace() throws PersistenceLayerException
    {
        runRoundRobin("Finger Lakes");
    }

    @Test
    public void testRoundRobinNonEnglishAndN() throws PersistenceLayerException
    {
        runRoundRobin("Niederösterreich");
    }

    @Test
    public void testRoundRobinHyphen() throws PersistenceLayerException
    {
        runRoundRobin("Languedoc-Roussillon");
    }

    @Test
    public void testRoundRobinY() throws PersistenceLayerException
    {
        runRoundRobin("Yarra Valley");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "Languedoc-Roussillon";
    }

    @Override
    protected String getLegalValue()
    {
        return "Yarra Valley";
    }

    @Test
    public void testUnparseNo()
    {
        expectSingle(setAndUnparse(getLegalValue()), getLegalValue());
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(getStringKey(), null);
        assertNull(getWriteToken().unparse(primaryContext, primaryProf));
    }

    /*
     * TODO Need to define the appropriate behavior here - is the token
     * responsible for catching this?
     */
    // @Test
    // public void testUnparseEmpty() throws PersistenceLayerException
    // {
    // primaryProf.put(getStringKey(), "");
    // assertNull(getToken().unparse(primaryContext, primaryProf));
    // }

    protected String[] setAndUnparse(String val)
    {
        primaryProf.put(getStringKey(), val);
        return getWriteToken().unparse(primaryContext, primaryProf);
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }
}
