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
package plugin.lsttokens.kit.ability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.core.kit.KitAbilities;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractKitTokenTestCase;

import org.junit.jupiter.api.Test;

public class FreeTokenTest extends AbstractKitTokenTestCase<KitAbilities>
{

    static FreeToken token = new FreeToken();
    static CDOMSubLineLoader<KitAbilities> loader = new CDOMSubLineLoader<>(
            "SKILL", KitAbilities.class);

    @Override
    public Class<KitAbilities> getCDOMClass()
    {
        return KitAbilities.class;
    }

    @Override
    public CDOMSubLineLoader<KitAbilities> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<KitAbilities> getToken()
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
        assertTrue(parse("YES"));
        assertTrue(parseSecondary("YES"));
        assertEquals(Boolean.TRUE, getValue());
        internalTestInvalidInputString(Boolean.TRUE);
    }

    public void internalTestInvalidInputString(Object val)
    {
        assertEquals(val, getValue());
        assertFalse(parse("String"));
        assertEquals(val, getValue());
        assertFalse(parse("TYPE=TestType"));
        assertEquals(val, getValue());
        assertFalse(parse("TYPE.TestType"));
        assertEquals(val, getValue());
        assertFalse(parse("ALL"));
        assertEquals(val, getValue());
        assertFalse(parse("Yo!"));
        assertEquals(val, getValue());
        assertFalse(parse("Now"));
        assertEquals(val, getValue());
    }

    @Test
    public void testValidInputs()
    {
        assertTrue(parse("YES"));
        assertEquals(Boolean.TRUE, getValue());
        assertTrue(parse("NO"));
        assertEquals(Boolean.FALSE, getValue());
        // We're nice enough to be case insensitive here...
        assertTrue(parse("YeS"));
        assertEquals(Boolean.TRUE, getValue());
        assertTrue(parse("Yes"));
        assertEquals(Boolean.TRUE, getValue());
        assertTrue(parse("No"));
        assertEquals(Boolean.FALSE, getValue());
        // Allow abbreviations
        assertTrue(parse("Y"));
        assertEquals(Boolean.TRUE, getValue());
        assertTrue(parse("N"));
        assertEquals(Boolean.FALSE, getValue());
    }

    private Boolean getValue()
    {
        return primaryProf.getFree();
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
}
