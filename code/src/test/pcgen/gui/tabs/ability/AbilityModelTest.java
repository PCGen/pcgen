/*
 * AbilityModelTest.java
 * Copyright 2008 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on 23/03/2008
 *
 * $Id$
 */
package pcgen.gui.tabs.ability;

import java.util.ArrayList;
import java.util.List;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.gui.utils.PObjectNode;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.TestHelper;

/**
 * <code>AbilityModelTest</code> verifies that the AbilityModel class
 * is functioning correctly.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class AbilityModelTest extends AbstractCharacterTestCase
{

	/**
	 * Test building the prereq tree.
	 * @throws PersistenceLayerException 
	 */
	public void testBuildTreePrereqTree() throws PersistenceLayerException
	{
		final PreParserFactory factory = PreParserFactory.getInstance();
		List<Ability> abilityList = new ArrayList<Ability>();
		Ability dodge = TestHelper.makeAbility("Dodge", "FEAT", "General");
		Prerequisite prereq = factory.parse("PRESTAT:1,DEX=13");
		dodge.addPreReq(prereq);
		abilityList.add(dodge);

		Ability mobility = TestHelper.makeAbility("Mobility", "FEAT", "General");
		prereq = factory.parse("PREFEAT:1,KEY_Dodge");
		mobility.addPreReq(prereq);
		prereq = factory.parse("PRESTAT:1,DEX=13");
		mobility.addPreReq(prereq);
		abilityList.add(mobility);

		Ability mountedCbt = TestHelper.makeAbility("Mounted Combat", "FEAT", "General");
		prereq = factory.parse("PRESKILL:1,Ride=1");
		mountedCbt.addPreReq(prereq);
		abilityList.add(mountedCbt);

		Ability improvedFeint = TestHelper.makeAbility("Improved Feint", "FEAT", "General");
		prereq = factory.parse("PREFEAT:1,KEY_Combat Expertise");
		improvedFeint.addPreReq(prereq);
		abilityList.add(improvedFeint);

		Ability springAttack = TestHelper.makeAbility("Spring Attack", "FEAT", "General");
		prereq = factory.parse("PREATT:4");
		springAttack.addPreReq(prereq);
		prereq = factory.parse("PREFEAT:2,KEY_Dodge,KEY_Mobility");
		springAttack.addPreReq(prereq);
		prereq = factory.parse("PRESTAT:1,DEX=13");
		springAttack.addPreReq(prereq);
		abilityList.add(springAttack);
		
		AbilityModel am =
				new AbilityModel(getCharacter(), abilityList,
					AbilityCategory.FEAT,
					AbilitySelectionPanel.ViewMode.PREREQTREE, "Unit test");
		PObjectNode root = (PObjectNode) am.getRoot();
		// Check root
		assertNotNull(root);

		// Check first level
		assertEquals("First level first entry", "KEY_Dodge", ((PObject)root.getChild(0).getItem()).getKeyName());
		assertEquals("First level second entry", "KEY_Improved Feint", ((PObject)root.getChild(1).getItem()).getKeyName());
		assertEquals("First level third entry", "KEY_Mounted Combat", ((PObject)root.getChild(2).getItem()).getKeyName());
		assertEquals(3, root.getChildCount());

		// Check second level
		PObjectNode secondLevelBase = root.getChild(0);
		assertEquals("Second level first entry", "KEY_Mobility", ((PObject)secondLevelBase.getChild(0).getItem()).getKeyName());
		assertEquals(1, secondLevelBase.getChildCount());

		// Check third level
		PObjectNode thirdLevelBase = secondLevelBase.getChild(0);
		assertEquals("Third level first entry", "KEY_Spring Attack", ((PObject)thirdLevelBase.getChild(0).getItem()).getKeyName());
		assertEquals(1, thirdLevelBase.getChildCount());
	}

}
