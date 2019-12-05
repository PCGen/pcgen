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
package plugin.primitive.weaponprof;

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Race;
import pcgen.core.WeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.choose.WeaponProficiencyToken;
import plugin.lsttokens.testsupport.AbstractPrimitiveTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.BeforeEach;

public class TypeTokenTest extends
        AbstractPrimitiveTokenTestCase<CDOMObject, WeaponProf>
{

    static ChooseLst token = new ChooseLst();
    static WeaponProficiencyToken subtoken = new WeaponProficiencyToken();
    static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();
    private static final TypeToken TYPE_TOKEN = new TypeToken();

    public TypeTokenTest()
    {
        super("TYPE", "Medium");
    }

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(TYPE_TOKEN);
        Type med = Type.getConstant("Medium");
        WeaponProf pls =
                primaryContext.getReferenceContext().constructCDOMObject(
                        WeaponProf.class, "Longsword");
        pls.addToListFor(ListKey.TYPE, med);
        WeaponProf sls =
                secondaryContext.getReferenceContext().constructCDOMObject(
                        WeaponProf.class, "Longsword");
        sls.addToListFor(ListKey.TYPE, med);
    }

    @Override
    public CDOMSecondaryToken<?> getSubToken()
    {
        return subtoken;
    }

    @Override
    public Class<WeaponProf> getTargetClass()
    {
        return WeaponProf.class;
    }

    @Override
    public Class<Race> getCDOMClass()
    {
        return Race.class;
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

}
