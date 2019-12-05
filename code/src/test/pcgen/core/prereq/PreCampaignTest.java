/*
 * Copyright 2008 (C) James Dempsey
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.prereq;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.persistence.PersistenceManager;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The Class {@code PreCampaignTest} checks the processing
 * of the PRECAMPAIGN tag.
 */
public class PreCampaignTest extends AbstractCharacterTestCase
{
    private Campaign sourceCamp;
    private Campaign camp1;
    private Campaign camp2KeyParent;
    private Campaign camp3;
    private Campaign camp4Wild;
    private Campaign camp6TypeParent;

    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        sourceCamp = buildCampaign("Source");
        camp1 = buildCampaign("Camp1");

        camp2KeyParent = buildCampaign("Camp2");
        camp3 = buildCampaign("Camp3");
        CampaignSourceEntry cse = new CampaignSourceEntry(camp3, camp3.getSourceURI());
        camp2KeyParent.addToListFor(ListKey.FILE_PCC, cse);

        camp4Wild = buildCampaign("Camp4");
        camp4Wild.addToListFor(ListKey.BOOK_TYPE, "Wild");
        camp6TypeParent = buildCampaign("Camp5");
        cse = new CampaignSourceEntry(camp4Wild, camp4Wild.getSourceURI());
        camp6TypeParent.addToListFor(ListKey.FILE_PCC, cse);
    }


    /**
     * Test matching by key.
     *
     * @throws Exception the exception
     */
    @Test
    public void testKeyMatch() throws Exception
    {
        // Setup campaigns
        PersistenceManager pmgr = PersistenceManager.getInstance();
        List<URI> uris = new ArrayList<>();
        pmgr.setChosenCampaignSourcefiles(uris);

        final PreParserFactory factory = PreParserFactory.getInstance();
        Prerequisite preCamp1 = factory.parse("PRECAMPAIGN:1,Camp1");
        assertFalse("Nonpresent campaign should not be found",
                PrereqHandler.passes(preCamp1, null, sourceCamp));

        uris = new ArrayList<>();
        uris.add(camp1.getSourceURI());
        pmgr.setChosenCampaignSourcefiles(uris);

        assertTrue("Present campaign should be found",
                PrereqHandler.passes(preCamp1, null, sourceCamp));

        uris.add(camp2KeyParent.getSourceURI());
        pmgr.setChosenCampaignSourcefiles(uris);

        Prerequisite preCamp3 = factory.parse("PRECAMPAIGN:1,Camp3");
        assertFalse("Present but nested campaign should not be found",
                PrereqHandler.passes(preCamp3, null, sourceCamp));
    }

    /**
     * Test matching by book type.
     *
     * @throws Exception the exception
     */
    @Test
    public void testTypeMatch() throws Exception
    {
        // Setup campaigns
        PersistenceManager pmgr = PersistenceManager.getInstance();
        List<URI> uris = new ArrayList<>();
        uris.add(camp1.getSourceURI());
        pmgr.setChosenCampaignSourcefiles(uris);

        final PreParserFactory factory = PreParserFactory.getInstance();
        Prerequisite preCamp1 = factory.parse("PRECAMPAIGN:1,BOOKTYPE=Wild");
        assertFalse("No typed campaign should be found",
                PrereqHandler.passes(preCamp1, null, sourceCamp));

        uris.add(camp6TypeParent.getSourceURI());
        pmgr.setChosenCampaignSourcefiles(uris);

        assertFalse("Nested typed campaign should not be found",
                PrereqHandler.passes(preCamp1, null, sourceCamp));

        uris.add(camp4Wild.getSourceURI());
        pmgr.setChosenCampaignSourcefiles(uris);

        assertTrue("Typed campaign should be found",
                PrereqHandler.passes(preCamp1, null, sourceCamp));
    }

    /**
     * Test matching by key.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNestedKeyMatch() throws Exception
    {
        // Setup campaigns
        PersistenceManager pmgr = PersistenceManager.getInstance();
        List<URI> uris = new ArrayList<>();
        uris.add(camp1.getSourceURI());
        pmgr.setChosenCampaignSourcefiles(uris);

        final PreParserFactory factory = PreParserFactory.getInstance();
        Prerequisite preCampaign = factory.parse("PRECAMPAIGN:1,INCLUDES=Camp3");
        assertFalse("Nonpresent campaign should not be found",
                PrereqHandler.passes(preCampaign, null, sourceCamp));

        uris.add(camp2KeyParent.getSourceURI());
        pmgr.setChosenCampaignSourcefiles(uris);

        assertTrue("Present but nested campaign should be found",
                PrereqHandler.passes(preCampaign, null, sourceCamp));

    }

    /**
     * Test matching by book type.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNestedTypeMatch() throws Exception
    {
        // Setup campaigns
        PersistenceManager pmgr = PersistenceManager.getInstance();
        List<URI> uris = new ArrayList<>();
        uris.add(camp1.getSourceURI());
        pmgr.setChosenCampaignSourcefiles(uris);

        final PreParserFactory factory = PreParserFactory.getInstance();
        Prerequisite preCamp1 = factory.parse("PRECAMPAIGN:1,INCLUDESBOOKTYPE=Wild");
        assertFalse("No typed campaign should be found",
                PrereqHandler.passes(preCamp1, null, sourceCamp));

        uris.add(camp6TypeParent.getSourceURI());
        pmgr.setChosenCampaignSourcefiles(uris);

        assertTrue("Nested typed campaign should be found",
                PrereqHandler.passes(preCamp1, null, sourceCamp));

    }

    private Campaign buildCampaign(String key)
    {
        Campaign camp = new Campaign();
        camp.setKeyName(key);
        camp.setName(key);
        try
        {
            camp.setSourceURI(new URI("file:/" + key));
        } catch (URISyntaxException e)
        {
            Logging.errorPrint("PreCampaignTest.buildCampaign failed", e);
            throw new RuntimeException(e);
        }
        Globals.addCampaign(camp);
        return camp;
    }
}
