/*
 * SourceSelectionUtilsTest.java
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
 * Created on 19/11/2008 5:58:15 PM
 *
 * $Id: $
 */

package pcgen.gui.sources;

import org.junit.Test;

import pcgen.PCGenTestCase;


/**
 * The Class <code>SourceSelectionUtilsTest</code> is responsible for 
 * checking the function of the SourceSelectionUtils class.
 * 
 * Last Editor: $Author: $
 * Last Edited: $Date:  $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision:  $
 */
public class SourceSelectionUtilsTest extends PCGenTestCase
{

	/**
	 * Test method for {@link pcgen.gui.sources.SourceSelectionUtils#sanitiseFilename(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testSanitiseFilename()
	{
		assertEquals("No punct add ext", "MtTaylor.pcc", SourceSelectionUtils
			.sanitiseFilename("MtTaylor", ".pcc"));
		assertEquals("Punct add ext", "Mt_T_a_y__4lor.pcc", SourceSelectionUtils
			.sanitiseFilename("Mt T!a@y#$4lor", ".pcc"));
		assertEquals("No punct with ext", "MtTaylor.pcc", SourceSelectionUtils
			.sanitiseFilename("MtTaylor.pcc", ".pcc"));
		assertEquals("Allowed Punct with ext", "Mt.Tay--lor.pcc", SourceSelectionUtils
			.sanitiseFilename("Mt.Tay--lor.pcc", ".pcc"));
		assertEquals("No punct numbers add ext", "MtTaylor12.pcc", SourceSelectionUtils
			.sanitiseFilename("MtTaylor12", ".pcc"));
		assertEquals("No punct no ext", "MtTaylor", SourceSelectionUtils
			.sanitiseFilename("MtTaylor", ""));
		assertEquals("No punct null ext", "MtTaylor", SourceSelectionUtils
			.sanitiseFilename("MtTaylor", null));
	}

}
