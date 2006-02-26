/*
 * PreAlignParser.java
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
 * Created on 18-Dec-2003
 * 
 * Current Ver: $Revision: 1.4 $
 * 
 * Last Editor: $Author: karianna $
 * 
 * Last Edited: $Date: 2005/09/12 11:48:36 $
 *  
 */
package pcgen.persistence.lst.prereq;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.prereq.Prerequisite;

public class PreAlignParserTest extends TestCase {
    public static void main(String args[]) {
        junit.swingui.TestRunner.run(PreAlignParserTest.class);
    }

    /**
     * @return Test
     */
    public static Test suite() {
        return new TestSuite(PreAlignParserTest.class);
    }

    private PCAlignment createAlignment(String longName, String shortName) {
        PCAlignment align = new PCAlignment();
        align.setName(longName);
        align.setKeyName(shortName);
        return align;
    }

    /**
     * @throws Exception
     */
    public void test1() throws Exception {
 
        PreAlignParser parser = new PreAlignParser();
        Prerequisite prereq = parser.parse("align", "LE,LG", false, false);

        //System.out.println(prereq);
        assertEquals("<prereq operator=\"gteq\" operand=\"1\" >\n" + 
        		"<prereq kind=\"align\" key=\"LE\" operator=\"eq\" operand=\"1\" >\n" + 
        		"</prereq>\n" + 
        		"<prereq kind=\"align\" key=\"LG\" operator=\"eq\" operand=\"1\" >\n" + 
        		"</prereq>\n" + 
        		"</prereq>\n" + 
        		"", prereq.toString());
    }

    protected void setUp() throws Exception {
        Globals.setUseGUI(false);
        Globals.emptyLists();
        GameMode gamemode = new GameMode("3.5");
        gamemode.addToAlignmentList(createAlignment("Lawful Good", "LG"));
        gamemode.addToAlignmentList(createAlignment("Lawful Neutral", "LN"));
        gamemode.addToAlignmentList(createAlignment("Lawful Evil", "LE"));
        gamemode.addToAlignmentList(createAlignment("Neutral Good", "NG"));
        gamemode.addToAlignmentList(createAlignment("True Neutral", "TN"));
        gamemode.addToAlignmentList(createAlignment("Neutral Evil", "NE"));
        gamemode.addToAlignmentList(createAlignment("Chaotic Good", "CG"));
        gamemode.addToAlignmentList(createAlignment("Chaotic Neutral", "CN"));
        gamemode.addToAlignmentList(createAlignment("Chaotic Evil", "CE"));
        gamemode.addToAlignmentList(createAlignment("None", "NONE"));
        gamemode.addToAlignmentList(createAlignment("Deity's", "Deity"));
        SystemCollections.addToGameModeList(gamemode);
        SettingsHandler.setGame("3.5");
    }
}