/*
 * Copyright (c) 2020 Tom Parker <thpr@users.sourceforge.net>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen;

import pcgen.base.format.ArrayFormatManager;
import pcgen.base.format.dice.Dice;
import pcgen.base.format.dice.DiceFormat;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.util.FormatManager;
import pcgen.cdom.base.CDOMObject;

import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

/**
 * Stores common elements used across many PCGen tests.
 */
public class TestConstants
{

	public static final CDOMTokenLoader<CDOMObject> TOKEN_LOADER = new CDOMTokenLoader<>();

	public static final ChooseLst CHOOSE_TOKEN = new ChooseLst();

	public static final Integer I1 = 1;

	public static final Integer I2 = 2;

	public static final Integer I3 = 3;

	public static final FormatManager<Dice> DICE_MANAGER = new DiceFormat();

	public static final FormatManager<Number[]> NUMBER_ARR_FORMAT =
			new ArrayFormatManager<>(FormatUtilities.NUMBER_MANAGER, '\n', ',');

}
