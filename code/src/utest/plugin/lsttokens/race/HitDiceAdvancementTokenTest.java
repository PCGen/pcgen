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
package plugin.lsttokens.race;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class HitDiceAdvancementTokenTest extends AbstractCDOMTokenTestCase<Race>
{

    static HitdiceadvancementToken token = new HitdiceadvancementToken();
    static CDOMTokenLoader<Race> loader = new CDOMTokenLoader<>();

    @Override
    public Class<Race> getCDOMClass()
    {
        return Race.class;
    }

    @Override
    public CDOMLoader<Race> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Race> getToken()
    {
        return token;
    }

    @Test
    public void testInvalidEmpty()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf, "").passed());
        assertNoSideEffects();
    }

    // @Test
    // public void testInvalidTooManyValues() throws PersistenceLayerException
    // {
    // assertFalse(token.parse(primaryContext, primaryProf,
    // "1,2,3,4,5,6,7,8,9,0"));
    // }

    @Test
    public void testInvalidEmptyValue1()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf, ",2,3,4").passed());
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyValue2()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf, "1,,3,4").passed());
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyValueLast()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf, "1,2,3,").passed());
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNegativeValue()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf, "-1,2,3").passed());
        assertNoSideEffects();
    }

    @Test
    public void testInvalidDecreasingValue()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf, "5,3,8").passed());
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmbeddedSplat()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf, "5,*,8").passed());
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNaN()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf, "5,N").passed());
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTooMuchSplat()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf, "5,8*").passed());
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTooMuchAfterSplat()
    {
        assertFalse(token.parseToken(primaryContext, primaryProf, "5,*8").passed());
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinSingle() throws PersistenceLayerException
    {
        this.runRoundRobin("1");
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        this.runRoundRobin("1,2,3");
    }

    @Test
    public void testRoundRobinComplex() throws PersistenceLayerException
    {
        this.runRoundRobin("5,7,9,*");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "1,2,3";
    }

    @Override
    protected String getLegalValue()
    {
        return "5,7,9,*";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    protected CDOMObject getUnparseTarget()
    {
        return primaryProf;
    }

    @Test
    public void testUnparseNull()
    {
        getUnparseTarget().removeListFor(getListKey());
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    private static ListKey<Integer> getListKey()
    {
        return ListKey.HITDICE_ADVANCEMENT;
    }

    @Test
    public void testUnparseSingle()
    {
        getUnparseTarget().addToListFor(getListKey(), 1);
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, "1");
    }

    @Test
    public void testUnparseNullInList()
    {
        getUnparseTarget().addToListFor(getListKey(), null);
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (NullPointerException e)
        {
            // Yep!
        }
    }

    @Test
    public void testUnparseMultiple()
    {
        getUnparseTarget().addToListFor(getListKey(), 1);
        getUnparseTarget().addToListFor(getListKey(), 2);
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, "1,2");
    }


    @Test
    public void testUnparseMultipleStar()
    {
        getUnparseTarget().addToListFor(getListKey(), 1);
        getUnparseTarget().addToListFor(getListKey(), 2);
        getUnparseTarget().addToListFor(getListKey(), Integer.MAX_VALUE);
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, "1,2,*");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFail()
    {
        ListKey objectKey = getListKey();
        primaryProf.addToListFor(objectKey, new Object());
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (ClassCastException e)
        {
            // Yep!
        }
    }
}
