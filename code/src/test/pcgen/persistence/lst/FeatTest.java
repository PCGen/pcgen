/*
 * GNU LESSER GENERAL PUBLIC LICENSE
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.net.URISyntaxException;

import pcgen.base.lang.UnreachableError;
import pcgen.core.Ability;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.TestHelper;
import plugin.lsttokens.testsupport.BuildUtilities;

import junit.framework.TestCase;
import org.junit.Assert;

/**
 * JUnit testcases for {@code pcgen.core.Feat}.
 */
public class FeatTest extends TestCase
{
	/**
	 * Sets up the test case by loading the system plugins.
	 */
	@Override
	public void setUp() throws Exception
	{
		TestHelper.loadPlugins();
		Globals.getContext().getReferenceContext().importObject(BuildUtilities.getFeatCat());
	}

	/**
	 * Test Alertness Feat.
	 * 
	 * @throws PersistenceLayerException   if there is a problem with the LST syntax
	 */
	public void testAlertness() throws PersistenceLayerException
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
		Assert.assertEquals("Alertness", alertnessFeat.getKeyName());
	}

	/**
	 * Test ambidexterity feat.
	 * 
	 * @throws PersistenceLayerException   if there is a problem with the LST syntax
	 */
	public void testAmbidexterity() throws PersistenceLayerException
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
			.parseLine(Globals.getContext(), ambidexterityFeat,
				"Ambidexterity	PRESTAT:1,DEX=15	PREHANDSEQ:2	TYPE:General.Fighter	"
			+ "DESC:You ignore all penalties for using your off-hand	BONUS:COMBAT|TOHIT-SECONDARY|4",
				source);
		Assert.assertEquals("Ambidexterity", ambidexterityFeat.getKeyName());
	}

	/**
	 * Test simple weapon feat.
	 * 
	 * @throws PersistenceLayerException  if there is a problem with the LST syntax
	 */
	public void testSimpleWeapon() throws PersistenceLayerException
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
				"Simple Weapon Proficiency	TYPE:General	DESC:You are proficient with all simple weapons. "
						+ "Non-proficiency suffers -4 to hit.	ADD:WEAPONPROFS|Simple", source);
		Assert.assertEquals("Simple Weapon Proficiency", simpleWeaponFeat.getKeyName());
	}
}
