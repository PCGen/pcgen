/*
 * Copyright (c) 2014-15 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.output.actor;

import java.math.BigDecimal;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCStat;
import pcgen.core.Race;
import pcgen.output.publish.OutputDB;
import pcgen.output.testsupport.AbstractOutputTestCase;
import pcgen.output.wrapper.CDOMObjectWrapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ObjectKeyActorTest extends AbstractOutputTestCase
{

    private static final RaceFacet DF = new RaceFacet();

    @BeforeAll
    public static void classSetUp()
    {
        OutputDB.reset();
        DF.init();
    }

    @Test
    public void testBasicObjectKeyActor()
    {
        Race d = new Race();
        d.setName("Bob");
        BigDecimal expectedResult = new BigDecimal("4.063");
        DF.set(id, d);
        d.put(ObjectKey.COST, expectedResult);
        ObjectKeyActor<BigDecimal> oka =
                new ObjectKeyActor<>(ObjectKey.COST);
        CDOMObjectWrapper.load(dsid, d.getClass(), "cost", oka);
        processThroughFreeMarker("${race.cost}", expectedResult.toString());
    }

    @Test
    public void testWrappedObjectKeyActor()
    {
        Race d = new Race();
        d.setName("Bob");
        PCStat str = new PCStat();
        str.setName("Strength");
        BigDecimal expectedResult = new BigDecimal("4.063");
        str.put(ObjectKey.COST, expectedResult);
        DF.set(id, d);
        d.put(ObjectKey.SPELL_STAT, CDOMDirectSingleRef.getRef(str));
        ObjectKeyActor<BigDecimal> oka_cost =
                new ObjectKeyActor<>(ObjectKey.COST);
        CDOMObjectWrapper.load(dsid, str.getClass(), "cost", oka_cost);
        ObjectKeyActor<CDOMSingleRef<PCStat>> oka_stat =
                new ObjectKeyActor<>(ObjectKey.SPELL_STAT);
        CDOMObjectWrapper.load(dsid, d.getClass(), "stat", oka_stat);
        processThroughFreeMarker("${race.stat}", str.getDisplayName());
        processThroughFreeMarker("${race.stat.cost}", expectedResult.toString());
    }

}
