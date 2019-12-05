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

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.persistence.PersistenceLayerException;

import org.junit.jupiter.api.Test;

public abstract class AbstractGlobalIntegerTokenTestCase extends
        AbstractGlobalTokenTestCase
{

    public abstract IntegerKey getIntegerKey();

    public abstract boolean isZeroAllowed();

    public abstract boolean isNegativeAllowed();

    public abstract boolean isPositiveAllowed();

    @Test
    public void testInvalidInputUnset()
    {
        testInvalidInputs(null);
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputSet()
    {
        Integer con;
        con = isPositiveAllowed() ? 3 : -3;
        assertTrue(parse(con.toString()));
        assertTrue(parseSecondary(con.toString()));
        assertEquals(con, primaryProf.get(getIntegerKey()));
        testInvalidInputs(con);
        assertNoSideEffects();
    }

    public void testInvalidInputs(Integer val)
    {
        // Always ensure get is unchanged
        // since no invalid item should set or reset the value
        assertEquals(val, primaryProf.get(getIntegerKey()));
        assertFalse(parse("TestWP"));
        assertEquals(val, primaryProf.get(getIntegerKey()));
        assertFalse(parse("String"));
        assertEquals(val, primaryProf.get(getIntegerKey()));
        assertFalse(parse("TYPE=TestType"));
        assertEquals(val, primaryProf.get(getIntegerKey()));
        assertFalse(parse("TYPE.TestType"));
        assertEquals(val, primaryProf.get(getIntegerKey()));
        assertFalse(parse("ALL"));
        assertEquals(val, primaryProf.get(getIntegerKey()));
        assertFalse(parse("ANY"));
        assertEquals(val, primaryProf.get(getIntegerKey()));
        assertFalse(parse("FIVE"));
        assertEquals(val, primaryProf.get(getIntegerKey()));
        assertFalse(parse("4.5"));
        assertEquals(val, primaryProf.get(getIntegerKey()));
        assertFalse(parse("1/2"));
        assertEquals(val, primaryProf.get(getIntegerKey()));
        assertFalse(parse("1+3"));
        assertEquals(val, primaryProf.get(getIntegerKey()));
        // Require Integer greater than or equal to zero
        if (!isNegativeAllowed())
        {
            assertFalse(parse("-1"));
            assertEquals(val, primaryProf.get(getIntegerKey()));
        }
        if (!isPositiveAllowed())
        {
            assertFalse(parse("1"));
            assertEquals(val, primaryProf.get(getIntegerKey()));
        }
        if (!isZeroAllowed())
        {
            assertFalse(parse("0"));
            assertEquals(val, primaryProf.get(getIntegerKey()));
        }
    }

    @Test
    public void testValidInputs()
    {
        if (isPositiveAllowed())
        {
            assertTrue(parse("5"));
            assertEquals(Integer.valueOf(5), primaryProf.get(getIntegerKey()));
            assertTrue(parse("1"));
            assertEquals(Integer.valueOf(1), primaryProf.get(getIntegerKey()));
        }
        if (isZeroAllowed())
        {
            assertTrue(parse("0"));
            assertEquals(Integer.valueOf(0), primaryProf.get(getIntegerKey()));
        }
        if (isNegativeAllowed())
        {
            assertTrue(parse("-2"));
            assertEquals(Integer.valueOf(-2), primaryProf.get(getIntegerKey()));
        }
    }

    @Test
    public void testOutputOne()
    {
        assertEquals(0, primaryContext.getWriteMessageCount());
        primaryProf.put(getIntegerKey(), 1);
        String[] unparsed = getWriteToken().unparse(primaryContext, primaryProf);
        if (isPositiveAllowed())
        {
            assertEquals(1, unparsed.length);
            assertEquals("1", unparsed[0]);
        } else
        {
            assertNull(unparsed);
            assertTrue(0 != primaryContext.getWriteMessageCount());
        }
    }

    @Test
    public void testOutputZero()
    {
        assertEquals(0, primaryContext.getWriteMessageCount());
        primaryProf.put(getIntegerKey(), 0);
        String[] unparsed = getWriteToken().unparse(primaryContext, primaryProf);
        if (isZeroAllowed())
        {
            assertEquals(1, unparsed.length);
            assertEquals("0", unparsed[0]);
        } else
        {
            assertNull(unparsed);
            assertTrue(0 != primaryContext.getWriteMessageCount());
        }
    }

    @Test
    public void testOutputMinusTwo()
    {
        assertEquals(0, primaryContext.getWriteMessageCount());
        primaryProf.put(getIntegerKey(), -2);
        String[] unparsed = getWriteToken().unparse(primaryContext, primaryProf);
        if (isNegativeAllowed())
        {
            assertEquals(1, unparsed.length);
            assertEquals("-2", unparsed[0]);
        } else
        {
            assertNull(unparsed);
            assertTrue(0 != primaryContext.getWriteMessageCount());
        }
    }

    @Test
    public void testReplacementInputs()
    {
        if (isPositiveAllowed())
        {
            assertTrue(parse("5"));
            assertTrue(parse("1"));
            String[] unparsed = getWriteToken().unparse(primaryContext, primaryProf);
            assertEquals("1", unparsed[0]);
        } else
        {
            assertTrue(parse("-2"));
            assertTrue(parse("-4"));
            String[] unparsed = getWriteToken().unparse(primaryContext, primaryProf);
            assertEquals("-4", unparsed[0]);
        }
    }

    @Test
    public void testRoundRobinOne() throws PersistenceLayerException
    {
        if (isPositiveAllowed())
        {
            runRoundRobin("1");
        }
    }

    @Test
    public void testRoundRobinZero() throws PersistenceLayerException
    {
        if (isZeroAllowed())
        {
            runRoundRobin("0");
        }
    }

    @Test
    public void testRoundRobinNegative() throws PersistenceLayerException
    {
        if (isNegativeAllowed())
        {
            runRoundRobin("-3");
        }
    }

    @Test
    public void testRoundRobinFive() throws PersistenceLayerException
    {
        if (isPositiveAllowed())
        {
            runRoundRobin("5");
        }
    }

    @Override
    protected String getLegalValue()
    {
        return isPositiveAllowed() ? "1" : "-1";
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return isPositiveAllowed() ? "2" : "-2";
    }

    @Test
    public void testArchitecturePositiveNegative()
    {
        assertTrue(isPositiveAllowed() || isNegativeAllowed());
    }

    @Test
    public void testUnparseOne()
    {
        if (isPositiveAllowed())
        {
            expectSingle(setAndUnparse(1), Integer.toString(1));
        } else
        {
            primaryProf.put(getIntegerKey(), 1);
            assertBadUnparse();
        }
    }

    @Test
    public void testUnparseZero()
    {
        if (isZeroAllowed())
        {
            expectSingle(setAndUnparse(0), Integer.toString(0));
        } else
        {
            primaryProf.put(getIntegerKey(), 0);
            assertBadUnparse();
        }
    }

    @Test
    public void testUnparseNegative()
    {
        if (isNegativeAllowed())
        {
            expectSingle(setAndUnparse(-3), Integer.toString(-3));
        } else
        {
            primaryProf.put(getIntegerKey(), -3);
            assertBadUnparse();
        }
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(getIntegerKey(), null);
        assertNull(getWriteToken().unparse(primaryContext, primaryProf));
    }

    protected String[] setAndUnparse(int val)
    {
        primaryProf.put(getIntegerKey(), val);
        return getWriteToken().unparse(primaryContext, primaryProf);
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }
}
