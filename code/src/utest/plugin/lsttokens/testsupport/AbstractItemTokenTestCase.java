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
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;

import org.junit.jupiter.api.Test;

public abstract class AbstractItemTokenTestCase<T extends CDOMObject, TC extends CDOMObject>
        extends AbstractCDOMTokenTestCase<T>
{

    public abstract Class<TC> getTargetClass();

    public abstract boolean isClearLegal();

    public abstract ObjectKey<CDOMSingleRef<TC>> getObjectKey();

    @Test
    public void testInvalidInputEmpty()
    {
        assertFalse(parse(""));
        assertNull(primaryProf.get(getObjectKey()));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputString()
    {
        assertTrue(parse("String"));
        assertConstructionError();
    }

    @Test
    public void testInvalidInputType()
    {
        assertTrue(parse("TestType"));
        assertConstructionError();
    }

    @Test
    public void testInvalidInputJoinedComma()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        boolean ret = parse("TestWP1,TestWP2");
        if (ret)
        {
            assertConstructionError();
        } else
        {
            assertNull(primaryProf.get(getObjectKey()));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputJoinedPipe()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        boolean ret = parse("TestWP1|TestWP2");
        if (ret)
        {
            assertConstructionError();
        } else
        {
            assertNull(primaryProf.get(getObjectKey()));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputJoinedDot()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        boolean ret = parse("TestWP1.TestWP2");
        if (ret)
        {
            assertConstructionError();
        } else
        {
            assertNull(primaryProf.get(getObjectKey()));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputAny()
    {
        try
        {
            boolean result = parse("ANY");
            if (result)
            {
                assertConstructionError();
            }
        } catch (IllegalArgumentException e)
        {
            // This is okay too
        }
    }

    @Test
    public void testInvalidInputCheckType()
    {
        try
        {
            boolean result = getToken().parseToken(primaryContext, primaryProf,
                    "TYPE=TestType").passed();
            if (result)
            {
                assertConstructionError();
            }
        } catch (IllegalArgumentException e)
        {
            // This is okay too
        }
    }

    @Test
    public void testReplacementInputs()
    {
        String[] unparsed;
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        if (isClearLegal())
        {
            assertTrue(parse(Constants.LST_DOT_CLEAR));
            unparsed = getToken().unparse(primaryContext, primaryProf);
            assertNull(unparsed);
        }
        assertTrue(parse("TestWP1"));
        assertTrue(parse("TestWP2"));
        unparsed = getToken().unparse(primaryContext, primaryProf);
        assertEquals("TestWP2", unparsed[0]);
        if (isClearLegal())
        {
            assertTrue(parse(Constants.LST_DOT_CLEAR));
            unparsed = getToken().unparse(primaryContext, primaryProf);
            assertNull(unparsed);
        }
    }

    @Test
    public void testValidInputs()
    {
        construct(primaryContext, "TestWP1");
        assertTrue(parse("TestWP1"));
        assertCleanConstruction();
    }

    @Test
    public void testRoundRobinOne() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin("TestWP1");
    }

    @Test
    public void testRoundRobinOnePreFooler() throws PersistenceLayerException
    {
        construct(primaryContext, "Prefool");
        construct(secondaryContext, "Prefool");
        runRoundRobin("Prefool");
    }

    protected void construct(LoadContext loadContext, String one)
    {
        loadContext.getReferenceContext().constructCDOMObject(getTargetClass(), one);
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "TestWP2";
    }

    @Override
    protected String getLegalValue()
    {
        return "TestWP1";
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
        CDOMSingleRef<TC> o = primaryContext.getReferenceContext().getCDOMReference(getTargetClass(), getLegalValue());
        primaryProf.put(getObjectKey(), o);
        expectSingle(getToken().unparse(primaryContext, primaryProf), o.getLSTformat(false));
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
