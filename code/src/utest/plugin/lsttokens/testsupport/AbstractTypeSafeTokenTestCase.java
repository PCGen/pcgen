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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.persistence.PersistenceLayerException;

import org.junit.jupiter.api.Test;

public abstract class AbstractTypeSafeTokenTestCase<T extends CDOMObject, CT> extends
        AbstractCDOMTokenTestCase<T>
{

    public abstract boolean isClearLegal();

    @Test
    public void testValidInputs()
    {
        if (requiresPreconstruction())
        {
            getConstant("Niederösterreich");
            getConstant("Finger Lakes");
            getConstant("Rheinhessen");
            getConstant("Languedoc-Roussillon");
            getConstant("Yarra Valley");
        }
        assertTrue(parse("Niederösterreich"));
        assertEquals(getConstant("Niederösterreich"), primaryProf
                .get(getObjectKey()));
        assertTrue(parse("Finger Lakes"));
        assertEquals(getConstant("Finger Lakes"), primaryProf
                .get(getObjectKey()));
        assertTrue(parse("Rheinhessen"));
        assertEquals(getConstant("Rheinhessen"), primaryProf
                .get(getObjectKey()));
        assertTrue(parse("Languedoc-Roussillon"));
        assertEquals(getConstant("Languedoc-Roussillon"), primaryProf
                .get(getObjectKey()));
        assertTrue(parse("Yarra Valley"));
        assertEquals(getConstant("Yarra Valley"), primaryProf
                .get(getObjectKey()));
    }

    protected abstract boolean requiresPreconstruction();

    public abstract CT getConstant(String string);

    public abstract ObjectKey<CT> getObjectKey();

    @Test
    public void testReplacementInputs()
    {
        String[] unparsed;
        if (requiresPreconstruction())
        {
            getConstant("TestWP1");
            getConstant("TestWP2");
        }
        if (isClearLegal())
        {
            assertTrue(parse(Constants.LST_DOT_CLEAR));
            unparsed = getToken().unparse(primaryContext, primaryProf);
            assertNull(unparsed);
        }
        assertTrue(parse("TestWP1"));
        assertTrue(parse("TestWP2"));
        unparsed = getToken().unparse(primaryContext, primaryProf);
        assertEquals(1, unparsed.length);
        assertEquals("TestWP2", unparsed[0]);
        if (isClearLegal())
        {
            assertTrue(parse(Constants.LST_DOT_CLEAR));
            unparsed = getToken().unparse(primaryContext, primaryProf);
            assertNull(unparsed);
        }
    }

    @Test
    public void testInvalidPreconstruction()
    {
        if (requiresPreconstruction())
        {
            try
            {
                if (parse("Not Preconstructed"))
                {
                    assertConstructionError();
                }
            } catch (IllegalArgumentException e)
            {
                // OK as well
            }
        }
    }

    @Test
    public void testInvalidEmptyInput()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinBase() throws PersistenceLayerException
    {
        if (requiresPreconstruction())
        {
            getConstant("Rheinhessen");
        }
        runRoundRobin("Rheinhessen");
    }

    @Test
    public void testRoundRobinWithSpace() throws PersistenceLayerException
    {
        if (requiresPreconstruction())
        {
            getConstant("Finger Lakes");
        }
        runRoundRobin("Finger Lakes");
    }

    @Test
    public void testRoundRobinNonEnglishAndN() throws PersistenceLayerException
    {
        if (requiresPreconstruction())
        {
            getConstant("Niederösterreich");
        }
        runRoundRobin("Niederösterreich");
    }

    @Test
    public void testRoundRobinHyphen() throws PersistenceLayerException
    {
        if (requiresPreconstruction())
        {
            getConstant("Languedoc-Roussillon");
        }
        runRoundRobin("Languedoc-Roussillon");
    }

    @Test
    public void testRoundRobinY() throws PersistenceLayerException
    {
        if (requiresPreconstruction())
        {
            getConstant("Yarra Valley");
        }
        runRoundRobin("Yarra Valley");
    }

    @Override
    public void testOverwrite()
    {
        if (requiresPreconstruction())
        {
            getConstant(getLegalValue());
            getConstant(getAlternateLegalValue());
        }
        super.testOverwrite();
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "Languedoc-Roussillon";
    }

    @Override
    protected String getLegalValue()
    {
        return "Yarra Valley";
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(getObjectKey(), null);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseLegal()
    {
        CT o = getConstant(getLegalValue());
        primaryProf.put(getObjectKey(), o);
        expectSingle(getToken().unparse(primaryContext, primaryProf), o.toString());
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
            //Yep!
        }
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }
}
