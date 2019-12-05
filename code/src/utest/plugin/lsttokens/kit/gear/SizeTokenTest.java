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
package plugin.lsttokens.kit.gear;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;

import pcgen.core.SizeAdjustment;
import pcgen.core.kit.KitGear;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractKitTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SizeTokenTest extends AbstractKitTokenTestCase<KitGear>
{

    static SizeToken token = new SizeToken();
    static CDOMSubLineLoader<KitGear> loader = new CDOMSubLineLoader<>(
            "TABLE", KitGear.class);

    @Override
    public Class<KitGear> getCDOMClass()
    {
        return KitGear.class;
    }

    @Override
    public CDOMSubLineLoader<KitGear> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<KitGear> getToken()
    {
        return token;
    }

    @Override
    @BeforeEach
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        SizeAdjustment ps = BuildUtilities.createSize("S", 0);
        primaryContext.getReferenceContext().importObject(ps);
        SizeAdjustment pm = BuildUtilities.createSize("M", 1);
        primaryContext.getReferenceContext().importObject(pm);
        SizeAdjustment ss = BuildUtilities.createSize("S", 0);
        secondaryContext.getReferenceContext().importObject(ss);
        SizeAdjustment sm = BuildUtilities.createSize("M", 1);
        secondaryContext.getReferenceContext().importObject(sm);
    }

    @Test
    public void testInvalidNotASize()
    {
        assertTrue(token.parseToken(primaryContext, primaryProf, "W").passed());
        assertFalse(primaryContext.getReferenceContext().resolveReferences(null));
    }

    @Test
    public void testRoundRobinS() throws PersistenceLayerException
    {
        runRoundRobin("S");
    }

    @Test
    public void testRoundRobinPC() throws PersistenceLayerException
    {
        //Special Case
        runRoundRobin("PC");
    }

    @Test
    public void testRoundRobinM() throws PersistenceLayerException
    {
        runRoundRobin("M");
    }
}
