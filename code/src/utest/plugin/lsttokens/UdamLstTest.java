/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UdamLstTest extends AbstractGlobalTokenTestCase
{
    static UdamLst token = new UdamLst();
    static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

    protected SizeAdjustment colossal;
    protected SizeAdjustment gargantuan;
    protected SizeAdjustment huge;
    protected SizeAdjustment large;
    protected SizeAdjustment medium;
    protected SizeAdjustment small;
    protected SizeAdjustment tiny;
    protected SizeAdjustment diminutive;
    protected SizeAdjustment fine;

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        fine = BuildUtilities.createSize("Fine", 0);
        primaryContext.getReferenceContext().importObject(fine);
        secondaryContext.getReferenceContext().importObject(fine);

        diminutive = BuildUtilities.createSize("Diminutive", 1);
        primaryContext.getReferenceContext().importObject(diminutive);
        secondaryContext.getReferenceContext().importObject(diminutive);

        tiny = BuildUtilities.createSize("Tiny", 2);
        primaryContext.getReferenceContext().importObject(tiny);
        secondaryContext.getReferenceContext().importObject(tiny);

        small = BuildUtilities.createSize("Small", 3);
        primaryContext.getReferenceContext().importObject(small);
        secondaryContext.getReferenceContext().importObject(small);

        medium = BuildUtilities.createSize("Medium", 4);
        medium.put(ObjectKey.IS_DEFAULT_SIZE, true);
        primaryContext.getReferenceContext().importObject(medium);
        secondaryContext.getReferenceContext().importObject(medium);

        large = BuildUtilities.createSize("Large", 5);
        primaryContext.getReferenceContext().importObject(large);
        secondaryContext.getReferenceContext().importObject(large);

        huge = BuildUtilities.createSize("Huge", 6);
        primaryContext.getReferenceContext().importObject(huge);
        secondaryContext.getReferenceContext().importObject(huge);

        gargantuan = BuildUtilities.createSize("Gargantuan", 7);
        primaryContext.getReferenceContext().importObject(gargantuan);
        secondaryContext.getReferenceContext().importObject(gargantuan);

        colossal = BuildUtilities.createSize("Colossal", 8);
        primaryContext.getReferenceContext().importObject(colossal);
        secondaryContext.getReferenceContext().importObject(colossal);
    }

    @Override
    public CDOMLoader<PCTemplate> getLoader()
    {
        return loader;
    }

    @Override
    public Class<PCTemplate> getCDOMClass()
    {
        return PCTemplate.class;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getReadToken()
    {
        return token;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getWriteToken()
    {
        return token;
    }

    @Test
    public void testInvalidNotEnoughValues()
    {
        assertTrue(parse("1,2,3,4,5,6,7,8"));
        assertFalse(token.process(primaryContext, primaryProf));
    }

    @Test
    public void testInvalidTooManyValues()
    {
        assertTrue(parse("1,2,3,4,5,6,7,8,9,0"));
        assertFalse(token.process(primaryContext, primaryProf));
    }

    @Test
    public void testInvalidEmptyValue1()
    {
        assertFalse(parse(",2,3,4,5,6,7,8,9"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyValue2()
    {
        assertFalse(parse("1,,3,4,5,6,7,8,9"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyValue3()
    {
        assertFalse(parse("1,2,,4,5,6,7,8,9"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyValue4()
    {
        assertFalse(parse("1,2,3,,5,6,7,8,9"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyValue5()
    {
        assertFalse(parse("1,2,3,4,,6,7,8,9"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyValue6()
    {
        assertFalse(parse("1,2,3,4,5,,7,8,9"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyValue7()
    {
        assertFalse(parse("1,2,3,4,5,6,,8,9"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyValue8()
    {
        assertFalse(parse("1,2,3,4,5,6,7,,9"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyValue9()
    {
        assertFalse(parse("1,2,3,4,5,6,7,8,"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        this.runRoundRobin("1,2,3,4,5,6,7,8,9");
    }

    @Test
    public void testRoundRobinComplex() throws PersistenceLayerException
    {
        this.runRoundRobin("1,2,3,4*form,5*form,6,7*form,8,9");
    }

    @Override
    protected String getLegalValue()
    {
        return "1,2,3,4,5,6,7,8,9";
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "1,2,3,4*form,5*form,6,7*form,8,9";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }
}
