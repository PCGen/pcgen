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
package plugin.lsttokens.equipment;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URISyntaxException;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Equipment;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SizeTokenTest extends AbstractCDOMTokenTestCase<Equipment>
{

    static SizeToken token = new SizeToken();
    static CDOMTokenLoader<Equipment> loader = new CDOMTokenLoader<>();
    private SizeAdjustment ps;

    @Override
    public Class<Equipment> getCDOMClass()
    {
        return Equipment.class;
    }

    @Override
    public CDOMLoader<Equipment> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Equipment> getToken()
    {
        return token;
    }

    @Override
    @BeforeEach
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        ps = BuildUtilities.createSize("Small", 0);
        primaryContext.getReferenceContext().importObject(ps);
        SizeAdjustment pm = BuildUtilities.createSize("Medium", 1);
        primaryContext.getReferenceContext().importObject(pm);
        SizeAdjustment ss = BuildUtilities.createSize("Small", 0);
        secondaryContext.getReferenceContext().importObject(ss);
        SizeAdjustment sm = BuildUtilities.createSize("Medium", 1);
        secondaryContext.getReferenceContext().importObject(sm);
    }

    @Test
    public void testInvalidNotASize()
    {
        if (token.parseToken(primaryContext, primaryProf, "W").passed())
        {
            assertFalse(primaryContext.getReferenceContext().resolveReferences(null));
        } else
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testRoundRobinS() throws PersistenceLayerException
    {
        runRoundRobin("S");
    }

    @Test
    public void testRoundRobinM() throws PersistenceLayerException
    {
        runRoundRobin("M");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "S";
    }

    @Override
    protected String getLegalValue()
    {
        return "M";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(ObjectKey.BASESIZE, null);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseLegal()
    {
        primaryProf.put(ObjectKey.BASESIZE, CDOMDirectSingleRef.getRef(ps));
        expectSingle(getToken().unparse(primaryContext, primaryProf), ps
                .getKeyName());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFail()
    {
        ObjectKey objectKey = ObjectKey.BASESIZE;
        primaryProf.put(objectKey, new Object());
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (ClassCastException e)
        {
            // Yep!
        }
    }
}
