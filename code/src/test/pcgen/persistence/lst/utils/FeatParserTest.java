/*
 * FeatParserTest.java
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
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
 *
 * Created on 19-Nov-2004
 */
package pcgen.persistence.lst.utils;

import java.util.List;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;

/**
 */
public class FeatParserTest extends AbstractCharacterTestCase {

    /**
     * testParseVirtualFeatList
     */
	public void testParseVirtualFeatList() {
        Ability twf = new Ability();
        twf.setName("Two Weapon Fighting");
        twf.setCategory("FEAT");
        Globals.addAbility(twf);

        List feats = FeatParser.parseVirtualFeatList("Two Weapon Fighting|PREMULT:2,[PREVARGTEQ:RangerCombatTree,1],[!PREEQUIP:1,TYPE=Armor.Medium,TYPE=Armor.Heavy]");
        is(new Integer(feats.size()), eq(1), "size of list");
        Ability feat = (Ability)feats.get(0);

        is(feat.getName(), strEq("Two Weapon Fighting"));

        List prereqs = feat.getPreReqList();
        is(new Integer(prereqs.size()), eq(1), "there is only one Prerequisite.");
        Prerequisite prereq = (Prerequisite) prereqs.get(0);
        assertEquals("<prereq operator=\"gteq\" operand=\"2\" >\n" +
        		"<prereq kind=\"var\" key=\"RangerCombatTree\" operator=\"gteq\" operand=\"1\" >\n" +
        		"</prereq>\n" +
        		"<prereq operator=\"lt\" operand=\"1\" >\n" +
        		"<prereq kind=\"equip\" count-multiples=\"true\" key=\"TYPE=Armor.Medium\" operator=\"gteq\" operand=\"1\" >\n" +
        		"</prereq>\n" +
        		"<prereq kind=\"equip\" count-multiples=\"true\" key=\"TYPE=Armor.Heavy\" operator=\"gteq\" operand=\"1\" >\n" +
        		"</prereq>\n" +
        		"</prereq>\n" +
        		"</prereq>\n" +
        		"", prereq.toString());
    }

    /**
     * ensure that a VFEAT strips off all previous prereqs
     *
     */
    public void testParseVirtualFeatList2() {
        Ability twf = new Ability();
        twf.setName("Two Weapon Fighting");
        twf.setCategory("FEAT");
        Prerequisite prereq = new Prerequisite();
        prereq.setKind("STAT");
        prereq.setKey("DEX");
        prereq.setOperand("15");
        prereq.setOperator(PrerequisiteOperator.GTEQ);
        twf.addPreReq(prereq);
        Globals.addAbility(twf);


        List feats = FeatParser.parseVirtualFeatList("Two Weapon Fighting|PREMULT:2,[PREVARGTEQ:RangerCombatTree,1],[!PREEQUIP:1,TYPE=Armor.Medium,TYPE=Armor.Heavy]");
        is(new Integer(feats.size()), eq(1), "parsed one feat");
        Ability feat = (Ability)feats.get(0);

        is(feat.getName(), strEq("Two Weapon Fighting"));

        List prereqs = feat.getPreReqList();
        is(new Integer(prereqs.size()), eq(1), "there is only one Prerequisite.");
        prereq = (Prerequisite) prereqs.get(0);
        assertEquals("<prereq operator=\"gteq\" operand=\"2\" >\n" +
        		"<prereq kind=\"var\" key=\"RangerCombatTree\" operator=\"gteq\" operand=\"1\" >\n" +
        		"</prereq>\n" +
        		"<prereq operator=\"lt\" operand=\"1\" >\n" +
        		"<prereq kind=\"equip\" count-multiples=\"true\" key=\"TYPE=Armor.Medium\" operator=\"gteq\" operand=\"1\" >\n" +
        		"</prereq>\n" +
        		"<prereq kind=\"equip\" count-multiples=\"true\" key=\"TYPE=Armor.Heavy\" operator=\"gteq\" operand=\"1\" >\n" +
        		"</prereq>\n" +
        		"</prereq>\n" +
        		"</prereq>\n" +
        		"", prereq.toString());
    }

}
