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
package plugin.lsttokens.editcontext;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Domain;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.TypeLst;
import plugin.lsttokens.editcontext.testsupport.AbstractTypeSafeListIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class TypeIntegrationTest extends
        AbstractTypeSafeListIntegrationTestCase<CDOMObject>
{

    private static TypeLst token = new TypeLst();
    private static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

    @Override
    public Class<Domain> getCDOMClass()
    {
        return Domain.class;
    }

    @Override
    public CDOMLoader<CDOMObject> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getToken()
    {
        return token;
    }

    @Override
    public char getJoinCharacter()
    {
        return '.';
    }

    @Test
    public void dummyTest()
    {
        // Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
    }

    @Override
    public boolean isClearDotLegal()
    {
        return false;
    }

    @Override
    public boolean isClearLegal()
    {
        return true;
    }

    @Override
    public Object getConstant(String string)
    {
        return string;
    }

    @Override
    protected boolean requiresPreconstruction()
    {
        return false;
    }

    @Test
    public void testRoundRobinRemove() throws PersistenceLayerException
    {
        if (requiresPreconstruction())
        {
            getConstant("TestWP2");
        }
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "REMOVE.TestWP2");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinAddRemove() throws PersistenceLayerException
    {
        if (requiresPreconstruction())
        {
            getConstant("TestWP2");
        }
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "TestWP2");
        commit(modCampaign, tc, "REMOVE.TestWP2");
        completeRoundRobin(tc);
    }


    @Test
    public void testRoundRobinInsert() throws PersistenceLayerException
    {
        if (requiresPreconstruction())
        {
            getConstant("TestWP1");
            getConstant("TestWP2");
        }
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "TestWP2");
        commit(modCampaign, tc, "TestWP1.REMOVE.TestWP2");
        completeRoundRobin(tc);
    }

}
