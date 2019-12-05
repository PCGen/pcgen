/*
 *
 * Copyright 2004 (C) Frugal <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 *
 *
 *
 */
package pcgen.persistence.lst.prereq;

import static org.junit.jupiter.api.Assertions.assertEquals;

import pcgen.EnUsLocaleDependentTestCase;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.GameModeFileLoader;
import pcgen.persistence.PersistenceLayerException;
import plugin.pretokens.parser.PreAlignParser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Alignment;

class PreAlignParserTest extends EnUsLocaleDependentTestCase
{
    @Test
    public void test1() throws PersistenceLayerException
    {

        PreAlignParser parser = new PreAlignParser();
        Prerequisite prereq = parser.parse("align", "LE,LG", false, false);

        //System.out.println(prereq);
        assertEquals(
                "<prereq operator=\"GTEQ\" operand=\"1\" >\n"
                        + "<prereq kind=\"align\" key=\"LE\" operator=\"EQ\" operand=\"1\" >\n"
                        + "</prereq>\n"
                        + "<prereq kind=\"align\" key=\"LG\" operator=\"EQ\" operand=\"1\" >\n"
                        + "</prereq>\n" + "</prereq>\n", prereq.toString());
    }

    @BeforeEach
    void setUp()
    {
        Globals.setUseGUI(false);
        Globals.emptyLists();
        GameMode gamemode = new GameMode("3.5");
        GameModeFileLoader.addDefaultTabInfo(gamemode);
        SystemCollections.addToGameModeList(gamemode);
        SettingsHandler.setGame("3.5");
        Alignment.createAllAlignments();
    }
}
