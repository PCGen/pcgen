/*
 * Copyright 2006 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package pcgen.core;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.util.TestHelper;

/**
 * <code>EquipmentListTest</code> checks the functionality of the EquipmentList class.
 */
@SuppressWarnings("nls")
public class EquipmentListTest extends TestCase
{

	private Equipment eq = null;
	private static final String ORIGINAL_KEY = "OrigKey";
	private boolean firstTime = true;

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(EquipmentListTest.class);
	}

	/**
	 * Constructs a new <code>EquipmentListTest</code>.
	 *
	 * @see pcgen.PCGenTestCase#PCGenTestCase()
	 */
	public EquipmentListTest()
	{
		// Constructor
	}

	/**
	 * Constructs a new <code>EquipmentListTest</code> with the given
	 * <var>name</var>.
	 *
	 * @param name the test case name
	 *
	 * @see pcgen.PCGenTestCase#PCGenTestCase(String)
	 */
	public EquipmentListTest(final String name)
	{
		super(name);
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		if (firstTime)
		{
			TestHelper.makeSizeAdjustments();
			firstTime = false;
		}

		this.eq = new Equipment();
		this.eq.setName("Dummy");
		SizeAdjustment sa = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
				SizeAdjustment.class, "M");
		CDOMDirectSingleRef<SizeAdjustment> mediumRef = CDOMDirectSingleRef.getRef(sa);
		eq.put(ObjectKey.SIZE, mediumRef);
		eq.put(ObjectKey.BASESIZE, mediumRef);
		TestHelper.addType(eq, "WEAPON.MELEE.CHOCOLATE");

		this.eq.put(StringKey.KEY_NAME, ORIGINAL_KEY);
	}

	/**
	 * test the getEquipmentOfType method
	 */
	public void testGetEquipmentOfType()
	{
		Globals.getContext().getReferenceContext().importObject(eq);

		List<Equipment> results =
				EquipmentList.getEquipmentOfType("Weapon.Melee", "Magic");
		assertEquals("Should get a single result", 1, results.size());
		assertEquals("Should find the DUmmy equipment object.", eq, results
			.get(0));
	}
}
