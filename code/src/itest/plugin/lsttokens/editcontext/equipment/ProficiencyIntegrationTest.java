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

import pcgen.core.ArmorProf;
import pcgen.core.Equipment;
import pcgen.core.ShieldProf;
import pcgen.core.WeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.equipment.ProficiencyToken;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class ProficiencyIntegrationTest extends
        AbstractIntegrationTestCase<Equipment>
{
    private static ProficiencyToken token = new ProficiencyToken();
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

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(ArmorProf.class, "TestWP1");
        secondaryContext.getReferenceContext().constructCDOMObject(ArmorProf.class, "TestWP1");
        primaryContext.getReferenceContext().constructCDOMObject(ShieldProf.class, "TestWP2");
        secondaryContext.getReferenceContext().constructCDOMObject(ShieldProf.class, "TestWP2");
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "ARMOR|TestWP1");
        commit(modCampaign, tc, "SHIELD|TestWP2");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinRemove() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(ArmorProf.class, "TestWP1");
        secondaryContext.getReferenceContext().constructCDOMObject(ArmorProf.class, "TestWP1");
        primaryContext.getReferenceContext().constructCDOMObject(ArmorProf.class, "TestWP2");
        secondaryContext.getReferenceContext().constructCDOMObject(ArmorProf.class, "TestWP2");
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "ARMOR|TestWP1");
        commit(modCampaign, tc, "ARMOR|TestWP2");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoSet() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "TestWP1");
        secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "TestWP1");
        verifyCleanStart();
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "WEAPON|TestWP1");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoReset() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(ArmorProf.class, "TestWP1");
        secondaryContext.getReferenceContext().constructCDOMObject(ArmorProf.class, "TestWP1");
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "ARMOR|TestWP1");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
    }

}
