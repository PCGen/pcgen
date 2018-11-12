/*
 * Copyright 2001 (C) B. K. Oxley (binkley) <binkley@alumni.rice.edu>
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
 */
package pcgen.core;

import pcgen.core.utils.CoreUtilityTest;
import pcgen.persistence.lst.FeatTest;

import junit.framework.Test;
import junit.framework.TestSuite;

//import pcgen.core.LevelAbilityTest;

/**
 * TestSuite that is composed of the individual test classes.  Pick up all the
 * individual PCGen core test cases into this one.
 */
public final class AllJUnitTests
{
	private AllJUnitTests()
	{
	}

	/**
	 * suite
	 * @return Test
	 */
	public static Test suite()
	{
		final TestSuite suite = new TestSuite("PCGEN Core Tests");
		suite.addTest(new TestSuite(BioSetTest.class));
		suite.addTest(new TestSuite(ClassTypeTest.class));
		suite.addTest(new TestSuite(EquipmentListTest.class));
		suite.addTest(new TestSuite(EquipmentModifierTest.class));
		suite.addTest(new TestSuite(EquipmentTest.class));
		suite.addTest(new TestSuite(EquipmentUtilitiesTest.class));
		suite.addTest(new TestSuite(GlobalsTest.class));
		suite.addTest(new TestSuite(PCClassTest.class));
		suite.addTest(new TestSuite(PlayerCharacterTest.class));
		suite.addTest(new TestSuite(PObjectTest.class));
		suite.addTest(new TestSuite(PrereqHandlerTest.class));
		suite.addTest(new TestSuite(CoreUtilityTest.class));

		// core.bonus
		suite.addTest(new TestSuite(pcgen.core.bonus.BonusTest.class));

		// core.chooser
		suite.addTest(new TestSuite(
			pcgen.core.chooser.DomainChoiceManagerTest.class));

		// core.levelability
		suite.addTest(new TestSuite(
			pcgen.core.levelability.AddClassSkillsTest.class));

		// core.prereq
		suite.addTest(new TestSuite(
			pcgen.core.prereq.AbstractPrerequisiteTestTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreAlignTest.class));
		//suite.addTest(new TestSuite(pcgen.core.prereq.PreArmorProfTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreAttTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreBaseSizeTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreBirthplaceTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreCharactertypeTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreCheckBaseTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreCheckTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreCityTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreClassTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreCSkillTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreDeityAlignTest.class));
		suite
			.addTest(new TestSuite(pcgen.core.prereq.PreDeityDomainTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreDeityTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreDomainTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreDRTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreEquipTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreMultTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreRaceTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreReqHandlerTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreSizeTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreSkillTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreSubClassTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreTemplateTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreTypeTest.class));
		suite.addTest(new TestSuite(pcgen.core.prereq.PreVisionTest.class));

		suite.addTest(FeatTest.suite());

		return suite;
	}
}
