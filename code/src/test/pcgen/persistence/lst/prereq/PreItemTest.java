/*
 * PreItemTest.java
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
 * Created on Sep 4, 2004
 *
 * $Id: PreItemTest.java,v 1.2 2005/09/12 11:48:36 karianna Exp $
 *
 */
package pcgen.persistence.lst.prereq;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.core.prereq.Prerequisite;

/**
 * <code>PreItemTest</code> is ...
 *
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2005/09/12 11:48:36 $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 1.2 $
 */

public class PreItemTest extends TestCase
{

	public static void main(String[] args)
	{
		TestRunner.run(PreEquipTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreItemTest.class);
	}

	public void testItemPresent() throws Exception
	{
		PreItemParser parser = new PreItemParser();
		// Test of |PREITEM:1,TYPE.Saddle";

		Prerequisite prereq = parser.parse("ITEM",
			"1,TYPE.Saddle", false, false);

		assertEquals(
			"<prereq kind=\"item\" key=\"TYPE.Saddle\" operator=\"gteq\" operand=\"1\" >\n"
			+ "</prereq>\n", prereq.toString());
	}

	public void testItemNotPresent() throws Exception
	{
		PreItemParser parser = new PreItemParser();
		// Test of |!PREITEM:1,TYPE.Saddle";

		Prerequisite prereq = parser.parse("ITEM",
			"1,TYPE.Saddle", true, false);

		assertEquals(
			"<prereq kind=\"item\" key=\"TYPE.Saddle\" operator=\"lt\" operand=\"1\" >\n"
				+ "</prereq>\n", prereq.toString());
	}

}