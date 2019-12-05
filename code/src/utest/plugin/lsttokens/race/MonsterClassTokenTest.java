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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class MonsterClassTokenTest extends AbstractCDOMTokenTestCase<Race>
{

    static MonsterclassToken token = new MonsterclassToken();
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
    public void testInvalidNoColon()
    {
        assertFalse(parse("Fighter"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTwoColon()
    {
        assertFalse(parse("Fighter:4:1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidLevelNegative()
    {
        assertFalse(parse("Fighter:-4"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidLevelZero()
    {
        assertFalse(parse("Fighter:0"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidLevelNaN()
    {
        assertFalse(parse("Fighter:Level"));
        assertNoSideEffects();
    }

    @Test
    public void testBadClass()
    {
        assertTrue(parse("Fighter:4"));
        assertConstructionError();
    }

    @Test
    public void testSimple() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
        secondaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
        runRoundRobin("Fighter:4");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "Cleric:2";
    }

    @Override
    protected String getLegalValue()
    {
        return "Fighter:4";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(ObjectKey.MONSTER_CLASS, null);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseSingle()
    {
        primaryContext.getReferenceContext().constructCDOMObject(PCClass.class, "Fighter");
        CDOMSingleRef<PCClass> cl = primaryContext.getReferenceContext().getCDOMReference(
                PCClass.class, "Fighter");
        primaryProf.put(ObjectKey.MONSTER_CLASS, new LevelCommandFactory(cl,
                FormulaFactory.getFormulaFor(4)));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, "Fighter:4");
    }

    /*
     * TODO Need to get responsibility set for this
     */
    // @Test
    // public void testUnparseNegativeLevel() throws PersistenceLayerException
    // {
    // try
    // {
    // primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
    // CDOMSingleRef<PCClass> cl = primaryContext.ref.getCDOMReference(
    // PCClass.class, "Fighter");
    // primaryProf.put(ObjectKey.MONSTER_CLASS, new LevelCommandFactory(cl,
    // FormulaFactory.getFormulaFor(-4)));
    // assertBadUnparse();
    // }
    // catch (IllegalArgumentException e)
    // {
    // //Good here too :)
    //		}
    //	}

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFail()
    {
        ObjectKey objectKey = ObjectKey.MONSTER_CLASS;
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
}
