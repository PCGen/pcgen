/*
 * @(#) $Id$
 * GNU LESSER GENERAL PUBLIC LICENSE
 */
package pcgen.persistence.lst;

import gmgen.pluginmgr.PluginLoader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import pcgen.core.Campaign;
import pcgen.core.Ability;
import pcgen.core.Constants;

/**
 * JUnit testcases for <code>pcgen.core.Feat</code>.
 *
 * @version $Revision$
 */
public class FeatTest extends TestCase
{
	/**
	 * Constructor
	 * @param name
	 */
	public FeatTest(String name)
	{
		super(name);
	}

	/**
	 * Main, run the test
	 * @param args
	 */
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(FeatTest.class);
	}

	/**
	 * Return a new test suite
	 * @return Test
	 */
	public static Test suite()
	{
		// quick method, adds all methods beginning with "test"
		return new TestSuite(FeatTest.class);
	}

	/**
	 * Test Alertness Feat
	 * @throws Exception
	 */
	public void testAlertness() throws Exception
	{
		PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.s_SYSTEM_TOKENS);
		Ability alertnessFeat;
		FeatLoader featLoader = new FeatLoader();
		CampaignSourceEntry source = new CampaignSourceEntry(new Campaign(),
				FeatTest.class.getName() + ".java");
		featLoader.setCurrentSource(source);

		alertnessFeat = new Ability();
		featLoader.parseLine(alertnessFeat,
				"Alertness	TYPE:General	DESC:+2 on Listen and Spot checks	BONUS:SKILL|Listen,Spot|2", source);
		assertEquals("Alertness", alertnessFeat.getKeyName());
	}

	/**
	 * Test ambidexterity feat
	 * @throws Exception
	 */
	public void testAmbidexterity() throws Exception
	{
		PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.s_SYSTEM_TOKENS);
		FeatLoader featLoader = new FeatLoader();
		CampaignSourceEntry source = new CampaignSourceEntry(new Campaign(),
				FeatTest.class.getName() + ".java");
		featLoader.setCurrentSource(source);

		Ability ambidexterityFeat = new Ability();
		featLoader.parseLine(ambidexterityFeat,
				"Ambidexterity	PRESTAT:1,DEX=15	PREHANDSEQ:2	TYPE:General.Fighter	DESC:You ignore all penalties for using your off-hand	BONUS:COMBAT|TOHIT-SECONDARY|4",
				source);
		assertEquals("Ambidexterity", ambidexterityFeat.getKeyName());
	}

	/**
	 * Test simple weapon feat
	 * @throws Exception
	 */
	public void testSimpleWeapon() throws Exception
	{
		PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.s_SYSTEM_TOKENS);
		FeatLoader featLoader = new FeatLoader();
		CampaignSourceEntry source = new CampaignSourceEntry(new Campaign(),
				FeatTest.class.getName() + ".java");
		featLoader.setCurrentSource(source);

		Ability simpleWeaponFeat = new Ability();
		featLoader.parseLine(simpleWeaponFeat,
				"Simple Weapon Proficiency	TYPE:General	DESC:You are proficient with all simple weapons. Non-proficiency suffers -4 to hit.	ADD:WEAPONPROFS|Simple",
				source);
		assertEquals("Simple Weapon Proficiency", simpleWeaponFeat.getKeyName());
	}
}
