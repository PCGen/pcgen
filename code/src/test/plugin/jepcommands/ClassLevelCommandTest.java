/*
 * OrCommandTest.java
 * Copyright 2007 (C) andrew wilson <nuance@users.sourceforge.net>
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
 * Created on Oct 26, 2007
 *
 * $Id: OrCommandTest.java 4432 2007-10-27 02:19:47Z nuance $
 *
 */
package plugin.jepcommands;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.core.Description;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;

/**
 * <code>OrCommandTest</code> tests the functioning of the jep or plugin
 * 
 * Last Editor: $Author: nuance $ Last Edited: $Date: 2007-10-26 22:19:47 -0400
 * (Fri, 26 Oct 2007) $
 * 
 * @author andrew wilson <nuance@users.sourceforge.net>
 * @version $Revision: 4432 $
 */
public class ClassLevelCommandTest extends AbstractCharacterTestCase
{
	private Race nymphRace;
	private PCClass megaCasterClass;
	private PCClass humanoidClass;
	private PCClass nymphClass;

	/**
	 * Quick test suite creation - adds all methods beginning with "test"
	 * 
	 * @return The Test suite
	 */
	public static Test suite()
	{
		return new TestSuite(ClassLevelCommandTest.class);
	}

	/**
	 * @throws Exception
	 * @see pcgen.AbstractCharacterTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		Campaign customCampaign = new Campaign();
		customCampaign.setName("Unit Test");
		customCampaign.setName("KEY_Unit Test");
		customCampaign.addDescription(new Description("Unit Test data"));

		// Create the monseter class type
		SettingsHandler.getGame().addClassType(
				"Monster		CRFORMULA:0			ISMONSTER:YES	XPPENALTY:NO");
		SettingsHandler.getGame().setSkillMultiplierLevels("4");

		// Create the Nymph race
		nymphRace = new Race();
		nymphRace.setName("Nymph");
		nymphRace.addToListFor(ListKey.HITDICE_ADVANCEMENT, Integer.MAX_VALUE);
		nymphRace.setSize("M");
		Globals.addRace(nymphRace);

		// Create the humanoid class
		humanoidClass = new PCClass();
		humanoidClass.setName("Humanoid");
		humanoidClass.setTypeInfo("Monster");
		humanoidClass.setAbbrev("HM");
		Globals.getClassList().add(humanoidClass);

		nymphClass = new PCClass();
		nymphClass.setName("Nymph");
		nymphClass.setTypeInfo("Monster");
		nymphClass.setAbbrev("NM");
		Globals.getClassList().add(nymphClass);

		megaCasterClass = new PCClass();
		megaCasterClass.setName("MegaCaster");
		megaCasterClass.setAbbrev("MC");
		megaCasterClass.setSpellType("ARCANE");
		megaCasterClass.setSpellBaseStat("CHA");
		megaCasterClass.setSpellBookUsed(false);
		megaCasterClass.setMemorizeSpells(false);
		Globals.getClassList().add(megaCasterClass);

	}

	public void testClassLevel()
	{
		PlayerCharacter pc = this.getCharacter();
		pc.setRace(nymphRace);
		pc.incrementClassLevel(1, megaCasterClass);
		is(pc.getVariableValue("classlevel(\"Humanoid\")", ""), eq(0.0, 0.001),
				"classlevel(\"Humanoid\")");
		is(pc.getVariableValue("classlevel(\"Nymph\")", ""), eq(0.0, 0.001),
				"classlevel(\"Humanoid\")");
		is(pc.getVariableValue("classlevel(\"MegaCaster\")", ""),
				eq(1.0, 0.001), "classlevel(\"MegaCaster\")");
		is(pc.getVariableValue("classlevel(\"TYPE=Monster\")", ""), eq(0.0,
				0.001), "classlevel(\"TYPE=Monster\")");
		pc.incrementClassLevel(1, megaCasterClass);
		is(pc.getVariableValue("classlevel(\"Humanoid\")", ""), eq(0.0, 0.001),
				"classlevel(\"Humanoid\")");
		is(pc.getVariableValue("classlevel(\"Nymph\")", ""), eq(0.0, 0.001),
				"classlevel(\"Humanoid\")");
		is(pc.getVariableValue("classlevel(\"MegaCaster\")", ""),
				eq(2.0, 0.001), "classlevel(\"MegaCaster\")");
		is(pc.getVariableValue("classlevel(\"TYPE=Monster\")", ""), eq(0.0,
				0.001), "classlevel(\"TYPE=Monster\")");
		pc.incrementClassLevel(1, humanoidClass);
		is(pc.getVariableValue("classlevel(\"Humanoid\")", ""), eq(1.0, 0.001),
				"classlevel(\"Humanoid\")");
		is(pc.getVariableValue("classlevel(\"Nymph\")", ""), eq(0.0, 0.001),
				"classlevel(\"Humanoid\")");
		is(pc.getVariableValue("classlevel(\"MegaCaster\")", ""),
				eq(2.0, 0.001), "classlevel(\"MegaCaster\")");
		is(pc.getVariableValue("classlevel(\"TYPE=Monster\")", ""), eq(1.0,
				0.001), "classlevel(\"TYPE=Monster\")");
		pc.incrementClassLevel(1, nymphClass);
		is(pc.getVariableValue("classlevel(\"Humanoid\")", ""), eq(1.0, 0.001),
				"classlevel(\"Humanoid\")");
		is(pc.getVariableValue("classlevel(\"Nymph\")", ""), eq(1.0, 0.001),
				"classlevel(\"Humanoid\")");
		is(pc.getVariableValue("classlevel(\"MegaCaster\")", ""),
				eq(2.0, 0.001), "classlevel(\"MegaCaster\")");
		is(pc.getVariableValue("classlevel(\"TYPE=Monster\")", ""), eq(2.0,
				0.001), "classlevel(\"TYPE=Monster\")");
	}
}