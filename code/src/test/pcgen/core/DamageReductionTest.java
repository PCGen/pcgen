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

import java.util.IdentityHashMap;
import java.util.Map;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.CDOMObject;

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
		Map<DamageReduction, CDOMObject> drList = new IdentityHashMap<DamageReduction, CDOMObject>();
		String listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC(""));

		drList.put(new DamageReduction("10", "magic"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("10/magic"));

		drList.put(new DamageReduction("10", "good"), null);
		listResult = DamageReduction.getDRString(null, drList);
		//is(listResult, strEqIC("10/magic and good"));
		is(listResult, or(strEqIC("10/magic and good"),strEqIC("10/good and magic")));

		drList.put(new DamageReduction("10", "good"), null);
		listResult = DamageReduction.getDRString(null, drList);
		//is(listResult, strEqIC("10/magic and good"));
		is(listResult, or(strEqIC("10/magic and good"),strEqIC("10/good and magic")));

		drList.put(new DamageReduction("5", "good"), null);
		listResult = DamageReduction.getDRString(null, drList);
		//is(listResult, strEqIC("10/magic and good"));
		is(listResult, or(strEqIC("10/magic and good"),strEqIC("10/good and magic")));

		drList.put(new DamageReduction("10", "magic"), null);
		listResult = DamageReduction.getDRString(null, drList);
		//is(listResult, strEqIC("10/magic and good"));
		is(listResult, or(strEqIC("10/magic and good"),strEqIC("10/good and magic")));

		drList.put(new DamageReduction("5", "good"), null);
		listResult = DamageReduction.getDRString(null, drList);
		//is(listResult, strEqIC("10/magic and good"));
		is(listResult, or(strEqIC("10/magic and good"),strEqIC("10/good and magic")));

		drList.put(new DamageReduction("15", "Good"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/Good; 10/magic"));

		drList.put(new DamageReduction("10", "good"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/Good; 10/magic"));

		drList.put(new DamageReduction("10", "magic"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/Good; 10/magic"));

		drList.put(new DamageReduction("5", "good"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/Good; 10/magic"));

		drList.put(new DamageReduction("10", "magic and good"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/Good; 10/magic"));

		drList.put(new DamageReduction("5", "evil"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/Good; 10/magic; 5/evil"));

		drList.put(new DamageReduction("10", "magic or good"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/Good; 10/magic; 5/evil"));

		drList.put(new DamageReduction("10", "good"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/Good; 10/magic; 5/evil"));

		drList.put(new DamageReduction("10", "magic or good"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/Good; 10/magic; 5/evil"));

		drList.put(new DamageReduction("5", "good"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/Good; 10/magic; 5/evil"));

		drList.put(new DamageReduction("10", "magic and good"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/Good; 10/magic; 5/evil"));

		drList.put(new DamageReduction("5", "magic and good"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/Good; 10/magic; 5/evil"));

		drList.put(new DamageReduction("10", "magic or good"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/Good; 10/magic; 5/evil"));

		drList.put(new DamageReduction("5", "magic and good"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/Good; 10/magic; 5/evil"));

		drList.put(new DamageReduction("10", "magic or good"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/Good; 10/magic; 5/evil"));

		drList.put(new DamageReduction("15", "magic"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/magic and Good; 5/evil"));

		drList.put(new DamageReduction("10", "magic or good"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/magic and Good; 5/evil"));

		drList.put(new DamageReduction("15", "magic and good"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/magic and Good; 5/evil"));

		drList.put(new DamageReduction("10", "magic or lawful"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/magic and Good; 5/evil"));

		drList.put(new DamageReduction("15", "magic and good"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/magic and Good; 5/evil"));

		drList.put(new DamageReduction("10", "magic and good"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/magic and Good; 5/evil"));

		drList.put(new DamageReduction("10", "magic and lawful"), null);
		listResult = DamageReduction.getDRString(null, drList);
		is(listResult, strEqIC("15/magic and Good; 10/lawful; 5/evil"));

		Map<DamageReduction, CDOMObject> drList1 = new IdentityHashMap<DamageReduction, CDOMObject>();
		drList1.put(new DamageReduction("10", "epic"), null);
		drList1.put(new DamageReduction("10", "lawful or good"), null);
		listResult = DamageReduction.getDRString(null, drList1);
		is(listResult, strEqIC("10/epic; 10/lawful or good"));

		drList1.clear();
		drList1.put(new DamageReduction("10",
			"epic and good or epic and lawful"), null);
		listResult = DamageReduction.getDRString(null, drList1);
		is(listResult, strEqIC("10/epic and good or epic and lawful"));

		// Can't handle this case at the moment.
		//		drList1.add(new DamageReduction("10", "lawful"));
		//		listResult = DamageReduction.getDRString(null, drList1);
		//		System.out.println("DR List: " + drList1.toString() + " = " + listResult);
		//		is(listResult, strEqIC("10/epic and good"));
	}
}
