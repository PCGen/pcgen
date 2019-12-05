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
package plugin.lsttokens.editcontext.equipmentmodifier;

import pcgen.core.EquipmentModifier;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractTypeSafeListIntegrationTestCase;
import plugin.lsttokens.equipmentmodifier.ReplacesToken;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class ReplacesIntegrationTest extends
        AbstractTypeSafeListIntegrationTestCase<EquipmentModifier>
{

    private static ReplacesToken token = new ReplacesToken();
    private static CDOMTokenLoader<EquipmentModifier> loader = new CDOMTokenLoader<>();

    @Override
    public Class<EquipmentModifier> getCDOMClass()
    {
        return EquipmentModifier.class;
    }

    @Override
    public CDOMLoader<EquipmentModifier> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<EquipmentModifier> getToken()
    {
        return token;
    }

    @Override
    public Object getConstant(String string)
    {
        primaryContext.getReferenceContext()
                .constructIfNecessary(EquipmentModifier.class, string);
        secondaryContext.getReferenceContext().constructIfNecessary(EquipmentModifier.class,
                string);
        return null;
    }

    @Override
    protected boolean requiresPreconstruction()
    {
        return true;
    }

    @Test
    public void dummyTest()
    {
        // Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
    }

    @Override
    public char getJoinCharacter()
    {
        return ',';
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

}
