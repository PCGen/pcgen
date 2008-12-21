/*
 * ChooserUtilitiesTest.java
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
 * Created on 21/12/2008 11:13:53 AM
 *
 * $Id: $
 */

package pcgen.core.chooser;

import pcgen.PCGenTestCase;
import pcgen.core.PCTemplate;


/**
 * The Class <code>ChooserUtilitiesTest</code> verifies that the 
 * ChooserUtilities methods are opertaing correctly.
 * 
 * Last Editor: $Author: $
 * Last Edited: $Date:  $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision:  $
 */
public class ChooserUtilitiesTest extends PCGenTestCase
{

	/**
	 * Test method for {@link pcgen.core.chooser.ChooserUtilities#getChoiceManager(pcgen.core.PObject, java.lang.String, pcgen.core.PlayerCharacter)}.
	 */
	public void testGetChoiceManager()
	{
		assertNull("Number should not be processed by a choice manager",
			ChooserUtilities.getChoiceManager(null,
				"NUMBER|MIN=1|MAX=5|TITLE=Combat Expertise trade off", null));
		assertNotNull(
			"Anything nother than number should be processed by achoice manager",
			ChooserUtilities.getChoiceManager(new PCTemplate(), "FOO|", null));
	}

}
