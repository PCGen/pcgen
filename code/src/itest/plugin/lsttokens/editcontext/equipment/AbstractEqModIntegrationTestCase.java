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

import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import plugin.lsttokens.editcontext.testsupport.AbstractListIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public abstract class AbstractEqModIntegrationTestCase extends
        AbstractListIntegrationTestCase<Equipment, EquipmentModifier>
{

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
    public Class<EquipmentModifier> getTargetClass()
    {
        return EquipmentModifier.class;
    }

    @Override
    public boolean isTypeLegal()
    {
        return false;
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
        return false;
    }

    @Override
    public boolean isPrereqLegal()
    {
        return false;
    }

    @Override
    public boolean isAllLegal()
    {
        return false;
    }

    @Test
    public void testRoundRobinMods() throws PersistenceLayerException
    {
        construct(primaryContext, "EQMOD2");
        construct(secondaryContext, "EQMOD2");
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "EQMOD2|9500");
        commit(modCampaign, tc, "EQMOD2|COST[9500]");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinModsTwo() throws PersistenceLayerException
    {
        construct(primaryContext, "EQMOD2");
        construct(secondaryContext, "EQMOD2");
        verifyCleanStart();
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "EQMOD2|COST[9500]PLUS[+1]");
        commit(modCampaign, tc, "EQMOD2|COST[9500]");
        completeRoundRobin(tc);
    }

}
