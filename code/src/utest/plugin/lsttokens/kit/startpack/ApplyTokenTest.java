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
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.enumeration.KitApply;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Kit;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class ApplyTokenTest extends AbstractCDOMTokenTestCase<Kit>
{

    static ApplyToken token = new ApplyToken();

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
    public void testInvalidInputString()
    {
        internalTestInvalidInputString(null);
    }

    @Test
    public void testInvalidInputStringSet()
    {
        assertTrue(parse("INSTANT"));
        assertTrue(parseSecondary("INSTANT"));
        assertEquals(KitApply.INSTANT, primaryProf.get(ObjectKey.APPLY_MODE));
        internalTestInvalidInputString(KitApply.INSTANT);
    }

    public void internalTestInvalidInputString(Object val)
    {
        assertEquals(val, primaryProf.get(ObjectKey.APPLY_MODE));
        assertFalse(parse("Always"));
        assertEquals(val, primaryProf.get(ObjectKey.APPLY_MODE));
        assertFalse(parse("String"));
        assertEquals(val, primaryProf.get(ObjectKey.APPLY_MODE));
        assertFalse(parse("TYPE=TestType"));
        assertEquals(val, primaryProf.get(ObjectKey.APPLY_MODE));
        assertFalse(parse("TYPE.TestType"));
        assertEquals(val, primaryProf.get(ObjectKey.APPLY_MODE));
        assertFalse(parse("ALL"));
        assertEquals(val, primaryProf.get(ObjectKey.APPLY_MODE));
        // Note case sensitivity
        assertFalse(parse("Permanent"));
    }

    @Test
    public void testValidInputs()
    {
        assertTrue(parse("INSTANT"));
        assertEquals(KitApply.INSTANT, primaryProf.get(ObjectKey.APPLY_MODE));
        assertTrue(parse("PERMANENT"));
        assertEquals(KitApply.PERMANENT, primaryProf.get(ObjectKey.APPLY_MODE));
    }

    @Test
    public void testRoundRobinPermanent() throws PersistenceLayerException
    {
        runRoundRobin("PERMANENT");
    }

    @Test
    public void testRoundRobinInstant() throws PersistenceLayerException
    {
        runRoundRobin("INSTANT");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "PERMANENT";
    }

    @Override
    protected String getLegalValue()
    {
        return "INSTANT";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }
}
