/*
 * PCTemplateTest.java
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
 * Created on Jan 3, 2007
 *
 * $Id: PCTemplateTest.java 1855 2007-01-02 06:42:02Z jdempsey $
 *
 */
package pcgen.core;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.PCGenTestCase;
import pcgen.base.lang.UnreachableError;
import pcgen.core.Ability.Nature;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.PCTemplateLoader;
import pcgen.rules.context.RuntimeLoadContext;

/**
 * <code>PCTemplateTest</code> tests the fucntion of the PCTemplate class.
 *
 * Last Editor: $Author:  $
 * Last Edited: $Date:  $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: $
 */
public class PCTemplateTest extends AbstractCharacterTestCase
{
	private PCClass testClass;
	private PCTemplateLoader loader = new PCTemplateLoader();
	
	/**
	 * Constructs a new <code>PCTemplateTest</code>.
	 *
	 * @see PCGenTestCase#PCGenTestCase()
	 */
	public PCTemplateTest()
	{
		// Do Nothing
	}

	/**
	 * Constructs a new <code>PCTemplateTest</code> with the given <var>name</var>.
	 *
	 * @param name the test case name
	 *
	 * @see PCGenTestCase#PCGenTestCase(String)
	 */
	public PCTemplateTest(final String name)
	{
		super(name);
	}

	/**
	 * Run the tests
	 * @param args
	 */
	public static void main(final String[] args)
	{
		junit.textui.TestRunner.run(PCTemplateTest.class);
	}

	/**
	 * Returns all the test methods in this class.
	 * @return A <tt>TestSuite</tt>
	 */
	public static Test suite()
	{
		// quick method, adds all methods beginning with "test"
		return new TestSuite(PCTemplateTest.class);
	}

	/**
	 * Test the definition and application of abilities. 
	 * @throws PersistenceLayerException 
	 * @throws MalformedURLException 
	 */
	public void testAddAbility() throws PersistenceLayerException, MalformedURLException
	{
		// Create some abilities to be added
		Ability ab1 = new Ability();
		ab1.setName("Ability1");
		ab1.setCategory("TestCat");
		Ability ab2 = new Ability();
		ab2.setName("Ability2");
		ab2.setCategory("TestCat");
		AbilityCategory cat = new AbilityCategory("TestCat");
		SettingsHandler.getGame().addAbilityCategory(cat);
		Globals.addAbility(ab1);
		Globals.addAbility(ab2);

		// Link them to a template
		PCTemplate template = new PCTemplate();
		//CampaignSourceEntry cse = new CampaignSourceEntry(new Campaign(), "");

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
		loader
			.parseLine(
				new RuntimeLoadContext(),
				template,
				"Template1	ABILITY:TestCat|AUTOMATIC|Ability1	ABILITY:TestCat|AUTOMATIC|Ability2", source);
		List<String> keys = template.getAbilityKeys(null, cat, Nature.AUTOMATIC);
		assertEquals(2, keys.size());
		assertEquals(ab1.getKeyName(), keys.get(0));
		assertEquals(ab2.getKeyName(), keys.get(1));

		// Add the template to the character
		PlayerCharacter pc = getCharacter();
		pc.addTemplate(template);
		// Need to do this to populate the ability list
		pc.getAutomaticAbilityList(cat);
		assertTrue("Character should have ability1.", pc.hasAbility(null,
			Nature.AUTOMATIC, ab1));
		assertTrue("Character should have ability2.", pc.hasAbility(cat,
			Nature.AUTOMATIC, ab2));
	}

	/**
	 * Test the definition and application of abilities of category FEAT. 
	 * @throws PersistenceLayerException 
	 * @throws MalformedURLException 
	 */
	public void testAddFeatAbility() throws PersistenceLayerException, MalformedURLException
	{
		// Create some abilities to be added
		Ability ab1 = new Ability();
		ab1.setName("Ability1");
		ab1.setCategory(AbilityCategory.FEAT.getKeyName());
		Ability ab2 = new Ability();
		ab2.setName("Ability2");
		ab2.setCategory(AbilityCategory.FEAT.getKeyName());
		Globals.addAbility(ab1);
		Globals.addAbility(ab2);

		// Link them to a template
		PCTemplate template = new PCTemplate();
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
		loader
			.parseLine(
					new RuntimeLoadContext(),
				template,
				"Template1	ABILITY:FEAT|AUTOMATIC|Ability1	ABILITY:FEAT|AUTOMATIC|Ability2", source);
		List<String> keys = template.getAbilityKeys(null, AbilityCategory.FEAT, Nature.AUTOMATIC);
		assertEquals(2, keys.size());
		assertEquals(ab1.getKeyName(), keys.get(0));
		assertEquals(ab2.getKeyName(), keys.get(1));

		// Add the template to the character
		PlayerCharacter pc = getCharacter();
		pc.addTemplate(template);
		// Need to do this to populate the ability list
		//pc.getAutomaticAbilityList(AbilityCategory.FEAT);
		assertTrue("Character should have ability1.", pc.hasAbility(null,
			Nature.AUTOMATIC, ab1));
		assertTrue("Character should have ability2.", pc.hasAbility(AbilityCategory.FEAT,
			Nature.AUTOMATIC, ab2));
	}

	/**
	 * Test the definition and application of abilities. 
	 * @throws PersistenceLayerException 
	 * @throws MalformedURLException 
	 */
	public void testAddLevelAbility() throws PersistenceLayerException, MalformedURLException
	{
		// Create some abilities to be added
		Ability ab1 = new Ability();
		ab1.setName("Ability1");
		ab1.setCategory("TestCat");
		Ability ab2 = new Ability();
		ab2.setName("Ability2");
		ab2.setCategory("TestCat");
		AbilityCategory cat = new AbilityCategory("TestCat");
		SettingsHandler.getGame().addAbilityCategory(cat);
		Globals.addAbility(ab1);
		Globals.addAbility(ab2);

		// Link them to a template
		PCTemplate template = new PCTemplate();
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
		loader
			.parseLine(
					new RuntimeLoadContext(),
				template,
				"Template1	LEVEL:2:ABILITY:TestCat|AUTOMATIC|Ability1	ABILITY:TestCat|AUTOMATIC|Ability2", source);
		List<String> keys = template.getAbilityKeys(null, cat, Nature.AUTOMATIC);
		assertEquals(2, keys.size());
		assertEquals(ab1.getKeyName(), keys.get(0));
		assertEquals(ab2.getKeyName(), keys.get(1));

		// Add the template to the character
		PlayerCharacter pc = getCharacter();
		pc.addTemplate(template);
		// Need to do this to populate the ability list
		pc.getAutomaticAbilityList(cat);
		assertFalse("Character should not have ability1.", pc.hasAbility(null,
			Nature.AUTOMATIC, ab1));
		assertTrue("Character should have ability2.", pc.hasAbility(cat,
			Nature.AUTOMATIC, ab2));
		
		// Level the character up, testing for when the level tag kicks in
		pc.incrementClassLevel(1, testClass);
		pc.calcActiveBonuses();
		pc.getAutomaticAbilityList(cat);
		assertFalse("Character should not have ability1.", pc.hasAbility(null,
			Nature.AUTOMATIC, ab1));

		pc.incrementClassLevel(1, testClass);
		pc.calcActiveBonuses();
		pc.getAutomaticAbilityList(cat);
		assertTrue("Character should have ability1.", pc.hasAbility(null,
			Nature.AUTOMATIC, ab1));
		
	}

	/**
	 * Test the definition and application of abilities of category FEAT. 
	 * @throws PersistenceLayerException 
	 * @throws MalformedURLException 
	 */
	public void testAddLevelFeatAbility() throws PersistenceLayerException, MalformedURLException
	{
		// Create some abilities to be added
		Ability ab1 = new Ability();
		ab1.setName("Ability1");
		ab1.setCategory(AbilityCategory.FEAT.getKeyName());
		Ability ab2 = new Ability();
		ab2.setName("Ability2");
		ab2.setCategory(AbilityCategory.FEAT.getKeyName());
		Globals.addAbility(ab1);
		Globals.addAbility(ab2);

		// Link them to a template
		PCTemplate template = new PCTemplate();
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
		loader
			.parseLine(
					new RuntimeLoadContext(),
				template,
				"Template1	LEVEL:2:ABILITY:Feat|AUTOMATIC|Ability1	ABILITY:Feat|AUTOMATIC|Ability2", source);
		List<String> keys = template.getAbilityKeys(null, AbilityCategory.FEAT, Nature.AUTOMATIC);
		assertEquals(2, keys.size());
		assertEquals(ab1.getKeyName(), keys.get(0));
		assertEquals(ab2.getKeyName(), keys.get(1));

		// Add the template to the character
		PlayerCharacter pc = getCharacter();
		pc.addTemplate(template);
		// Need to do this to populate the ability list
		pc.getAutomaticAbilityList(AbilityCategory.FEAT);
		assertFalse("Character should not have ability1.", pc.hasAbility(null,
			Nature.AUTOMATIC, ab1));
		assertTrue("Character should have ability2.", pc.hasAbility(AbilityCategory.FEAT,
			Nature.AUTOMATIC, ab2));
		
		// Level the character up, testing for when the level tag kicks in
		pc.incrementClassLevel(1, testClass);
		pc.calcActiveBonuses();
		pc.getAutomaticAbilityList(AbilityCategory.FEAT);
		assertFalse("Character should not have ability1.", pc.hasAbility(null,
			Nature.AUTOMATIC, ab1));

		pc.incrementClassLevel(1, testClass);
		pc.calcActiveBonuses();
		pc.getAutomaticAbilityList(AbilityCategory.FEAT);
		assertTrue("Character should have ability1.", pc.hasAbility(null,
			Nature.AUTOMATIC, ab1));
		
	}
	
	/**
	 * Test the isUnlocked method of PCTemplate.
	 */
	public void testIsUnlocked()
	{
		PCTemplate template = new PCTemplate();
		template.setName("Test Template");
		int index = getCharacter().getStatList().getIndexOfStatFor("STR");
		assertEquals("Template has not been unlocked", false, template.isUnlocked(index));
		template.addVariable(-9, "LOCK.STR", "12");
		assertEquals("Template has not been unlocked", false, template.isUnlocked(index));
		template.addVariable(-9, "UNLOCK.STR", "");
		assertEquals("Template has been unlocked", true, template.isUnlocked(index));
	}
	
	/**
	 * Test the isNonAbility method of PCTemplate.
	 */
	public void testIsNonAbility()
	{
		PCTemplate template = new PCTemplate();
		template.setName("Test Template");
		int index = getCharacter().getStatList().getIndexOfStatFor("STR");
		assertEquals("Template has not been locked to a nonability", false, template.isNonAbility(index));
		template.addVariable(-9, "LOCK.STR", "12");
		assertEquals("Template has been locked to an ability", false, template.isNonAbility(index));
		template.addVariable(-9, "LOCK.STR", "10");
		assertEquals("Template has been locked to a nonability", true, template.isNonAbility(index));
		template.addVariable(-9, "UNLOCK.STR", "");
		assertEquals("Template has been unlocked", false, template.isNonAbility(index));
	}
	
	
	/**
	 * @see pcgen.AbstractCharacterTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		// Create the test class
		testClass = new PCClass();
		testClass.setName("TestClass");
		testClass.setKeyName("KEY_TestClass");
		Globals.getClassList().add(testClass);

	}

	/**
	 * @see pcgen.AbstractCharacterTestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
}
