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

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.helper.ShieldProfProvider;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Equipment;
import pcgen.core.ShieldProf;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.testsupport.AbstractAutoTokenTestCase;

import org.junit.jupiter.api.Test;

public class ShieldProfTokenTest extends AbstractAutoTokenTestCase<ShieldProf>
{

    static ShieldProfToken subtoken = new ShieldProfToken();

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
    protected CDOMObject constructTyped(LoadContext loadContext, String one)
    {
        CDOMObject cdo = loadContext.getReferenceContext().constructCDOMObject(Equipment.class, one);
        cdo.addToListFor(ListKey.TYPE, Type.getConstant("Shield"));
        return cdo;
    }

    @Override
    public String getTypePrefix()
    {
        return "SHIELD";
    }

    @Override
    public boolean isAllLegal()
    {
        return true;
    }

    @Override
    protected ChooseSelectionActor<ShieldProf> getActor()
    {
        return subtoken;
    }

    @Override
    protected void loadAllReference()
    {
        List<CDOMReference<ShieldProf>> shieldProfs = new ArrayList<>();
        List<CDOMReference<Equipment>> equipTypes = new ArrayList<>();
        shieldProfs.add(primaryContext.getReferenceContext()
                .getCDOMAllReference(ShieldProf.class));
        ShieldProfProvider pp = new ShieldProfProvider(shieldProfs, equipTypes);
        primaryProf.addToListFor(ListKey.AUTO_SHIELDPROF, pp);
    }

    @Override
    protected void loadProf(CDOMSingleRef<ShieldProf> ref)
    {
        List<CDOMReference<ShieldProf>> shieldProfs = new ArrayList<>();
        List<CDOMReference<Equipment>> equipTypes = new ArrayList<>();
        shieldProfs.add(ref);
        ShieldProfProvider pp = new ShieldProfProvider(shieldProfs, equipTypes);
        primaryProf.addToListFor(ListKey.AUTO_SHIELDPROF, pp);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFail()
    {
        ListKey listKey = ListKey.AUTO_SHIELDPROF;
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

    @Override
    protected void loadTypeProf(String... types)
    {
        CDOMGroupRef<Equipment> ref = primaryContext.getReferenceContext().getCDOMTypeReference(Equipment.class, types);
        List<CDOMReference<ShieldProf>> shieldProfs = new ArrayList<>();
        List<CDOMReference<Equipment>> equipTypes = new ArrayList<>();
        equipTypes.add(ref);
        ShieldProfProvider pp = new ShieldProfProvider(shieldProfs, equipTypes);
        primaryProf.addToListFor(ListKey.AUTO_SHIELDPROF, pp);
    }

    @Override
    protected boolean allowsPrerequisite()
    {
        return true;
    }
}
