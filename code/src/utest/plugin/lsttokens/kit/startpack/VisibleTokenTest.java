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
package plugin.lsttokens.kit.startpack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Kit;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.enumeration.Visibility;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class VisibleTokenTest extends AbstractCDOMTokenTestCase<Kit>
{

    static VisibleToken token = new VisibleToken();

    static CDOMTokenLoader<Kit> loader = new CDOMTokenLoader<>();

    @Override
    public Class<Kit> getCDOMClass()
    {
        return Kit.class;
    }

    @Override
    public CDOMLoader<Kit> getLoader()
    {
        return loader;
    }


    @Override
    public CDOMPrimaryToken<Kit> getToken()
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
    }

    @Test
    public void testInvalidInputStringSet()
    {
        assertTrue(parse("QUALIFY"));
        assertTrue(parseSecondary("QUALIFY"));
        assertEquals(Visibility.QUALIFY, primaryProf.get(ObjectKey.VISIBILITY));
        internalTestInvalidInputString(Visibility.QUALIFY);
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
        assertTrue(parse("QUALIFY"));
        assertEquals(Visibility.QUALIFY, primaryProf.get(ObjectKey.VISIBILITY));
        assertTrue(parse("YES"));
        assertEquals(Visibility.DEFAULT, primaryProf.get(ObjectKey.VISIBILITY));
        assertTrue(parse("NO"));
        assertEquals(Visibility.HIDDEN, primaryProf.get(ObjectKey.VISIBILITY));
    }

    @Test
    public void testRoundRobinQualify() throws PersistenceLayerException
    {
        runRoundRobin("QUALIFY");
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
}
