/*
 * PluginBuildTest.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 *
 * Created on Nov 16, 2007
 *
 * $Id$
 *
 */
package plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.PCGenTestCase;

/**
 * <code>PluginBuildTest</code> verifies that the pluginbuild.xml file has all 
 * required data. As a result this unit tets is a bit different in structure to 
 * a normal test.  
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class PluginBuildTest extends PCGenTestCase
{
	String [][] exceptions = new String[][] {
		{ "EqBuilderSpell", "EqBuilder.Spell" },
		{ "GetVar", "Var" },
		{ "HitdieLst", "Hitdie" },
		{ "Casttime", "Casttimes" },
		{ "PreVariable", "PreVar" }
	};
	
	/**
	 * Quick test suite creation - adds all methods beginning with "test"
	 * @return The Test suite
	 */
	public static Test suite()
	{
		return new TestSuite(PluginBuildTest.class);
	}

	/**
	 * Test that all required lst-choose plugins are defined.
	 * @throws Exception
	 */
	
	public void testBonusPlugins() throws Exception
	{
		String jarPrefix = "BonusToken-";
		File sourceFolder = new File("code/src/java/plugin/bonustokens");
		File jarFolder = new File("plugins/bonusplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	//TODO: Deprecated tokens don't match the standard format as they are all mixed in
	// As a result this still needs to be tackled.

	public void testExportPlugins() throws Exception
	{
		String jarPrefix = "ExportToken-";
		File sourceFolder = new File("code/src/java/plugin/exporttokens");
		File jarFolder = new File("plugins/outputplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	public void testJepPlugins() throws Exception
	{
		String jarPrefix = "JepCommand-";
		File sourceFolder = new File("code/src/java/plugin/jepcommands");
		File jarFolder = new File("plugins/jepplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder, "Command");
	}
	public void testLstAbilityPlugins() throws Exception
	{
		String jarPrefix = "AbilityLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/ability");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	public void testLstAddPlugins() throws Exception
	{
		String jarPrefix = "AddLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/add");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	public void testLstAutoPlugins() throws Exception
	{
		String jarPrefix = "AutoLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/auto");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	//TODO: BaseKitLstToken
	
	public void testLstCampaignPlugins() throws Exception
	{
		String jarPrefix = "CampaignLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/campaign");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	public void testLstChoosePlugins() throws Exception
	{
		String jarPrefix = "ChooseToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/choose");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	public void testLstClassPlugins() throws Exception
	{
		String jarPrefix = "ClassLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/pcclass");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	public void testLstCompanionmodPlugins() throws Exception
	{
		String jarPrefix = "CompanionModLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/companionmod");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	public void testLstDietyPlugins() throws Exception
	{
		String jarPrefix = "DeityLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/deity");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	public void testLstDomainPlugins() throws Exception
	{
		String jarPrefix = "DomainLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/domain");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	public void testLstEquipmentPlugins() throws Exception
	{
		String jarPrefix = "EquipmentLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/equipment");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	public void testLstEquipmentmodifierPlugins() throws Exception
	{
		String jarPrefix = "EquipmentModifierLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/equipmentmodifier");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	public void testLstGlobalPlugins() throws Exception
	{
		String jarPrefix = "LstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder, "Lst");
	}
	public void testLstKitPlugins() throws Exception
	{
		String jarPrefix = "KitLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/kit");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	//TODO: Rest of kit plugins
	public void testLstRacePlugins() throws Exception
	{
		String jarPrefix = "RaceLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/race");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	public void testLstSkillPlugins() throws Exception
	{
		String jarPrefix = "SkillLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/skill");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	public void testLstSpellPlugins() throws Exception
	{
		String jarPrefix = "SpellLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/spell");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	public void testLstSubclassPlugins() throws Exception
	{
		String jarPrefix = "SubClassLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/subclass");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	public void testLstSubstitutionlevelPlugins() throws Exception
	{
		String jarPrefix = "SubstitutionLevelLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/substitutionlevel");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	public void testLstTemplatePlugins() throws Exception
	{
		String jarPrefix = "TemplateLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/template");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	public void testLstWeaponprofPlugins() throws Exception
	{
		String jarPrefix = "WeaponProfLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/weaponprof");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	public void testPrePlugins() throws Exception
	{
		String jarPrefix = "PreToken-";
		File sourceFolder = new File("code/src/java/plugin/pretokens/parser");
		File jarFolder = new File("plugins/preplugins");
		assertTrue("Source folder " + sourceFolder.getAbsolutePath() + " should be a directory", sourceFolder.isDirectory());
		String[] sources = sourceFolder.list();
		List<String> srcList = new ArrayList<String>();
		srcList.addAll(Arrays.asList(sources));
		srcList.remove("PreSkillTotalParser.java");
		sources = (String[]) srcList.toArray(sources);
		checkPluginJars(jarPrefix, sourceFolder, jarFolder, "Parser", sources);
	}
	public void testSystemLstplugins() throws Exception
	{
		String jarPrefix = "GameModeLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/gamemode");
		File jarFolder = new File("plugins/systemlstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "GameMode-BaseDiceLstToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/gamemode/basedice");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "GameMode-EqSizePenaltyToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/gamemode/eqsizepenalty");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "GameMode-RollMethodToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/gamemode/rollmethod");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "GameMode-TabToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/gamemode/tab");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "GameMode-UnitSetToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/gamemode/unitset");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "GameMode-WieldCategory-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/gamemode/wieldcategory");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "GameMode-AbilityCategory-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/gamemode/abilitycategory");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "PointBuyLstToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/pointbuy");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "RuleCheckLstToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/rules");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "SizeAdjustmentLstToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/sizeadjustment");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "StatsAndChecksLstToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/statsandchecks");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "StatsAndChecks-AlignmentLstToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/statsandchecks/alignment");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "StatsAndChecks-BonusSpellLstToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/statsandchecks/bonusspell");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "StatsAndChecks-CheckLstToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/statsandchecks/check");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "StatsAndChecks-StatLstToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/statsandchecks/stat");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "Level-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/level");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "Eqslot-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/eqslot");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "Load-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/load");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "Paper-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/paper");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "Sponsor-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/sponsor");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
		
	}

	/**
	 * @param jarPrefix
	 * @param sourceFolder
	 * @param jarFolder
	 */
	private void checkPluginJars(String jarPrefix, File sourceFolder,
		File jarFolder)
	{
		checkPluginJars(jarPrefix, sourceFolder, jarFolder, "");
	}

	private void checkPluginJars(String jarPrefix, File sourceFolder,
		File jarFolder, String classSuffix)
	{
		assertTrue("Source folder " + sourceFolder.getAbsolutePath() + " should be a directory", sourceFolder.isDirectory());
		String[] sources = sourceFolder.list();
		checkPluginJars(jarPrefix, sourceFolder, jarFolder, classSuffix, sources);
	}
	
	private void checkPluginJars(String jarPrefix, File sourceFolder,
		File jarFolder, String classSuffix, String[] sources)
	{
		assertTrue("Jar folder " + jarFolder.getAbsolutePath() + " should be a directory", jarFolder.isDirectory());
		Set<String> jarSet = new HashSet<String>();
		String[] jars = jarFolder.list();
		for (int i = 0; i < jars.length; i++)
		{
			if (jars[i].startsWith(jarPrefix))
			{
				jarSet.add(jars[i].toLowerCase());
			}
		}
		for (int i = 0; i < sources.length; i++)
		{
			if (sources[i] != null && sources[i].endsWith(".java"))
			{
				//String testString = jarPrefix + sources[i].substring(0, sources[i].length()-5);
				String testString = sources[i];
				testString = testString.replaceAll(".java", "");
				testString = testString.replaceAll("Token", "");
				if (classSuffix.length() > 0)
				{
					testString = testString.replaceAll(classSuffix, "");
				}
				for (String exception[] : exceptions)
				{
					testString = testString.replaceAll(exception[0], exception[1]);
				}

				testString = jarPrefix + testString + ".jar";
				testString = testString.toLowerCase();
				assertTrue("Jar for " + sources[i] + " should be present in jars list", jarSet.contains(testString));
			}
		}
	}

}