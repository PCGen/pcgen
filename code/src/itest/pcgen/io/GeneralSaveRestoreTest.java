/*
 * Copyright (c) 2014 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.io;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import pcgen.cdom.content.CNAbility;
import pcgen.core.Ability;
import pcgen.core.Language;
import pcgen.core.PCTemplate;
import pcgen.io.testsupport.AbstractSaveRestoreTest;
import plugin.exporttokens.deprecated.TemplateToken;
import plugin.lsttokens.ability.StackToken;
import plugin.lsttokens.deprecated.TemplateFeatToken;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.Test;

class GeneralSaveRestoreTest extends AbstractSaveRestoreTest
{

    @Test
    public void testTemplateFeat()
    {
        //Represents CODE-2547
        TokenRegistration.register(new TemplateFeatToken());
        TokenRegistration.register(new StackToken());
        TokenRegistration.register(new TemplateToken());
        Language lang = context.getReferenceContext().constructCDOMObject(Language.class, "English");
        Ability a = BuildUtilities.buildAbility(context, BuildUtilities.getFeatCat(), "Ab");
        PCTemplate pct = context.getReferenceContext().constructCDOMObject(PCTemplate.class, "Templ");
        assertDoesNotThrow(() -> {
            assertTrue(context.processToken(a, "MULT", "YES"));
            assertTrue(context.processToken(a, "STACK", "YES"));
            assertTrue(context.processToken(a, "CHOOSE", "LANG|English"));
            assertTrue(context.processToken(a, "AUTO", "LANG|%LIST"));
            assertTrue(context.processToken(pct, "FEAT", "Ab"));
        });
        finishLoad();
        pc.addTemplate(pct);
        assertTrue(pc.hasLanguage(lang));
        runRoundRobin(null);
        assertTrue(reloadedPC.hasLanguage(lang));
        List<CNAbility> cnaList = pc.getMatchingCNAbilities(a);
        assertEquals(1, cnaList.size());
        List<String> assocs = pc.getAssociationList(cnaList.get(0));
        assertEquals(1, assocs.size());
        assertEquals("English", assocs.get(0));
        cnaList = reloadedPC.getMatchingCNAbilities(a);
        assertEquals(1, cnaList.size());
        assocs = reloadedPC.getAssociationList(cnaList.get(0));
        assertEquals(1, assocs.size());
        assertEquals("English", assocs.get(0));
        assertEquals("Ab(English)", ExportHandler.getTokenString(pc, "TEMPLATE.0.FEAT"));
        assertEquals("Ab(English)", ExportHandler.getTokenString(reloadedPC, "TEMPLATE.0.FEAT"));
        reloadedPC.removeTemplate(pct);
        assertFalse(reloadedPC.hasLanguage(lang));
    }


}
