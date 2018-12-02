/**
 * Copyright James Dempsey, 2010
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
 */
package pcgen.core.analysis;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PObject;

import junit.framework.TestCase;

/**
 * Verify the function of the OutputNameFormatting class.
 */
public class OutputNameFormattingTest extends TestCase
{
	private static final String NAME = "Companion (Race (Subtype))";

	private PObject testObj;

	@Override
	protected void setUp() throws Exception
	{
		testObj = new PObject();
		testObj.setName(NAME);
		super.setUp();
	}

	
	/**
	 * Test method for {@link pcgen.core.analysis.OutputNameFormatting#getOutputName(CDOMObject)}.
	 * Check that a default output name will work correctly.
	 */
	public final void testGetOutputNameDisplay()
	{
		assertEquals("Expected unmodified name", NAME, OutputNameFormatting.getOutputName(testObj));
	}
	
	/**
	 * Test method for {@link pcgen.core.analysis.OutputNameFormatting#getOutputName(CDOMObject)}.
	 * Check that the [BASE] macro in output name will work correctly.
	 */
	public final void testGetOutputNameBase()
	{
		testObj.put(StringKey.OUTPUT_NAME, "[BASE]");
		assertEquals("Expected just the name outside of brackets", "Companion",
			OutputNameFormatting.getOutputName(testObj));

		testObj.put(StringKey.OUTPUT_NAME, "Prefix [BASE]");
		assertEquals("Expected the BASE macro to be ignored", "Prefix [BASE]",
			OutputNameFormatting.getOutputName(testObj));
	}
	
	/**
	 * Test method for {@link pcgen.core.analysis.OutputNameFormatting#getOutputName(CDOMObject)}.
	 * Check that the [NAME] macro in output name will work correctly.
	 */
	public final void testGetOutputNameName()
	{
		testObj.put(StringKey.OUTPUT_NAME, "[NAME]");
		assertEquals("Incorrect [NAME] expansion", "Race (Subtype)",
			OutputNameFormatting.getOutputName(testObj));

		testObj.put(StringKey.OUTPUT_NAME, "Prefix [NAME]");
		assertEquals("Incorrect [NAME] expansion", "Prefix Race (Subtype)",
			OutputNameFormatting.getOutputName(testObj));

		testObj.put(StringKey.OUTPUT_NAME, "Prefix [NAME]|[NAME]");
		assertEquals("Incorrect double [NAME] expansion", "Prefix Race (Subtype)|Race (Subtype)",
			OutputNameFormatting.getOutputName(testObj));
	}


}
