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

/**
 * This class tests the handling of DRs in PCGen
 */
@SuppressWarnings("nls")
public class DamageReductionTest extends AbstractCharacterTestCase
{
	/**
	 * Constructs a new <code>DamageReductionTest</code>.
	 */
	public DamageReductionTest()
	{
		super();
	}

	/**
	 * Test the basic DR Handling
	 */
	public void testBasicDRHandling()
	{
		DamageReduction dr1 = new DamageReduction("5", "magic");
		DamageReduction dr2 = new DamageReduction("5", "-");

		is(false, eq(dr1.equals(dr2)));

		dr2 = new DamageReduction("5", "Magic");
		is(true, eq(dr1.equals(dr2)));

		dr2 = new DamageReduction("10", "magic");
		is(false, eq(dr1.equals(dr2)));

		dr1 = new DamageReduction("10", "magic and good");
		dr2 = new DamageReduction("10", "good and magic");
		is(true, eq(dr1.equals(dr2)));

		dr2 = new DamageReduction("10", "Good and magic");
		is(true, eq(dr1.equals(dr2)));
	}

	/**
	 * Test the retrieval of the DR String
	 */
	public void testGetDRString()
	{
		ArrayList<DamageReduction> drList = new ArrayList<DamageReduction>();
		String listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq(""));

		drList.add(new DamageReduction("10", "magic"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("10/magic"));

		drList.add(new DamageReduction("10", "good"));
		listResult = DamageReduction.getDRString(null, drList);
		//is(listResult, strEq("10/magic and good"));
		is(listResult, or(strEq("10/magic and good"),strEq("10/good and magic")));

		drList.add(new DamageReduction("10", "good"));
		listResult = DamageReduction.getDRString(null, drList);
		//is(listResult, strEq("10/magic and good"));
		is(listResult, or(strEq("10/magic and good"),strEq("10/good and magic")));

		drList.add(new DamageReduction("5", "good"));
		listResult = DamageReduction.getDRString(null, drList);
		//is(listResult, strEq("10/magic and good"));
		is(listResult, or(strEq("10/magic and good"),strEq("10/good and magic")));

		drList.add(new DamageReduction("10", "magic"));
		listResult = DamageReduction.getDRString(null, drList);
		//is(listResult, strEq("10/magic and good"));
		is(listResult, or(strEq("10/magic and good"),strEq("10/good and magic")));

		drList.add(new DamageReduction("5", "good"));
		listResult = DamageReduction.getDRString(null, drList);
		//is(listResult, strEq("10/magic and good"));
		is(listResult, or(strEq("10/magic and good"),strEq("10/good and magic")));

		drList.add(new DamageReduction("15", "Good"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/Good; 10/magic"));

		drList.add(new DamageReduction("10", "good"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/Good; 10/magic"));

		drList.add(new DamageReduction("10", "magic"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/Good; 10/magic"));

		drList.add(new DamageReduction("5", "good"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/Good; 10/magic"));

		drList.add(new DamageReduction("10", "magic and good"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/Good; 10/magic"));

		drList.add(new DamageReduction("5", "evil"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/Good; 10/magic; 5/evil"));

		drList.add(new DamageReduction("10", "magic or good"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/Good; 10/magic; 5/evil"));

		drList.add(new DamageReduction("10", "good"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/Good; 10/magic; 5/evil"));

		drList.add(new DamageReduction("10", "magic or good"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/Good; 10/magic; 5/evil"));

		drList.add(new DamageReduction("5", "good"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/Good; 10/magic; 5/evil"));

		drList.add(new DamageReduction("10", "magic and good"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/Good; 10/magic; 5/evil"));

		drList.add(new DamageReduction("5", "magic and good"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/Good; 10/magic; 5/evil"));

		drList.add(new DamageReduction("10", "magic or good"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/Good; 10/magic; 5/evil"));

		drList.add(new DamageReduction("5", "magic and good"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/Good; 10/magic; 5/evil"));

		drList.add(new DamageReduction("10", "magic or good"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/Good; 10/magic; 5/evil"));

		drList.add(new DamageReduction("15", "magic"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/magic and Good; 5/evil"));

		drList.add(new DamageReduction("10", "magic or good"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/magic and Good; 5/evil"));

		drList.add(new DamageReduction("15", "magic and good"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/magic and Good; 5/evil"));

		drList.add(new DamageReduction("10", "magic or lawful"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/magic and Good; 5/evil"));

		drList.add(new DamageReduction("15", "magic and good"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/magic and Good; 5/evil"));

		drList.add(new DamageReduction("10", "magic and good"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/magic and Good; 5/evil"));

		drList.add(new DamageReduction("10", "magic and lawful"));
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEq("15/magic and Good; 10/lawful; 5/evil"));

		ArrayList<DamageReduction> drList1 = new ArrayList<DamageReduction>();
		drList1.add(new DamageReduction("10", "epic"));
		drList1.add(new DamageReduction("10", "lawful or good"));
		listResult = DamageReduction.getDRString(null, drList1);
		is(listResult, strEq("10/epic; 10/lawful or good"));

		drList1.clear();
		drList1.add(new DamageReduction("10",
			"epic and good or epic and lawful"));
		listResult = DamageReduction.getDRString(null, drList1);
		is(listResult, strEq("10/epic and good or epic and lawful"));

		// Can't handle this case at the moment.
		//		drList1.add(new DamageReduction("10", "lawful"));
		//		listResult = DamageReduction.getDRString(null, drList1);
		//		System.out.println("DR List: " + drList1.toString() + " = " + listResult);
		//		is(listResult, strEq("10/epic and good"));
	}
}
