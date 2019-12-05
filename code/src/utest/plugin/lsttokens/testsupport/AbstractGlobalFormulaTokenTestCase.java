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
package plugin.lsttokens.testsupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.persistence.PersistenceLayerException;

import org.junit.jupiter.api.Test;

public abstract class AbstractGlobalFormulaTokenTestCase extends
        AbstractGlobalTokenTestCase
{

    @Test
    public void testValidInputs()
    {
        assertTrue(parse("Variable1"));
        assertEquals("Variable1", getFormula().toString());
        assertTrue(parse("3"));
        assertEquals("3", getFormula().toString());
        assertTrue(parse("3+CL(\"Fighter\")"));
        assertEquals("3+CL(\"Fighter\")", getFormula().toString());
    }

    protected Formula getFormula()
    {
        return primaryProf.get(getFormulaKey());
    }

    public abstract FormulaKey getFormulaKey();

    @Test
    public void testInvalidInputEmpty()
    {
        try
        {
            assertFalse(parse(""));
        } catch (IllegalArgumentException e)
        {
            // This is okay too
        }
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinBase() throws PersistenceLayerException
    {
        runRoundRobin("Variable1");
    }

    @Test
    public void testRoundRobinNumber() throws PersistenceLayerException
    {
        runRoundRobin("3");
    }

    @Test
    public void testRoundRobinFormula() throws PersistenceLayerException
    {
        runRoundRobin("3+CL(\"Fighter\")");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "3+CL(\"Fighter\")";
    }

    @Override
    protected String getLegalValue()
    {
        return "3";
    }

    @Test
    public void testUnparseNumber()
    {
        setAndUnparseMatch(FormulaFactory.getFormulaFor(1));
    }

    @Test
    public void testUnparseFormula()
    {
        setAndUnparseMatch(FormulaFactory.getFormulaFor("Formula"));
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(getFormulaKey(), null);
        assertNull(getWriteToken().unparse(primaryContext, primaryProf));
    }

    private void setAndUnparseMatch(Formula val)
    {
        expectSingle(setAndUnparse(val), getFormula().toString());
    }

    protected String[] setAndUnparse(Formula val)
    {
        setFormula(val);
        return getWriteToken().unparse(primaryContext, primaryProf);
    }

    protected void setFormula(Formula val)
    {
        primaryProf.put(getFormulaKey(), val);
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }
}
