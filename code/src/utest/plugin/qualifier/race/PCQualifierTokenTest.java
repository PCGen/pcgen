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
package plugin.qualifier.race;

import java.net.URISyntaxException;

import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.QualifierToken;
import plugin.lsttokens.choose.RaceToken;
import plugin.lsttokens.testsupport.AbstractPCQualifierTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.lsttokens.testsupport.TransparentPlayerCharacter;

import org.junit.jupiter.api.BeforeEach;

public class PCQualifierTokenTest extends
        AbstractPCQualifierTokenTestCase<Race>
{

    private static final CDOMSecondaryToken SUBTOKEN = new RaceToken();

    private static final LstToken PC_TOKEN =
            new plugin.qualifier.race.PCToken();

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
    public Class<Race> getTargetClass()
    {
        return Race.class;
    }

    @Override
    protected boolean typeAllowsMult()
    {
        return false;
    }

    @Override
    protected void addToPCSet(TransparentPlayerCharacter pc, Race item)
    {
        pc.race = item;
    }

    @Override
    protected Class<? extends QualifierToken<?>> getQualifierClass()
    {
        return plugin.qualifier.race.PCToken.class;
    }
}
