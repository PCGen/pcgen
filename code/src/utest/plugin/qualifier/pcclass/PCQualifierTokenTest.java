/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.qualifier.pcclass;

import java.net.URISyntaxException;

import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.QualifierToken;
import plugin.lsttokens.choose.ClassToken;
import plugin.lsttokens.testsupport.AbstractPCQualifierTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.lsttokens.testsupport.TransparentPlayerCharacter;

import org.junit.jupiter.api.BeforeEach;

public class PCQualifierTokenTest extends
        AbstractPCQualifierTokenTestCase<PCClass>
{

    static ClassToken subtoken = new ClassToken();

    private static final plugin.qualifier.pcclass.PCToken PC_TOKEN =
            new plugin.qualifier.pcclass.PCToken();

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(PC_TOKEN);
    }

    @Override
    public CDOMSecondaryToken<?> getSubToken()
    {
        return subtoken;
    }

    @Override
    public Class<PCClass> getTargetClass()
    {
        return PCClass.class;
    }

    @Override
    protected void addToPCSet(TransparentPlayerCharacter pc, PCClass item)
    {
        pc.classMap.put(item, 2);
    }

    @Override
    protected Class<? extends QualifierToken<?>> getQualifierClass()
    {
        return plugin.qualifier.pcclass.PCToken.class;
    }
}
