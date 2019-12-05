/**
 * Copyright 2006 (C) Andrew Wilson <nuance@sourceforge.net>
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
 * <p>
 * $Revision$
 * $Date$
 * $Time$
 * <p>
 * $id$
 */
package pcgen.core.prereq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PreSizeTest extends AbstractCharacterTestCase
{
    Race race = new Race();
    Equipment eq1;
    Equipment eq2;
    Equipment eq3;

    @BeforeEach
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

    @Test
    public void testEquipmentPreSize() throws Exception
    {
        final PlayerCharacter character = getCharacter();
        Globals.getContext().getReferenceContext().resolveReferences(null);

        assertEquals("Item one is expected size",
                3,
                eq1.sizeInt());
        assertEquals("Item two is expected size",
                4,
                eq2.sizeInt());
        assertEquals("Item three is expected size",
                5,
                eq3.sizeInt());

        Prerequisite prereq;

        final PreParserFactory factory = PreParserFactory.getInstance();

        prereq = factory.parse("PRESIZEEQ:L");

        assertFalse("Item one is not Large", PrereqHandler.passes(prereq, eq1, character));
        assertFalse("Item two is not Large", PrereqHandler.passes(prereq, eq2, character));
        assertTrue("Item three Large", PrereqHandler.passes(prereq, eq3, character));

        prereq = factory.parse("PRESIZEGT:S");

        assertFalse("Item one is not larger than Small", PrereqHandler.passes(prereq, eq1, character));
        assertTrue("Item two is larger than Small", PrereqHandler.passes(prereq, eq2, character));
        assertTrue("Item three larger than Small", PrereqHandler.passes(prereq, eq3, character));
    }
}
