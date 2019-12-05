/*
 * Copyright (c) 2019 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA
 */
package tokencontent;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.VariableID;
import pcgen.base.solver.SolverManager;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.facet.EquipmentFacet;
import pcgen.cdom.facet.EquippedEquipmentFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.SolverManagerFacet;
import pcgen.cdom.formula.VariableUtilities;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.ModifyLst;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tokencontent.testsupport.AbstractContentTokenTest;
import util.TestURI;

public class GlobalModifyTest extends AbstractContentTokenTest
{

    private static ModifyLst token = new ModifyLst();
    private static final plugin.modifier.number.SetModifierFactory SMF =
            new plugin.modifier.number.SetModifierFactory();
    private static EquipmentFacet equipmentFacet =
            FacetLibrary.getFacet(EquipmentFacet.class);
    private static EquippedEquipmentFacet equippedEquipmentFacet =
            FacetLibrary.getFacet(EquippedEquipmentFacet.class);

    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        TokenRegistration.register(SMF);
        context.getVariableContext().assertLegalVariableID("MyVar",
                context.getActiveScope(), FormatUtilities.NUMBER_MANAGER);
    }

    @Override
    public void processToken(CDOMObject source)
    {
        ParseResult result = token.parseToken(context, source, "MyVar|SET|30");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            assertEquals("Test Setup Failed", ParseResult.SUCCESS, result);
        }
        finishLoad();
    }

    @Override
    public CDOMToken<?> getToken()
    {
        return token;
    }

    @Override
    protected boolean containsExpected()
    {
        Object var = pc.getGlobal("MyVar");
        return var.equals(30);
    }

    @Override
    protected int targetFacetCount()
    {
        SolverManager sm =
                FacetLibrary.getFacet(SolverManagerFacet.class).get(id);
        VariableID<?> varID =
                VariableUtilities.getGlobalVariableID(id, "myVar");
        int size;
        try
        {
            List<?> list = sm.diagnose(varID);
            size = list.size() - 1;
        } catch (IllegalArgumentException e)
        {
            //Really, SolverManager should have isChannel(varID) to avoid diagnose complaining if something doesn't exist
            size = 0;
        }
        return size;
    }

    @Override
    protected int baseCount()
    {
        return 0;
    }

    @Override
    @Test
    public void testFromEqMod()
    {
        EquipmentModifier source = create(EquipmentModifier.class, "Source");
        Equipment equipment = create(Equipment.class, "Parent");
        source.setVariableParent(equipment);
        equipment.addEqModifier(source, true, pc);
        processToken(source);
        assertEquals(baseCount(), targetFacetCount());
        equipment.setIsEquipped(true, pc);
        equipmentFacet.add(id, equipment, this);
        equippedEquipmentFacet.reset(id);
        assertTrue(containsExpected());
        assertEquals(baseCount() + 1, targetFacetCount());
        equipment.setIsEquipped(false, pc);
        equippedEquipmentFacet.reset(id);
        assertEquals(baseCount(), targetFacetCount());
    }
}
