/*
 * Copyright (c) 2007-12 Tom Parker <thpr@users.sourceforge.net>
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.core.Campaign;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.ConsolidatedListCommitStrategy;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.RuntimeLoadContext;
import pcgen.rules.context.RuntimeReferenceContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.TokenLibrary;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.CDOMWriteToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.FormatSupport;
import util.TestURI;

public abstract class AbstractGlobalTokenTestCase
{
    protected LoadContext primaryContext;
    protected LoadContext secondaryContext;
    protected CDOMObject primaryProf;
    protected CDOMObject secondaryProf;

    protected static CampaignSourceEntry testCampaign;

    @BeforeAll
    static void classSetUp()
    {
        Locale.setDefault(Locale.US);
        testCampaign = new CampaignSourceEntry(new Campaign(), TestURI.getURI());
    }

    @BeforeEach
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        TokenRegistration.clearTokens();
        TokenRegistration.register(getReadToken());
        TokenRegistration.register(getWriteToken());
        primaryContext = new RuntimeLoadContext(RuntimeReferenceContext.createRuntimeReferenceContext(),
                new ConsolidatedListCommitStrategy());
        secondaryContext = new RuntimeLoadContext(RuntimeReferenceContext.createRuntimeReferenceContext(),
                new ConsolidatedListCommitStrategy());
        primaryProf = primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(),
                "TestObj");
        secondaryProf = secondaryContext.getReferenceContext().constructCDOMObject(
                getCDOMClass(), "TestObj");
        additionalSetup(primaryContext);
        additionalSetup(secondaryContext);
    }

    @AfterEach
    public void tearDown()
    {
        TokenRegistration.clearTokens();
    }

    public abstract <T extends CDOMObject> Class<T> getCDOMClass();

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
        assertNull(getWriteToken().unparse(primaryContext, primaryProf));

        // Set value
        for (String s : str)
        {
            assertTrue(parse(s), () -> "Should be able to parse " + s);
        }
        // Get back the appropriate token:
        String[] unparsed = getWriteToken().unparse(primaryContext, primaryProf);
        assertArrayEquals(str, unparsed);

        // Do round Robin
        String unparsedBuilt = Arrays.stream(unparsed)
                .map(s -> getReadToken().getTokenName() + ':' + s + '\t')
                .collect(Collectors.joining());
        getLoader().parseLine(secondaryContext, secondaryProf,
                unparsedBuilt, testCampaign.getURI());

        // Ensure the objects are the same
        assertEquals(primaryProf, secondaryProf);

        // And that it comes back out the same again
        validateUnparsed(secondaryContext, secondaryProf, unparsed);
        assertCleanConstruction();
        assertTrue(secondaryContext.getReferenceContext().validate(null));
        assertTrue(secondaryContext.getReferenceContext().resolveReferences(null));
        assertEquals(0, primaryContext.getWriteMessageCount());
        assertEquals(0, secondaryContext.getWriteMessageCount());
    }


    /**
     * Run a test for conversion of a deprecated format to a supported format.
     *
     * @param deprecated The old token format.
     * @param target     The expected new token format.
     * @throws PersistenceLayerException If the parsing
     */
    protected void runMigrationRoundRobin(String deprecated, String target)
            throws PersistenceLayerException
    {
        // Default is not to write out anything
        assertNull(getWriteToken().unparse(primaryContext, primaryProf));

        parse(deprecated);
        primaryProf.setSourceURI(testCampaign.getURI());
        String[] unparsed = validateUnparsed(primaryContext, primaryProf, target);

        // Do round Robin
        String unparsedBuilt = Arrays.stream(unparsed)
                .map(s -> getReadToken().getTokenName() + ':' + s + '\t')
                .collect(Collectors.joining());
        getLoader().parseLine(secondaryContext, secondaryProf,
                unparsedBuilt, testCampaign.getURI());
        // Ensure the objects are the same
        isCDOMEqual(primaryProf, secondaryProf);
        validateUnparsed(secondaryContext, secondaryProf, unparsed);
    }

    private String[] validateUnparsed(LoadContext sc, CDOMObject sp,
            String... unparsed)
    {
        String[] sUnparsed = getWriteToken().unparse(sc, sp);
        if (unparsed == null)
        {
            assertNull(sUnparsed);
        } else
        {
            assertArrayEquals(sUnparsed, unparsed);
        }

        return sUnparsed;
    }

    public boolean parse(String str)
    {
        ParseResult pr;
        try
        {
            pr = getReadToken().parseToken(primaryContext, primaryProf, str);
        } catch (IllegalArgumentException | NullPointerException e)
        {
            Logging.addParseMessage(
                    Logging.LST_ERROR,
                    "Token generated an " + e.getClass().getSimpleName() + ": "
                            + e.getLocalizedMessage());
            pr = new ParseResult.Fail("Token processing failed");
        }

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
        boolean b = getReadToken().parseToken(secondaryContext, secondaryProf, str).passed();
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

    protected String getTokenName()
    {
        return getReadToken().getTokenName();
    }

    private static void isCDOMEqual(CDOMObject cdo1, CDOMObject cdo2)
    {
        assertTrue(cdo1.isCDOMEqual(cdo2));
    }

    public void assertNoSideEffects()
    {
        isCDOMEqual(primaryProf, secondaryProf);
        assertFalse(primaryContext.getListContext().hasMasterLists());
    }

    public abstract <T extends ConcretePrereqObject> CDOMToken<T> getReadToken();

    public abstract <T extends ConcretePrereqObject> CDOMWriteToken<T> getWriteToken();

    public abstract <T extends CDOMObject> CDOMLoader<T> getLoader();

    @Test
    public void testOverwrite()
    {
        parse(getLegalValue());
        validateUnparsed(primaryContext, primaryProf, getLegalValue());
        parse(getAlternateLegalValue());
        validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
                .getAnswer(getLegalValue(), getAlternateLegalValue()));
    }

    protected abstract String getLegalValue();

    protected abstract String getAlternateLegalValue();

    protected abstract ConsolidationRule getConsolidationRule();

    protected static void expectSingle(String[] unparsed, String expected)
    {
        assertNotNull(unparsed);
        assertEquals(1, unparsed.length);
        assertEquals(expected, unparsed[0]);
    }

    protected void assertBadUnparse()
    {
        assertNull(getWriteToken().unparse(primaryContext, primaryProf));
        assertTrue(primaryContext.getWriteMessageCount() > 0);
    }

    protected void assertConstructionError()
    {
        boolean validate = primaryContext.getReferenceContext().validate(null);
        boolean resolve = primaryContext.getReferenceContext().resolveReferences(null);
        assertFalse(validate && resolve);
    }

    protected void assertCleanConstruction()
    {
        assertTrue(primaryContext.getReferenceContext().validate(null));
        assertTrue(primaryContext.getReferenceContext().resolveReferences(null));
    }

    @Test
    public void testCleanup()
    {
        String s = getLegalValue();
        assertTrue(parse(s));
    }

    @Test
    public void testAvoidContext()
    {
        RuntimeLoadContext context = new RuntimeLoadContext(
                RuntimeReferenceContext.createRuntimeReferenceContext(),
                new ConsolidatedListCommitStrategy());
        additionalSetup(context);
        CDOMObject item = context.getReferenceContext()
                .constructCDOMObject(getCDOMClass(), "TestObj");
        ParseResult pr = getReadToken().parseToken(context, item, getLegalValue());
        if (!pr.passed())
        {
            fail();
        }
        context.commit();
        assertTrue(pr.passed());
    }

    protected void additionalSetup(LoadContext context)
    {
        FormatSupport.addBasicDefaults(context);
        context.getReferenceContext().importObject(BuildUtilities.getFeatCat());
    }

}
