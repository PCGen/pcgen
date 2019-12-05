/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * {@code FacetDefinitionTest} verifies that all facets are registered in the
 * applicationContext.xml file. As a result this unit test is a bit different in
 * structure to a normal test.
 * <p>
 * Note: pcgen.cdom.facet.base only contains abstract base classes for facets
 * so is not itself checked.
 */
class FacetDefinitionTest
{
    /**
     * The file in which we expect all facets to be defined.
     */
    private static final String APP_CONTEXT_FILE = "code/src/java/applicationContext.xml";
    /**
     * Array of exceptions to normal names. Each entry is a pair of
     * Java source file name and JAR file name.
     */
    private final List<String> exceptions = Arrays.asList("AssociationChangeEvent", "AssociationChangeListener",
            "DataFacetChangeEvent", "DataFacetChangeListener",
            "ScopeFacetChangeEvent", "ScopeFacetChangeListener",
            "SubScopeFacetChangeEvent", "SubScopeFacetChangeListener",
            "CategorizedDataFacetChangeEvent", "FacetInitialization",
            "FacetLibrary");

    /**
     * Check for the presence of all 'general' facets in the spring definition.
     *
     * @throws Exception
     */
    @Test
    public void testGeneralFacets() throws Exception
    {
        File sourceFolder = new File("code/src/java/pcgen/cdom/facet");
        checkFacetsDefined(sourceFolder);
    }

    /**
     * Check for the presence of all 'analysis' facets in the spring definition.
     *
     * @throws Exception
     */
    @Test
    public void testAnalysisFacets() throws Exception
    {
        File sourceFolder = new File("code/src/java/pcgen/cdom/facet/analysis");
        checkFacetsDefined(sourceFolder);
    }

    /**
     * Check for the presence of all 'event' facets in the spring definition.
     *
     * @throws Exception
     */
    @Test
    public void testEventFacets() throws Exception
    {
        File sourceFolder = new File("code/src/java/pcgen/cdom/facet/event");
        checkFacetsDefined(sourceFolder);
    }

    /**
     * Check for the presence of all 'fact' facets in the spring definition.
     *
     * @throws Exception
     */
    @Test
    public void testFactFacets() throws Exception
    {
        File sourceFolder = new File("code/src/java/pcgen/cdom/facet/fact");
        checkFacetsDefined(sourceFolder);
    }

    /**
     * Check for the presence of all 'filter' facets in the spring definition.
     * NB: These do not exist yet so the test is disabled.
     *
     * @throws Exception
     */
    @Disabled
    @Test
    public void testFilterFacets() throws Exception
    {
        File sourceFolder = new File("code/src/java/pcgen/cdom/facet/filter");
        checkFacetsDefined(sourceFolder);
    }

    /**
     * Check for the presence of all 'input' facets in the spring definition.
     *
     * @throws Exception
     */
    @Test
    public void testInputFacets() throws Exception
    {
        File sourceFolder = new File("code/src/java/pcgen/cdom/facet/input");
        checkFacetsDefined(sourceFolder);
    }

    /**
     * Check for the presence of all 'link' facets in the spring definition.
     * NB: These do not exist yet so the test is disabled.
     *
     * @throws Exception
     */
    @Disabled
    @Test
    public void testLinkFacets() throws Exception
    {
        File sourceFolder = new File("code/src/java/pcgen/cdom/facet/link");
        checkFacetsDefined(sourceFolder);
    }

    /**
     * Check for the presence of all 'list' facets in the spring definition.
     * NB: These do not exist yet so the test is disabled.
     *
     * @throws Exception
     */
    @Disabled
    @Test
    public void testListFacets() throws Exception
    {
        File sourceFolder = new File("code/src/java/pcgen/cdom/facet/list");
        checkFacetsDefined(sourceFolder);
    }

    /**
     * Check for the presence of all 'model' facets in the spring definition.
     *
     * @throws Exception
     */
    @Test
    public void testModelFacets() throws Exception
    {
        File sourceFolder = new File("code/src/java/pcgen/cdom/facet/model");
        checkFacetsDefined(sourceFolder);
    }

    /**
     * Check for the presence of all 'utility' facets in the spring definition.
     * NB: These do not exist yet so the test is disabled.
     *
     * @throws Exception
     */
    @Disabled
    @Test
    public void testUtilityFacets() throws Exception
    {
        File sourceFolder = new File("code/src/java/pcgen/cdom/facet/utility");
        checkFacetsDefined(sourceFolder);
    }

    /**
     * Verify that all non-excluded java files are represented by an entry
     * in the applicationContext file. An exceptions list is used to track
     * classes which are not facets.
     *
     * @param sourceFolder The folder containing the source files.
     * @throws IOException
     */
    private void checkFacetsDefined(File sourceFolder) throws IOException
    {
        Assertions.assertTrue(sourceFolder.isDirectory(), "Source folder " + sourceFolder.getAbsolutePath()
                + " should be a directory");

        String packageName =
                sourceFolder.getPath().replace(File.separatorChar, '.')
                        .replace("code.src.java.", "");

        String contextData = Files.readString(Path.of(APP_CONTEXT_FILE), StandardCharsets.UTF_8);


        Files.walk(sourceFolder.toPath()).iterator().forEachRemaining(srcFile ->
        {
            String testString = srcFile.toString();
            testString = testString.replaceAll(".java", "");
            if (!exceptions.contains(testString))
            {
                testString = "class=\"" + packageName + "." + testString + "\"";
                Assertions.assertTrue(
                        contextData.contains(testString),
                        "Unable to find Spring definition for " + srcFile
                );
            }
        });
    }

}
