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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * <code>PluginBuildTest</code> verifies that the pluginbuild.xml file has all 
 * required data. As a result this unit test is a bit different in structure to 
 * a normal test.  
 */
public class PluginBuildTest extends TestCase
{
	/**
	 * Array of exceptions to normal names. Each entry is a pair of
	 * Java source file name and JAR file name. 
	 */
	String[][] exceptions = new String[][] {
		{"EqBuilderSpell", "EqBuilder.Spell"},
		{"EqBuilderEqType", "EqBuilder.EqType"},
		{"GetVar", "Var"},
		{"HitdieLst", "Hitdie"},
		{"Casttime", "Casttimes"},
		{"PreVariable", "PreVar"}
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
	 * Check for the presence of all 'bonus' token plugins.
	 */
	public void testBonusPlugins()
	{
		String jarPrefix = "BonusToken-";
		File sourceFolder = new File("code/src/java/plugin/bonustokens");
		File jarFolder = new File("plugins/bonusplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all deprecated token plugins.
	 */
	public void testDeprecatedPlugins()
	{
		String jarPrefix = "^[a-zA-Z]*-DEPRECATED-";
		String sourceSuffix = "Lst";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/deprecated");
		File[] jarFolder = new File[]{new File("plugins/lstplugins"), new File("plugins/preplugins"),
			new File("plugins/bonusplugins")};
		assertTrue("Source folder " + sourceFolder.getAbsolutePath() + " should be a directory",
			sourceFolder.isDirectory());
		String[] sources = sourceFolder.list();
		List<String> srcList = new ArrayList<>();
		srcList.addAll(Arrays.asList(sources));
		srcList.remove("PreDefaultMonsterTester.java");
		srcList.remove("PreDefaultMonsterWriter.java");
		sources = srcList.toArray(sources);
		String[][] exceptions = new String[][]{
			{"MoveBonus", "Move"}
		};
		checkPluginJarsByRegex(jarPrefix, jarFolder, sourceSuffix, sources, exceptions);
	}

	/**
	 * Check for the presence of all export token plugins.
	 */
	public void testExportPlugins()
	{
		String jarPrefix = "ExportToken-";
		File sourceFolder = new File("code/src/java/plugin/exporttokens");
		File jarFolder = new File("plugins/outputplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all JEP command plugins.
	 */
	public void testJepPlugins()
	{
		String jarPrefix = "JepCommand-";
		File sourceFolder = new File("code/src/java/plugin/jepcommands");
		File jarFolder = new File("plugins/jepplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder, "Command");
	}
	
	/**
	 * Check for the presence of all ability
	 * token parsing plugins.
	 */
	public void testLstAbilityPlugins()
	{
		String jarPrefix = "AbilityLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/ability");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all 'add'
	 * token parsing plugins.
	 */
	public void testLstAddPlugins()
	{
		String jarPrefix = "AddLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/add");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all 'automatic'
	 * token parsing plugins.
	 */
	public void testLstAutoPlugins()
	{
		String jarPrefix = "AutoLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/auto");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all campaign
	 * token parsing plugins.
	 */
	public void testLstCampaignPlugins()
	{
		String jarPrefix = "CampaignLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/campaign");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all campaign
	 * token parsing plugins.
	 */
	public void testLstInstallableCampaignPlugins()
	{
		String jarPrefix = "InstCampaignLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/campaign/installable");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all choose
	 * token parsing plugins.
	 */
	public void testLstChoosePlugins()
	{
		String jarPrefix = "ChooseToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/choose");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all choose
	 * token parsing plugins.
	 */
	public void testLstEqModChoosePlugins()
	{
		String jarPrefix = "EquipmentModifierChooseLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/equipmentmodifier/choose");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all class
	 * token parsing plugins.
	 */
	public void testLstClassPlugins()
	{
		String jarPrefix = "ClassLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/pcclass");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all class
	 * token parsing plugins.
	 */
	public void testLstClassLevelPlugins()
	{
		String jarPrefix = "ClassLevelLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/pcclass/level");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all companion mod
	 * token parsing plugins.
	 */
	public void testLstCompanionModPlugins()
	{
		String jarPrefix = "CompanionModLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/companionmod");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all deity
	 * token parsing plugins.
	 */
	public void testLstDietyPlugins()
	{
		String jarPrefix = "DeityLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/deity");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all domain
	 * token parsing plugins.
	 */
	public void testLstDomainPlugins()
	{
		String jarPrefix = "DomainLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/domain");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all equipment
	 * token parsing plugins.
	 */
	public void testLstEquipmentPlugins()
	{
		String jarPrefix = "EquipmentLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/equipment");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all equipment modifier
	 * token parsing plugins.
	 */
	public void testLstEquipmentModifierPlugins()
	{
		String jarPrefix = "EquipmentModifierLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/equipmentmodifier");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all global
	 * token parsing plugins.
	 */
	public void testLstGlobalPlugins()
	{
		String jarPrefix = "LstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder, "Lst");
	}
	
	/**
	 * Check for the presence of all kit
	 * token parsing plugins.
	 */
	public void testLstKitPlugins()
	{
		String jarPrefix = "KitLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/kit");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "BaseKitLstToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/kit/basekit");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "KitAbilityLstToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/kit/ability");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "KitClassLstToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/kit/clazz");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "KitDeityLstToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/kit/deity");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "KitFundsLstToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/kit/funds");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "KitGearLstToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/kit/gear");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "KitLevelAbilityLstToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/kit/levelability");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "KitProfLstToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/kit/prof");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "KitSkillLstToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/kit/skill");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "KitSpellsLstToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/kit/spells");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "KitStartpackLstToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/kit/startpack");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

		jarPrefix = "KitTableLstToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/kit/table");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all race
	 * token parsing plugins.
	 */
	public void testLstRacePlugins()
	{
		String jarPrefix = "RaceLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/race");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all skill
	 * token parsing plugins.
	 */
	public void testLstSkillPlugins()
	{
		String jarPrefix = "SkillLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/skill");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all spell
	 * token parsing plugins.
	 */
	public void testLstSpellPlugins()
	{
		String jarPrefix = "SpellLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/spell");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all sub class
	 * token parsing plugins.
	 */
	public void testLstSubclassPlugins()
	{
		String jarPrefix = "SubClassLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/subclass");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all template
	 * token parsing plugins.
	 */
	public void testLstTemplatePlugins()
	{
		String jarPrefix = "TemplateLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/template");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all weapon proficiency
	 * token parsing plugins.
	 */
	public void testLstWeaponProfPlugins()
	{
		String jarPrefix = "WeaponProfLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/weaponprof");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all prerequisite
	 * token parsing plugins.
	 */
	public void testPrePlugins()
	{
		String jarPrefix = "PreToken-";
		File sourceFolder = new File("code/src/java/plugin/pretokens/parser");
		File jarFolder = new File("plugins/preplugins");
		assertTrue(
			"Source folder " //$NON-NLS-1$
					+ sourceFolder.getAbsolutePath()
						+ " should be a directory", //$NON-NLS-1$
			sourceFolder.isDirectory());
		String[] sources = sourceFolder.list();
		List<String> srcList = new ArrayList<>();
		srcList.addAll(Arrays.asList(sources));
		srcList.remove("PreSkillTotalParser.java");
		sources = srcList.toArray(sources);
		checkPluginJars(jarPrefix, jarFolder, "Parser", sources);
	}
	
	/**
	 * Check for the presence of all variable
	 * token parsing plugins.
	 */
	public void testLstVariablePlugins()
	{
		String jarPrefix = "VariableLstToken-";
		File sourceFolder = new File("code/src/java/plugin/lsttokens/variable");
		File jarFolder = new File("plugins/lstplugins");
		checkPluginJars(jarPrefix, sourceFolder, jarFolder);
	}
	
	/**
	 * Check for the presence of all system (gamemode, miscinfo etc) file
	 * token parsing plugins.
	 */
	public void testSystemLstPlugins()
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

		jarPrefix = "GameMode-MigrateToken-";
		sourceFolder = new File("code/src/java/plugin/lsttokens/gamemode/migrate");
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

//		jarPrefix = "StatsAndChecks-CheckLstToken-";
//		sourceFolder = new File("code/src/java/plugin/lsttokens/statsandchecks/check");
//		checkPluginJars(jarPrefix, sourceFolder, jarFolder);

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
	 * Verify that all non-excluded java files are represented by a jar 
	 * file of the same name. An exceptions list is used to track jars 
	 * which are know to have an name varying from the standard.
	 *   
	 * @param jarPrefix The text that the jar file name is expected to start with. 
	 * @param sourceFolder The folder containing the source files.
	 * @param jarFolder The folder containing the JAR files.
	 */
	private void checkPluginJars(String jarPrefix, File sourceFolder,
		File jarFolder)
	{
		checkPluginJars(jarPrefix, sourceFolder, jarFolder, "");
	}

	/**
	 * Verify that all non-excluded java files are represented by a jar 
	 * file of the same name. An exceptions list is used to track jars 
	 * which are know to have an name varying from the standard.
	 *   
	 * @param jarPrefix The text that the jar file name is expected to start with. 
	 * @param sourceFolder The folder containing the source files.
	 * @param jarFolder The folder containing the JAR files.
	 * @param classSuffix The optional suffix on the class name that should be ignored.
	 */
	private void checkPluginJars(String jarPrefix, File sourceFolder,
		File jarFolder, String classSuffix)
	{
		assertTrue(
			"Source folder "  //$NON-NLS-1$
					+ sourceFolder.getAbsolutePath()
						+ " should be a directory",  //$NON-NLS-1$
			sourceFolder.isDirectory());
		String[] sources = sourceFolder.list();
		checkPluginJars(jarPrefix, jarFolder, classSuffix, sources);
	}
	
	/**
	 * Verify that all non-excluded java files are represented by a jar 
	 * file of the same name. An exceptions list is used to track jars 
	 * which are know to have an name varying from the standard.
	 *   
	 * @param jarPrefix The text that the jar file name is expected to start with. 
	 * @param jarFolder The folder containing the JAR files.
	 * @param classSuffix The optional suffix on the class name that should be ignored.
	 * @param sources The array of names of java source files.
	 */
	private void checkPluginJars(String jarPrefix, 
		File jarFolder, String classSuffix, String[] sources)
	{
		assertTrue("Jar folder " + jarFolder.getAbsolutePath() + " should be a directory", jarFolder.isDirectory());
		Set<String> jarSet = new HashSet<>();
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
				for (String[] exception : exceptions)
				{
					testString = testString.replaceAll(exception[0], exception[1]);
				}

				testString = jarPrefix + testString + ".jar";
				testString = testString.toLowerCase();
				assertTrue("Jar for " + sources[i]
					+ " should be present in jars list as " + testString,
					jarSet.contains(testString));
			}
		}
	}
	
	/**
	 * Verify that all non-excluded java files are represented by a jar 
	 * file of the same name. An exceptions list is used to track jars 
	 * which are know to have an name varying from the standard.
	 *   
	 * @param jarRegexPrefix The regex text that the jar file name is expected to start with. 
	 * @param jarFolder The folder containing the JAR files.
	 * @param classSuffix The optional suffix on the class name that should be ignored.
	 * @param sources The array of names of java source files.
	 * @param exceptions The list of known exceptions to the naming standards.
	 */
	private void checkPluginJarsByRegex(String jarRegexPrefix,
		File[] jarFolder, String classSuffix, String[] sources,
		String[][] exceptions)
	{
		for (File folder : jarFolder)
		{
			assertTrue("Jar folder " + folder.getAbsolutePath() + " should be a directory", folder.isDirectory());
		}
		Set<String> jarSet = new HashSet<>();
		for (File folder : jarFolder)
		{
			String[] jars = folder.list();
			String jarRegexPattern = jarRegexPrefix + ".*";
			for (int i = 0; i < jars.length; i++)
			{
				if (jars[i].matches(jarRegexPattern))
				{
					String jarStr = jars[i].replaceFirst(jarRegexPrefix, "");
					jarSet.add(jarStr.toLowerCase());
				}
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
				for (String[] exception : exceptions)
				{
					testString = testString.replaceAll(exception[0], exception[1]);
				}

				testString = testString + ".jar";
				testString = testString.toLowerCase();
				assertTrue("Jar for " + sources[i] + " should be present in jars list", jarSet.contains(testString));
			}
		}
	}

}
