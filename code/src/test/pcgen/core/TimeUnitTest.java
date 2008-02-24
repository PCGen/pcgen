/*
 * TimeUnitTest.java
 * Copyright 2008 (C) James Dempsey
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
 * Created on 24/02/2008
 *
 * $Id$
 */

package pcgen.core;

import junit.framework.TestCase;

/**
 * <code>TimeUnitTest</code> validates the TimeUnit functionality
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class TimeUnitTest extends TestCase
{

	private static final int TU_SORT_ORDER = 5;
	private static final String TU_KEY = "T1";
	private static final String TU_TITLE = "Test of TUs";

	/**
	 * Test method for TimeUnit two field constructor.
	 */
	public void testTimeUnitConstructor1()
	{
		TimeUnit test = new TimeUnit(TU_KEY);
		assertEquals("Constructor did not set key", TU_KEY, test.getKeyName());
		assertEquals("Constructor did not set name", TU_KEY, test
			.getDisplayName());
	}

	/**
	 * Test method for TimeUnit two field constructor.
	 */
	public void testTimeUnitConstructor2()
	{
		TimeUnit test = new TimeUnit(TU_KEY, TU_TITLE);
		assertEquals("Constructor did not set key", TU_KEY, test.getKeyName());
		assertEquals("Constructor did not set name", TU_TITLE, test
			.getDisplayName());
	}

	/**
	 * Test method for TimeUnit getters and setters.
	 */
	public void testTimeUnitGettersAndSetters()
	{
		TimeUnit test = new TimeUnit("foo");
		assertEquals("Constructor did not set key", "foo", test.getKeyName());
		test.setKeyName(TU_KEY);
		assertEquals("Setter did not set key", TU_KEY, test.getKeyName());
		test.setName(TU_TITLE);
		assertEquals("Setter did not set displayname", TU_TITLE, test.getDisplayName());
		test.setOrder(TU_SORT_ORDER);
		assertEquals("Setter did not set sort order", TU_SORT_ORDER, test.getOrder());
	}

}
