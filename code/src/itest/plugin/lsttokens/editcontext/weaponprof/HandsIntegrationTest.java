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
package plugin.lsttokens.editcontext.weaponprof;

import pcgen.core.WeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegerIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.weaponprof.HandsToken;

import org.junit.jupiter.api.Test;

public class HandsIntegrationTest extends
        AbstractIntegerIntegrationTestCase<WeaponProf>
{

    private static HandsToken token = new HandsToken();
    private static CDOMTokenLoader<WeaponProf> loader = new CDOMTokenLoader<>();

    @Override
    public Class<WeaponProf> getCDOMClass()
    {
        return WeaponProf.class;
    }

    @Override
    public CDOMLoader<WeaponProf> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<WeaponProf> getToken()
    {
        return token;
    }

    @Override
    public boolean isNegativeAllowed()
    {
        return false;
    }

    @Override
    public boolean isZeroAllowed()
    {
        return true;
    }

    @Test
    public void testRoundRobinSpecialCaseOne() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "1IFLARGERTHANWEAPON");
        commit(modCampaign, tc, "2");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinSpecialCaseTwo() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "2");
        commit(modCampaign, tc, "1IFLARGERTHANWEAPON");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinSpecialNoSet() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "1IFLARGERTHANWEAPON");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinSpecialNoReset() throws PersistenceLayerException
    {
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "1IFLARGERTHANWEAPON");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

    @Override
    public boolean isPositiveAllowed()
    {
        return true;
    }

    @Override
    public boolean doesOverwrite()
    {
        return true;
    }

    @Override
    protected boolean isClearAllowed()
    {
        return false;
    }
}
