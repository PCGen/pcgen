/*
 *
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.add;

import pcgen.cdom.base.ChoiceActor;
import pcgen.core.Equipment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.testsupport.AbstractAddTokenTestCase;

import org.junit.jupiter.api.Test;

public class EquipTokenTest extends
        AbstractAddTokenTestCase<Equipment>
{

    static EquipToken subtoken = new EquipToken();

    @Override
    public CDOMSecondaryToken<?> getSubToken()
    {
        return subtoken;
    }

    @Override
    public Class<Equipment> getTargetClass()
    {
        return Equipment.class;
    }

    @Override
    public boolean isAllLegal()
    {
        return false;
    }

    @Override
    public boolean allowsFormula()
    {
        return true;
    }

    @Test
    public void testRoundRobinDupe() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        runRoundRobin(getSubTokenName() + '|' + "TestWP1",
                getSubTokenName() + '|' + "TestWP1");
    }

    @Override
    protected ChoiceActor<Equipment> getActor()
    {
        return subtoken;
    }

}
