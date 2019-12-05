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
package plugin.lsttokens.editcontext.deity;

import java.net.URISyntaxException;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Deity;
import pcgen.core.PCAlignment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.deity.AlignToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AlignIntegrationTest extends AbstractIntegrationTestCase<Deity>
{
    private static AlignToken token = new AlignToken();
    private static CDOMTokenLoader<Deity> loader = new CDOMTokenLoader<>();

    @Override
    @BeforeEach
    public final void setUp() throws PersistenceLayerException,
            URISyntaxException
    {
        super.setUp();
        PCAlignment lg = BuildUtilities.createAlignment("Lawful Good", "LG");
        primaryContext.getReferenceContext().importObject(lg);
        PCAlignment ln = BuildUtilities.createAlignment("Lawful Neutral", "LN");
        primaryContext.getReferenceContext().importObject(ln);
        PCAlignment slg = BuildUtilities.createAlignment("Lawful Good", "LG");
        secondaryContext.getReferenceContext().importObject(slg);
        PCAlignment sln = BuildUtilities.createAlignment("Lawful Neutral", "LN");
        secondaryContext.getReferenceContext().importObject(sln);
    }

    @Override
    public Class<Deity> getCDOMClass()
    {
        return Deity.class;
    }

    @Override
    public CDOMLoader<Deity> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Deity> getToken()
    {
        return token;
    }

    public Object getConstant(String string)
    {
        return primaryContext.getReferenceContext()
                .silentlyGetConstructedCDOMObject(PCAlignment.class, string);
    }

    public ObjectKey<?> getObjectKey()
    {
        return ObjectKey.ALIGNMENT;
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "LN");
        commit(modCampaign, tc, "LG");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoSet() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "LG");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoReset() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "LN");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }
}
