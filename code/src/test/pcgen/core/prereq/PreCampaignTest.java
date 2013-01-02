/*
 * PreCampaignTest.java
 * Copyright 2008 (C) James Dempsey
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 27/07/2008 15:46:38
 *
 * $Id: $
 */
package pcgen.core.prereq;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.PCGenTestCase;
import pcgen.base.lang.UnreachableError;
import pcgen.core.Ability;
import pcgen.core.Campaign;
import pcgen.core.SettingsHandler;
import pcgen.persistence.lst.CampaignSourceEntry;

/**
 * The Class <code>PreCampaignTest</code> checks the procesing
 * of the PRECAMPAIGN tag.
 * 
 * Last Editor: $Author: $
 * Last Edited: $Date:  $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision:  $
 */
public class PreCampaignTest extends PCGenTestCase
{
	
	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 */
	public static void main(final String[] args)
	{
		TestRunner.run(PreCampaignTest.class);
	}

	/**
	 * Suite.
	 * 
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreCampaignTest.class);
	}

	/**
	 * Test966023c.
	 * 
	 * @throws Exception the exception
	 */
	public void test966023c() throws Exception
	{
		SettingsHandler.getGame().addToSchoolList("Conjuration");
		SettingsHandler.getGame().addToSchoolList("Evocation");
		SettingsHandler.getGame().addToSchoolList("Illusion");
		SettingsHandler.getGame().addToSchoolList("Necromany");
//		final PlayerCharacter character = getCharacter();
		final Ability spellFocus = new Ability();

		CampaignSourceEntry cse;
		try
		{
			cse = new CampaignSourceEntry(new Campaign(),
					new URI("file:/" + getClass().getName() + ".java"));
		}
		catch (URISyntaxException e)
		{
			throw new UnreachableError(e);
		}

//		final String spellFocusStr =
//				"Spell Focus	TYPE:General	DESC:See Text	STACK:NO	MULT:YES	CHOOSE:SCHOOLS|1	BONUS:DC|SCHOOL.%LIST|1	SOURCEPAGE:Feats.rtf";
//		final FeatLoader featLoader = new FeatLoader();
//		featLoader.parseLine(Globals.getContext(), spellFocus, spellFocusStr, cse);
//		character.addFeat(spellFocus, null);
//		spellFocus.addAssociated("Evocation");
//
//		final Prerequisite preSpellFocus = new Prerequisite();
//		preSpellFocus.setKind("FEAT");
//		preSpellFocus.setKey("Spell Focus");
//		preSpellFocus.setSubKey("Conjuration");
//		preSpellFocus.setOperand("1");
//		preSpellFocus.setOperator(PrerequisiteOperator.EQ);
//
//		final boolean passes =
//				PrereqHandler.passes(preSpellFocus, character, null);
//		assertFalse(passes);
	}
}
