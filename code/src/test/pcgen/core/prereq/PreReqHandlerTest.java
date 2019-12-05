/*
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.prereq;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pcgen.LocaleDependentTestCase;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PreReqHandlerTest
{

    /**
     * Sets up the test case by loading the system plugins.
     */
    @BeforeEach
    void setUp()
    {
        TestHelper.loadPlugins();
    }

    @AfterEach
    void tearDown()
    {
        LocaleDependentTestCase.after();
        TokenRegistration.clearTokens();
    }

    /**
     * Print out as HTML.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testToHtml() throws PersistenceLayerException
    {
        final PreParserFactory factory = PreParserFactory.getInstance();
        final List<Prerequisite> list = new ArrayList<>();
        list.add(factory.parse("PRESKILL:1,Spellcraft=15"));
        list.add(factory.parse("PRESPELLTYPE:1,Arcane=8"));
        list.add(factory.parse("PREFEAT:2,TYPE=Metamagic"));
        list.add(factory.parse("PREFEAT:2,TYPE=ItemCreation"));
        list.add(factory.parse("PRESKILLTOT:TYPE.Knowledge=20"));

        LocaleDependentTestCase.before(Locale.US);
        final String htmlString = PrereqHandler.toHtmlString(list);
        System.out.println(htmlString);
        assertEquals(
                "at least 15 ranks in Spellcraft and at least 1 Arcane spell of level 8 and at least 2 FEAT(s) of "
                        + "type "
                        + "Metamagic and at least 2 FEAT(s) of type ItemCreation and at least 20 of "
                        + "( at least 1 ranks in TYPE.Knowledge )",
                htmlString
        );

    }
}
