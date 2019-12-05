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
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.persistence.PersistenceLayerException;

import org.junit.jupiter.api.Test;


public abstract class AbstractBigDecimalTokenTestCase<T extends CDOMObject>
        extends AbstractCDOMTokenTestCase<T>
{

    public abstract ObjectKey<BigDecimal> getObjectKey();

    public abstract boolean isZeroAllowed();

    public abstract boolean isNegativeAllowed();

    public abstract boolean isPositiveAllowed();

    public abstract boolean isClearLegal();

    @Test
    public void testInvalidInputUnset()
    {
        testInvalidInputs(null);
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputSet()
    {
        BigDecimal con = new BigDecimal(isPositiveAllowed() ? 3 : -3);
        assertTrue(parse(con.toString()));
        assertTrue(parseSecondary(con.toString()));
        assertEquals(con, primaryProf.get(getObjectKey()));
        testInvalidInputs(con);
        assertNoSideEffects();
    }

    public void testInvalidInputs(BigDecimal val)
    {
        // Always ensure get is unchanged
        // since no invalid item should set or reset the value
        assertEquals(val, primaryProf.get(getObjectKey()));
        assertFalse(parse("TestWP"));
        assertEquals(val, primaryProf.get(getObjectKey()));
        assertFalse(parse("String"));
        assertEquals(val, primaryProf.get(getObjectKey()));
        assertFalse(parse("TYPE=TestType"));
        assertEquals(val, primaryProf.get(getObjectKey()));
        assertFalse(parse("TYPE.TestType"));
        assertEquals(val, primaryProf.get(getObjectKey()));
        assertFalse(parse("ALL"));
        assertEquals(val, primaryProf.get(getObjectKey()));
        assertFalse(parse("ANY"));
        assertEquals(val, primaryProf.get(getObjectKey()));
        assertFalse(parse("FIVE"));
        assertEquals(val, primaryProf.get(getObjectKey()));
        assertFalse(parse("1/2"));
        assertEquals(val, primaryProf.get(getObjectKey()));
        assertFalse(parse("1+3"));
        assertEquals(val, primaryProf.get(getObjectKey()));
        // Require Integer greater than or equal to zero
        if (!isNegativeAllowed())
        {
            assertFalse(parse("-1"));
            assertEquals(val, primaryProf.get(getObjectKey()));
        }
        if (!isPositiveAllowed())
        {
            assertFalse(parse("1"));
            assertEquals(val, primaryProf.get(getObjectKey()));
        }
        if (!isZeroAllowed())
        {
            assertFalse(parse("0"));
            assertEquals(val, primaryProf.get(getObjectKey()));
        }
    }

    @Test
    public void testValidInputs()
    {
        if (isPositiveAllowed())
        {
            assertTrue(parse("4.5"));
            assertEquals(new BigDecimal("4.5"), primaryProf.get(getObjectKey()));
            assertTrue(parse("5"));
            assertEquals(new BigDecimal(5), primaryProf.get(getObjectKey()));
            assertTrue(parse("1"));
            assertEquals(new BigDecimal(1), primaryProf.get(getObjectKey()));
        }
        if (isZeroAllowed())
        {
            assertTrue(parse("0"));
            assertEquals(new BigDecimal(0), primaryProf.get(getObjectKey()));
        }
        if (isNegativeAllowed())
        {
            assertTrue(parse("-2"));
            assertEquals(new BigDecimal(-2), primaryProf.get(getObjectKey()));
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
    public void testRoundRobinThreePointFive() throws PersistenceLayerException
    {
        if (isPositiveAllowed())
        {
            runRoundRobin("3.5");
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
        return isPositiveAllowed() ? "2.2" : "-2.2";
    }

    @Test
    public void testArchitecturePositiveNegative()
    {
        assertTrue(isPositiveAllowed() || isNegativeAllowed());
    }

    @Test
    public void testUnparseOne()
    {
        BigDecimal val = new BigDecimal("4.5");
        if (isPositiveAllowed())
        {
            primaryProf.put(getObjectKey(), val);
            expectSingle(getToken().unparse(primaryContext, primaryProf), val
                    .toString());
        } else
        {
            primaryProf.put(getObjectKey(), val);
            assertBadUnparse();
        }
    }

    @Test
    public void testUnparseZero()
    {
        BigDecimal val = new BigDecimal(0);
        if (isZeroAllowed())
        {
            primaryProf.put(getObjectKey(), val);
            expectSingle(getToken().unparse(primaryContext, primaryProf), val
                    .toString());
        } else
        {
            primaryProf.put(getObjectKey(), val);
            assertBadUnparse();
        }
    }

    @Test
    public void testUnparseNegative()
    {
        BigDecimal val = new BigDecimal(-2);
        if (isNegativeAllowed())
        {
            primaryProf.put(getObjectKey(), val);
            expectSingle(getToken().unparse(primaryContext, primaryProf), val
                    .toString());
        } else
        {
            primaryProf.put(getObjectKey(), val);
            assertBadUnparse();
        }
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(getObjectKey(), null);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFail()
    {
        ObjectKey objectKey = getObjectKey();
        primaryProf.put(objectKey, new Object());
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (ClassCastException e)
        {
            // Yep!
        }
    }

    @Test
    public void testReplacementInputs()
    {
        String[] unparsed;
        if (isClearLegal())
        {
            assertTrue(parse(Constants.LST_DOT_CLEAR));
            unparsed = getToken().unparse(primaryContext, primaryProf);
            assertNull(unparsed);
        }
        assertTrue(parse("3.14"));
        unparsed = getToken().unparse(primaryContext, primaryProf);
        assertEquals("3.14", unparsed[0]);
        if (isClearLegal())
        {
            assertTrue(parse(Constants.LST_DOT_CLEAR));
            unparsed = getToken().unparse(primaryContext, primaryProf);
            assertNull(unparsed);
        }
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }
}
