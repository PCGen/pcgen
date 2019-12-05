/*
 * Copyright 2006 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code EquipmentListTest} checks the functionality of the EquipmentList class.
 */
public class EquipmentListTest
{

    private Equipment eq = null;
    private static final String ORIGINAL_KEY = "OrigKey";

    @BeforeAll
    public static void beforeClass()
    {
        TestHelper.makeSizeAdjustments();
    }

    @BeforeEach
    public void setUp()
    {
        this.eq = new Equipment();
        this.eq.setName("Dummy");
        SizeAdjustment sa = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(
                SizeAdjustment.class, "M");
        CDOMDirectSingleRef<SizeAdjustment> mediumRef = CDOMDirectSingleRef.getRef(sa);
        eq.put(ObjectKey.SIZE, mediumRef);
        eq.put(ObjectKey.BASESIZE, mediumRef);
        TestHelper.addType(eq, "WEAPON.MELEE.CHOCOLATE");

        this.eq.put(StringKey.KEY_NAME, ORIGINAL_KEY);
    }

    /**
     * test the getEquipmentOfType method
     */
    @Test
    public void testGetEquipmentOfType()
    {
        Globals.getContext().getReferenceContext().importObject(eq);

        List<Equipment> results =
                EquipmentList.getEquipmentOfType("Weapon.Melee", "Magic");
        assertThat("Should get a single result", results.size(), is(1));
        assertThat("Should find the DUmmy equipment object.", results
                .get(0), is(eq));
    }
}
