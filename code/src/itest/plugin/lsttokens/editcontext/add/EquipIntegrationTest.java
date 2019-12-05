/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTEquipment or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.editcontext.add;

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Equipment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.AddLst;
import plugin.lsttokens.add.EquipToken;
import plugin.lsttokens.editcontext.testsupport.AbstractListIntegrationTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.Test;

public class EquipIntegrationTest extends
        AbstractListIntegrationTestCase<CDOMObject, Equipment>
{

    private static EquipToken ft = new EquipToken();
    private static AddLst token = new AddLst();
    private static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(ft);
    }

    @Override
    public String getPrefix()
    {
        return "EQUIP|";
    }

    @Override
    public Class<Equipment> getCDOMClass()
    {
        return Equipment.class;
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
        return '|';
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
        return false;
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
    public boolean isPrereqLegal()
    {
        return false;
    }

    @Override
    public boolean isTypeLegal()
    {
        return false;
    }
}
