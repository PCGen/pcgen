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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.PCGenTestCase;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.helper.StatLock;
import pcgen.cdom.list.AbilityList;
import pcgen.core.Ability.Nature;
import pcgen.core.analysis.TemplateStat;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.GenericLoader;
import pcgen.rules.context.LoadContext;

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
	private GenericLoader<PCTemplate> loader = new GenericLoader<PCTemplate>(PCTemplate.class);
	
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
	 * Returns all the test methasVFeatshods in this class.
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
		AbilityCategory cat = new AbilityCategory("TestCat");
		SettingsHandler.getGame().addAbilityCategory(cat);
		Ability ab1 = new Ability();
		ab1.setName("Ability1");
		ab1.setCDOMCategory(cat);
		Ability ab2 = new Ability();
		ab2.setName("Ability2");
		ab2.setCDOMCategory(cat);
		Globals.addAbility(ab1);
		Globals.addAbility(ab2);

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
		LoadContext context = Globals.getContext();
		loader
			.parseLine(
				context,
				null,
				"Template1	ABILITY:TestCat|AUTOMATIC|Ability1	ABILITY:TestCat|AUTOMATIC|Ability2", source);
		PCTemplate template = context.ref.silentlyGetConstructedCDOMObject(PCTemplate.class, "Template1");
		context.ref.importObject(ab1);
		context.ref.importObject(ab2);
		context.resolveReferences();
		CDOMReference<AbilityList> autoList = AbilityList.getAbilityListReference(cat, Ability.Nature.AUTOMATIC);
		Collection<CDOMReference<Ability>> listMods = template.getListMods(autoList);
		assertEquals(2, listMods.size());
		Iterator<CDOMReference<Ability>> iterator = listMods.iterator();
		CDOMReference<Ability> ref1 = iterator.next();
		CDOMReference<Ability> ref2 = iterator.next();
		Collection<Ability> contained1 = ref1.getContainedObjects();
		Collection<Ability> contained2 = ref2.getContainedObjects();
		assertEquals(1, contained1.size());
		assertEquals(1, contained2.size());
		assertTrue(contained1.contains(ab1) || contained2.contains(ab1));
		assertTrue(contained1.contains(ab2) || contained2.contains(ab2));

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
		ab1.setCDOMCategory(AbilityCategory.FEAT);
		Ability ab2 = new Ability();
		ab2.setName("Ability2");
		ab2.setCDOMCategory(AbilityCategory.FEAT);
		Globals.addAbility(ab1);
		Globals.addAbility(ab2);

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
		LoadContext context = Globals.getContext();
		loader
			.parseLine(
					context,
					null,
				"Template1	ABILITY:FEAT|AUTOMATIC|Ability1	ABILITY:FEAT|AUTOMATIC|Ability2", source);
		PCTemplate template = context.ref.silentlyGetConstructedCDOMObject(PCTemplate.class, "Template1");
		context.ref.importObject(ab1);
		context.ref.importObject(ab2);
		context.resolveReferences();
		CDOMReference<AbilityList> autoList = AbilityList.getAbilityListReference(AbilityCategory.FEAT, Ability.Nature.AUTOMATIC);
		Collection<CDOMReference<Ability>> listMods = template.getListMods(autoList);
		assertEquals(2, listMods.size());
		Iterator<CDOMReference<Ability>> iterator = listMods.iterator();
		CDOMReference<Ability> ref1 = iterator.next();
		CDOMReference<Ability> ref2 = iterator.next();
		Collection<Ability> contained1 = ref1.getContainedObjects();
		Collection<Ability> contained2 = ref2.getContainedObjects();
		assertEquals(1, contained1.size());
		assertEquals(1, contained2.size());
		assertTrue(contained1.contains(ab1) || contained2.contains(ab1));
		assertTrue(contained1.contains(ab2) || contained2.contains(ab2));

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
		AbilityCategory cat = new AbilityCategory("TestCat");
		SettingsHandler.getGame().addAbilityCategory(cat);
		// Create some abilities to be added
		Ability ab1 = new Ability();
		ab1.setName("Ability1");
		ab1.setCDOMCategory(cat);
		Ability ab2 = new Ability();
		ab2.setName("Ability2");
		ab2.setCDOMCategory(cat);
		Globals.addAbility(ab1);
		Globals.addAbility(ab2);

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
		LoadContext context = Globals.getContext();
		loader
			.parseLine(
					context,
					null,
				"Template1	LEVEL:2:ABILITY:TestCat|AUTOMATIC|Ability1	ABILITY:TestCat|AUTOMATIC|Ability2", source);
		PCTemplate template = context.ref.silentlyGetConstructedCDOMObject(PCTemplate.class, "Template1");
		context.ref.importObject(ab1);
		context.ref.importObject(ab2);
		context.resolveReferences();
		CDOMReference<AbilityList> autoList = AbilityList.getAbilityListReference(cat, Ability.Nature.AUTOMATIC);
		Collection<CDOMReference<Ability>> listMods = template.getListMods(autoList);
		assertEquals(1, listMods.size());
		Iterator<CDOMReference<Ability>> iterator = listMods.iterator();
		CDOMReference<Ability> ref1 = iterator.next();
		Collection<Ability> contained1 = ref1.getContainedObjects();
		assertEquals(1, contained1.size());
		assertTrue(contained1.contains(ab2));

		List<PCTemplate> lvlTemplates = template.getSafeListFor(ListKey.LEVEL_TEMPLATES);
		assertEquals(1, lvlTemplates.size());
		PCTemplate lvl2 = lvlTemplates.get(0);
		assertEquals(2, lvl2.get(IntegerKey.LEVEL).intValue());
		
		listMods = lvl2.getListMods(autoList);
		assertEquals(1, listMods.size());
		iterator = listMods.iterator();
		ref1 = iterator.next();
		contained1 = ref1.getContainedObjects();
		assertEquals(1, contained1.size());
		assertTrue(contained1.contains(ab1));

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
		ab1.setCDOMCategory(AbilityCategory.FEAT);
		Ability ab2 = new Ability();
		ab2.setName("Ability2");
		ab2.setCDOMCategory(AbilityCategory.FEAT);
		Globals.addAbility(ab1);
		Globals.addAbility(ab2);

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
		LoadContext context = Globals.getContext();
		loader
			.parseLine(
					context,
				null,
				"Template1	LEVEL:2:ABILITY:Feat|AUTOMATIC|Ability1	ABILITY:Feat|AUTOMATIC|Ability2", source);
		PCTemplate template = context.ref.silentlyGetConstructedCDOMObject(PCTemplate.class, "Template1");
		context.ref.importObject(ab1);
		context.ref.importObject(ab2);
		context.resolveReferences();
		CDOMReference<AbilityList> autoList = AbilityList.getAbilityListReference(AbilityCategory.FEAT, Ability.Nature.AUTOMATIC);
		Collection<CDOMReference<Ability>> listMods = template.getListMods(autoList);
		assertEquals(1, listMods.size());
		Iterator<CDOMReference<Ability>> iterator = listMods.iterator();
		CDOMReference<Ability> ref1 = iterator.next();
		Collection<Ability> contained1 = ref1.getContainedObjects();
		assertEquals(1, contained1.size());
		assertTrue(contained1.contains(ab2));

		List<PCTemplate> lvlTemplates = template.getSafeListFor(ListKey.LEVEL_TEMPLATES);
		assertEquals(1, lvlTemplates.size());
		PCTemplate lvl2 = lvlTemplates.get(0);
		assertEquals(2, lvl2.get(IntegerKey.LEVEL).intValue());
		
		listMods = lvl2.getListMods(autoList);
		assertEquals(1, listMods.size());
		iterator = listMods.iterator();
		ref1 = iterator.next();
		contained1 = ref1.getContainedObjects();
		assertEquals(1, contained1.size());
		assertTrue(contained1.contains(ab1));

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
		StatList statList = getCharacter().getStatList();
		int index = statList.getIndexOfStatFor("STR");
		PCStat str = statList.getStatAt(index);
		assertEquals("Template has not been unlocked", false, TemplateStat.isUnlocked(template, index));
		template.addToListFor(ListKey.STAT_LOCKS, new StatLock(str, FormulaFactory.getFormulaFor(12)));
		assertEquals("Template has not been unlocked", false, TemplateStat.isUnlocked(template, index));
		template.addToListFor(ListKey.UNLOCKED_STATS, str);
		assertEquals("Template has been unlocked", true, TemplateStat.isUnlocked(template, index));
	}
	
	/**
	 * Test the isNonAbility method of PCTemplate.
	 */
	public void testIsNonAbility()
	{
		PCTemplate template = new PCTemplate();
		template.setName("Test Template");
		StatList statList = getCharacter().getStatList();
		int index = statList.getIndexOfStatFor("STR");
		PCStat str = statList.getStatAt(index);
		assertEquals("Template has not been locked to a nonability", false, TemplateStat.isNonAbility(template, index));
		template.addToListFor(ListKey.STAT_LOCKS, new StatLock(str, FormulaFactory.getFormulaFor(12)));
		assertEquals("Template has been locked to an ability", false, TemplateStat.isNonAbility(template, index));
		template.addToListFor(ListKey.STAT_LOCKS, new StatLock(str, FormulaFactory.getFormulaFor(10)));
		assertEquals("Template has been locked to a nonability", true, TemplateStat.isNonAbility(template, index));
		template.addToListFor(ListKey.UNLOCKED_STATS, str);
		assertEquals("Template has been unlocked", false, TemplateStat.isNonAbility(template, index));
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
		testClass.put(StringKey.KEY_NAME, "KEY_TestClass");
		Globals.getContext().ref.importObject(testClass);

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
