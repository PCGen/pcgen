/*
 * Copyright (c) 2012 Tom Parker <thpr@users.sourceforge.net>
 *
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
package tokenmodel.testsupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.list.CompanionList;
import pcgen.core.Campaign;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.PCCheck;
import pcgen.core.PCStat;
import pcgen.core.character.CompanionMod;
import pcgen.output.channel.compat.AlignmentCompat;

import org.junit.jupiter.api.Test;

public abstract class AbstractGrantedListTokenTest<T extends CDOMObject>
        extends AbstractAddListTokenTest<T>
{
    @Test
    public void testFromAlignment()
    {
        T granted = createGrantedObject();
        processToken(lg);
        assertEquals(0, getCount());
        AlignmentCompat.setCurrentAlignment(pc.getCharID(), lg);
        assertTrue(containsExpected(granted));
        assertEquals(1, getCount());
        AlignmentCompat.setCurrentAlignment(pc.getCharID(), ng);
        assertEquals(0, getCount());
        assertTrue(cleanedSideEffects());
    }

    //BioSet not *supposed* to do things like this

    @Test
    public void testFromCampaign()
    {
        Campaign source = create(Campaign.class, "Source");
        T granted = createGrantedObject();
        processToken(source);
        assertEquals(0, getCount());
        expandedCampaignFacet.add(id, source, this);
        assertTrue(containsExpected(granted));
        assertEquals((expandedCampaignFacet == getTargetFacet()) ? 2 : 1,
                getCount());
        expandedCampaignFacet.remove(id, source, this);
        assertEquals(0, getCount());
        assertTrue(cleanedSideEffects());
    }

    @Test
    public void testFromCheck()
    {
        PCCheck source = create(PCCheck.class, "Source");
        T granted = createGrantedObject();
        processToken(source);
        /*
         * We never get a chance to test zero since the Checks are added at
         * Player Character Construction :)
         */
        assertTrue(containsExpected(granted));
        assertEquals(1, getCount());
        checkFacet.remove(id, source);
        assertEquals(0, getCount());
        assertTrue(cleanedSideEffects());
    }

    @Test
    public void testFromCompanionMod()
    {
        CompanionList cat = create(CompanionList.class, "Category");
        context.getReferenceContext().importObject(cat);
        CompanionMod source = cat.newInstance();
        cat.setName("Source");
        context.getReferenceContext().importObject(source);
        T granted = createGrantedObject();
        processToken(source);
        assertEquals(0, getCount());
        companionModFacet.add(id, source);
        assertTrue(containsExpected(granted));
        assertEquals(1, getCount());
        companionModFacet.remove(id, source);
        assertEquals(0, getCount());
        assertTrue(cleanedSideEffects());
    }

    @Test
    public void testFromEqMod()
    {
        EquipmentModifier source = create(EquipmentModifier.class, "Source");
        Equipment e = create(Equipment.class, "Parent");
        source.setVariableParent(e);
        T granted = createGrantedObject();
        processToken(source);
        assertEquals(0, getCount());
        activeEqModFacet.add(id, source, this);
        assertTrue(containsExpected(granted));
        assertEquals((activeEqModFacet == getTargetFacet()) ? 2 : 1, getCount());
        activeEqModFacet.remove(id, source, this);
        assertEquals(0, getCount());
        assertTrue(cleanedSideEffects());
    }

    //Language not *supposed* to do things like this

    //TODO SizeFacet is not a very good model for doing this by hand :(
    //Need to separate the setting of size from the facet that holds it

    //Skill not *supposed* to do things like this

    @Test
    public void testFromStat()
    {
        PCStat source = cha;
        T granted = createGrantedObject();
        processToken(source);
        /*
         * We never get a chance to test zero since the Stats are added at
         * Player Character Construction :)
         */
        assertTrue(containsExpected(granted));
        assertEquals(1, getCount());
        statFacet.remove(id, source);
        assertEquals(0, getCount());
        assertTrue(cleanedSideEffects());
    }

    //WeaponProf not *supposed* to do things like this

}
