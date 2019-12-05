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
package plugin.lsttokens.pcclass.level;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.ListKey;
import pcgen.persistence.PersistenceLayerException;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public abstract class AbstractSpellCastingTokenTestCase extends
        AbstractPCClassLevelTokenTestCase
{

    @Override
    public void runRoundRobin(String... str) throws PersistenceLayerException
    {
        // Default is not to write out anything
        assertNull(getToken().unparse(primaryContext, primaryProf1));
        assertNull(getToken().unparse(primaryContext, primaryProf2));
        assertNull(getToken().unparse(primaryContext, primaryProf3));

        // Set value
        for (String s : str)
        {
            assertTrue(parse(s, 2));
        }
        // Doesn't pollute other levels
        assertNull(getToken().unparse(primaryContext, primaryProf1));
        // Get back the appropriate token:
        String[] unparsed = getToken().unparse(primaryContext, primaryProf2);
        assertArrayEquals(str, unparsed);

        // And fails for subsequent levels
        assertNull(getToken().unparse(primaryContext, primaryProf3));

        // Do round Robin
        StringBuilder unparsedBuilt = new StringBuilder();
        for (String s : unparsed)
        {
            unparsedBuilt.append(getToken().getTokenName()).append(':').append(
                    s).append('\t');
        }
        loader.parseLine(secondaryContext, secondaryProf2, unparsedBuilt
                .toString(), testCampaign.getURI());

        // Ensure the objects are the same
        assertEquals(primaryProf, secondaryProf);

        // And that it comes back out the same again
        // Doesn't pollute other levels
        assertNull(getToken().unparse(secondaryContext, secondaryProf1));
        String[] sUnparsed = getToken().unparse(secondaryContext,
                secondaryProf2);
        assertArrayEquals(str, unparsed);
        assertEquals(0, primaryContext.getWriteMessageCount());
        assertEquals(0, secondaryContext.getWriteMessageCount());
    }

    @Test
    public void testInvalidListEmpty()
    {
        assertFalse(parse("", 2));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListEnd()
    {
        assertFalse(parse("1,", 2));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListStart()
    {
        assertFalse(parse(",1", 2));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListDoubleJoin()
    {
        assertFalse(parse("1,,2", 2));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListNegativeNumber()
    {
        assertFalse(parse("1,-2", 2));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        runRoundRobin("3");
    }

    @Test
    public void testRoundRobinList() throws PersistenceLayerException
    {
        runRoundRobin("3,2,1");
    }

    @Test
    public void testRoundRobinFormula() throws PersistenceLayerException
    {
        runRoundRobin("Form,Form2+Form3,1");
    }

    @Test
    public void testRoundRobinHardFormula() throws PersistenceLayerException
    {
        runRoundRobin("Form,Form2+Form3,if(var(\"SIZE==3||SIZE==4\"),5,0),1");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "Form,Form2+Form3,1";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    @Override
    protected String getLegalValue()
    {
        return "3,2,1";
    }

    @Test
    public void testUnparseSingle()
    {
        primaryProf1.addToListFor(getListKey(), FormulaFactory.ONE);
        String[] unparsed = getToken().unparse(primaryContext, primaryProf1);
        assertArrayEquals(new String[]{"1"}, unparsed);
    }

    @Test
    public void testUnparseNullInList()
    {
        primaryProf1.addToListFor(getListKey(), null);
        assertThrows(NullPointerException.class,
                () -> getToken().unparse(primaryContext, primaryProf1));
    }

    @Test
    public void testUnparseMultiple()
    {
        primaryProf1
                .addToListFor(getListKey(), FormulaFactory.getFormulaFor(1));
        primaryProf1
                .addToListFor(getListKey(), FormulaFactory.getFormulaFor(2));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf1);
        assertArrayEquals(new String[]{"1,2"}, unparsed);
    }

    /*
     * TODO Need to figure out responsibility for this behavior
     */
    // @Test
    // public void testUnparseGenericsFail() throws PersistenceLayerException
    // {
    // ListKey objectKey = getListKey();
    // primaryProf.addToListFor(objectKey, new Object());
    // try
    // {
    // getToken().unparse(primaryContext, primaryProf1);
    // fail();
    // }
    // catch (ClassCastException e)
    // {
    // // Yep!
    //		}
    //	}

    protected abstract ListKey<Formula> getListKey();
}
