/*
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package pcgen.persistence.lst;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.InstallableCampaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A collection of tests to validate the functioning of the InstallLoader class.
 */
public final class InstallLoaderTest
{
    private static final String PUBNAMESHORT = "PCGen";
    private static final String PUBNAMELONG = "PCGen Open Source Team";
    private static final String SOURCEDATE = "2007-12";
    private static final String SOURCEWEB = "http://pcgen.sourceforge.net/";
    private static final String SOURCESHORT = "PCGen TOCC";
    private static final String SOURCELONG = "PCGen Test Out of Cycle Releases";
    private static final String INFOTEXT =
            "PCGen Open Gaming Content objects are detailed in the PCGen "
                    + "documentation under the Source Help section";
    private static final String COPYRIGHT_3 =
            "PCGen OGC dataset Copyright 2006, PCGen Data team (Including, "
                    + "but not limited to Eddy Anthony (MoSaT), Andrew McDougall (Tir Gwaith)).";
    private static final String COPYRIGHT_2 =
            "System Reference Document Copyright 2000-2003, Wizards of the "
                    + "Coast, Inc.; Authors Jonathan Tweet, Monte Cook, Skip Williams, "
                    + "Rich Baker, Andy Collins, David Noonan, Rich Redman, Bruce R. "
                    + "Cordell, John D. Rateliff, Thomas Reid, James Wyatt, based on "
                    + "original material by E. Gary Gygax and Dave Arneson.";
    private static final String COPYRIGHT_1 =
            "Open Game License v 1.0a Copyright 2000, Wizards of the Coast, Inc.";
    private static final String DEST = "DATA";
    private static final String MINDEVVER = "5.13.5";
    private static final String MINVER = "5.14.0";
    private static final String CAMPAIGN_NAME = "PCGen Test OCC";

    /**
     * The sample install data for testing.
     */
    private static final String[] INSTALL_DATA =
            {"CAMPAIGN:" + CAMPAIGN_NAME, "MINVER:" + MINVER,
                    "MINDEVVER: " + MINDEVVER, "DEST:" + DEST,
                    "COPYRIGHT:" + COPYRIGHT_1, "COPYRIGHT:" + COPYRIGHT_2,
                    "COPYRIGHT:" + COPYRIGHT_3, "INFOTEXT:" + INFOTEXT,
                    "SOURCELONG:" + SOURCELONG, "SOURCESHORT:" + SOURCESHORT,
                    "SOURCEWEB:" + SOURCEWEB, "SOURCEDATE:" + SOURCEDATE,
                    "PUBNAMELONG:" + PUBNAMELONG, "PUBNAMESHORT:" + PUBNAMESHORT,
                    "PUBNAMEWEB:" + SOURCEWEB};

    @BeforeEach
    public void setUp()
    {
        TestHelper.loadPlugins();
    }

    @AfterEach
    public void tearDown()
    {
        TokenRegistration.clearTokens();
    }

    /**
     * Load the supplied installable campaign set data.
     *
     * @param installData The data to be loaded.
     * @throws PersistenceLayerException
     * @throws URISyntaxException
     */
    private static InstallableCampaign loadInstallData(
            final String[] installData) throws PersistenceLayerException,
            URISyntaxException
    {
        final InstallLoader loader = new InstallLoader();
        StringBuilder data = new StringBuilder();
        for (final String line : installData)
        {
            data.append(line);
            data.append("\n");
        }
        loader.loadLstString(null, new URI("http://UNIT_TEST_CASE"), data.toString());
        return loader.getCampaign();
    }

    /**
     * Validate the test data can be loaded successfully.
     *
     * @throws PersistenceLayerException the persistence layer exception
     * @throws URISyntaxException        the URI syntax exception
     * @throws ParseException            the parse exception
     */
    @Test
    public void testParseLine() throws PersistenceLayerException, URISyntaxException, ParseException
    {
        InstallableCampaign camp = loadInstallData(INSTALL_DATA);

        assertEquals(CAMPAIGN_NAME, camp
                .getDisplayName(), "Checking campaign name");
        assertEquals(COPYRIGHT_1, camp.getSafeListFor(ListKey.SECTION_15)
                .get(0), "Checking copyright 1");
        assertEquals(COPYRIGHT_2, camp.getSafeListFor(ListKey.SECTION_15)
                .get(1), "Checking copyright 2");
        assertEquals(COPYRIGHT_3, camp.getSafeListFor(ListKey.SECTION_15)
                .get(2), "Checking copyright 3");
        assertEquals(INFOTEXT, camp.getSafeListFor(ListKey.INFO_TEXT)
                .get(0), "Checking info text");
        assertEquals(PUBNAMESHORT, camp.getSafe(StringKey.PUB_NAME_SHORT), "Checking pub name short");
        assertEquals(PUBNAMELONG, camp.getSafe(StringKey.PUB_NAME_LONG), "Checking pub name long");
        assertEquals(SOURCEWEB, camp.getSafe(StringKey.PUB_NAME_WEB), "Checking pub name web");
        assertEquals(CAMPAIGN_NAME, camp
                .getDisplayName(), "Checking campaign name");
        assertEquals(SOURCESHORT, camp
                .get(StringKey.SOURCE_SHORT), "Checking source name short");
        assertEquals(SOURCELONG, camp
                .get(StringKey.SOURCE_LONG), "Checking source name long");
        assertEquals(SOURCEWEB, camp
                .get(StringKey.SOURCE_WEB), "Checking source name web");

        Date theDate;
        DateFormat df = new SimpleDateFormat("yyyy-MM"); //$NON-NLS-1$
        try
        {
            theDate = df.parse(SOURCEDATE);
        } catch (ParseException pe)
        {
            df = DateFormat.getDateInstance();
            theDate = df.parse(SOURCEDATE);
        }
        assertEquals(theDate, camp
                .get(ObjectKey.SOURCE_DATE), "Checking source date");

        assertEquals(MINVER, camp.getSafe(StringKey.MINVER), "Checking min ver");
        assertEquals(MINVER, camp.getSafe(StringKey.MINVER), "Checking min dev ver");
        assertEquals(DEST, camp.get(ObjectKey.DESTINATION).toString(), "Checking destination");
    }

}
