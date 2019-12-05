/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.editcontext.subclass;

import java.net.URISyntaxException;

import pcgen.cdom.enumeration.SubClassCategory;
import pcgen.core.SubClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.subclass.ChoiceToken;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class ChoiceIntegrationTest extends
        AbstractIntegrationTestCase<SubClass>
{

    private static ChoiceToken token = new ChoiceToken();
    private static CDOMTokenLoader<SubClass> loader = new CDOMTokenLoader<>();

    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        prefix = "CLASS:";
    }

    @Override
    public Class<SubClass> getCDOMClass()
    {
        return SubClass.class;
    }

    @Override
    public CDOMLoader<SubClass> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<SubClass> getToken()
    {
        return token;
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "SCHOOL|Illusion");
        commit(modCampaign, tc, "DESCRIPTOR|Fear");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoSet() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "DESCRIPTOR|Fear");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoReset() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "DESCRIPTOR|Fear");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Override
    protected SubClass construct(LoadContext context, String name)
    {
        SubClass a = SubClassCategory.getConstant("SCC").newInstance();
        a.setName(name);
        context.getReferenceContext().importObject(a);
        return a;
    }
}
