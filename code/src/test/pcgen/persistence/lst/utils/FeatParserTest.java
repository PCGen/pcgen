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
import pcgen.util.TestHelper;

/**
 */
public class FeatParserTest extends AbstractCharacterTestCase
{

	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(FeatParserTest.class);
	}

	/**
	 * testParseVirtualFeatList
	 */
	public void testParseVirtualFeatList()
	{
		Ability twf = new Ability();
		twf.setName("Two Weapon Fighting");
		twf.setCategory("FEAT");
		Globals.addAbility(twf);

		List<Ability> feats =
				FeatParser
					.parseVirtualFeatList("Two Weapon Fighting|PREMULT:2,[PREVARGTEQ:RangerCombatTree,1],[!PREEQUIP:1,TYPE=Armor.Medium,TYPE=Armor.Heavy]");
		is(feats.size(), eq(1), "size of list");
		Ability feat = feats.get(0);

		is(feat.getKeyName(), strEq("Two Weapon Fighting"));

		List<Prerequisite> prereqs = feat.getPreReqList();
		is(prereqs.size(), eq(1), "there is only one Prerequisite.");
		Prerequisite prereq = prereqs.get(0);
		assertEquals(
			"<prereq operator=\"gteq\" operand=\"2\" >\n"
				+ "<prereq kind=\"var\" key=\"RangerCombatTree\" operator=\"gteq\" operand=\"1\" >\n"
				+ "</prereq>\n"
				+ "<prereq operator=\"lt\" operand=\"1\" >\n"
				+ "<prereq kind=\"equip\" count-multiples=\"true\" key=\"TYPE=Armor.Medium\" operator=\"gteq\" operand=\"1\" >\n"
				+ "</prereq>\n"
				+ "<prereq kind=\"equip\" count-multiples=\"true\" key=\"TYPE=Armor.Heavy\" operator=\"gteq\" operand=\"1\" >\n"
				+ "</prereq>\n" + "</prereq>\n" + "</prereq>\n" + "", prereq
				.toString());
	}

	/**
	 * ensure that a VFEAT strips off all previous prereqs
	 *
	 */
	public void testParseVirtualFeatList2()
	{
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

		List<Ability> feats =
				FeatParser
					.parseVirtualFeatList("Two Weapon Fighting|PREMULT:2,[PREVARGTEQ:RangerCombatTree,1],[!PREEQUIP:1,TYPE=Armor.Medium,TYPE=Armor.Heavy]");
		is(feats.size(), eq(1), "parsed one feat");
		Ability feat = feats.get(0);

		is(feat.getKeyName(), strEq("Two Weapon Fighting"));

		List<Prerequisite> prereqs = feat.getPreReqList();
		is(prereqs.size(), eq(1), "there is only one Prerequisite.");
		prereq = prereqs.get(0);
		assertEquals(
			"<prereq operator=\"gteq\" operand=\"2\" >\n"
				+ "<prereq kind=\"var\" key=\"RangerCombatTree\" operator=\"gteq\" operand=\"1\" >\n"
				+ "</prereq>\n"
				+ "<prereq operator=\"lt\" operand=\"1\" >\n"
				+ "<prereq kind=\"equip\" count-multiples=\"true\" key=\"TYPE=Armor.Medium\" operator=\"gteq\" operand=\"1\" >\n"
				+ "</prereq>\n"
				+ "<prereq kind=\"equip\" count-multiples=\"true\" key=\"TYPE=Armor.Heavy\" operator=\"gteq\" operand=\"1\" >\n"
				+ "</prereq>\n" + "</prereq>\n" + "</prereq>\n" + "", prereq
				.toString());
	}

	/**
	 * testParseVirtualFeatList3
	 */
	public void testParseVirtualFeatList3()
	{
		TestHelper.makeAbility("Two Weapon Fighting", "FEAT", "Fighter");
		TestHelper.makeAbility("Weapon Finesse", "FEAT", "Fighter");
		TestHelper.makeAbility("Random Ability 1", "FEAT", "Fighter");
		TestHelper.makeAbility("Random Ability 1", "FEAT", "Fighter");

		List<Ability> feats =
				FeatParser
					.parseVirtualFeatList("KEY_Two Weapon Fighting|KEY_Weapon Finesse (Bite, Claws)");
		is(feats.size(), eq(2), "size of list");

		is((feats.get(0)).getDisplayName(), strEq("Two Weapon Fighting"),
			"First feat is correct");
		is((feats.get(1)).getDisplayName(), strEq("Weapon Finesse"),
			"Second feat is correct");

		is((feats.get(1)).getAssociated(0), strEq("Bite"),
			"First choice is correct");
		is((feats.get(1)).getAssociated(1), strEq("Claws"),
			"Second choice is correct");
	}
}
