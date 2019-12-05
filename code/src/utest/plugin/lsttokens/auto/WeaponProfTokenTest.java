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
package plugin.lsttokens.auto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URISyntaxException;

import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.WeaponProfProvider;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.QualifiedObject;
import pcgen.core.WeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.testsupport.AbstractAutoTokenTestCase;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreWeaponProfParser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WeaponProfTokenTest extends AbstractAutoTokenTestCase<WeaponProf>
{

    static WeaponProfToken subtoken = new WeaponProfToken();

    PreWeaponProfParser preWpnProf = new PreWeaponProfParser();

    @Override
    @BeforeEach
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(preWpnProf);
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
    public boolean isAllLegal()
    {
        return true;
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.removeListFor(ListKey.WEAPONPROF);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseSingleEmpty()
    {
        WeaponProfProvider wpp = new WeaponProfProvider();
        primaryProf.addToListFor(ListKey.WEAPONPROF, wpp);
        assertBadUnparse();
    }

    @Test
    public void testRoundRobinDeityWeapons() throws PersistenceLayerException
    {
        runRoundRobin(getSubTokenName() + '|' + "DEITYWEAPONS");
    }

    @Test
    public void testRoundRobinDeityWeaponsPre() throws PersistenceLayerException
    {
        runRoundRobin(getSubTokenName() + '|' + "DEITYWEAPONS|PRERACE:1,Dwarf");
    }

    @Test
    public void testUnparseDeityWeaponsAll()
    {
        loadAllReference();
        primaryProf.put(ObjectKey.HAS_DEITY_WEAPONPROF,
                new QualifiedObject<>(Boolean.TRUE));
        assertBadUnparse();
    }

    @Test
    public void testUnparseIndivAll()
    {
        WeaponProfProvider wpp = new WeaponProfProvider();
        wpp.addWeaponProfAll(primaryContext.getReferenceContext()
                .getCDOMAllReference(WeaponProf.class));
        WeaponProf wp1 = construct(primaryContext, "TestWP1");
        CDOMSingleRef<WeaponProf> ref = CDOMDirectSingleRef.getRef(wp1);
        wpp.addWeaponProf(ref);
        primaryProf.addToListFor(ListKey.WEAPONPROF, wpp);
        assertBadUnparse();
    }

    @Test
    public void testUnparseDeityWeapons()
    {
        primaryProf.put(ObjectKey.HAS_DEITY_WEAPONPROF,
                new QualifiedObject<>(Boolean.TRUE));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, "WEAPONPROF|DEITYWEAPONS");
    }

    @Test
    public void testUnparseDeityWeaponsFalse()
    {
        primaryProf.put(ObjectKey.HAS_DEITY_WEAPONPROF,
                new QualifiedObject<>(Boolean.FALSE));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        assertNull(unparsed);
    }


    @Override
    protected void loadProf(CDOMSingleRef<WeaponProf> ref)
    {
        WeaponProfProvider wpp = new WeaponProfProvider();
        wpp.addWeaponProf(ref);
        primaryProf.addToListFor(ListKey.WEAPONPROF, wpp);
    }

    @Override
    protected void loadAllReference()
    {
        WeaponProfProvider wpp = new WeaponProfProvider();
        wpp.addWeaponProfAll(primaryContext.getReferenceContext()
                .getCDOMAllReference(WeaponProf.class));
        primaryProf.addToListFor(ListKey.WEAPONPROF, wpp);
    }

    @Test
    public void testUnparseNullInList()
    {
        WeaponProfProvider wpp = new WeaponProfProvider();
        wpp.addWeaponProf(null);
        primaryProf.addToListFor(ListKey.WEAPONPROF, wpp);
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (NullPointerException e)
        {
            // Yep!
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFail()
    {
        ListKey listKey = ListKey.WEAPONPROF;
        primaryProf.addToListFor(listKey, new Object());
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (ClassCastException e)
        {
            // Yep!
        }
    }

    @Test
    public void testInvalidAllPlusDeityWeaponsIllegal()
    {
        assertFalse(parse(getSubTokenName() + '|' + "DEITYWEAPONS|ALL"));
        assertNoSideEffects();
    }

    @Test
    public void testValidPrereqLegal()
    {
        assertTrue(parse(getSubTokenName() + '|' + "CROSSBOW|PREWEAPONPROF:1,DAGGER"));
    }

    @Test
    public void testInvalidPrereqIllegal()
    {
        assertFalse(parse(getSubTokenName() + '|' + "CROSSBOW|PREWEAPONPROF:1,TYPE=Piercing"));
    }

    @Override
    protected ChooseSelectionActor<WeaponProf> getActor()
    {
        return subtoken;
    }

    @Override
    protected void loadTypeProf(String... types)
    {
        WeaponProfProvider wpp = new WeaponProfProvider();
        CDOMGroupRef<WeaponProf> ref = primaryContext.getReferenceContext().getCDOMTypeReference(
                WeaponProf.class, types);
        wpp.addWeaponProfType(ref);
        primaryProf.addToListFor(ListKey.WEAPONPROF, wpp);
    }

    @Override
    protected boolean allowsPrerequisite()
    {
        return true;
    }
}
