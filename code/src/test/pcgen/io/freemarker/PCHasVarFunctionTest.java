/*
 * Copyright James Dempsey, 2014
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.io.freemarker;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URI;
import java.util.Collections;

import pcgen.AbstractJunit5CharacterTestCase;
import pcgen.core.Ability;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.FeatLoader;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.Test;

/**
 * The Class {@code PCHasVarFunctionTest} verifies the correctness of
 * the PCHasVarFunction class.
 */

public class PCHasVarFunctionTest extends AbstractJunit5CharacterTestCase
{
    private Ability fooFeat;

    @Override
    protected void additionalSetUp() throws Exception
    {
        CampaignSourceEntry cse = new CampaignSourceEntry(new Campaign(),
                new URI("file:/" + getClass().getName() + ".java"));
        final FeatLoader featLoader = new FeatLoader();

        fooFeat = new Ability();
        final String fooFeatStr =
                "Foo	TYPE:General	DEFINE:FooV|0";
        featLoader.parseLine(Globals.getContext(), fooFeat, fooFeatStr, cse);

    }

    /**
     * Test method for {@link pcgen.io.freemarker.PCBooleanFunction#exec(java.util.List)}.
     *
     * @throws Exception
     */
    @Test
    public void testExec() throws Exception
    {
        PlayerCharacter pc = getCharacter();
        ExportHandler eh = ExportHandler.createExportHandler(new File(""));
        PCHasVarFunction pchv = new PCHasVarFunction(pc, eh);

        Boolean result = (Boolean) pchv.exec(Collections.singletonList("FooV"));
        assertFalse(result, "Should not have var");

        addAbility(BuildUtilities.getFeatCat(), fooFeat);
        pc.calcActiveBonuses();
        assertTrue(pc.hasVariable("FooV"), "Should have var FooV");
        result = (Boolean) pchv.exec(Collections.singletonList("FooV"));
        assertTrue(result, "PCHasVar could not see FooV");
    }

}
