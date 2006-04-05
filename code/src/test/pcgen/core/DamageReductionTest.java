/*
 * DamageReductionTest.java
 *
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 *
 * Current Ver: $Revision: $
 *
 * Last Editor: $Author: $
 *
 * Last Edited: $Date:  $
 *
 */
package pcgen.core;

import java.util.ArrayList;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * This class tests the handling of DRs in PCGen
 */
public class DamageReductionTest extends AbstractCharacterTestCase
{
	/**
	 * Constructs a new <code>DamageReductionTest</code>.
	 */
	public DamageReductionTest()
	{
		super();
	}

	public void testBasicDRHandling()
	{
		DamageReduction dr1 = new DamageReduction("5", "magic");
		DamageReduction dr2 = new DamageReduction("5", "-");

		is(new Boolean(false), eq(dr1.equals(dr2)));

		dr2 = new DamageReduction("5", "Magic");
		is(new Boolean(true), eq(dr1.equals(dr2)));

		dr2 = new DamageReduction("10", "magic");
		is(new Boolean(false), eq(dr1.equals(dr2)));

		dr1 = new DamageReduction("10", "magic and good");
		dr2 = new DamageReduction("10", "good and magic");
		is(new Boolean(true), eq(dr1.equals(dr2)));

		dr2 = new DamageReduction("10", "Good and magic");
		is(new Boolean(true), eq(dr1.equals(dr2)));
	}

	public void testAddDRs()
	{
		DamageReduction dr1 = new DamageReduction("10", "magic");
		DamageReduction dr2 = new DamageReduction("10", "good");
		DamageReduction result = DamageReduction.addDRs(dr1, dr2);

		assertEquals(new DamageReduction("10", "magic and good"), result);

		dr1 = new DamageReduction("10", "magic");
		dr2 = new DamageReduction("5", "good");

		result = DamageReduction.addDRs(dr1, dr2);
		assertEquals(true, result == null);
	}

	public void testPreReqs() throws Exception
	{
		DamageReduction dr1 = new DamageReduction("10", "magic");
		dr1.setPC(getCharacter());

		final PreParserFactory factory = PreParserFactory.getInstance();

		final Prerequisite prereqNE = factory.parse("PRETEMPLATE:Natural Lycanthrope");
		dr1.addPreReq(prereqNE);
		assertEquals("", dr1.toString());

		PCTemplate template = new PCTemplate();
		template.setName("Natural Lycanthrope");
		getCharacter().addTemplate(template);
		assertEquals("10/magic", dr1.toString());

		DamageReduction dr2 = new DamageReduction("10", "good");
		dr2.setPC(getCharacter());

		String result = DamageReduction.combineDRs(dr1, dr2);
		assertEquals("10/magic and good", result);

		getCharacter().removeTemplate(template);

		result = DamageReduction.combineDRs(dr1, dr2);
		assertEquals("10/good", result);
	}

	public void testCombineDRs()
	{
		DamageReduction dr1 = new DamageReduction("10", "magic");
		DamageReduction dr2 = new DamageReduction("10", "good");

		// Add two unrelated DRs
		String result = DamageReduction.combineDRs(dr1, dr2);
		assertEquals("10/magic and good", result);

		// Add two related DRs
		dr1 = new DamageReduction("10", "good");
		dr2 = new DamageReduction("5", "good");
		result = DamageReduction.combineDRs(dr1, dr2);
//		System.out.println(dr1.toString() + " + " + dr2.toString() + " = " + result);
		assertEquals("10/good", result);

		// Add two unrelated DRs with different values
		dr1 = new DamageReduction("10", "magic");
		dr2 = new DamageReduction("5", "good");
		result = DamageReduction.combineDRs(dr1, dr2);
//		System.out.println(dr1.toString() + " + " + dr2.toString() + " = " + result);
		assertEquals("10/magic; 5/good", result);

		// Make sure case is NOT significant
		dr1 = new DamageReduction("15", "Good");
		dr2 = new DamageReduction("10", "good");
		result = DamageReduction.combineDRs(dr1, dr2);
//		System.out.println(dr1.toString() + " + " + dr2.toString() + " = " + result);
		assertEquals("15/Good", result);

		// Check DRs with "and" in them
		dr1 = new DamageReduction("15", "Good");
		dr2 = new DamageReduction("10", "magic and good");
		result = DamageReduction.combineDRs(dr1, dr2);
//		System.out.println(dr1.toString() + " + " + dr2.toString() + " = " + result);
		assertEquals("15/Good; 10/magic and good", result);

		// Check DRs with "and" in them
		dr1 = new DamageReduction("10", "Good");
		dr2 = new DamageReduction("10", "magic and good");
		result = DamageReduction.combineDRs(dr1, dr2);
//		System.out.println(dr1.toString() + " + " + dr2.toString() + " = " + result);
		assertEquals("10/magic and good", result);

		// Check DRs with "and" in them
		dr1 = new DamageReduction("10", "Evil");
		dr2 = new DamageReduction("10", "magic and good");
		result = DamageReduction.combineDRs(dr1, dr2);
//		System.out.println(dr1.toString() + " + " + dr2.toString() + " = " + result);
		assertEquals("10/Evil and magic and good", result);

		// Check DRs with "and" in them
		dr1 = new DamageReduction("10", "magic and good");
		dr2 = new DamageReduction("5", "evil");
		result = DamageReduction.combineDRs(dr1, dr2);
//		System.out.println(dr1.toString() + " + " + dr2.toString() + " = " + result);
		assertEquals("10/magic and good; 5/evil", result);

		// Make sure order isn't significant i.e. A+B = B+A
		dr1 = new DamageReduction("5", "good");
		dr2 = new DamageReduction("10", "magic");
		result = DamageReduction.combineDRs(dr1, dr2);
//		System.out.println(dr1.toString() + " + " + dr2.toString() + " = " + result);
		assertEquals("10/magic; 5/good", result);

		// Check "or" cases.
		dr1 = new DamageReduction("10", "magic or good");
		dr2 = new DamageReduction("10", "good");
		result = DamageReduction.combineDRs(dr1, dr2);
//		System.out.println(dr1.toString() + " + " + dr2.toString() + " = " + result);
		assertEquals("10/good", result);

		// Check "or" cases.
		dr1 = new DamageReduction("10", "magic or good");
		dr2 = new DamageReduction("5", "good");
		result = DamageReduction.combineDRs(dr1, dr2);
//		System.out.println(dr1.toString() + " + " + dr2.toString() + " = " + result);
		assertEquals("10/magic or good; 5/good", result);

		// Check 2 ands
		dr1 = new DamageReduction("10", "magic and good");
		dr2 = new DamageReduction("5", "magic and good");
		result = DamageReduction.combineDRs(dr1, dr2);
//		System.out.println(dr1.toString() + " + " + dr2.toString() + " = " + result);
		assertEquals("10/magic and good", result);

		// Check ANDs and ORs together
		dr1 = new DamageReduction("10", "magic or good");
		dr2 = new DamageReduction("5", "magic and good");
		result = DamageReduction.combineDRs(dr1, dr2);
//		System.out.println(dr1.toString() + " + " + dr2.toString() + " = " + result);
		assertEquals("10/magic or good; 5/magic and good", result);

		// Order test for ORs
		dr1 = new DamageReduction("10", "magic or good");
		dr2 = new DamageReduction("15", "magic");
		result = DamageReduction.combineDRs(dr1, dr2);
//		System.out.println(dr1.toString() + " + " + dr2.toString() + " = " + result);
		assertEquals("15/magic", result);

		// order test for ANDs and ORs
		dr1 = new DamageReduction("10", "magic or good");
		dr2 = new DamageReduction("15", "magic and good");
		result = DamageReduction.combineDRs(dr1, dr2);
//		System.out.println(dr1.toString() + " + " + dr2.toString() + " = " + result);
		assertEquals("15/magic and good", result);

		// Unrelated bypass values in OR
		dr1 = new DamageReduction("10", "magic or lawful");
		dr2 = new DamageReduction("15", "magic and good");
		result = DamageReduction.combineDRs(dr1, dr2);
//		System.out.println(dr1.toString() + " + " + dr2.toString() + " = " + result);
		assertEquals("15/magic and good", result);

		// Combine ANDs
		dr1 = new DamageReduction("10", "magic and good");
		dr2 = new DamageReduction("10", "magic and lawful");
		result = DamageReduction.combineDRs(dr1, dr2);
//		System.out.println(dr1.toString() + " + " + dr2.toString() + " = " + result);
		assertEquals("10/magic and good and lawful", result);

		// Sanity check we don't have anything hardcoded.
		dr1 = new DamageReduction("10", "lawful");
		dr2 = new DamageReduction("5", "evil");
		result = DamageReduction.combineDRs(dr1, dr2);
//		System.out.println(dr1.toString() + " + " + dr2.toString() + " = " + result);
		assertEquals("10/lawful; 5/evil", result);
	}

	public void testGetDRString()
	{
		ArrayList drList = new ArrayList();
		String listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("", listResult);

		drList.add(new DamageReduction("10", "magic"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("10/magic", listResult);

		drList.add(new DamageReduction("10", "good"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("10/magic and good", listResult);

		drList.add(new DamageReduction("10", "good"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("10/magic and good", listResult);

		drList.add(new DamageReduction("5", "good"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("10/magic and good", listResult);

		drList.add(new DamageReduction("10", "magic"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("10/magic and good", listResult);

		drList.add(new DamageReduction("5", "good"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("10/magic and good", listResult);

		drList.add(new DamageReduction("15", "Good"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/Good; 10/magic", listResult);

		drList.add(new DamageReduction("10", "good"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/Good; 10/magic", listResult);

		drList.add(new DamageReduction("10", "magic"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/Good; 10/magic", listResult);

		drList.add(new DamageReduction("5", "good"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/Good; 10/magic", listResult);

		drList.add(new DamageReduction("10", "magic and good"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/Good; 10/magic", listResult);

		drList.add(new DamageReduction("5", "evil"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/Good; 10/magic; 5/evil", listResult);

		drList.add(new DamageReduction("10", "magic or good"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/Good; 10/magic; 5/evil", listResult);

		drList.add(new DamageReduction("10", "good"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/Good; 10/magic; 5/evil", listResult);

		drList.add(new DamageReduction("10", "magic or good"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/Good; 10/magic; 5/evil", listResult);

		drList.add(new DamageReduction("5", "good"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/Good; 10/magic; 5/evil", listResult);

		drList.add(new DamageReduction("10", "magic and good"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/Good; 10/magic; 5/evil", listResult);

		drList.add(new DamageReduction("5", "magic and good"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/Good; 10/magic; 5/evil", listResult);

		drList.add(new DamageReduction("10", "magic or good"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/Good; 10/magic; 5/evil", listResult);

		drList.add(new DamageReduction("5", "magic and good"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/Good; 10/magic; 5/evil", listResult);

		drList.add(new DamageReduction("10", "magic or good"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/Good; 10/magic; 5/evil", listResult);

		drList.add(new DamageReduction("15", "magic"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/magic and Good; 5/evil", listResult);

		drList.add(new DamageReduction("10", "magic or good"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/magic and Good; 5/evil", listResult);

		drList.add(new DamageReduction("15", "magic and good"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/magic and Good; 5/evil", listResult);

		drList.add(new DamageReduction("10", "magic or lawful"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/magic and Good; 5/evil", listResult);

		drList.add(new DamageReduction("15", "magic and good"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/magic and Good; 5/evil", listResult);

		drList.add(new DamageReduction("10", "magic and good"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/magic and Good; 5/evil", listResult);

		drList.add(new DamageReduction("10", "magic and lawful"));
		listResult = DamageReduction.getDRString(null, drList);
//		System.out.println("DR List: " + drList.toString() + " = " + listResult);
		assertEquals("15/magic and Good; 10/lawful; 5/evil", listResult);

		ArrayList drList1 = new ArrayList();
		drList1.add(new DamageReduction("10", "epic"));
		drList1.add(new DamageReduction("10", "lawful or good"));
		listResult = DamageReduction.getDRString(null, drList1);
//		System.out.println("DR List: " + drList1.toString() + " = " + listResult);
		assertEquals("10/epic; 10/lawful or good", listResult);

		drList1.clear();
		drList1.add(new DamageReduction("10", "epic and good or epic and lawful"));
		listResult = DamageReduction.getDRString(null, drList1);
//		System.out.println("DR List: " + drList1.toString() + " = " + listResult);
		assertEquals("10/epic and good or epic and lawful", listResult);

		// Can't handle this case at the moment.
//		drList1.add(new DamageReduction("10", "lawful"));
//		listResult = DamageReduction.getDRString(null, drList1);
//		System.out.println("DR List: " + drList1.toString() + " = " + listResult);
//		assertEquals("10/epic and good", listResult);
	}
}
