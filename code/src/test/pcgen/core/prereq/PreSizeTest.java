/**
 * Copyright 2006 (C) Andrew Wilson <nuance@sourceforge.net>
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
 * $Revision$
 * $Date$
 * $Time$
 * 
 * $id$
 */
package pcgen.core.prereq;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.TestHelper;

public class PreSizeTest extends AbstractCharacterTestCase
{
	Race race = new Race();
	Equipment eq1;
	Equipment eq2;
	Equipment eq3;

    @Override
	protected void setUp() throws Exception
	{
		super.setUp();

		final PlayerCharacter character = getCharacter();

		TestHelper.makeEquipment("Item One\tTYPE:Goods.Magic\tSIZE:S");
		TestHelper.makeEquipment("Item Two\tTYPE:Goods.General\tSIZE:M");
		TestHelper
			.makeEquipment("Item Three\tTYPE:Weapon.Melee.Finesseable.Simple.Standard.Piercing.Dagger:\tSIZE:L");

		eq1 = EquipmentList.getEquipmentFromName("Item One", character);
		eq2 = EquipmentList.getEquipmentFromName("Item Two", character);
		eq3 = EquipmentList.getEquipmentFromName("Item Three", character);
	}

	/*
	 * @see AbstractCharacterTestCase#tearDown()
	 */
    @Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public static Test suite()
	{
		return new TestSuite(PreSizeTest.class);
	}

	public void testEquipmentPreSize() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		Globals.getContext().getReferenceContext().resolveReferences(null);

		is(eq1.sizeInt(), eq(3), "Item one is expected size");
		is(eq2.sizeInt(), eq(4), "Item two is expected size");
		is(eq3.sizeInt(), eq(5), "Item three is expected size");

		Prerequisite prereq;

		final PreParserFactory factory = PreParserFactory.getInstance();

		prereq = factory.parse("PRESIZEEQ:L");

		is(PrereqHandler.passes(prereq, eq1, character), eq(false),
			"Item one is not Large");
		is(PrereqHandler.passes(prereq, eq2, character), eq(false),
			"Item two is not Large");
		is(PrereqHandler.passes(prereq, eq3, character), eq(true),
			"Item three Large");

		prereq = factory.parse("PRESIZEGT:S");

		is(PrereqHandler.passes(prereq, eq1, character), eq(false),
			"Item one is not larger than Small");
		is(PrereqHandler.passes(prereq, eq2, character), eq(true),
			"Item two is larger than Small");
		is(PrereqHandler.passes(prereq, eq3, character), eq(true),
			"Item three larger than Small");
	}
}
