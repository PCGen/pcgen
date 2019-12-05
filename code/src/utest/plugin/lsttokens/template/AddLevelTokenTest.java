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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.List;

import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class AddLevelTokenTest extends AbstractCDOMTokenTestCase<PCTemplate>
{
    static AddLevelToken token = new AddLevelToken();
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

    @Test
    public void testInvalidInputNoPipe()
    {
        assertFalse(parse("Fighter:3"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNoClass()
    {
        assertFalse(parse("|3"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNoLevelCount()
    {
        assertFalse(parse("Fighter|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputTwoPipes()
    {
        assertFalse(parse("Fighter|3|3"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNegativeLevelCount()
    {
        assertFalse(parse("Fighter|-5"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputZeroLevelCount()
    {
        assertFalse(parse("Fighter|0"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNotAClass()
    {
        assertTrue(parse("NotAClass|3"));
        assertConstructionError();
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
        runRoundRobin("Fighter|3");
    }

    @Test
    public void testRoundRobinFormula() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
        runRoundRobin("Fighter|Formula");
    }

    @Test
    public void testRoundRobinHardFormula() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
        runRoundRobin("Fighter|if(var(\"SIZE==3||SIZE==4\"),5,0)");
    }

    @Test
    public void testRoundRobinMultiple() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Thief");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Thief");
        runRoundRobin("Fighter|3", "Thief|4");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "Fighter|3";
    }

    @Override
    protected String getLegalValue()
    {
        return "Thief|4";
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.removeListFor(ListKey.ADD_LEVEL);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseSingle()
    {
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
        CDOMSingleRef<PCClass> cl = primaryContext.getReferenceContext().getCDOMReference(
                PCClass.class, "Fighter");
        primaryProf.addToListFor(ListKey.ADD_LEVEL, new LevelCommandFactory(cl,
                FormulaFactory.getFormulaFor(4)));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, "Fighter|4");
    }

    /*
     * TODO Need to set responsibility for this
     */
    // @Test
    // public void testUnparseNegativeLevel() throws PersistenceLayerException
    // {
    // try
    // {
    // primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
    // CDOMSingleRef<PCClass> cl = primaryContext.ref.getCDOMReference(
    // PCClass.class, "Fighter");
    // primaryProf.addToListFor(ListKey.ADD_LEVEL,
    // new LevelCommandFactory(cl, FormulaFactory
    // .getFormulaFor(-4)));
    // assertBadUnparse();
    // }
    // catch (IllegalArgumentException e)
    // {
    // //Good here too :)
    //		}
    //	}

    @Test
    public void testUnparseNullInList()
    {
        primaryProf.addToListFor(ListKey.ADD_LEVEL, null);
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
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Cleric");
        CDOMSingleRef<PCClass> fi = primaryContext.getReferenceContext().getCDOMReference(
                PCClass.class, "Fighter");
        primaryProf.addToListFor(ListKey.ADD_LEVEL, new LevelCommandFactory(fi,
                FormulaFactory.getFormulaFor(2)));
        CDOMSingleRef<PCClass> cl = primaryContext.getReferenceContext().getCDOMReference(
                PCClass.class, "Cleric");
        primaryProf.addToListFor(ListKey.ADD_LEVEL, new LevelCommandFactory(cl,
                FormulaFactory.getFormulaFor("Formula")));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        assertNotNull(unparsed);
        assertEquals(2, unparsed.length);
        List<String> upList = Arrays.asList(unparsed);
        assertTrue(upList.contains("Fighter|2"));
        assertTrue(upList.contains("Cleric|Formula"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFail()
    {
        ListKey objectKey = ListKey.ADD_LEVEL;
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

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }

}
