/*
 * GNU LESSER GENERAL PUBLIC LICENSE
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import pcgen.base.lang.UnreachableError;
import pcgen.core.Ability;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.BuildUtilities;

/**
 * JUnit testcases for <code>pcgen.core.Feat</code>.
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
	 * Sets up the test case by loading the system plugins.
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	public void setUp() throws Exception
	{
		TestHelper.loadPlugins();
		Globals.getContext().getReferenceContext().importObject(BuildUtilities.getFeatCat());
	}

	/**
	 * Test Alertness Feat
	 * @throws Exception
	 */
	public void testAlertness() throws Exception
	{
		Ability alertnessFeat;
		FeatLoader featLoader = new FeatLoader();
		CampaignSourceEntry source;
		try
		{
			source = new CampaignSourceEntry(new Campaign(),
					new URI("file:/" + getClass().getName() + ".java"));
		}
		catch (URISyntaxException e)
		{
			throw new UnreachableError(e);
		}

		alertnessFeat = new Ability();
		featLoader
			.parseLine(
				Globals.getContext(),
				alertnessFeat,
				"Alertness	TYPE:General	DESC:+2 on Listen and Spot checks	BONUS:SKILL|Listen,Spot|2", source);
		assertEquals("Alertness", alertnessFeat.getKeyName());
	}

	/**
	 * Test ambidexterity feat
	 * @throws Exception
	 */
	public void testAmbidexterity() throws Exception
	{
		FeatLoader featLoader = new FeatLoader();
		CampaignSourceEntry source;
		try
		{
			source = new CampaignSourceEntry(new Campaign(),
					new URI("file:/" + getClass().getName() + ".java"));
		}
		catch (URISyntaxException e)
		{
			throw new UnreachableError(e);
		}

		Ability ambidexterityFeat = new Ability();
		featLoader
			.parseLine(
				Globals.getContext(),
				ambidexterityFeat,
				"Ambidexterity	PRESTAT:1,DEX=15	PREHANDSEQ:2	TYPE:General.Fighter	DESC:You ignore all penalties for using your off-hand	BONUS:COMBAT|TOHIT-SECONDARY|4", source);
		assertEquals("Ambidexterity", ambidexterityFeat.getKeyName());
	}

	/**
	 * Test simple weapon feat
	 * @throws Exception
	 */
	public void testSimpleWeapon() throws Exception
	{
		FeatLoader featLoader = new FeatLoader();
		CampaignSourceEntry source;
		try
		{
			source = new CampaignSourceEntry(new Campaign(),
					new URI("file:/" + getClass().getName() + ".java"));
		}
		catch (URISyntaxException e)
		{
			throw new UnreachableError(e);
		}

		Ability simpleWeaponFeat = new Ability();
		featLoader
			.parseLine(
				Globals.getContext(),
				simpleWeaponFeat,
				"Simple Weapon Proficiency	TYPE:General	DESC:You are proficient with all simple weapons. Non-proficiency suffers -4 to hit.	ADD:WEAPONPROFS|Simple", source);
		assertEquals("Simple Weapon Proficiency", simpleWeaponFeat.getKeyName());
	}
}
