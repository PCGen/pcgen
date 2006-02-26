/*
 * EquipmentTest.java
 *
 * Copyright 2005 (C) Andrew Wilson <nuance@sourceforge.net>
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
 * Created on 14-Aug-2005
 *
 * Current Ver: $Revision: 1.7 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2005/11/29 14:14:22 $
 *
 */
package pcgen.core;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;
import pcgen.PCGenTestCase;
import pcgen.core.Equipment;
import pcgen.core.Constants;

import pcgen.util.TestHelper;

/**
 * Equipment Test
 */
public class EquipmentTest extends PCGenTestCase {

	private Equipment    eq          = null;
	private final String OriginalKey = "OrigKey";
	private boolean firstTime = true;
	
	/**
	 * Main
	 * @param args
	 */
	public static void main(final String[] args)
	{
		TestRunner.run(EquipmentTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(EquipmentTest.class);
	}

	/**
	 * Constructs a new <code>EquipmentTest</code>.
	 *
	 * @see PCGenTestCase#PCGenTestCase()
	 */
	public EquipmentTest()
	{
		// Constructor
	}

	/**
	 * Constructs a new <code>EquipmentTest</code> with the given
	 * <var>name</var>.
	 *
	 * @param name the test case name
	 *
	 * @see PCGenTestCase#PCGenTestCase()
	 */
	public EquipmentTest(final String name)
	{
		super(name);
	}

	public void setUp() throws Exception
	{
		super.setUp();

		if (firstTime) {
			TestHelper.makeSizeAdjustments();
			firstTime = false;
		}

		this.eq = new Equipment();
		this.eq.setName("Dummy");
		this.eq.setSize("M", true);
		
		this.eq.setKeyName(this.OriginalKey);
	}

	/*****************************************************************************
	 * createKeyForAutoResize Tests
	 ****************************************************************************/

	// Original Key was what I expected
	public void testcreateKeyForAutoResize001()
	{
		is(this.eq.getKeyName(), strEq(this.OriginalKey));
	}

	/** 
	 * Try lower case letter for size
	 */
	public void testcreateKeyForAutoResize002()
	{
		final String newSize = "s";
		
		final String expectedKey = Constants.s_AUTO_RESIZE + newSize.toUpperCase() + this.OriginalKey;

		is(this.eq.createKeyForAutoResize(newSize), strEq(expectedKey));
	}

	/** 
	 * Try upper case word for size
	 */
	public void testcreateKeyForAutoResize003()
	{
		final String newSize = "COLOSSAL";

		final String expectedKey = Constants.s_AUTO_RESIZE + 
					   newSize.toUpperCase().substring(0,1) + 
					   this.OriginalKey;

		is(this.eq.createKeyForAutoResize(newSize), strEq(expectedKey));
	}

	/** Try empty new size */
	public void testcreateKeyForAutoResize004()
	{
		is(this.eq.createKeyForAutoResize(""), strEq(this.OriginalKey)); 
	}

	/** Ensure that second customisation will work correctly */
	public void testcreateKeyForAutoResize005()
	{
		String newSize = "Fine";

		String expectedKey = Constants.s_AUTO_RESIZE + 
		  		   newSize.toUpperCase().substring(0,1) + 
		  		   this.OriginalKey;

		is(this.eq.createKeyForAutoResize(newSize), strEq(expectedKey));

		newSize = "Diminutive";

		expectedKey = Constants.s_AUTO_RESIZE + 
		   	   newSize.toUpperCase().substring(0,1) + 
		   	   this.OriginalKey;

		is(this.eq.createKeyForAutoResize(newSize), strEq(expectedKey));
	}

	/** Try nonsense abbreviation for Size */
	public void testcreateKeyForAutoResize006()
	{
		String newSize = "XXL";
	
		String unExpectedKey = Constants.s_AUTO_RESIZE + 
		  	  	   newSize.toUpperCase().substring(0,1) + 
		  		   this.OriginalKey;
		
		is(this.eq.createKeyForAutoResize(newSize), not(strEq(unExpectedKey)));
		is(this.eq.createKeyForAutoResize(newSize), strEq(this.OriginalKey));
	}

	/*****************************************************************************
	 * createNameForAutoResize tests
	 ****************************************************************************/
	
	/** Test with Size that exists and is formatted correctly */ 
	public void testcreateNameForAutoResize001()
	{
		is(this.eq.createNameForAutoResize("Large"), strEq("Dummy (Large)"));
	}

	/** Test with Size that exists and is lower Case */
	public void testcreateNameForAutoResize002()
	{
		is(this.eq.createNameForAutoResize("large"), strEq("Dummy (Large)"));
	}

	/** Test with Abbreviation for Size that exists */
	public void testcreateNameForAutoResize003()
	{
		is(this.eq.createNameForAutoResize("f"), strEq("Dummy (Fine)"));
	}

	/** Test with Nonexistant size */ 
	public void testcreateNameForAutoResize004()
	{
		is(this.eq.createNameForAutoResize("z"), strEq("Dummy"));
	}

	/** Test that size is replaced correctly */
	public void testcreateNameForAutoResize005()
	{
		String newKey = eq.createKeyForAutoResize("L");
		eq.setSize("Large", false);
		eq.setName("Pointy Stick (Large)");
		eq.setKeyName(newKey);

		String expectedKey = Constants.s_AUTO_RESIZE + "L" + this.OriginalKey;

		// confirm test set up
		is(eq.getKeyName(), strEq(expectedKey));
		is(eq.getName(),    strEq("Pointy Stick (Large)"));
		is(eq.getSize(),    strEq("L"));

		// Now check that new name is generated Correctly
		is(this.eq.createNameForAutoResize("d"), strEq("Pointy Stick (Diminutive)"));
		
	}

	/** Test that size is replaced correctly */
	public void testcreateNameForAutoResize006()
	{
		String newKey = eq.createKeyForAutoResize("L");
		eq.setSize("Large", false);
		eq.setName("Pointy Stick (+1/Large)");
		eq.setKeyName(newKey);

		String expectedKey = Constants.s_AUTO_RESIZE + "L" + this.OriginalKey;

		// confirm test set up
		is(eq.getKeyName(), strEq(expectedKey));
		is(eq.getName(),    strEq("Pointy Stick (+1/Large)"));
		is(eq.getSize(),    strEq("L"));

		// Now check that new name is generated Correctly
		is(this.eq.createNameForAutoResize("g"), strEq("Pointy Stick (+1/Gargantuan)"));
		
	}

	/** Test that size is replaced correctly */
	public void testcreateNameForAutoResize007()
	{
		String newKey = eq.createKeyForAutoResize("L");
		eq.setSize("Large", false);
		eq.setName("Pointy Stick (+1/Large/Speed)");
		eq.setKeyName(newKey);

		String expectedKey = Constants.s_AUTO_RESIZE + "L" + this.OriginalKey;

		// confirm test set up
		is(eq.getKeyName(), strEq(expectedKey));
		is(eq.getName(),    strEq("Pointy Stick (+1/Large/Speed)"));
		is(eq.getSize(),    strEq("L"));

		// Now check that new name is generated Correctly
		is(this.eq.createNameForAutoResize("c"), strEq("Pointy Stick (+1/Colossal/Speed)"));
	}

	/** Test that size is replaced correctly */
	public void testcreateNameForAutoResize008()
	{
		String newKey = eq.createKeyForAutoResize("L");
		eq.setSize("Large", false);
		eq.setName("Pointy Stick (+1/Speed)");
		eq.setKeyName(newKey);

		String expectedKey = Constants.s_AUTO_RESIZE + "L" + this.OriginalKey;

		// confirm test set up
		is(eq.getKeyName(), strEq(expectedKey));
		is(eq.getName(),    strEq("Pointy Stick (+1/Speed)"));
		is(eq.getSize(),    strEq("L"));

		// Now check that new name is generated Correctly
		is(this.eq.createNameForAutoResize("c"), strEq("Pointy Stick (+1/Speed) (Colossal)"));
	}

}
