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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * {@code FacetDefinitionTest} verifies that all facets are registered in the
 * applicationContext.xml file. As a result this unit test is a bit different in 
 * structure to a normal test.
 * 
 * Note: pcgen.cdom.facet.base only contains abstract base classes for facets 
 * so is not itself checked.
 */
public class FacetDefinitionTest
{
	/** The file in which we expect all facets to be defined. */
	static final String APP_CONTEXT_FILE = "code/src/java/applicationContext.xml"; 
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
	 * @throws Exception 
	 */
	@Ignore
	@Test
	public void testFilterFacets() throws Exception
	{
		File sourceFolder = new File("code/src/java/pcgen/cdom/facet/filter");
		checkFacetsDefined(sourceFolder);
	}
	
	/**
	 * Check for the presence of all 'input' facets in the spring definition.
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
	 * @throws Exception 
	 */
	@Ignore
	@Test
	public void testLinkFacets() throws Exception
	{
		File sourceFolder = new File("code/src/java/pcgen/cdom/facet/link");
		checkFacetsDefined(sourceFolder);
	}
	
	/**
	 * Check for the presence of all 'list' facets in the spring definition.
	 * NB: These do not exist yet so the test is disabled.
	 * @throws Exception 
	 */
	@Ignore
	@Test
	public void testListFacets() throws Exception
	{
		File sourceFolder = new File("code/src/java/pcgen/cdom/facet/list");
		checkFacetsDefined(sourceFolder);
	}
	
	/**
	 * Check for the presence of all 'model' facets in the spring definition.
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
	 * @throws Exception 
	 */
	@Ignore
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
		assertTrue("Source folder " + sourceFolder.getAbsolutePath()
			+ " should be a directory", sourceFolder.isDirectory());

		String packageName =
				sourceFolder.getPath().replace(File.separatorChar, '.')
					.replace("code.src.java.", "");
		String contextData =
				FileUtils.readFileToString(new File(APP_CONTEXT_FILE), "UTF-8");

		for (Iterator<File> facetSourceFileIter =
				FileUtils.iterateFiles(sourceFolder, new String[]{"java"},
					false); facetSourceFileIter.hasNext();)
		{
			File srcFile = facetSourceFileIter.next();
			String testString = srcFile.getName();
			testString = testString.replaceAll(".java", "");
			if (exceptions.contains(testString))
			{
				//System.out.println("Skipping " + srcFile);
				continue;
			}
			testString = "class=\"" + packageName + "." + testString + "\"";
			assertTrue("Unable to find Spring definition for " + srcFile,
					contextData.contains(testString)
			);
		}
	}

}
