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
package plugin.lsttokens.template;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SubClassCategory;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractListInputTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class FavoredClassTokenTest
        extends AbstractListInputTokenTestCase<PCTemplate, PCClass>
{

    static FavoredclassToken token = new FavoredclassToken();
    static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

    @Override
    public Class<PCTemplate> getCDOMClass()
    {
        return PCTemplate.class;
    }

    @Override
    public CDOMLoader<PCTemplate> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<PCTemplate> getToken()
    {
        return token;
    }

    @Override
    public Class<PCClass> getTargetClass()
    {
        return PCClass.class;
    }

    @Override
    public boolean isTypeLegal()
    {
        return false;
    }

    @Override
    public boolean isAllLegal()
    {
        return true;
    }

    @Override
    public boolean isClearDotLegal()
    {
        return false;
    }

    @Override
    public boolean isClearLegal()
    {
        return false;
    }

    @Override
    public char getJoinCharacter()
    {
        return '|';
    }

    @Test
    public void testInvalidInputSubClassNoSub()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("TestWP1."));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputSubClassNoClass()
    {
        assertFalse(parse(".TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputSubDoubleSeparator()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("TestWP1..Two"));
        assertNoSideEffects();
    }

    @Test
    public void testCategorizationFail()
    {
        construct(primaryContext, "TestWP1");
        assertTrue(parse("TestWP1.Two"));
        SubClassCategory cat = SubClassCategory.getConstant("TestWP2");
        constructCategorized(primaryContext, cat, "Two");
        assertConstructionError();
    }

    @Test
    public void testCategorizationPass()
    {
        construct(primaryContext, "TestWP1");
        assertTrue(parse("TestWP1.Two"));
        SubClassCategory cat = SubClassCategory.getConstant("TestWP2");
        constructCategorized(primaryContext, cat, "Two");
        cat = SubClassCategory.getConstant("TestWP1");
        constructCategorized(primaryContext, cat, "Two");
        assertCleanConstruction();
    }

    @Test
    public void testRoundRobinThreeSub() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(primaryContext, "TestWP3");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        construct(secondaryContext, "TestWP3");
        SubClassCategory cat = SubClassCategory.getConstant("TestWP2");
        constructCategorized(primaryContext, cat, "Sub");
        constructCategorized(secondaryContext, cat, "Sub");
        runRoundRobin("TestWP1" + getJoinCharacter() + "TestWP2.Sub"
                + getJoinCharacter() + "TestWP3");
    }

    @Override
    protected String getAllString()
    {
        return Constants.HIGHEST_LEVEL_CLASS;
    }

    @Test
    public void testRoundRobinList() throws PersistenceLayerException
    {
        runRoundRobin("%LIST");
    }

    @Override
    public boolean allowDups()
    {
        return false;
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    @Test
    public void testOverwriteList()
    {
        parse("%LIST");
        validateUnparsed(primaryContext, primaryProf, "%LIST");
        parse("TestWP1");
        validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
                .getAnswer("TestWP1"));
    }

    @Test
    public void testOverwriteWithList()
    {
        parse("TestWP1");
        validateUnparsed(primaryContext, primaryProf, "TestWP1");
        parse("%LIST");
        validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
                .getAnswer("%LIST"));
    }

    @Test
    public void testOverwriteHighest()
    {
        parse(Constants.HIGHEST_LEVEL_CLASS);
        validateUnparsed(primaryContext, primaryProf, Constants.HIGHEST_LEVEL_CLASS);
        parse("TestWP1");
        validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
                .getAnswer("TestWP1"));
    }

    @Test
    public void testOverwriteWithHighest()
    {
        parse("TestWP1");
        validateUnparsed(primaryContext, primaryProf, "TestWP1");
        parse(Constants.HIGHEST_LEVEL_CLASS);
        validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
                .getAnswer(Constants.HIGHEST_LEVEL_CLASS));
    }

    @Test
    public void testUnparseHighest()
    {
        primaryProf.put(ObjectKey.ANY_FAVORED_CLASS, true);
        expectSingle(getToken().unparse(primaryContext, primaryProf),
                Constants.HIGHEST_LEVEL_CLASS);
    }

    @Test
    public void testUnparseHighestUnset()
    {
        primaryProf.put(ObjectKey.ANY_FAVORED_CLASS, false);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseHighestNull()
    {
        primaryProf.put(ObjectKey.ANY_FAVORED_CLASS, null);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFailHighest()
    {
        ObjectKey objectKey = ObjectKey.ANY_FAVORED_CLASS;
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
    public void testUnparseNull()
    {
        primaryProf.removeListFor(ListKey.FAVORED_CLASS);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseSingle()
    {
        PCClass wp1 = construct(primaryContext, "TestWP1");
        primaryProf.addToListFor(ListKey.FAVORED_CLASS, CDOMDirectSingleRef
                .getRef(wp1));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getLegalValue());
    }

    @Test
    public void testUnparseNullInList()
    {
        primaryProf.addToListFor(ListKey.FAVORED_CLASS, null);
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
        PCClass wp1 = construct(primaryContext, getLegalValue());
        primaryProf.addToListFor(ListKey.FAVORED_CLASS, CDOMDirectSingleRef
                .getRef(wp1));
        PCClass wp2 = construct(primaryContext, getAlternateLegalValue());
        primaryProf.addToListFor(ListKey.FAVORED_CLASS, CDOMDirectSingleRef
                .getRef(wp2));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getLegalValue() + getJoinCharacter()
                + getAlternateLegalValue());
    }

    @Test
    public void testUnparseMultipleHighest()
    {
        PCClass wp1 = construct(primaryContext, getLegalValue());
        primaryProf.addToListFor(ListKey.FAVORED_CLASS, CDOMDirectSingleRef
                .getRef(wp1));
        PCClass wp2 = construct(primaryContext, getAlternateLegalValue());
        primaryProf.addToListFor(ListKey.FAVORED_CLASS, CDOMDirectSingleRef
                .getRef(wp2));
        primaryProf.put(ObjectKey.ANY_FAVORED_CLASS, true);
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, Constants.HIGHEST_LEVEL_CLASS + getJoinCharacter()
                + getLegalValue() + getJoinCharacter()
                + getAlternateLegalValue());
    }

    /*
     * TODO Need to define the appropriate behavior here (LIST) - is this the token's
     * responsibility?
     */
    // @Test
    // public void testUnparseGenericsFail() throws PersistenceLayerException
    // {
    // ListKey objectKey = getListKey();
    // primaryProf.addToListFor(objectKey, new Object());
    // try
    // {
    // String[] unparsed = getToken().unparse(primaryContext, primaryProf);
    // fail();
    // }
    // catch (ClassCastException e)
    // {
    // //Yep!
    // }
    // }

    @Test
    public void testUnparseNullCA()
    {
        primaryProf.removeListFor(ListKey.NEW_CHOOSE_ACTOR);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseCA()
    {
        primaryProf.addToListFor(ListKey.NEW_CHOOSE_ACTOR, token);
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, "%LIST");
    }

    @Test
    public void testUnparseNullInCAList()
    {
        primaryProf.addToListFor(ListKey.NEW_CHOOSE_ACTOR, null);
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
    public void testUnparseMultipleAll()
    {
        primaryProf.addToListFor(ListKey.NEW_CHOOSE_ACTOR, token);
        PCClass wp1 = construct(primaryContext, getLegalValue());
        primaryProf.addToListFor(ListKey.FAVORED_CLASS, CDOMDirectSingleRef
                .getRef(wp1));
        PCClass wp2 = construct(primaryContext, getAlternateLegalValue());
        primaryProf.addToListFor(ListKey.FAVORED_CLASS, CDOMDirectSingleRef
                .getRef(wp2));
        primaryProf.put(ObjectKey.ANY_FAVORED_CLASS, true);
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, "%LIST" + getJoinCharacter()
                + Constants.HIGHEST_LEVEL_CLASS + getJoinCharacter() + getLegalValue()
                + getJoinCharacter() + getAlternateLegalValue());
    }

    /*
     * TODO Need to define the appropriate behavior here (CA) - is this the token's
     * responsibility?
     */
    // @Test
    // public void testUnparseGenericsFail() throws PersistenceLayerException
    // {
    // ListKey objectKey = getListKey();
    // primaryProf.addToListFor(objectKey, new Object());
    // try
    // {
    // String[] unparsed = getToken().unparse(primaryContext, primaryProf);
    // fail();
    // }
    // catch (ClassCastException e)
    // {
    // //Yep!
    // }
    // }
}
