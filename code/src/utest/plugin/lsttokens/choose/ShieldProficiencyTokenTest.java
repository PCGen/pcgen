/*
 *
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
package plugin.lsttokens.choose;


import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Equipment;
import pcgen.core.Race;
import pcgen.core.ShieldProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.QualifierToken;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.testsupport.AbstractChooseTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.qualifier.shieldprof.EquipmentToken;
import plugin.qualifier.shieldprof.PCToken;

public class ShieldProficiencyTokenTest extends
        AbstractChooseTokenTestCase<CDOMObject, ShieldProf>
{
    static ChooseLst token = new ChooseLst();
    static ShieldProficiencyToken subtoken = new ShieldProficiencyToken();
    static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

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

    @Override
    public CDOMSecondaryToken<?> getSubToken()
    {
        return subtoken;
    }

    @Override
    public Class<ShieldProf> getTargetClass()
    {
        return ShieldProf.class;
    }

    @Override
    protected boolean allowsQualifier()
    {
        return true;
    }

    @Override
    protected String getChoiceTitle()
    {
        return subtoken.getDefaultTitle();
    }

    @Override
    protected QualifierToken<ShieldProf> getPCQualifier()
    {
        return new PCToken();
    }

    @Override
    protected boolean isTypeLegal()
    {
        return true;
    }

    @Override
    protected boolean isAllLegal()
    {
        return true;
    }

    /**
     * Check that a PC qualifier with a restriction is parsed correctly.
     *
     * @throws PersistenceLayerException If an error occurs.
     */
    public void testValidPc() throws PersistenceLayerException
    {
        CDOMObject a = (CDOMObject) construct(primaryContext, "DwarvenShield");
        a.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
        CDOMObject c = (CDOMObject) construct(secondaryContext, "Typed1");
        c.addToListFor(ListKey.TYPE, Type.getConstant("Exotic"));
        runRoundRobin("SHIELDPROFICIENCY|PC[TYPE=Exotic]");
    }

    /**
     * Check that an EQUIPMENT qualifier with a restriction to a single piece
     * of equipment is parsed correctly.
     *
     * @throws PersistenceLayerException If an error occurs.
     */
    public void testValidEquipment() throws PersistenceLayerException
    {
        TokenRegistration.register(new EquipmentToken());
        construct(primaryContext, Equipment.class, "Buckler");
        construct(secondaryContext, Equipment.class, "Buckler");
        runRoundRobin("SHIELDPROFICIENCY|EQUIPMENT[Buckler]");
    }
}
