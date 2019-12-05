/**
 * Copyright James Dempsey, 2010
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PObject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Verify the function of the OutputNameFormatting class.
 */
public class OutputNameFormattingTest
{
    private static final String NAME = "Companion (Race (Subtype))";

    private PObject testObj;

    @BeforeEach
    public void setUp()
    {
        testObj = new PObject();
        testObj.setName(NAME);
    }

    @AfterEach
    public void tearDown()
    {
        testObj = null;
    }

    /**
     * Test method for {@link pcgen.core.analysis.OutputNameFormatting#getOutputName(CDOMObject)}.
     * Check that a default output name will work correctly.
     */
    @Test
    public final void testGetOutputNameDisplay()
    {
        assertEquals(NAME, OutputNameFormatting.getOutputName(testObj), "Expected unmodified name");
    }

    /**
     * Test method for {@link pcgen.core.analysis.OutputNameFormatting#getOutputName(CDOMObject)}.
     * Check that the [BASE] macro in output name will work correctly.
     */
    @Test
    public final void testGetOutputNameBase()
    {
        testObj.put(StringKey.OUTPUT_NAME, "[BASE]");
        assertEquals(
                "Companion",
                OutputNameFormatting.getOutputName(testObj),
                "Expected just the name outside of brackets"
        );

        testObj.put(StringKey.OUTPUT_NAME, "Prefix [BASE]");
        assertEquals(
                "Prefix [BASE]",
                OutputNameFormatting.getOutputName(testObj),
                "Expected the BASE macro to be ignored"
        );
    }

    /**
     * Test method for {@link pcgen.core.analysis.OutputNameFormatting#getOutputName(CDOMObject)}.
     * Check that the [NAME] macro in output name will work correctly.
     */
    @Test
    public final void testGetOutputNameName()
    {
        testObj.put(StringKey.OUTPUT_NAME, "[NAME]");
        assertEquals(
                "Race (Subtype)",
                OutputNameFormatting.getOutputName(testObj),
                "Incorrect [NAME] expansion"
        );

        testObj.put(StringKey.OUTPUT_NAME, "Prefix [NAME]");
        assertEquals(
                "Prefix Race (Subtype)",
                OutputNameFormatting.getOutputName(testObj),
                "Incorrect [NAME] expansion"
        );

        testObj.put(StringKey.OUTPUT_NAME, "Prefix [NAME]|[NAME]");
        assertEquals(
                "Prefix Race (Subtype)|Race (Subtype)",
                OutputNameFormatting.getOutputName(testObj),
                "Incorrect double [NAME] expansion"
        );
    }


}
