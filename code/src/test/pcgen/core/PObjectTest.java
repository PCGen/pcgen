/*
 * PObjectTest.java
 * Copyright 2005 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on Apr 9, 2005
 *
 * $Id:  $
 *
 */
package pcgen.core;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.PCGenTestCase;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.persistence.lst.RaceLoader;

/**
 * Test the PObject class.
 * @author jdempsey
 */
public class PObjectTest extends AbstractCharacterTestCase
{

	/**
	 * Constructs a new <code>PObjectTest</code>.
	 *
	 * @see PCGenTestCase#PCGenTestCase()
	 */
	public PObjectTest()
	{
		// Do Nothing
	}

	/**
	 * Constructs a new <code>PObjectTest</code> with the given <var>name</var>.
	 *
	 * @param name the test case name
	 *
	 * @see PCGenTestCase#PCGenTestCase(String)
	 */
	public PObjectTest(final String name)
	{
		super(name);
	}

	public static void main(final String[] args)
	{
		junit.textui.TestRunner.run(PObjectTest.class);
	}

	public static Test suite()
	{
		// quick method, adds all methods beginning with "test"
		return new TestSuite(PObjectTest.class);
	}

	public void testDR() throws Exception
	{
		Race race = new Race();
		race.setName("Race");
		PCTemplate template = new PCTemplate();
		race.setName("Template");

		race.setDR("5/Good");
		assertEquals("Basic DR set.", "5/Good", race.getDR());

		race.setDR(".CLEAR");
		race.setDR("0/-");
		assertEquals("Basic DR set.", "0/-", race.getDR());

		template.setDR("0/-");
		template.addBonusList("DR|-|1");
		PlayerCharacter pc = getCharacter();
		pc.setRace(race);
		pc.addTemplate(template);
		pc.calcActiveBonuses();
		assertEquals("Basic DR set.", "1/-", pc.calcDR());
	}

	/**
	 * Test the processing of getPCCText to ensure that it correctly produces
	 * an LST representation of an object and that the LST can then be reloaded
	 * to recrete the object.
	 *
	 * @throws PersistenceLayerException
	 */
	public void testGetPCCText() throws PersistenceLayerException
	{
		Race race = new Race();
		race.setName("TestRace");
		race.setCR(5);
		String racePCCText = race.getPCCText();
		assertNotNull("PCC Text for race should not be null", racePCCText);

		RaceLoader raceLoader = new RaceLoader();
		CampaignSourceEntry source = new CampaignSourceEntry(new Campaign(),
				getClass().getName() + ".java");
		raceLoader.setCurrentSource(source);
		Race reconstRace = new Race();
		raceLoader.parseLine(reconstRace, racePCCText, source);
		assertEquals(
			"getPCCText should be the same after being encoded and reloaded",
			racePCCText, reconstRace.getPCCText());
		assertEquals("Racial CR was not restored after saving and reloading.",
			race.getCR(), reconstRace.getCR());

		PCClass aClass = new PCClass();
		aClass.setName("TestClass");
		aClass.setAbbrev("TC");
		String classPCCText = aClass.getPCCText();
		assertNotNull("PCC Text for race should not be null", racePCCText);

		PCClassLoader classLoader = new PCClassLoader();
		classLoader.setCurrentSource(source);
		PCClass reconstClass = new PCClass();
		reconstClass = (PCClass) classLoader.parseLine(reconstClass, classPCCText, source);
		assertEquals(
			"getPCCText should be the same after being encoded and reloaded",
			classPCCText, reconstClass.getPCCText());
		assertEquals("Class abbrev was not restored after saving and reloading.",
			aClass.getAbbrev(), reconstClass.getAbbrev());

	}

	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
}