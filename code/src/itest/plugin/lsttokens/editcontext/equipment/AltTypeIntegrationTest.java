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
package plugin.lsttokens.editcontext.equipment;

import pcgen.cdom.enumeration.Type;
import pcgen.core.Equipment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractTypeSafeListIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.equipment.AlttypeToken;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class AltTypeIntegrationTest extends
        AbstractTypeSafeListIntegrationTestCase<Equipment>
{

    private static AlttypeToken token = new AlttypeToken();
    private static CDOMTokenLoader<Equipment> loader = new CDOMTokenLoader<>();

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
    public Object getConstant(String string)
    {
        return Type.getConstant(string);
    }

    @Override
    protected boolean requiresPreconstruction()
    {
        return false;
    }

    @Test
    public void dummyTest()
    {
        // Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
    }

    @Override
    public char getJoinCharacter()
    {
        return '.';
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
