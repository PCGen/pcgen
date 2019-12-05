/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
package pcgen.cdom.facet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.DamageReduction;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.base.AbstractSourcedListFacet;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.testsupport.AbstractExtractingFacetTest;
import pcgen.core.PCTemplate;
import pcgen.core.Race;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class tests the handling of DRs in PCGen
 */
@SuppressWarnings("nls")
public class DamageReductionFacetTest extends
        AbstractExtractingFacetTest<CDOMObject, DamageReduction>
{
    private final DamageReductionFacet facet = new DamageReductionFacet();
    private DamageReduction[] target;
    private CDOMObject[] source;

    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        CDOMObject cdo1 = new PCTemplate();
        cdo1.setName("Templ");
        CDOMObject cdo2 = new Race();
        cdo2.setName("Race");
        DamageReduction dr1 =
                new DamageReduction(FormulaFactory.getFormulaFor(4), "good");
        DamageReduction dr2 =
                new DamageReduction(FormulaFactory.getFormulaFor(2), "bad");
        cdo1.addToListFor(ListKey.DAMAGE_REDUCTION, dr1);
        cdo2.addToListFor(ListKey.DAMAGE_REDUCTION, dr2);
        source = new CDOMObject[]{cdo1, cdo2};
        target = new DamageReduction[]{dr1, dr2};
    }

    /**
     * Test the retrieval of the DR String
     */
    @Test
    public void testGetDRString()
    {
        DamageReductionFacet drFacet = new DamageReductionFacet();
        drFacet.setPrerequisiteFacet(new PrerequisiteFacet());
        drFacet.setFormulaResolvingFacet(new FormulaResolvingFacet());
        drFacet.setBonusCheckingFacet(new BonusCheckingFacet()
        {
            @Override
            public double getBonus(CharID charID, String bonusType, String bonusName)
            {
                return 0.0d;
            }
        });

        Map<DamageReduction, Set<Object>> drList =
                new IdentityHashMap<>();
        String listResult = drFacet.getDRString(id, drList);
        assertEquals("", listResult);
        Set<Object> sourceSet = new HashSet<>();
        sourceSet.add(new Object());

        drList.put(new DamageReduction(FormulaFactory.getFormulaFor(10),
                "magic"), sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("10/magic"));

        drList.put(
                new DamageReduction(FormulaFactory.getFormulaFor(10), "good"),
                sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("10/good and magic"));

        drList.put(
                new DamageReduction(FormulaFactory.getFormulaFor(10), "good"),
                sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("10/good and magic"));

        drList.put(
                new DamageReduction(FormulaFactory.getFormulaFor(5), "good"),
                sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("10/good and magic"));

        drList.put(new DamageReduction(FormulaFactory.getFormulaFor(10),
                "magic"), sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("10/good and magic"));

        drList.put(
                new DamageReduction(FormulaFactory.getFormulaFor(5), "good"),
                sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("10/good and magic"));

        drList.put(
                new DamageReduction(FormulaFactory.getFormulaFor(15), "Good"),
                sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("15/Good; 10/magic"));

        drList.put(
                new DamageReduction(FormulaFactory.getFormulaFor(10), "good"),
                sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("15/Good; 10/magic"));

        drList.put(new DamageReduction(FormulaFactory.getFormulaFor(10),
                "magic"), sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("15/Good; 10/magic"));

        drList.put(
                new DamageReduction(FormulaFactory.getFormulaFor(5), "good"),
                sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("15/Good; 10/magic"));

        drList.put(new DamageReduction(FormulaFactory.getFormulaFor(10),
                "magic and good"), sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("15/Good; 10/magic"));

        drList.put(
                new DamageReduction(FormulaFactory.getFormulaFor(5), "evil"),
                sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("15/Good; 10/magic; 5/evil"));

        drList.put(new DamageReduction(FormulaFactory.getFormulaFor(10),
                "magic or good"), sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("15/Good; 10/magic; 5/evil"));

        drList.put(
                new DamageReduction(FormulaFactory.getFormulaFor(10), "good"),
                sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("15/Good; 10/magic; 5/evil"));

        drList.put(new DamageReduction(FormulaFactory.getFormulaFor(10),
                "magic or good"), sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("15/Good; 10/magic; 5/evil"));

        drList.put(
                new DamageReduction(FormulaFactory.getFormulaFor(5), "good"),
                sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("15/Good; 10/magic; 5/evil"));

        drList.put(new DamageReduction(FormulaFactory.getFormulaFor(10),
                "magic and good"), sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("15/Good; 10/magic; 5/evil"));

        drList.put(new DamageReduction(FormulaFactory.getFormulaFor(5),
                "magic and good"), sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("15/Good; 10/magic; 5/evil"));

        drList.put(new DamageReduction(FormulaFactory.getFormulaFor(10),
                "magic or good"), sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("15/Good; 10/magic; 5/evil"));

        drList.put(new DamageReduction(FormulaFactory.getFormulaFor(5),
                "magic and good"), sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("15/Good; 10/magic; 5/evil"));

        drList.put(new DamageReduction(FormulaFactory.getFormulaFor(10),
                "magic or good"), sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("15/good; 10/magic; 5/evil"));

        drList.put(new DamageReduction(FormulaFactory.getFormulaFor(15),
                "magic"), sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("15/good and magic; 5/evil"));

        drList.put(new DamageReduction(FormulaFactory.getFormulaFor(10),
                "magic or good"), sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("15/good and magic; 5/evil"));

        drList.put(new DamageReduction(FormulaFactory.getFormulaFor(15),
                "magic and good"), sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("15/good and magic; 5/evil"));

        drList.put(new DamageReduction(FormulaFactory.getFormulaFor(10),
                "magic or lawful"), sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("15/good and magic; 5/evil"));

        drList.put(new DamageReduction(FormulaFactory.getFormulaFor(15),
                "magic and good"), sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("15/good and magic; 5/evil"));

        drList.put(new DamageReduction(FormulaFactory.getFormulaFor(10),
                "magic and good"), sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult.equalsIgnoreCase("15/good and magic; 5/evil"));

        drList.put(new DamageReduction(FormulaFactory.getFormulaFor(10),
                "magic and lawful"), sourceSet);
        listResult = drFacet.getDRString(id, drList);
        assertTrue(listResult
                .equalsIgnoreCase("15/good and magic; 10/lawful; 5/evil"));

        Map<DamageReduction, Set<Object>> drList1 =
                new IdentityHashMap<>();
        drList1.put(new DamageReduction(FormulaFactory.getFormulaFor(10),
                "epic"), sourceSet);
        drList1.put(new DamageReduction(FormulaFactory.getFormulaFor(10),
                "lawful or good"), sourceSet);
        listResult = drFacet.getDRString(id, drList1);
        assertTrue(listResult.equalsIgnoreCase("10/epic; 10/lawful or good"));

        drList1.clear();
        drList1.put(new DamageReduction(FormulaFactory.getFormulaFor(10),
                "epic and good or epic and lawful"), sourceSet);
        listResult = drFacet.getDRString(id, drList1);
        assertTrue(listResult
                .equalsIgnoreCase("10/epic and good or epic and lawful"));

        // TODO Better consolidation: Can't handle this case at the moment.
        // drList1.add(new DamageReduction(FormulaFactory.getFormulaFor(10),
        // "lawful"));
        // listResult = drFacet.getDRString(null, drList1);
        // System.out.println("DR List: " + drList1.toString() + " = " +
        // listResult);
        // assertTrue(listResult.equalsIgnoreCase("10/epic and lawful");
    }

    @Override
    protected CDOMObject getContainingObject(int i)
    {
        return source[i];
    }

    @Override
    protected DataFacetChangeListener<CharID, CDOMObject> getListener()
    {
        return facet;
    }

    @Override
    protected DamageReduction getTargetObject(int i)
    {
        return target[i];
    }

    @Override
    protected AbstractSourcedListFacet<CharID, DamageReduction> getFacet()
    {
        return facet;
    }

    public static int n = 0;

    @Override
    protected DamageReduction getObject()
    {
        return new DamageReduction(FormulaFactory.getFormulaFor(4), "good" + n);
    }
}
