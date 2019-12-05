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
package plugin.lsttokens.template;

import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.URISyntaxException;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCTemplate;
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

public class SizeTokenTest extends AbstractCDOMTokenTestCase<PCTemplate>
{

    static SizeToken token = new SizeToken();
    static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

    @Override
    public Class<PCTemplate> getCDOMClass()
    {
        return PCTemplate.class;
    }

    @Override
    public CDOMLoader<PCTemplate> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<PCTemplate> getToken()
    {
        return token;
    }

    @Override
    @BeforeEach
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        SizeAdjustment ps = BuildUtilities.createSize("Small", 0);
        primaryContext.getReferenceContext().importObject(ps);
        SizeAdjustment pm = BuildUtilities.createSize("Medium", 1);
        primaryContext.getReferenceContext().importObject(pm);
        SizeAdjustment ss = BuildUtilities.createSize("Small", 0);
        secondaryContext.getReferenceContext().importObject(ss);
        SizeAdjustment sm = BuildUtilities.createSize("Medium", 1);
        secondaryContext.getReferenceContext().importObject(sm);

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

    @Test
    public void testRoundRobinFormula() throws PersistenceLayerException
    {
        runRoundRobin("max(4,String)");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "max(4,String)";
    }

    @Override
    protected String getLegalValue()
    {
        return "M";
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(StringKey.SIZEFORMULA, null);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseLegal()
    {
        primaryProf.put(StringKey.SIZEFORMULA, "1");
        expectSingle(getToken().unparse(primaryContext, primaryProf), "1");
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }
}
