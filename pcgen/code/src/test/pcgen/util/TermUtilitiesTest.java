package pcgen.util;

import pcgen.PCGenTestCase;
import pcgen.core.term.TermEvaulatorException;

/**
 * TermUtilities Tester.
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
 *
 * Created 10/04/2008
 */

public class TermUtilitiesTest extends PCGenTestCase
{

    public TermUtilitiesTest(String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

	/**
	 * Method: checkEqTypeTypesArray(String originalText, String[] types, int first)
	 */
	public void testcheckEqTypeTypesArray01() {
		boolean ok;
		try
		{
			ok = true;
			String[] types = new String[]{"EQUIPPED"};
			TermUtilities.checkEqTypeTypesArray("COUNT[EQTYPE.EQUIPPED]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		is(ok, eq(true), "EqtypesTypesArray01 Single Type EQUIPPED");
	}

	/**
	 * Method: checkEqTypeTypesArray(String originalText, String[] types, int first)
	 */
	public void testcheckEqTypeTypesArray02() {
		boolean ok;
		try
		{
			ok = true;
			String[] types = new String[]{"NOTEQUIPPED"};
			TermUtilities.checkEqTypeTypesArray("COUNT[EQTYPE.NOTEQUIPPED]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		is(ok, eq(true), "EqtypesTypesArray02 Single Type NOTEQUIPPED");
	}

	/**
	 * Method: checkEqTypeTypesArray(String originalText, String[] types, int first)
	 */
	public void testcheckEqTypeTypesArray03() {
		boolean ok;
		try
		{
			ok = true;
			String[] types = new String[]{"EQUIPPED", "FOO"};
			TermUtilities.checkEqTypeTypesArray("COUNT[EQTYPE.EQUIPPED.FOO]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		is(ok, eq(false), "EqtypesTypesArray03 EQUIPPED with spurious type");
	}

	/**
	 * Method: checkEqTypeTypesArray(String originalText, String[] types, int first)
	 */
	public void testcheckEqTypeTypesArray04() {
		boolean ok;
		try
		{
			ok = true;
			String[] types = new String[]{"BAR", "NOT", "FOO"};
			TermUtilities.checkEqTypeTypesArray("COUNT[EQTYPE.BAR.NOT.FOO]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		is(ok, eq(true), "EqtypesTypesArray04 Exclude FOO");
	}

	/**
	 * Method: checkEqTypeTypesArray(String originalText, String[] types, int first)
	 */
	public void testcheckEqTypeTypesArray05() {
		boolean ok;
		try
		{
			ok = true;
			String[] types = new String[]{"BAR", "ADD", "FOO"};
			TermUtilities.checkEqTypeTypesArray("COUNT[EQTYPE.BAR.ADD.FOO]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		is(ok, eq(true), "EqtypesTypesArray05 Include FOO");
	}

	/**
	 * Method: checkEqTypeTypesArray(String originalText, String[] types, int first)
	 */
	public void testcheckEqTypeTypesArray06() {
		boolean ok;
		try
		{
			ok = true;
			String[] types = new String[]{"BAR", "IS", "FOO"};
			TermUtilities.checkEqTypeTypesArray("COUNT[EQTYPE.BAR.IS.FOO]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		is(ok, eq(true), "EqtypesTypesArray06 Only FOO");
	}

	/**
	 * Method: checkEqTypeTypesArray(String originalText, String[] types, int first)
	 */
	public void testcheckEqTypeTypesArray07() {
		boolean ok;
		try
		{
			ok = true;
			String[] types = new String[]{"QUX", "NOT", "FOO", "ADD", "BAR", "IS", "BAZ"};
			TermUtilities.checkEqTypeTypesArray("COUNT[EQTYPE.QUX.NOT.FOO.ADD.BAR.IS.BAZ]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		is(ok, eq(true), "EqtypesTypesArray07 All options");
	}

	/**
	 * Method: checkEqTypeTypesArray(String originalText, String[] types, int first)
	 */
	public void testcheckEqTypeTypesArray08() {
		boolean ok;
		try
		{
			ok = true;
			String[] types = new String[]{"QUUX", "NOT", "FOO", "ADD", "BAR", "IS", "BAZ", "QUX"};
			TermUtilities.checkEqTypeTypesArray("COUNT[EQTYPE.QUUX.NOT.FOO.ADD.BAR.IS.BAZ.QUX]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		is(ok, eq(false), "EqtypesTypesArray08 All options with spurious");
	}

	/**
	 * Method: checkEquipmentTypesArray(String originalText, String[] types, int first)
	 */
	public void testcheckEquipmentTypesArray01() {
		boolean ok;
		try
		{
			ok = true;
			String[] types = new String[]{"EQUIPPED"};
			TermUtilities.checkEquipmentTypesArray("COUNT[EQUIPMENT.EQUIPPED]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		is(ok, eq(false), "EquipmentTypesArray01 Single Type EQUIPPED");
	}

	/**
	 * Method: checkEquipmentTypesArray(String originalText, String[] types, int first)
	 */
	public void testcheckEquipmentTypesArray02() {
		boolean ok;
		try
		{
			ok = true;
			String[] types = new String[]{"NOTEQUIPPED"};
			TermUtilities.checkEquipmentTypesArray("COUNT[EQUIPMENT.NOTEQUIPPED]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		is(ok, eq(false), "EquipmentTypesArray02 Single Type NOTEQUIPPED");
	}

	/**
	 * Method: checkEquipmentTypesArray(String originalText, String[] types, int first)
	 */
	public void testcheckEquipmentTypesArray03() {
		boolean ok;
		try
		{
			ok = true;
			String[] types = new String[]{"EQUIPPED", "FOO"};
			TermUtilities.checkEquipmentTypesArray("COUNT[EQUIPMENT.EQUIPPED.FOO]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		is(ok, eq(false), "EquipmentTypesArray03 EQUIPPED with spurious type");
	}

	/**
	 * Method: checkEquipmentTypesArray(String originalText, String[] types, int first)
	 */
	public void testcheckEquipmentTypesArray04() {
		boolean ok;
		try
		{
			ok = true;
			String[] types = new String[]{"NOT", "FOO"};
			TermUtilities.checkEquipmentTypesArray("COUNT[EQUIPMENT.NOT.FOO]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		is(ok, eq(true), "EquipmentTypesArray04 Exclude FOO");
	}

	/**
	 * Method: checkEquipmentTypesArray(String originalText, String[] types, int first)
	 */
	public void testcheckEquipmentTypesArray05() {
		boolean ok;
		try
		{
			ok = true;
			String[] types = new String[]{"ADD", "FOO"};
			TermUtilities.checkEquipmentTypesArray("COUNT[EQUIPMENT.ADD.FOO]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		is(ok, eq(true), "EquipmentTypesArray05 Include FOO");
	}

	/**
	 * Method: checkEquipmentTypesArray(String originalText, String[] types, int first)
	 */
	public void testcheckEquipmentTypesArray06() {
		boolean ok;
		try
		{
			ok = true;
			String[] types = new String[]{"IS", "FOO"};
			TermUtilities.checkEquipmentTypesArray("COUNT[EQUIPMENT.IS.FOO]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		is(ok, eq(true), "EquipmentTypesArray06 Only FOO");
	}

	/**
	 * Method: checkEquipmentTypesArray(String originalText, String[] types, int first)
	 */
	public void testcheckEquipmentTypesArray07() {
		boolean ok;
		try
		{
			ok = true;
			String[] types = new String[]{"NOT", "FOO", "ADD", "BAR", "IS", "BAZ"};
			TermUtilities.checkEquipmentTypesArray("COUNT[EQUIPMENT.NOT.FOO.ADD.BAR.IS.BAZ]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		is(ok, eq(true), "EquipmentTypesArray07 All options");
	}

	/**
	 * Method: checkEquipmentTypesArray(String originalText, String[] types, int first)
	 */
	public void testcheckEquipmentTypesArray08() {
		boolean ok;
		try
		{
			ok = true;
			String[] types = new String[]{"NOT", "FOO", "ADD", "BAR", "IS", "BAZ", "QUX"};
			TermUtilities.checkEquipmentTypesArray("COUNT[EQUIPMENT.NOT.FOO.ADD.BAR.IS.BAZ.QUX]", types, 0);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		is(ok, eq(false), "EquipmentTypesArray08 All options with spurious");
	}


	/**
	 * Method: splitAndConvertIntegers(final String source, int numOfFields)
	 */
	public void testSplitAndConvertIntegers01() {
		boolean ok;
		int[] nums = new int[1];
		try
		{
			ok = true;
			nums = TermUtilities.splitAndConvertIntegers("Test:3", "3", 1);
		}
		catch (NumberFormatException e)
		{
			ok = false;
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		is(ok, eq(true), "one int is ok");
		is(nums[0], eq(3), "one int is ok:first");
	}

	/**
	 * Method: splitAndConvertIntegers(final String source, int numOfFields)
	 */
	public void testSplitAndConvertIntegers02() {
		boolean ok;
		int[] nums = new int[1];
		try
		{
			ok = true;
			nums = TermUtilities.splitAndConvertIntegers("Test:3.57", "3.57", 2);
		}
		catch (NumberFormatException e)
		{
			ok = false;
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}
		
		is(ok, eq(true), "two ints is ok");
		is(nums[0], eq(3), "two ints is ok:first");
		is(nums[1], eq(57), "two ints is ok:second");
	}

	/**
	 * Method: splitAndConvertIntegers(final String source, int numOfFields)
	 */
	public void testSplitAndConvertIntegers03() {
		boolean ok;
		int[] nums = new int[1];
		try
		{
			ok = true;
			nums = TermUtilities.splitAndConvertIntegers("Test.3.57.67", "3.57.67", 3);
		}
		catch (NumberFormatException e)
		{
			ok = false;
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}

		is(ok, eq(true), "three ints is ok");
		is(nums[0], eq(3), "three ints is ok:first");
		is(nums[1], eq(57), "three ints is ok:second");
		is(nums[2], eq(67), "three ints is ok:second");
	}

	/**
	 * Method: splitAndConvertIntegers(final String source, int numOfFields)
	 */
	public void testSplitAndConvertIntegers04() {
		boolean ok;
		try
		{
			ok = true;
			TermUtilities.splitAndConvertIntegers(
					"Test.3.57.67.foo", "3.57.67.foo", 3);
		}
		catch (NumberFormatException e)
		{
			ok = false;
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}

		is(ok, eq(false), "three ints plus spurious non-int fails");
	}

	/**
	 * Method: extractContentsOfBrackets(String expressionString, String src, int fixed)
	 */
	public void testExtractContentsOfBrackets01() {
		String orig = "COUNT[MARSHMALLOWS.FOO]";
		String inside = "";
		int length = orig.indexOf('[');

		boolean ok;
		try
		{
			ok = true;
			inside = TermUtilities.extractContentsOfBrackets(orig, "CLASS:Foo Bar", length + 1);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}

		is(ok, eq(true), "Extracts Text correctly");		
		is(inside, strEq("MARSHMALLOWS.FOO"), "Text is correct ExtractContentsOfBrackets01");
	}

	/**
	 * Method: extractContentsOfBrackets(String expressionString, String src, int fixed)
	 */
	public void testExtractContentsOfBrackets02() {
		String orig = "COUNT[MARSHMALLOWS.FOO";
		int length = orig.indexOf('[');

		boolean ok;
		try
		{
			ok = true;
			TermUtilities.extractContentsOfBrackets(orig, "CLASS:Foo Bar", length + 1);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}

		is(ok, eq(false), "Fail, no ] found");		
	}

	/**
	 * Method: extractContentsOfBrackets(String expressionString, String src, int fixed)
	 */
	public void testExtractContentsOfBrackets03() {
		String orig = "COUNT[MARSHMALLOWS.FOO]B";
		int length = orig.indexOf('[');

		boolean ok;
		try
		{
			ok = true;
			TermUtilities.extractContentsOfBrackets(orig, "CLASS:Foo Bar", length + 1);
		}
		catch (TermEvaulatorException e)
		{
			ok = false;
		}

		is(ok, eq(false), "Fail, ] not the last char");		
	}


	/**
	 * Method: splitAndConvertIntegers(final String source, int numOfFields)
	 */
	public void testConvertToIntegers01() {
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
		is(ok, eq(true), "ConvertToIntegers: one int is ok");
		is(nums[0], eq(1), "ConvertToIntegers: one int is ok - first");
	}

	/**
	 * Method: splitAndConvertIntegers(final String source, int numOfFields)
	 */
	public void testConvertToIntegers02() {
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
		is(ok, eq(true), "ConvertToIntegers: two ints is ok");
		is(nums[0], eq(1), "ConvertToIntegers: two ints is ok:first");
		is(nums[1], eq(2), "ConvertToIntegers: two ints is ok:second");
	}

	/**
	 * Method: splitAndConvertIntegers(final String source, int numOfFields)
	 */
	public void testConvertToIntegers03() {
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
		is(ok, eq(true), "ConvertToIntegers: three ints is ok");
		is(nums[0], eq(1), "ConvertToIntegers: three ints is ok:first");
		is(nums[1], eq(2), "ConvertToIntegers: three ints is ok:second");
		is(nums[2], eq(3), "ConvertToIntegers: three ints is ok:third");
	}

	/**
	 * Method: splitAndConvertIntegers(final String source, int numOfFields)
	 */
	public void testConvertToIntegers04() {
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
		is(ok, eq(false), "ConvertToIntegers: three ints plus spurious non-int fails");
	}

	/**
	 * Method: splitAndConvertIntegers(final String source, int numOfFields)
	 */
	public void testConvertToIntegers05() {
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
		is(ok, eq(false), "ConvertToIntegers: ask for three with four present fails");
	}


}
