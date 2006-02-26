/*
 * DomainTest.java
 * Copyright 2004 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on Sep 6, 2004
 *
 * $Id: DomainTest.java,v 1.5 2005/09/14 23:49:17 nuance Exp $
 *
 */
package pcgen.core;

import pcgen.PCGenTestCase;

import java.util.Iterator;

/**
 * <code>DomainTest</code> test that the Domain class is functioning correctly.
 *
 * Last Editor: $Author: nuance $ Last Edited: $Date: 2005/09/14 23:49:17 $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 1.5 $
 */

public class DomainTest extends PCGenTestCase {
	/**
	 * Constructs a new <code>DomainTest</code>.
	 *
	 * @see PCGenTestCase#PCGenTestCase()
	 */
	public DomainTest()
	{
		// Do Nothing
	}

	/**
	 * Constructs a new <code>DomainTest</code> with the given <var>name</var>.
	 *
	 * @param name the test case name
	 *
	 * @see PCGenTestCase#PCGenTestCase(String)
	 */
	public DomainTest(final String name)
	{
		super(name);
	}

	/**
	 * Check that the addFeat method correctly adds feats.
	 */
	public void testAddFeatToList()
	{
		final Domain foo = new Domain();

		assertEquals("Empty feat list initially", 0, foo.getNumberOfFeats());

		foo.addFeat("Feat1");
		assertEquals(1, foo.getNumberOfFeats());

		foo.addFeat("Feat2,Feat3");
		assertEquals(3, foo.getNumberOfFeats());

		foo.addFeat("Feat4|Feat5");
		assertEquals(5, foo.getNumberOfFeats());

		foo.addFeat(".CLEAR");
		assertEquals(0, foo.getNumberOfFeats());

		foo.addFeat("Feat6|Feat7,Feat8");
		assertEquals(3, foo.getNumberOfFeats());

		Iterator it = foo.getFeatIterator();
		AbilityInfo anAI = (AbilityInfo) it.next();
		
		assertEquals("Feat6", anAI.getKeyName());
	}

}
