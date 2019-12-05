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
package plugin.qualifier.template;

import java.net.URISyntaxException;

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.QualifierToken;
import plugin.lsttokens.choose.TemplateToken;
import plugin.lsttokens.testsupport.AbstractPCQualifierTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.lsttokens.testsupport.TransparentPlayerCharacter;

import org.junit.jupiter.api.BeforeEach;

public class PCQualifierTokenTest extends
        AbstractPCQualifierTokenTestCase<PCTemplate>
{

    private static final CDOMSecondaryToken SUBTOKEN = new TemplateToken();

    private static final LstToken PC_TOKEN =
            new PCToken();

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
        return SUBTOKEN;
    }

    @Override
    public Class<PCTemplate> getTargetClass()
    {
        return PCTemplate.class;
    }

    @Override
    protected void addToPCSet(TransparentPlayerCharacter pc, PCTemplate item)
    {
        pc.templateSet.add(item);
    }

    @Override
    protected Class<? extends QualifierToken<?>> getQualifierClass()
    {
        return PCToken.class;
    }
}
