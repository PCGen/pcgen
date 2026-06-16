package pcgen.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.core.term.TermEvaulatorException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Copyright (c) 2008 Andrew Wilson <nuance@users.sourceforge.net>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

class TermUtilitiesTest
{
	/**
	 * Method: checkEqTypeTypesArray(String originalText, String[] types, int first)
	 */
	@Test
	public void testcheckEqTypeTypesArray01()
	{
		String[] types = {"EQUIPPED"};
		assertDoesNotThrow(() -> TermUtilities.checkEqTypeTypesArray("COUNT[EQTYPE.EQUIPPED]", types, 0), "EqtypesTypesArray01 Single Type EQUIPPED");
	}

	/**
	 * Method: checkEqTypeTypesArray(String originalText, String[] types, int first)
	 */
	@Test
	public void testcheckEqTypeTypesArray02()
	{
		String[] types = {"NOTEQUIPPED"};
		assertDoesNotThrow(() -> TermUtilities.checkEqTypeTypesArray("COUNT[EQTYPE.NOTEQUIPPED]", types, 0), "EqtypesTypesArray01 Single Type NOTEQUIPPEDZ");
	}

	/**
	 * Method: checkEqTypeTypesArray(String originalText, String[] types, int first)
	 */
	@Test
	public void testcheckEqTypeTypesArray03()
	{
		assertThrows(TermEvaulatorException.class, () -> {
			String[] types = {"EQUIPPED", "FOO"};
			TermUtilities.checkEqTypeTypesArray("COUNT[EQTYPE.EQUIPPED.FOO]", types, 0);
		}, "EqtypesTypesArray03 EQUIPPED with spurious type");
	}

	/**
	 * Method: checkEqTypeTypesArray(String originalText, String[] types, int first)
	 */
	@Test
	public void testcheckEqTypeTypesArray04()
	{
		String[] types = {"BAR", "NOT", "FOO"};
		assertDoesNotThrow(() -> TermUtilities.checkEqTypeTypesArray("COUNT[EQTYPE.BAR.NOT.FOO]", types, 0), "EqtypesTypesArray04 Exclude FOO");
	}

	/**
	 * Method: checkEqTypeTypesArray(String originalText, String[] types, int first)
	 */
	@Test
	public void testcheckEqTypeTypesArray05()
	{
		assertDoesNotThrow(() ->  {
			String[] types = {"BAR", "ADD", "FOO"};
			TermUtilities.checkEqTypeTypesArray("COUNT[EQTYPE.BAR.ADD.FOO]", types, 0);
		}, "EqtypesTypesArray05 Include FOO");
	}

	/**
	 * Method: checkEqTypeTypesArray(String originalText, String[] types, int first)
	 */
	@Test
	public void testcheckEqTypeTypesArray06()
	{
		assertDoesNotThrow(() -> {
			String[] types = {"BAR", "IS", "FOO"};
			TermUtilities.checkEqTypeTypesArray("COUNT[EQTYPE.BAR.IS.FOO]", types, 0);
		}, "EqtypesTypesArray06 Only FOO");
	}

	/**
	 * Method: checkEqTypeTypesArray(String originalText, String[] types, int first)
	 */
	@Test
	public void testcheckEqTypeTypesArray07()
	{
		boolean ok;
		try
		{
			ok = true;
			String[] types = {"QUX", "NOT", "FOO", "ADD", "BAR", "IS", "BAZ"};
			TermUtilities.checkEqTypeTypesArray("COUNT[EQTYPE.QUX.NOT.FOO.ADD.BAR.IS.BAZ]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		assertTrue(ok, "EqtypesTypesArray07 All options");
	}

	/**
	 * Method: checkEqTypeTypesArray(String originalText, String[] types, int first)
	 */
	@Test
	public void testcheckEqTypeTypesArray08()
	{
		boolean ok;
		try
		{
			ok = true;
			String[] types = {"QUUX", "NOT", "FOO", "ADD", "BAR", "IS", "BAZ", "QUX"};
			TermUtilities.checkEqTypeTypesArray("COUNT[EQTYPE.QUUX.NOT.FOO.ADD.BAR.IS.BAZ.QUX]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		assertFalse(ok, "EqtypesTypesArray08 All options with spurious");
	}

	/**
	 * Method: checkEquipmentTypesArray(String originalText, String[] types, int first)
	 */
	@Test
	public void testcheckEquipmentTypesArray01()
	{
		boolean ok;
		try
		{
			ok = true;
			String[] types = {"EQUIPPED"};
			TermUtilities.checkEquipmentTypesArray("COUNT[EQUIPMENT.EQUIPPED]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		assertFalse(ok, "EquipmentTypesArray01 Single Type EQUIPPED");
	}

	/**
	 * Method: checkEquipmentTypesArray(String originalText, String[] types, int first)
	 */
	@Test
	public void testcheckEquipmentTypesArray02()
	{
		boolean ok;
		try
		{
			ok = true;
			String[] types = {"NOTEQUIPPED"};
			TermUtilities.checkEquipmentTypesArray("COUNT[EQUIPMENT.NOTEQUIPPED]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		assertFalse(ok, "EquipmentTypesArray02 Single Type NOTEQUIPPED");
	}

	/**
	 * Method: checkEquipmentTypesArray(String originalText, String[] types, int first)
	 */
	@Test
	public void testcheckEquipmentTypesArray03()
	{
		boolean ok;
		try
		{
			ok = true;
			String[] types = {"EQUIPPED", "FOO"};
			TermUtilities.checkEquipmentTypesArray("COUNT[EQUIPMENT.EQUIPPED.FOO]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		assertFalse(ok, "EquipmentTypesArray03 EQUIPPED with spurious type");
	}

	/**
	 * Method: checkEquipmentTypesArray(String originalText, String[] types, int first)
	 */
	@Test
	public void testcheckEquipmentTypesArray04()
	{
		boolean ok;
		try
		{
			ok = true;
			String[] types = {"NOT", "FOO"};
			TermUtilities.checkEquipmentTypesArray("COUNT[EQUIPMENT.NOT.FOO]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		assertTrue(ok, "EquipmentTypesArray04 Exclude FOO");
	}

	/**
	 * Method: checkEquipmentTypesArray(String originalText, String[] types, int first)
	 */
	@Test
	public void testcheckEquipmentTypesArray05()
	{
		boolean ok;
		try
		{
			ok = true;
			String[] types = {"ADD", "FOO"};
			TermUtilities.checkEquipmentTypesArray("COUNT[EQUIPMENT.ADD.FOO]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		assertTrue(ok, "EquipmentTypesArray05 Include FOO");
	}

	/**
	 * Method: checkEquipmentTypesArray(String originalText, String[] types, int first)
	 */
	@Test
	public void testcheckEquipmentTypesArray06()
	{
		boolean ok;
		try
		{
			ok = true;
			String[] types = {"IS", "FOO"};
			TermUtilities.checkEquipmentTypesArray("COUNT[EQUIPMENT.IS.FOO]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		assertTrue(ok, "EquipmentTypesArray06 Only FOO");
	}

	/**
	 * Method: checkEquipmentTypesArray(String originalText, String[] types, int first)
	 */
	@Test
	public void testcheckEquipmentTypesArray07()
	{
		boolean ok;
		try
		{
			ok = true;
			String[] types = {"NOT", "FOO", "ADD", "BAR", "IS", "BAZ"};
			TermUtilities.checkEquipmentTypesArray("COUNT[EQUIPMENT.NOT.FOO.ADD.BAR.IS.BAZ]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		assertTrue(ok, "EquipmentTypesArray07 All options");
	}

	/**
	 * Method: checkEquipmentTypesArray(String originalText, String[] types, int first)
	 */
	@Test
	public void testcheckEquipmentTypesArray08()
	{
		boolean ok;
		try
		{
			ok = true;
			String[] types = {"NOT", "FOO", "ADD", "BAR", "IS", "BAZ", "QUX"};
			TermUtilities.checkEquipmentTypesArray("COUNT[EQUIPMENT.NOT.FOO.ADD.BAR.IS.BAZ.QUX]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		assertFalse(ok, "EquipmentTypesArray08 All options with spurious");
	}


	/**
	 * Method: splitAndConvertIntegers(final String source, int numOfFields)
	 */
	@Test
	public void testSplitAndConvertIntegers01()
	{
		boolean ok;
		int[] nums = new int[1];
		try
		{
			ok = true;
			nums = TermUtilities.splitAndConvertIntegers("Test:3", "3", 1);
		}
		catch (NumberFormatException | TermEvaulatorException e)
		{
			ok = false;
		}
		assertTrue(ok, "one int is ok");
		MatcherAssert.assertThat("one int is ok:first", nums[0], Matchers.is(3));
	}

	/**
	 * Method: splitAndConvertIntegers(final String source, int numOfFields)
	 */
	@Test
	public void testSplitAndConvertIntegers02()
	{
		boolean ok;
		int[] nums = new int[1];
		try
		{
			ok = true;
			nums = TermUtilities.splitAndConvertIntegers("Test:3.57", "3.57", 2);
		}
		catch (NumberFormatException | TermEvaulatorException e)
		{
			ok = false;
		}

		assertTrue(ok, "two ints is ok");
		assertArrayEquals(nums, new int[]{3, 57});
	}

	/**
	 * Method: splitAndConvertIntegers(final String source, int numOfFields)
	 */
	@Test
	public void testSplitAndConvertIntegers03()
	{
		boolean ok;
		int[] nums = new int[1];
		try
		{
			ok = true;
			nums = TermUtilities.splitAndConvertIntegers("Test.3.57.67", "3.57.67", 3);
		}
		catch (NumberFormatException | TermEvaulatorException e)
		{
			ok = false;
		}

		assertTrue(ok, "three ints is ok");
		assertArrayEquals(nums, new int[]{3, 57, 67});
	}

	/**
	 * Method: splitAndConvertIntegers(final String source, int numOfFields)
	 */
	@Test
	public void testSplitAndConvertIntegers04()
	{
		boolean ok;
		try
		{
			ok = true;
			TermUtilities.splitAndConvertIntegers(
				"Test.3.57.67.foo", "3.57.67.foo", 3);
		}
		catch (NumberFormatException | TermEvaulatorException e)
		{
			ok = false;
		}

		assertFalse(ok, "three ints plus spurious non-int fails");
	}

	/**
	 * Method: extractContentsOfBrackets(String expressionString, String src, int fixed)
	 */
	@Test
	public void testExtractContentsOfBrackets01()
	{
		String inside = "";

		boolean ok;
		try
		{
			ok = true;
			String orig = "COUNT[MARSHMALLOWS.FOO]";
			int length = orig.indexOf('[');
			inside = TermUtilities.extractContentsOfBrackets(orig, "CLASS:Foo Bar", length + 1);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}

		assertTrue(ok, "Extracts Text correctly");
		assertEquals("MARSHMALLOWS.FOO", inside, "Text is correct ExtractContentsOfBrackets01");
	}

	/**
	 * Method: extractContentsOfBrackets(String expressionString, String src, int fixed)
	 */
	@Test
	public void testExtractContentsOfBrackets02()
	{

		boolean ok;
		try
		{
			ok = true;
			String orig = "COUNT[MARSHMALLOWS.FOO";
			int length = orig.indexOf('[');
			TermUtilities.extractContentsOfBrackets(orig, "CLASS:Foo Bar", length + 1);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}

		assertFalse(ok, "Fail, no ] found");
	}

	/**
	 * Method: extractContentsOfBrackets(String expressionString, String src, int fixed)
	 */
	@Test
	public void testExtractContentsOfBrackets03()
	{

		boolean ok;
		try
		{
			ok = true;
			String orig = "COUNT[MARSHMALLOWS.FOO]B";
			int length = orig.indexOf('[');
			TermUtilities.extractContentsOfBrackets(orig, "CLASS:Foo Bar", length + 1);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}

		assertFalse(ok, "Fail, ] not the last char");
	}


	/**
	 * Method: splitAndConvertIntegers(final String source, int numOfFields)
	 */
	@Test
	public void testConvertToIntegers01()
	{
		boolean ok;
		int[] nums = new int[1];
		try
		{
			ok = true;
			String orig = "COUNT[MARSHMALLOWS.1]";
			nums = TermUtilities.convertToIntegers(orig, "1", 6, 1);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		assertTrue(ok, "ConvertToIntegers: one int is ok");
		assertEquals(1, nums[0], "ConvertToIntegers: one int is ok - first");
	}

	/**
	 * Method: splitAndConvertIntegers(final String source, int numOfFields)
	 */
	@Test
	public void testConvertToIntegers02()
	{
		boolean ok;
		int[] nums = new int[1];
		try
		{
			ok = true;
			String orig = "COUNT[MARSHMALLOWS.1.2]";
			nums = TermUtilities.convertToIntegers(orig, "1.2", 6, 2);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		assertTrue(ok, "ConvertToIntegers: two ints is ok");
		assertArrayEquals(nums, new int[] {1, 2});
	}

	/**
	 * Method: splitAndConvertIntegers(final String source, int numOfFields)
	 */
	@Test
	public void testConvertToIntegers03()
	{
		boolean ok;
		int[] nums = new int[1];
		try
		{
			ok = true;
			String orig = "COUNT[MARSHMALLOWS.1.2.3]";
			nums = TermUtilities.convertToIntegers(orig, "1.2.3", 6, 3);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		assertTrue(ok, "ConvertToIntegers: three ints is ok");
		assertArrayEquals(nums, new int[] {1, 2, 3});
	}

	/**
	 * Method: splitAndConvertIntegers(final String source, int numOfFields)
	 */
	@Test
	public void testConvertToIntegers04()
	{
		boolean ok;
		try
		{
			ok = true;
			String orig = "COUNT[MARSHMALLOWS.1.2.3.foo]";
			TermUtilities.convertToIntegers(orig, "1.2.3.foo", 6, 4);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		assertFalse(ok, "ConvertToIntegers: three ints plus spurious non-int fails");
	}

	/**
	 * Method: splitAndConvertIntegers(final String source, int numOfFields)
	 */
	@Test
	public void testConvertToIntegers05()
	{
		boolean ok;
		try
		{
			ok = true;
			String orig = "COUNT[MARSHMALLOWS.1.2.3.4]";
			TermUtilities.convertToIntegers(orig, "1.2.3.4", 6, 3);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		assertFalse(ok, "ConvertToIntegers: ask for three with four present fails");
	}


}
