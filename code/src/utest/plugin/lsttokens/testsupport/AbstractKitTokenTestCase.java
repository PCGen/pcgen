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


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

import pcgen.cdom.base.Categorized;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Loadable;
import pcgen.core.Campaign;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestURI;

public abstract class AbstractKitTokenTestCase<T extends Loadable>
{
    protected LoadContext primaryContext;
    protected LoadContext secondaryContext;
    protected T primaryProf;
    protected T secondaryProf;
    private int expectedPrimaryMessageCount = 0;

    protected static CampaignSourceEntry testCampaign;

    @BeforeAll
    public static void classSetUp()
    {
        testCampaign = new CampaignSourceEntry(new Campaign(), TestURI.getURI());
    }

    @BeforeEach
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        // Yea, this causes warnings...
        TokenRegistration.register(getToken());
        primaryContext = new RuntimeLoadContext(RuntimeReferenceContext.createRuntimeReferenceContext(),
                new ConsolidatedListCommitStrategy());
        secondaryContext = new RuntimeLoadContext(RuntimeReferenceContext.createRuntimeReferenceContext(),
                new ConsolidatedListCommitStrategy());
        URI testURI = testCampaign.getURI();
        primaryContext.setSourceURI(testURI);
        primaryContext.setExtractURI(testURI);
        secondaryContext.setSourceURI(testURI);
        secondaryContext.setExtractURI(testURI);
        primaryContext.getReferenceContext().importObject(BuildUtilities.getFeatCat());
        secondaryContext.getReferenceContext().importObject(BuildUtilities.getFeatCat());
        primaryProf = getSubInstance();
        secondaryProf = getSubInstance();
        expectedPrimaryMessageCount = 0;
    }

    protected T getSubInstance()
    {
        try
        {
            return getCDOMClass().getConstructor().newInstance();
        } catch (InstantiationException
                | IllegalAccessException
                | NoSuchMethodException
                | InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    public abstract Class<? extends T> getCDOMClass();

    public static void addBonus(Class<? extends BonusObj> clazz)
    {
        try
        {
            TokenLibrary.addBonusClass(clazz);
        } catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    public void runRoundRobin(String... str) throws PersistenceLayerException
    {
        // Default is not to write out anything
        assertNull(getToken().unparse(primaryContext, primaryProf));

        // Set value
        for (String s : str)
        {
            assertTrue(parse(s));
        }
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        assertArrayEquals(str, unparsed);

        // Do round Robin
        StringBuilder unparsedBuilt = new StringBuilder();
        for (String s : unparsed)
        {
            unparsedBuilt.append(getToken().getTokenName()).append(':').append(
                    s).append('\t');
        }
        getLoader().parseLine(secondaryContext, secondaryProf,
                unparsedBuilt.toString());
        // And that it comes back out the same again
        String[] sUnparsed = getToken()
                .unparse(secondaryContext, secondaryProf);
        assertEquals(unparsed.length, sUnparsed.length);
        assertArrayEquals(sUnparsed, unparsed);

        assertCleanConstruction();
        assertTrue(secondaryContext.getReferenceContext().validate(null));
        assertTrue(secondaryContext.getReferenceContext().resolveReferences(null));
        assertEquals(expectedPrimaryMessageCount, primaryContext
                .getWriteMessageCount());
        assertEquals(0, secondaryContext.getWriteMessageCount());
    }

    public boolean parse(String str)
    {
        ParseResult pr = getToken()
                .parseToken(primaryContext, primaryProf, str);
        if (pr.passed())
        {
            primaryContext.commit();
        } else
        {
            pr.addMessagesToLog(TestURI.getURI());
            primaryContext.rollback();
            Logging.rewindParseMessages();
            Logging.replayParsedMessages();
        }
        return pr.passed();
    }

    public boolean parseSecondary(String str)
    {
        boolean b = getToken().parseToken(secondaryContext, secondaryProf, str).passed();
        if (b)
        {
            secondaryContext.commit();
        } else
        {
            secondaryContext.rollback();
            Logging.rewindParseMessages();
            Logging.replayParsedMessages();
        }
        return b;
    }

    public abstract CDOMSubLineLoader<T> getLoader();

    public abstract CDOMPrimaryToken<T> getToken();

    @Test
    public void testNoStackTrace()
    {
        try
        {
            getToken().parseToken(primaryContext, primaryProf, null);
        } catch (Exception e)
        {
            fail("Token should not throw an exception with null input");
        }
    }

    @Test
    void testInvalidEmpty()
    {
        assertFalse(parse(""));
    }

    protected void assertConstructionError()
    {
        assertFalse(primaryContext.getReferenceContext().validate(null)
                && primaryContext.getReferenceContext().resolveReferences(null));
    }

    protected void assertCleanConstruction()
    {
        assertTrue(primaryContext.getReferenceContext().validate(null));
        assertTrue(primaryContext.getReferenceContext().resolveReferences(null));
    }

    protected <C extends Categorized<C>> void constructCategorized(LoadContext context,
            Category<C> cat,
            String name)
    {
        C obj = cat.newInstance();
        obj.setName(name);
        context.getReferenceContext().importObject(obj);
    }
}
