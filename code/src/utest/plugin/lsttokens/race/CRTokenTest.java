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

import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.content.ChallengeRating;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class CRTokenTest extends AbstractCDOMTokenTestCase<Race>
{

    static CrToken token = new CrToken();
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
    public void testBadInputNegative()
    {
        try
        {
            boolean parse = parse("-1");
            assertFalse(parse);
        } catch (IllegalArgumentException e)
        {
            // OK
        }
        assertNoSideEffects();
    }

    @Test
    public void testBadInputNonFloat()
    {
        try
        {
            boolean parse = parse("1/x");
            assertFalse(parse);
        } catch (IllegalArgumentException e)
        {
            // OK
        }
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinZero() throws PersistenceLayerException
    {
        runRoundRobin("0");
    }

    @Test
    public void testRoundRobinFraction() throws PersistenceLayerException
    {
        runRoundRobin("1/3");
    }

    // @Test
    // public void testRoundRobinFractionFormula()
    // throws PersistenceLayerException
    // {
    // runRoundRobin("1/Formula");
    // }
    //
    // @Test
    // public void testRoundRobinFractionFormulaNegative()
    // throws PersistenceLayerException
    // {
    // runRoundRobin("1/-Formula");
    // }
    //
    // @Test
    // public void testRoundRobinFormula() throws PersistenceLayerException
    // {
    // runRoundRobin("Formula");
    // }
    //
    @Test
    public void testRoundRobinFive() throws PersistenceLayerException
    {
        runRoundRobin("5");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "5";
    }

    @Override
    protected String getLegalValue()
    {
        return "1/3";
    }

    // @Test
    // public void testEmpty()
    // {
    // //Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
    // }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    @Test
    public void testUnparseOne()
    {
        ChallengeRating cr = new ChallengeRating(FormulaFactory.ONE);
        primaryProf.put(ObjectKey.CHALLENGE_RATING, cr);
        expectSingle(getToken().unparse(primaryContext, primaryProf), "1");
    }

    @Test
    public void testUnparseFraction()
    {
        ChallengeRating cr = new ChallengeRating(FormulaFactory.getFormulaFor("1/2"));
        primaryProf.put(ObjectKey.CHALLENGE_RATING, cr);
        expectSingle(getToken().unparse(primaryContext, primaryProf), "1/2");
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(ObjectKey.CHALLENGE_RATING, null);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFail()
    {
        ObjectKey objectKey = ObjectKey.CHALLENGE_RATING;
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

    //TODO Decide if this is a critical test
//	@Test
//	public void testUnparseBadFraction() throws PersistenceLayerException
//	{
//		try
//		{
//			ChallengeRating cr = new ChallengeRating(FormulaFactory.getFormulaFor("2/3"));
//			primaryProf.put(ObjectKey.CHALLENGE_RATING, cr);
//			assertBadUnparse();
//		}
//		catch (IllegalArgumentException e)
//		{
//			//Good here too :)
//		}
//	}

}
